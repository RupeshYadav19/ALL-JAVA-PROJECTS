package com.pharmacypro.dao;

import com.pharmacypro.models.Patient;
import com.pharmacypro.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public List<Patient> searchPatients(String query) throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ? OR mobile LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient p = new Patient();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setMobile(rs.getString("mobile"));
                    p.setEmail(rs.getString("email"));
                    p.setAddress(rs.getString("address"));
                    p.setIdentifier(rs.getString("identifier"));
                    if (rs.getDate("date_of_birth") != null) {
                        p.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                    }
                    p.setGender(rs.getString("gender"));
                    p.setOutstanding(rs.getBigDecimal("outstanding"));
                    if (rs.getTimestamp("created_at") != null) {
                        p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    list.add(p);
                }
            }
        }
        return list;
    }

    public void addPatient(Patient p) throws SQLException {
        String sql = "INSERT INTO patients (name, mobile, email, address, identifier, date_of_birth, gender, outstanding) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getName());
            pstmt.setString(2, p.getMobile());
            pstmt.setString(3, p.getEmail());
            pstmt.setString(4, p.getAddress());
            pstmt.setString(5, p.getIdentifier());
            pstmt.setDate(6, p.getDateOfBirth() != null ? Date.valueOf(p.getDateOfBirth()) : null);
            pstmt.setString(7, p.getGender());
            pstmt.setBigDecimal(8, p.getOutstanding());
            pstmt.executeUpdate();
        }
    }

    public void deletePatient(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM patients WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
