package com.pharmacypro.dao;

import com.pharmacypro.models.Doctor;
import com.pharmacypro.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    
    public List<Doctor> searchDoctors(String query) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doctor d = new Doctor();
                    d.setId(rs.getInt("id"));
                    d.setName(rs.getString("name"));
                    d.setMobile(rs.getString("mobile"));
                    d.setEmail(rs.getString("email"));
                    d.setSpecialization(rs.getString("specialization"));
                    d.setAddress(rs.getString("address"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    public void addDoctor(Doctor d) throws SQLException {
        String sql = "INSERT INTO doctors (name, mobile, email, specialization, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getName());
            pstmt.setString(2, d.getMobile());
            pstmt.setString(3, d.getEmail());
            pstmt.setString(4, d.getSpecialization());
            pstmt.setString(5, d.getAddress());
            pstmt.executeUpdate();
        }
    }

    public void deleteDoctor(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM doctors WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
