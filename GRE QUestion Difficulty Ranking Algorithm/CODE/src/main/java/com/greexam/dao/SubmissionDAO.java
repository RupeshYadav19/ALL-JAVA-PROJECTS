package com.greexam.dao;

import com.greexam.db.DBConnection;
import com.greexam.model.StudentAnswer;
import com.greexam.model.Submission;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Submission and StudentAnswer operations.
 */
public class SubmissionDAO {

    // ─── Submission CRUD ───────────────────────────────────
    public int createSubmission(int testId, int studentId) {
        String sql = "INSERT INTO Submissions (test_id, student_id, start_time) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, testId);
            ps.setInt(2, studentId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Submission findById(int id) {
        String sql = "SELECT s.*, u.name as student_name, t.title as test_title FROM Submissions s JOIN Users u ON s.student_id = u.id JOIN Tests t ON s.test_id = t.id WHERE s.id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Submission sub = mapSubmission(rs);
                sub.setStudentName(rs.getString("student_name"));
                sub.setTestTitle(rs.getString("test_title"));
                loadAnswers(sub);
                return sub;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Submission findByTestAndStudent(int testId, int studentId) {
        String sql = "SELECT * FROM Submissions WHERE test_id = ? AND student_id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            ps.setInt(2, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Submission sub = mapSubmission(rs);
                loadAnswers(sub);
                return sub;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Submission> findByTest(int testId) {
        List<Submission> subs = new ArrayList<>();
        String sql = "SELECT s.*, u.name as student_name FROM Submissions s JOIN Users u ON s.student_id = u.id WHERE s.test_id = ? ORDER BY s.total_marks_obtained DESC";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Submission sub = mapSubmission(rs);
                sub.setStudentName(rs.getString("student_name"));
                subs.add(sub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subs;
    }

    public List<Submission> findByStudent(int studentId) {
        List<Submission> subs = new ArrayList<>();
        String sql = "SELECT s.*, t.title as test_title FROM Submissions s JOIN Tests t ON s.test_id = t.id WHERE s.student_id = ? AND s.is_completed = TRUE ORDER BY s.end_time DESC";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Submission sub = mapSubmission(rs);
                sub.setTestTitle(rs.getString("test_title"));
                subs.add(sub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subs;
    }

    public boolean completeSubmission(int submissionId, double totalMarks, boolean autoSubmitted) {
        String sql = "UPDATE Submissions SET end_time = ?, total_marks_obtained = ?, is_completed = TRUE, auto_submitted = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setDouble(2, totalMarks);
            ps.setBoolean(3, autoSubmitted);
            ps.setInt(4, submissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTotalMarks(int submissionId, double totalMarks) {
        String sql = "UPDATE Submissions SET total_marks_obtained = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setDouble(1, totalMarks);
            ps.setInt(2, submissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ─── StudentAnswer CRUD ────────────────────────────────
    public boolean saveAnswer(StudentAnswer answer) {
        // Check if answer already exists
        String check = "SELECT id FROM StudentAnswers WHERE submission_id = ? AND question_id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(check)) {
            ps.setInt(1, answer.getSubmissionId());
            ps.setInt(2, answer.getQuestionId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Update existing
                return updateAnswer(answer, rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Insert new
        String sql = "INSERT INTO StudentAnswers (submission_id, question_id, answer_text, is_correct, marks_obtained, time_spent_seconds, is_skipped) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, answer.getSubmissionId());
            ps.setInt(2, answer.getQuestionId());
            ps.setString(3, answer.getAnswerText());
            if (answer.getIsCorrect() != null) {
                ps.setBoolean(4, answer.getIsCorrect());
            } else {
                ps.setNull(4, Types.BOOLEAN);
            }
            ps.setDouble(5, answer.getMarksObtained());
            ps.setInt(6, answer.getTimeSpentSeconds());
            ps.setBoolean(7, answer.isSkipped());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) answer.setId(keys.getInt(1));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean updateAnswer(StudentAnswer answer, int existingId) {
        String sql = "UPDATE StudentAnswers SET answer_text = ?, is_correct = ?, marks_obtained = ?, time_spent_seconds = time_spent_seconds + ?, is_skipped = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setString(1, answer.getAnswerText());
            if (answer.getIsCorrect() != null) {
                ps.setBoolean(2, answer.getIsCorrect());
            } else {
                ps.setNull(2, Types.BOOLEAN);
            }
            ps.setDouble(3, answer.getMarksObtained());
            ps.setInt(4, answer.getTimeSpentSeconds());
            ps.setBoolean(5, answer.isSkipped());
            ps.setInt(6, existingId);
            answer.setId(existingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAnswerTimeSpent(int submissionId, int questionId, int additionalSeconds) {
        String sql = "UPDATE StudentAnswers SET time_spent_seconds = time_spent_seconds + ? WHERE submission_id = ? AND question_id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, additionalSeconds);
            ps.setInt(2, submissionId);
            ps.setInt(3, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<StudentAnswer> findAnswersBySubmission(int submissionId) {
        List<StudentAnswer> answers = new ArrayList<>();
        String sql = """
            SELECT sa.*, q.question_text, q.question_type, q.marks as q_marks
            FROM StudentAnswers sa
            JOIN Questions q ON sa.question_id = q.id
            WHERE sa.submission_id = ?
            ORDER BY sa.id
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StudentAnswer sa = mapStudentAnswer(rs);
                sa.setQuestionText(rs.getString("question_text"));
                sa.setQuestionType(rs.getString("question_type"));
                sa.setQuestionMarks(rs.getInt("q_marks"));
                answers.add(sa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }

    private void loadAnswers(Submission sub) {
        sub.setAnswers(findAnswersBySubmission(sub.getId()));
    }

    // ─── Teacher Feedback ──────────────────────────────────
    public boolean saveFeedback(int submissionId, int questionId, String comment, double manualMarks) {
        // Check existing
        String check = "SELECT id FROM TeacherFeedback WHERE submission_id = ? AND question_id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(check)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Update
                String upd = "UPDATE TeacherFeedback SET teacher_comment = ?, manual_marks = ? WHERE id = ?";
                try (PreparedStatement ps2 = DBConnection.conn().prepareStatement(upd)) {
                    ps2.setString(1, comment);
                    ps2.setDouble(2, manualMarks);
                    ps2.setInt(3, rs.getInt("id"));
                    ps2.executeUpdate();
                }
            } else {
                // Insert
                String ins = "INSERT INTO TeacherFeedback (submission_id, question_id, teacher_comment, manual_marks) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps2 = DBConnection.conn().prepareStatement(ins)) {
                    ps2.setInt(1, submissionId);
                    ps2.setInt(2, questionId);
                    ps2.setString(3, comment);
                    ps2.setDouble(4, manualMarks);
                    ps2.executeUpdate();
                }
            }

            // Update StudentAnswer marks
            String updSa = "UPDATE StudentAnswers SET marks_obtained = ?, is_correct = ? WHERE submission_id = ? AND question_id = ?";
            try (PreparedStatement ps3 = DBConnection.conn().prepareStatement(updSa)) {
                ps3.setDouble(1, manualMarks);
                ps3.setBoolean(2, manualMarks > 0);
                ps3.setInt(3, submissionId);
                ps3.setInt(4, questionId);
                ps3.executeUpdate();
            }

            // Recalculate total marks
            recalculateTotalMarks(submissionId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getFeedbackComment(int submissionId, int questionId) {
        String sql = "SELECT teacher_comment FROM TeacherFeedback WHERE submission_id = ? AND question_id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("teacher_comment");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void recalculateTotalMarks(int submissionId) {
        String sql = "UPDATE Submissions SET total_marks_obtained = (SELECT COALESCE(SUM(marks_obtained), 0) FROM StudentAnswers WHERE submission_id = ?) WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, submissionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ─── Mappers ───────────────────────────────────────────
    private Submission mapSubmission(ResultSet rs) throws SQLException {
        Submission sub = new Submission();
        sub.setId(rs.getInt("id"));
        sub.setTestId(rs.getInt("test_id"));
        sub.setStudentId(rs.getInt("student_id"));
        Timestamp st = rs.getTimestamp("start_time");
        Timestamp et = rs.getTimestamp("end_time");
        sub.setStartTime(st != null ? st.toLocalDateTime() : null);
        sub.setEndTime(et != null ? et.toLocalDateTime() : null);
        sub.setTotalMarksObtained(rs.getDouble("total_marks_obtained"));
        sub.setCompleted(rs.getBoolean("is_completed"));
        sub.setAutoSubmitted(rs.getBoolean("auto_submitted"));
        return sub;
    }

    private StudentAnswer mapStudentAnswer(ResultSet rs) throws SQLException {
        StudentAnswer sa = new StudentAnswer();
        sa.setId(rs.getInt("id"));
        sa.setSubmissionId(rs.getInt("submission_id"));
        sa.setQuestionId(rs.getInt("question_id"));
        sa.setAnswerText(rs.getString("answer_text"));
        boolean isCorrectVal = rs.getBoolean("is_correct");
        sa.setIsCorrect(rs.wasNull() ? null : isCorrectVal);
        sa.setMarksObtained(rs.getDouble("marks_obtained"));
        sa.setTimeSpentSeconds(rs.getInt("time_spent_seconds"));
        sa.setSkipped(rs.getBoolean("is_skipped"));
        return sa;
    }
}
