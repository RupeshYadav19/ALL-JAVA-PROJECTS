package com.greexam.service;

import com.greexam.dao.QuestionDAO;
import com.greexam.dao.SubmissionDAO;
import com.greexam.dao.TestDAO;
import com.greexam.model.Question;
import com.greexam.model.Question.*;
import com.greexam.model.StudentAnswer;
import com.greexam.model.Submission;
import com.greexam.model.Test;
import com.greexam.model.Test.TestQuestion;

import java.util.List;

/**
 * Service for exam-related business logic including auto-grading.
 */
public class ExamService {

    private final TestDAO testDAO = new TestDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final NotificationService notificationService = NotificationService.getInstance();

    /**
     * Start a test session for a student.
     * @return submission ID
     */
    public int startTest(int testId, int studentId) {
        // Check if already submitted
        if (testDAO.hasStudentSubmitted(testId, studentId)) {
            return -1;
        }
        // Check for existing incomplete submission
        Submission existing = submissionDAO.findByTestAndStudent(testId, studentId);
        if (existing != null && !existing.isCompleted()) {
            return existing.getId();
        }
        return submissionDAO.createSubmission(testId, studentId);
    }

    /**
     * Save a student's answer for a question.
     */
    public void saveAnswer(int submissionId, int questionId, String answerText, int timeSpent, boolean isSkipped) {
        StudentAnswer sa = new StudentAnswer();
        sa.setSubmissionId(submissionId);
        sa.setQuestionId(questionId);
        sa.setAnswerText(answerText);
        sa.setTimeSpentSeconds(timeSpent);
        sa.setSkipped(isSkipped);
        submissionDAO.saveAnswer(sa);
    }

    /**
     * Submit and auto-grade a test.
     */
    public Submission submitTest(int submissionId, boolean autoSubmitted) {
        Submission sub = submissionDAO.findById(submissionId);
        if (sub == null || sub.isCompleted()) return sub;

        // Load test with questions
        Test test = testDAO.findById(sub.getTestId());
        List<StudentAnswer> answers = submissionDAO.findAnswersBySubmission(submissionId);

        double totalMarks = 0;

        for (StudentAnswer sa : answers) {
            if (sa.isSkipped()) continue;

            // Find the corresponding test question for marks
            TestQuestion tq = test.getTestQuestions().stream()
                    .filter(q -> q.getQuestionId() == sa.getQuestionId())
                    .findFirst().orElse(null);
            if (tq == null) continue;

            Question question = tq.getQuestion();
            if (question == null) continue;

            double earned = autoGrade(question, sa, tq.getMarks());
            sa.setMarksObtained(earned);
            totalMarks += earned;

            // Update the answer in DB
            submissionDAO.saveAnswer(sa);
        }

        // Complete the submission
        submissionDAO.completeSubmission(submissionId, totalMarks, autoSubmitted);

        // Notify teacher
        notificationService.notifyTeacher(test.getTeacherId(),
                "Student submitted test: " + test.getTitle());

        // Reload and return
        return submissionDAO.findById(submissionId);
    }

    /**
     * Auto-grade a question based on its type.
     */
    private double autoGrade(Question question, StudentAnswer answer, int maxMarks) {
        if (answer.getAnswerText() == null || answer.getAnswerText().isEmpty()) {
            answer.setIsCorrect(false);
            return 0;
        }

        return switch (question.getQuestionType()) {
            case MCQ -> gradeMCQ(question, answer, maxMarks);
            case FILL_BLANK -> gradeFillBlank(question, answer, maxMarks);
            case ONE_WORD -> gradeOneWord(question, answer, maxMarks);
            case MATCH -> gradeMatch(question, answer, maxMarks);
            case SHORT_ANSWER, LONG_ANSWER -> {
                // Requires manual grading — mark as pending
                answer.setIsCorrect(null);
                yield 0;
            }
        };
    }

    private double gradeMCQ(Question q, StudentAnswer sa, int maxMarks) {
        String correct = q.getOptions().stream()
                .filter(QuestionOption::isCorrect)
                .map(QuestionOption::getOptionText)
                .findFirst().orElse("");
        boolean isCorrect = correct.equalsIgnoreCase(sa.getAnswerText().trim());
        sa.setIsCorrect(isCorrect);
        return isCorrect ? maxMarks : 0;
    }

    private double gradeFillBlank(Question q, StudentAnswer sa, int maxMarks) {
        String[] answers = sa.getAnswerText().split("\\|\\|\\|");
        List<FillBlankAnswer> blanks = q.getFillBlanks();

        int correctCount = 0;
        for (int i = 0; i < blanks.size() && i < answers.length; i++) {
            FillBlankAnswer blank = blanks.get(i);
            String studentAns = answers[i].trim();
            boolean match = blank.isCaseSensitive()
                    ? blank.getCorrectAnswer().equals(studentAns)
                    : blank.getCorrectAnswer().equalsIgnoreCase(studentAns);
            if (match) correctCount++;
        }

        boolean allCorrect = correctCount == blanks.size();
        sa.setIsCorrect(allCorrect);
        // Partial marks: proportional
        return blanks.isEmpty() ? 0 : ((double) correctCount / blanks.size()) * maxMarks;
    }

    private double gradeOneWord(Question q, StudentAnswer sa, int maxMarks) {
        String text = q.getQuestionText();
        int idx = text.indexOf("|||");
        String correctAnswer = idx >= 0 ? text.substring(idx + 3).trim() : "";
        boolean isCorrect = correctAnswer.equalsIgnoreCase(sa.getAnswerText().trim());
        sa.setIsCorrect(isCorrect);
        return isCorrect ? maxMarks : 0;
    }

    private double gradeMatch(Question q, StudentAnswer sa, int maxMarks) {
        // Answer format: "left1:right1|||left2:right2|||..."
        String[] pairs = sa.getAnswerText().split("\\|\\|\\|");
        List<MatchPair> correctPairs = q.getMatchPairs();

        int correctCount = 0;
        for (String pair : pairs) {
            String[] parts = pair.split(":", 2);
            if (parts.length != 2) continue;
            String left = parts[0].trim();
            String right = parts[1].trim();

            boolean match = correctPairs.stream()
                    .anyMatch(mp -> mp.getLeftItem().equalsIgnoreCase(left)
                            && mp.getRightItem().equalsIgnoreCase(right));
            if (match) correctCount++;
        }

        boolean allCorrect = correctCount == correctPairs.size();
        sa.setIsCorrect(allCorrect);
        // Partial marks
        return correctPairs.isEmpty() ? 0 : ((double) correctCount / correctPairs.size()) * maxMarks;
    }
}
