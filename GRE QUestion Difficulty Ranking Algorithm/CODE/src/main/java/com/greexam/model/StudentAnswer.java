package com.greexam.model;

/**
 * Represents a student's answer to a specific question within a submission.
 */
public class StudentAnswer {

    private int id;
    private int submissionId;
    private int questionId;
    private String answerText;
    private Boolean isCorrect;   // null = pending review
    private double marksObtained;
    private int timeSpentSeconds;
    private boolean isSkipped;

    // Display helpers (loaded via joins)
    private String questionText;
    private String correctAnswer;
    private String questionType;
    private int questionMarks;

    public StudentAnswer() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(double marksObtained) { this.marksObtained = marksObtained; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public boolean isSkipped() { return isSkipped; }
    public void setSkipped(boolean skipped) { isSkipped = skipped; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public int getQuestionMarks() { return questionMarks; }
    public void setQuestionMarks(int questionMarks) { this.questionMarks = questionMarks; }

    public boolean isPendingReview() {
        return isCorrect == null && !isSkipped;
    }

    public String getStatusText() {
        if (isSkipped) return "Skipped";
        if (isCorrect == null) return "Pending Review";
        return isCorrect ? "Correct" : "Incorrect";
    }
}
