package com.greexam.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a scheduled test/exam.
 */
public class Test {

    public enum TestStatus {
        UPCOMING, ACTIVE, COMPLETED
    }

    private int id;
    private int teacherId;
    private String title;
    private int durationMinutes;
    private int passingMarks;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isPublished;
    private boolean showOneAtTime;
    private Timestamp createdAt;

    // Loaded relationships
    private List<TestQuestion> testQuestions = new ArrayList<>();
    private List<Integer> assignedStudentIds = new ArrayList<>();
    private int studentCount;
    private int completedCount;

    public Test() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getPassingMarks() { return passingMarks; }
    public void setPassingMarks(int passingMarks) { this.passingMarks = passingMarks; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public boolean isShowOneAtTime() { return showOneAtTime; }
    public void setShowOneAtTime(boolean showOneAtTime) { this.showOneAtTime = showOneAtTime; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<TestQuestion> getTestQuestions() { return testQuestions; }
    public void setTestQuestions(List<TestQuestion> testQuestions) { this.testQuestions = testQuestions; }

    public List<Integer> getAssignedStudentIds() { return assignedStudentIds; }
    public void setAssignedStudentIds(List<Integer> assignedStudentIds) { this.assignedStudentIds = assignedStudentIds; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

    public int getTotalMarks() {
        return testQuestions.stream().mapToInt(TestQuestion::getMarks).sum();
    }

    public TestStatus getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) return TestStatus.UPCOMING;
        if (now.isAfter(endTime)) return TestStatus.COMPLETED;
        return TestStatus.ACTIVE;
    }

    public String getStatusText() {
        return switch (getStatus()) {
            case UPCOMING -> "Upcoming";
            case ACTIVE -> "Active";
            case COMPLETED -> "Completed";
        };
    }

    // Inner class for test-question mapping
    public static class TestQuestion {
        private int id;
        private int testId;
        private int questionId;
        private int orderNumber;
        private int marks;
        private Question question; // loaded lazily

        public TestQuestion() {}

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getTestId() { return testId; }
        public void setTestId(int testId) { this.testId = testId; }
        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }
        public int getOrderNumber() { return orderNumber; }
        public void setOrderNumber(int orderNumber) { this.orderNumber = orderNumber; }
        public int getMarks() { return marks; }
        public void setMarks(int marks) { this.marks = marks; }
        public Question getQuestion() { return question; }
        public void setQuestion(Question question) { this.question = question; }
    }
}
