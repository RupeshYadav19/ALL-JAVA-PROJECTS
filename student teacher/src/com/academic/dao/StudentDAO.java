package com.academic.dao;

import com.academic.db.DBConnection;
import com.academic.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public int saveStudent(Student s) {
        String sql = "INSERT INTO students (user_id, full_name, parent_name, email, phone, date_of_birth, address, " +
                "sgpa, credits, attendance_percent, conduct_violation, conduct_type, stream, year, semester, cgpa_first_year, sgpa_third_sem, sem_status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getUserId());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getParentName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getDateOfBirth());
            ps.setString(7, s.getAddress());
            ps.setDouble(8, s.getSgpa());
            ps.setInt(9, s.getCredits());
            ps.setDouble(10, s.getAttendancePercent());
            ps.setBoolean(11, s.isConductViolation());
            ps.setString(12, s.getConductType());
            ps.setString(13, s.getStream());
            ps.setInt(14, s.getYear());
            ps.setInt(15, s.getSemester());
            ps.setDouble(16, s.getCgpaFirstYear());
            ps.setDouble(17, s.getSgpaThirdSem());
            ps.setString(18, s.getSemStatus());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Student findByName(String name) {
        String sql = "SELECT * FROM students WHERE full_name LIKE ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setUserId(rs.getInt("user_id"));
                s.setFullName(rs.getString("full_name"));
                s.setParentName(rs.getString("parent_name"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setDateOfBirth(rs.getString("date_of_birth"));
                s.setAddress(rs.getString("address"));
                s.setSgpa(rs.getDouble("sgpa"));
                s.setCredits(rs.getInt("credits"));
                s.setAttendancePercent(rs.getDouble("attendance_percent"));
                s.setConductViolation(rs.getBoolean("conduct_violation"));
                s.setConductType(rs.getString("conduct_type"));
                s.setStream(rs.getString("stream"));
                s.setYear(rs.getInt("year"));
                s.setSemester(rs.getInt("semester"));
                s.setCgpaFirstYear(rs.getDouble("cgpa_first_year"));
                s.setSgpaThirdSem(rs.getDouble("sgpa_third_sem"));
                s.setSemStatus(rs.getString("sem_status"));
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveResults(int studentId, List<String[]> results) {
        String sql = "INSERT INTO eligibility_results (student_id, test_case_id, result_status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] row : results) {
                ps.setInt(1, studentId);
                ps.setString(2, row[0]); // testCaseId
                ps.setString(3, row[5]); // status (Pass/Fail)
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getResultsByStudentId(int studentId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT test_case_id, result_status, checked_at FROM eligibility_results WHERE student_id = ? ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[] { rs.getString("test_case_id"), rs.getString("result_status"),
                        rs.getString("checked_at") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
