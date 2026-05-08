package com.pharmacypro.dao;

import com.pharmacypro.models.Payment;
import com.pharmacypro.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentDAO {
    
    public void addPaymentReceived(Payment p) throws SQLException {
        String sql = "INSERT INTO payments_received (patient_id, bill_id, amount, payment_date, payment_type, transaction_no) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getPartyId());
            pstmt.setInt(2, p.getBillId());
            pstmt.setBigDecimal(3, p.getAmount());
            pstmt.setDate(4, java.sql.Date.valueOf(p.getPaymentDate()));
            pstmt.setString(5, p.getPaymentType());
            pstmt.setString(6, p.getTransactionNo());
            pstmt.executeUpdate();
        }
    }
}
