package com.greexam.dao;

import com.greexam.db.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for analytics and difficulty ranking queries.
 */
public class AnalyticsDAO {

    /**
     * Get test overview statistics.
     * Returns map: totalStudents, totalSubmissions, avgMarks, highestScore, lowestScore
     */
    public Map<String, Object> getTestOverview(int testId) {
        Map<String, Object> stats = new HashMap<>();
        String sql = """
            SELECT
                (SELECT COUNT(*) FROM TestStudents WHERE test_id = ?) as total_students,
                (SELECT COUNT(*) FROM Submissions WHERE test_id = ? AND is_completed = TRUE) as total_submissions,
                (SELECT COALESCE(AVG(total_marks_obtained), 0) FROM Submissions WHERE test_id = ? AND is_completed = TRUE) as avg_marks,
                (SELECT COALESCE(MAX(total_marks_obtained), 0) FROM Submissions WHERE test_id = ? AND is_completed = TRUE) as highest_score,
                (SELECT COALESCE(MIN(total_marks_obtained), 0) FROM Submissions WHERE test_id = ? AND is_completed = TRUE) as lowest_score
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) ps.setInt(i, testId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("totalStudents", rs.getInt("total_students"));
                stats.put("totalSubmissions", rs.getInt("total_submissions"));
                stats.put("avgMarks", rs.getDouble("avg_marks"));
                stats.put("highestScore", rs.getDouble("highest_score"));
                stats.put("lowestScore", rs.getDouble("lowest_score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Get per-question analytics for a test.
     * Returns list of maps with: questionId, questionText, questionType, avgTime, correctRate, skipRate, difficultyLevel
     */
    public List<Map<String, Object>> getPerQuestionAnalytics(int testId) {
        List<Map<String, Object>> analytics = new ArrayList<>();
        String sql = """
            SELECT
                tq.question_id,
                q.question_text,
                q.question_type,
                q.expected_time_seconds,
                q.topic,
                tq.marks,
                tq.order_number,
                COALESCE(AVG(sa.time_spent_seconds), 0) as avg_time,
                COALESCE(SUM(CASE WHEN sa.is_correct = TRUE THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(sa.id), 0), 0) as correct_rate,
                COALESCE(SUM(CASE WHEN sa.is_skipped = TRUE THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(sa.id), 0), 0) as skip_rate
            FROM TestQuestions tq
            JOIN Questions q ON tq.question_id = q.id
            LEFT JOIN Submissions s ON s.test_id = tq.test_id AND s.is_completed = TRUE
            LEFT JOIN StudentAnswers sa ON sa.question_id = tq.question_id AND sa.submission_id = s.id
            WHERE tq.test_id = ?
            GROUP BY tq.question_id, q.question_text, q.question_type, q.expected_time_seconds, q.topic, tq.marks, tq.order_number
            ORDER BY tq.order_number
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("questionId", rs.getInt("question_id"));
                row.put("questionText", rs.getString("question_text"));
                row.put("questionType", rs.getString("question_type"));
                row.put("expectedTime", rs.getInt("expected_time_seconds"));
                row.put("topic", rs.getString("topic"));
                row.put("marks", rs.getInt("marks"));
                row.put("avgTime", rs.getDouble("avg_time"));
                row.put("correctRate", rs.getDouble("correct_rate"));
                row.put("skipRate", rs.getDouble("skip_rate"));

                // Calculate difficulty
                double cr = rs.getDouble("correct_rate");
                double at = rs.getDouble("avg_time");
                double sr = rs.getDouble("skip_rate");
                int et = rs.getInt("expected_time_seconds");
                row.put("difficultyLevel", calculateDifficulty(cr, at, sr, et));
                analytics.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analytics;
    }

    /**
     * Difficulty Ranking Logic:
     * - EASY: correct_rate >= 70% AND avg_time <= expected_time
     * - DIFFICULT: correct_rate < 70% AND avg_time > expected_time
     * - MODERATE: correct_rate >= 40% AND correct_rate < 70% AND avg_time > expected_time
     * - NEEDS_ATTENTION: skip_rate > 30%
     */
    public String calculateDifficulty(double correctRate, double avgTime, double skipRate, int expectedTime) {
        if (skipRate > 30) return "NEEDS_ATTENTION";
        if (correctRate >= 70 && avgTime <= expectedTime) return "EASY";
        if (correctRate < 40) return "DIFFICULT";
        if (correctRate >= 40 && correctRate < 70 && avgTime > expectedTime) return "MODERATE";
        if (correctRate < 70 && avgTime > expectedTime) return "DIFFICULT";
        return "MODERATE";
    }

