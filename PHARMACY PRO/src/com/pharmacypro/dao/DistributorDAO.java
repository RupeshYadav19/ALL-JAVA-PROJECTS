package com.pharmacypro.dao;

import com.pharmacypro.models.Distributor;
import com.pharmacypro.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistributorDAO {
    
    public List<Distributor> searchDistributors(String query) throws SQLException {
        List<Distributor> list = new ArrayList<>();
        String sql = "SELECT * FROM distributors WHERE name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Distributor d = new Distributor();
                    d.setId(rs.getInt("id"));
                    d.setName(rs.getString("name"));
                    d.setAddress(rs.getString("address"));
                    d.setIdentifier(rs.getString("identifier"));
                    d.setMobile(rs.getString("mobile"));
                    d.setEmail(rs.getString("email"));
                    d.setGstNo(rs.getString("gst_no"));
                    d.setDrugLicense(rs.getString("drug_license"));
                    d.setPendingAmount(rs.getBigDecimal("pending_amount"));
                    d.setCreditCycleDays(rs.getInt("credit_cycle_days"));
                    d.setLastPaymentAmount(rs.getBigDecimal("last_payment_amount"));
                    if (rs.getDate("last_payment_date") != null) {
                        d.setLastPaymentDate(rs.getDate("last_payment_date").toLocalDate());
                    }
                    d.setLastInvoiceAmount(rs.getBigDecimal("last_invoice_amount"));
                    d.setTotalCnAmount(rs.getBigDecimal("total_cn_amount"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    public void addDistributor(Distributor d) throws SQLException {
        String sql = "INSERT INTO distributors (name, address, identifier, mobile, email, gst_no, drug_license, pending_amount, credit_cycle_days, last_payment_amount, last_payment_date, last_invoice_amount, total_cn_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, d.getName());
            pstmt.setString(2, d.getAddress());
            pstmt.setString(3, d.getIdentifier());
            pstmt.setString(4, d.getMobile());
            pstmt.setString(5, d.getEmail());
            pstmt.setString(6, d.getGstNo());
            pstmt.setString(7, d.getDrugLicense());
            pstmt.setBigDecimal(8, d.getPendingAmount() != null ? d.getPendingAmount() : java.math.BigDecimal.ZERO);
            pstmt.setInt(9, d.getCreditCycleDays());
            pstmt.setBigDecimal(10, d.getLastPaymentAmount());
            if (d.getLastPaymentDate() != null) {
                pstmt.setDate(11, java.sql.Date.valueOf(d.getLastPaymentDate()));
            } else {
                pstmt.setNull(11, java.sql.Types.DATE);
            }
            pstmt.setBigDecimal(12, d.getLastInvoiceAmount());
            pstmt.setBigDecimal(13, d.getTotalCnAmount() != null ? d.getTotalCnAmount() : java.math.BigDecimal.ZERO);
            pstmt.executeUpdate();
        }
    }

    public void deleteDistributor(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM distributors WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
