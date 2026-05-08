package com.greexam.dao;

import com.greexam.db.DBConnection;
import com.greexam.model.Test;
import com.greexam.model.Test.TestQuestion;
import com.greexam.util.DateTimeUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Test operations.
 */
public class TestDAO {

    private final QuestionDAO questionDAO = new QuestionDAO();

    // ─── Create ────────────────────────────────────────────
    public boolean insert(Test test) {
        String sql = "INSERT INTO Tests (teacher_id, title, duration_minutes, passing_marks, start_time, end_time, is_published, show_one_at_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, test.getTeacherId());
            ps.setString(2, test.getTitle());
            ps.setInt(3, test.getDurationMinutes());
            ps.setInt(4, test.getPassingMarks());
            ps.setTimestamp(5, DateTimeUtil.toTimestamp(test.getStartTime()));
            ps.setTimestamp(6, DateTimeUtil.toTimestamp(test.getEndTime()));
            ps.setBoolean(7, test.isPublished());
            ps.setBoolean(8, test.isShowOneAtTime());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    test.setId(keys.getInt(1));
                }
                // Insert test questions
                insertTestQuestions(test);
                // Insert student assignments
                insertTestStudents(test);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insertTestQuestions(Test test) throws SQLException {
        String sql = "INSERT INTO TestQuestions (test_id, question_id, order_number, marks) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            for (TestQuestion tq : test.getTestQuestions()) {
                ps.setInt(1, test.getId());
                ps.setInt(2, tq.getQuestionId());
                ps.setInt(3, tq.getOrderNumber());
                ps.setInt(4, tq.getMarks());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertTestStudents(Test test) throws SQLException {
        String sql = "INSERT INTO TestStudents (test_id, student_id) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            for (int studentId : test.getAssignedStudentIds()) {
                ps.setInt(1, test.getId());
                ps.setInt(2, studentId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ─── Read ──────────────────────────────────────────────
    public Test findById(int id) {
        String sql = "SELECT * FROM Tests WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Test test = mapTest(rs);
                loadTestQuestions(test);
                loadAssignedStudents(test);
                return test;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Test> findByTeacher(int teacherId) {
        List<Test> tests = new ArrayList<>();
        String sql = """
            SELECT t.*, 
                   (SELECT COUNT(*) FROM TestStudents ts WHERE ts.test_id = t.id) as student_count,
                   (SELECT COUNT(*) FROM Submissions s WHERE s.test_id = t.id AND s.is_completed = TRUE) as completed_count
            FROM Tests t WHERE t.teacher_id = ? ORDER BY t.created_at DESC
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Test test = mapTest(rs);
                test.setStudentCount(rs.getInt("student_count"));
                test.setCompletedCount(rs.getInt("completed_count"));
                tests.add(test);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tests;
    }

    public List<Test> findTestsForStudent(int studentId) {
        List<Test> tests = new ArrayList<>();
        String sql = """
            SELECT t.* FROM Tests t
            JOIN TestStudents ts ON t.id = ts.test_id
            WHERE ts.student_id = ? AND t.is_published = TRUE
            ORDER BY t.start_time DESC
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Test test = mapTest(rs);
                loadTestQuestions(test);
                tests.add(test);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tests;
    }

    public List<Test> findPublishedByTeacher(int teacherId) {
        List<Test> tests = new ArrayList<>();
        String sql = """
            SELECT t.*, 
                   (SELECT COUNT(*) FROM TestStudents ts WHERE ts.test_id = t.id) as student_count,
                   (SELECT COUNT(*) FROM Submissions s WHERE s.test_id = t.id AND s.is_completed = TRUE) as completed_count
            FROM Tests t WHERE t.teacher_id = ? AND t.is_published = TRUE 
            ORDER BY t.created_at DESC
        """;
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Test test = mapTest(rs);
                test.setStudentCount(rs.getInt("student_count"));
                test.setCompletedCount(rs.getInt("completed_count"));
                tests.add(test);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tests;
    }

    // ─── Load Relationships ────────────────────────────────
    public void loadTestQuestions(Test test) {
        String sql = "SELECT * FROM TestQuestions WHERE test_id = ? ORDER BY order_number";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, test.getId());
            ResultSet rs = ps.executeQuery();
            List<TestQuestion> tqs = new ArrayList<>();
            while (rs.next()) {
                TestQuestion tq = new TestQuestion();
                tq.setId(rs.getInt("id"));
                tq.setTestId(rs.getInt("test_id"));
                tq.setQuestionId(rs.getInt("question_id"));
                tq.setOrderNumber(rs.getInt("order_number"));
                tq.setMarks(rs.getInt("marks"));
                tq.setQuestion(questionDAO.findById(tq.getQuestionId()));
                tqs.add(tq);
            }
            test.setTestQuestions(tqs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAssignedStudents(Test test) {
        String sql = "SELECT student_id FROM TestStudents WHERE test_id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, test.getId());
            ResultSet rs = ps.executeQuery();
            List<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt("student_id"));
            }
            test.setAssignedStudentIds(ids);
            test.setStudentCount(ids.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ─── Update ────────────────────────────────────────────
    public boolean publish(int testId) {
        String sql = "UPDATE Tests SET is_published = TRUE WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Test test) {
        String sql = "UPDATE Tests SET title = ?, duration_minutes = ?, passing_marks = ?, start_time = ?, end_time = ?, show_one_at_time = ? WHERE id = ? AND is_published = FALSE";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setString(1, test.getTitle());
            ps.setInt(2, test.getDurationMinutes());
            ps.setInt(3, test.getPassingMarks());
            ps.setTimestamp(4, DateTimeUtil.toTimestamp(test.getStartTime()));
            ps.setTimestamp(5, DateTimeUtil.toTimestamp(test.getEndTime()));
            ps.setBoolean(6, test.isShowOneAtTime());
            ps.setInt(7, test.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ─── Delete ────────────────────────────────────────────
    public boolean delete(int testId) {
        String sql = "DELETE FROM Tests WHERE id = ? AND is_published = FALSE";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if a student has already submitted this test.
     */
    public boolean hasStudentSubmitted(int testId, int studentId) {
        String sql = "SELECT COUNT(*) FROM Submissions WHERE test_id = ? AND student_id = ? AND is_completed = TRUE";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, testId);
            ps.setInt(2, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Test mapTest(ResultSet rs) throws SQLException {
        Test test = new Test();
        test.setId(rs.getInt("id"));
        test.setTeacherId(rs.getInt("teacher_id"));
        test.setTitle(rs.getString("title"));
        test.setDurationMinutes(rs.getInt("duration_minutes"));
        test.setPassingMarks(rs.getInt("passing_marks"));
        Timestamp st = rs.getTimestamp("start_time");
        Timestamp et = rs.getTimestamp("end_time");
        test.setStartTime(st != null ? st.toLocalDateTime() : null);
        test.setEndTime(et != null ? et.toLocalDateTime() : null);
        test.setPublished(rs.getBoolean("is_published"));
        test.setShowOneAtTime(rs.getBoolean("show_one_at_time"));
        test.setCreatedAt(rs.getTimestamp("created_at"));
        return test;
    }
}