    /**
     * Save/update difficulty rankings for a test.
     */
    public void updateDifficultyRankings(int testId) {
        List<Map<String, Object>> analytics = getPerQuestionAnalytics(testId);
        
        // Use a transaction for consistency: clear old rankings and insert fresh ones
        try (Connection conn = DBConnection.conn()) {
            conn.setAutoCommit(false);
            try {
                // Delete existing rankings for this test
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM DifficultyRankings WHERE test_id = ?")) {
                    del.setInt(1, testId);
                    del.executeUpdate();
                }

                // Insert new rankings
                String insSql = "INSERT INTO DifficultyRankings (question_id, test_id, difficulty_level, avg_time_seconds, correct_rate, skip_rate) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ins = conn.prepareStatement(insSql)) {
                    for (Map<String, Object> row : analytics) {
                        ins.setInt(1, (int) row.get("questionId"));
                        ins.setInt(2, testId);
                        ins.setString(3, (String) row.get("difficultyLevel"));
                        ins.setDouble(4, ((Number) row.getOrDefault("avgTime", 0.0)).doubleValue());
                        ins.setDouble(5, ((Number) row.getOrDefault("correctRate", 0.0)).doubleValue());
                        ins.setDouble(6, ((Number) row.getOrDefault("skipRate", 0.0)).doubleValue());
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get topic weakness report for a test.
     */
    public List<Map<String, Object>> getTopicWeaknessReport(int testId) {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = """
            SELECT
                q.topic,
                COUNT(DISTINCT tq.question_id) as question_count,
                COALESCE(AVG(CASE WHEN sa.is_correct = TRUE THEN 1.0 ELSE 0.0 END) * 100, 0) as correct_rate,
                COALESCE(AVG(CASE WHEN sa.is_skipped = TRUE THEN 1.0 ELSE 0.0 END) * 100, 0) as skip_rate,
                COALESCE(AVG(sa.time_spent_seconds), 0) as avg_time
            FROM TestQuestions tq
            JOIN Questions q ON tq.question_id = q.id
            LEFT JOIN StudentAnswers sa ON sa.question_id = tq.question_id
                AND sa.submission_id IN (SELECT id FROM Submissions WHERE test_id = ? AND is_completed = TRUE)
            WHERE tq.test_id = ? AND q.topic IS NOT NULL
            GROUP BY q.topic
            ORDER BY correct_rate ASC
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            ps.setInt(2, testId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("topic", rs.getString("topic"));
                row.put("questionCount", rs.getInt("question_count"));
                row.put("correctRate", rs.getDouble("correct_rate"));
                row.put("skipRate", rs.getDouble("skip_rate"));
                row.put("avgTime", rs.getDouble("avg_time"));
                report.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    /**
     * Get leaderboard for a test.
     */
    public List<Map<String, Object>> getLeaderboard(int testId) {
        List<Map<String, Object>> board = new ArrayList<>();
        String sql = """
            SELECT s.student_id, u.name, s.total_marks_obtained,
                   TIMESTAMPDIFF(MINUTE, s.start_time, s.end_time) as time_taken_minutes
            FROM Submissions s
            JOIN Users u ON s.student_id = u.id
            WHERE s.test_id = ? AND s.is_completed = TRUE
            ORDER BY s.total_marks_obtained DESC, time_taken_minutes ASC
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("rank", rank++);
                row.put("studentName", rs.getString("name"));
                row.put("marks", rs.getDouble("total_marks_obtained"));
                row.put("timeTaken", rs.getInt("time_taken_minutes"));
                board.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return board;
    }

    /**
     * Get student score trend across all tests.
     */
    public List<Map<String, Object>> getStudentScoreTrend(int studentId) {
        List<Map<String, Object>> trend = new ArrayList<>();
        String sql = """
            SELECT t.title, s.total_marks_obtained,
                   (SELECT SUM(tq.marks) FROM TestQuestions tq WHERE tq.test_id = t.id) as total_marks
            FROM Submissions s
            JOIN Tests t ON s.test_id = t.id
            WHERE s.student_id = ? AND s.is_completed = TRUE
            ORDER BY s.end_time ASC
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("testTitle", rs.getString("title"));
                row.put("obtained", rs.getDouble("total_marks_obtained"));
                row.put("total", rs.getInt("total_marks"));
                double pct = rs.getInt("total_marks") > 0 ?
                        (rs.getDouble("total_marks_obtained") / rs.getInt("total_marks")) * 100 : 0;
                row.put("percentage", pct);
                trend.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trend;
    }

    /**
     * Count submissions per test for a teacher's notification use.
     */
    public int getSubmissionCount(int testId) {
        String sql = "SELECT COUNT(*) FROM Submissions WHERE test_id = ? AND is_completed = TRUE";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
