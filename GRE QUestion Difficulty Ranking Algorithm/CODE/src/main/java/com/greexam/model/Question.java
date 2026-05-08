package com.greexam.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a question in the question bank.
 * Supports MCQ, Fill-in-the-Blank, One Word, Short Answer, Long Answer, and Match the Following.
 */
public class Question {

    public enum QuestionType {
        MCQ, FILL_BLANK, ONE_WORD, SHORT_ANSWER, LONG_ANSWER, MATCH
    }

    private int id;
    private int teacherId;
    private String questionText;
    private QuestionType questionType;
    private int marks;
    private String topic;
    private int expectedTimeSeconds;
    private Timestamp createdAt;

    // Related data loaded via DAOs
    private List<QuestionOption> options = new ArrayList<>();       // For MCQ
    private List<FillBlankAnswer> fillBlanks = new ArrayList<>();   // For FILL_BLANK
    private List<MatchPair> matchPairs = new ArrayList<>();         // For MATCH
    private String expectedAnswer; // For ONE_WORD, SHORT_ANSWER, LONG_ANSWER (stored in question_text or separately)

    public Question() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public int getExpectedTimeSeconds() { return expectedTimeSeconds; }
    public void setExpectedTimeSeconds(int expectedTimeSeconds) { this.expectedTimeSeconds = expectedTimeSeconds; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<QuestionOption> getOptions() { return options; }
    public void setOptions(List<QuestionOption> options) { this.options = options; }

    public List<FillBlankAnswer> getFillBlanks() { return fillBlanks; }
    public void setFillBlanks(List<FillBlankAnswer> fillBlanks) { this.fillBlanks = fillBlanks; }

    public List<MatchPair> getMatchPairs() { return matchPairs; }
    public void setMatchPairs(List<MatchPair> matchPairs) { this.matchPairs = matchPairs; }

    public String getExpectedAnswer() { return expectedAnswer; }
    public void setExpectedAnswer(String expectedAnswer) { this.expectedAnswer = expectedAnswer; }

    public String getTypeDisplayName() {
        return switch (questionType) {
            case MCQ -> "Multiple Choice";
            case FILL_BLANK -> "Fill in the Blank";
            case ONE_WORD -> "One Word Answer";
            case SHORT_ANSWER -> "Short Answer";
            case LONG_ANSWER -> "Long Answer";
            case MATCH -> "Match the Following";
        };
    }

    // Inner Classes for related data
    public static class QuestionOption {
        private int id;
        private int questionId;
        private String optionText;
        private boolean isCorrect;

        public QuestionOption() {}
        public QuestionOption(String optionText, boolean isCorrect) {
            this.optionText = optionText;
            this.isCorrect = isCorrect;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }
        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
    }

    public static class FillBlankAnswer {
        private int id;
        private int questionId;
        private int blankPosition;
        private String correctAnswer;
        private boolean caseSensitive;

        public FillBlankAnswer() {}
        public FillBlankAnswer(int blankPosition, String correctAnswer, boolean caseSensitive) {
            this.blankPosition = blankPosition;
            this.correctAnswer = correctAnswer;
            this.caseSensitive = caseSensitive;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }
        public int getBlankPosition() { return blankPosition; }
        public void setBlankPosition(int blankPosition) { this.blankPosition = blankPosition; }
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
        public boolean isCaseSensitive() { return caseSensitive; }
        public void setCaseSensitive(boolean caseSensitive) { this.caseSensitive = caseSensitive; }
    }

    public static class MatchPair {
        private int id;
        private int questionId;
        private String leftItem;
        private String rightItem;

        public MatchPair() {}
        public MatchPair(String leftItem, String rightItem) {
            this.leftItem = leftItem;
            this.rightItem = rightItem;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }
        public String getLeftItem() { return leftItem; }
        public void setLeftItem(String leftItem) { this.leftItem = leftItem; }
        public String getRightItem() { return rightItem; }
        public void setRightItem(String rightItem) { this.rightItem = rightItem; }
    }
}
