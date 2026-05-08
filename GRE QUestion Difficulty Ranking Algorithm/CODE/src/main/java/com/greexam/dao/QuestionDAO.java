package com.greexam.dao;

import com.greexam.db.DBConnection;
import com.greexam.model.Question;
import com.greexam.model.Question.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Question operations including options, blanks, and match pairs.
 */
public class QuestionDAO {

    // ─── Create ────────────────────────────────────────────
    public boolean insert(Question q) {
        String sql = "INSERT INTO Questions (teacher_id, question_text, question_type, marks, topic, expected_time_seconds) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, q.getTeacherId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getQuestionType().name());
            ps.setInt(4, q.getMarks());
            ps.setString(5, q.getTopic());
            ps.setInt(6, q.getExpectedTimeSeconds());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    q.setId(keys.getInt(1));
                }
                // Insert related data based on type
                insertRelatedData(q);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insertRelatedData(Question q) throws SQLException {
        switch (q.getQuestionType()) {
            case MCQ -> insertOptions(q);
            case FILL_BLANK -> insertFillBlanks(q);
            case MATCH -> insertMatchPairs(q);
            default -> {} // ONE_WORD, SHORT_ANSWER, LONG_ANSWER — answer stored in question_text or expected_answer
        }
    }

    private void insertOptions(Question q) throws SQLException {
        String sql = "INSERT INTO QuestionOptions (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            for (QuestionOption opt : q.getOptions()) {
                ps.setInt(1, q.getId());
                ps.setString(2, opt.getOptionText());
                ps.setBoolean(3, opt.isCorrect());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertFillBlanks(Question q) throws SQLException {
        String sql = "INSERT INTO FillBlankAnswers (question_id, blank_position, correct_answer, case_sensitive) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            for (FillBlankAnswer fb : q.getFillBlanks()) {
                ps.setInt(1, q.getId());
                ps.setInt(2, fb.getBlankPosition());
                ps.setString(3, fb.getCorrectAnswer());
                ps.setBoolean(4, fb.isCaseSensitive());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertMatchPairs(Question q) throws SQLException {
        String sql = "INSERT INTO MatchPairs (question_id, left_item, right_item) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            for (MatchPair mp : q.getMatchPairs()) {
                ps.setInt(1, q.getId());
                ps.setString(2, mp.getLeftItem());
                ps.setString(3, mp.getRightItem());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ─── Read ──────────────────────────────────────────────
    public Question findById(int id) {
        String sql = "SELECT * FROM Questions WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Question q = mapQuestion(rs);
                loadRelatedData(q);
                return q;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Question> findByTeacher(int teacherId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM Questions WHERE teacher_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question q = mapQuestion(rs);
                loadRelatedData(q);
                questions.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public List<Question> findByTeacherAndType(int teacherId, String type) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM Questions WHERE teacher_id = ? AND question_type = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question q = mapQuestion(rs);
                loadRelatedData(q);
                questions.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public List<Question> findByTeacherAndTopic(int teacherId, String topic) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM Questions WHERE teacher_id = ? AND topic LIKE ? ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setString(2, "%" + topic + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question q = mapQuestion(rs);
                loadRelatedData(q);
                questions.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public List<String> findDistinctTopics(int teacherId) {
        List<String> topics = new ArrayList<>();
        String sql = "SELECT DISTINCT topic FROM Questions WHERE teacher_id = ? AND topic IS NOT NULL ORDER BY topic";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                topics.add(rs.getString("topic"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }

    // ─── Load Related Data ─────────────────────────────────
    public void loadRelatedData(Question q) {
        switch (q.getQuestionType()) {
            case MCQ -> loadOptions(q);
            case FILL_BLANK -> loadFillBlanks(q);
            case MATCH -> loadMatchPairs(q);
            default -> {}
        }
    }

    private void loadOptions(Question q) {
        String sql = "SELECT * FROM QuestionOptions WHERE question_id = ? ORDER BY id";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, q.getId());
            ResultSet rs = ps.executeQuery();
            List<QuestionOption> opts = new ArrayList<>();
            while (rs.next()) {
                QuestionOption o = new QuestionOption();
                o.setId(rs.getInt("id"));
                o.setQuestionId(rs.getInt("question_id"));
                o.setOptionText(rs.getString("option_text"));
                o.setCorrect(rs.getBoolean("is_correct"));
                opts.add(o);
            }
            q.setOptions(opts);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFillBlanks(Question q) {
        String sql = "SELECT * FROM FillBlankAnswers WHERE question_id = ? ORDER BY blank_position";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, q.getId());
            ResultSet rs = ps.executeQuery();
            List<FillBlankAnswer> blanks = new ArrayList<>();
            while (rs.next()) {
                FillBlankAnswer fb = new FillBlankAnswer();
                fb.setId(rs.getInt("id"));
                fb.setQuestionId(rs.getInt("question_id"));
                fb.setBlankPosition(rs.getInt("blank_position"));
                fb.setCorrectAnswer(rs.getString("correct_answer"));
                fb.setCaseSensitive(rs.getBoolean("case_sensitive"));
                blanks.add(fb);
            }
            q.setFillBlanks(blanks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMatchPairs(Question q) {
        String sql = "SELECT * FROM MatchPairs WHERE question_id = ? ORDER BY id";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, q.getId());
            ResultSet rs = ps.executeQuery();
            List<MatchPair> pairs = new ArrayList<>();
            while (rs.next()) {
                MatchPair mp = new MatchPair();
                mp.setId(rs.getInt("id"));
                mp.setQuestionId(rs.getInt("question_id"));
                mp.setLeftItem(rs.getString("left_item"));
                mp.setRightItem(rs.getString("right_item"));
                pairs.add(mp);
            }
            q.setMatchPairs(pairs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ─── Update ────────────────────────────────────────────
    public boolean update(Question q) {
        String sql = "UPDATE Questions SET question_text = ?, question_type = ?, marks = ?, topic = ?, expected_time_seconds = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getQuestionType().name());
            ps.setInt(3, q.getMarks());
            ps.setString(4, q.getTopic());
            ps.setInt(5, q.getExpectedTimeSeconds());
            ps.setInt(6, q.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                // Delete old related data and re-insert
                deleteRelatedData(q.getId());
                insertRelatedData(q);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deleteRelatedData(int questionId) throws SQLException {
        String[] tables = {"QuestionOptions", "FillBlankAnswers", "MatchPairs"};
        for (String table : tables) {
            try (PreparedStatement ps = DBConnection.conn().prepareStatement(
                    "DELETE FROM " + table + " WHERE question_id = ?")) {
                ps.setInt(1, questionId);
                ps.executeUpdate();
            }
        }
    }

    // ─── Delete ────────────────────────────────────────────
    public boolean delete(int questionId) {
        String sql = "DELETE FROM Questions WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the correct answer text for grading purposes.
     */
    public String getCorrectAnswer(Question q) {
        return switch (q.getQuestionType()) {
            case MCQ -> q.getOptions().stream()
                    .filter(QuestionOption::isCorrect)
                    .map(QuestionOption::getOptionText)
                    .findFirst().orElse("");
            case ONE_WORD -> {
                // For ONE_WORD, expected answer is stored after a delimiter in question_text
                // Format: "question|||answer"
                String text = q.getQuestionText();
                int idx = text.indexOf("|||");
                yield idx >= 0 ? text.substring(idx + 3).trim() : "";
            }
            case FILL_BLANK -> q.getFillBlanks().stream()
                    .map(FillBlankAnswer::getCorrectAnswer)
                    .reduce((a, b) -> a + ", " + b).orElse("");
            case MATCH -> q.getMatchPairs().stream()
                    .map(mp -> mp.getLeftItem() + " → " + mp.getRightItem())
                    .reduce((a, b) -> a + "; " + b).orElse("");
            default -> "Requires manual grading";
        };
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setId(rs.getInt("id"));
        q.setTeacherId(rs.getInt("teacher_id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setQuestionType(Question.QuestionType.valueOf(rs.getString("question_type")));
        q.setMarks(rs.getInt("marks"));
        q.setTopic(rs.getString("topic"));
        q.setExpectedTimeSeconds(rs.getInt("expected_time_seconds"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        return q;
    }
}
