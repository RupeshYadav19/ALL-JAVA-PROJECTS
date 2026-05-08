package com.pharmacypro.dao;

import com.pharmacypro.models.SalesBill;
import com.pharmacypro.models.SalesBillItem;
import com.pharmacypro.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class SalesDAO {

    public String generateInvoiceNumber() throws SQLException {
        String sql = "SELECT MAX(id) as max_id FROM sales_bills";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int nextId = rs.getInt("max_id") + 1;
                    return "SL-" + nextId + "F";
                }
            }
        }
        return "SL-1F";
    }

    public int saveBill(SalesBill bill, List<SalesBillItem> items) throws SQLException {
        String insertBill = "INSERT INTO sales_bills (invoice_no, patient_id, doctor_id, bill_date, payment_mode, discount, extra_discount, round_off, bill_amount, total_amount, amount_paid, balance, remarks, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int billId = -1;
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertBill, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, bill.getInvoiceNo());
                if (bill.getPatientId() > 0) pstmt.setInt(2, bill.getPatientId()); else pstmt.setNull(2, Types.INTEGER);
                if (bill.getDoctorId() > 0) pstmt.setInt(3, bill.getDoctorId()); else pstmt.setNull(3, Types.INTEGER);
                pstmt.setDate(4, java.sql.Date.valueOf(bill.getBillDate()));
                pstmt.setString(5, bill.getPaymentMode());
                pstmt.setBigDecimal(6, bill.getDiscount());
                pstmt.setBigDecimal(7, bill.getExtraDiscount());
                pstmt.setBigDecimal(8, bill.getRoundOff());
                pstmt.setBigDecimal(9, bill.getBillAmount());
                pstmt.setBigDecimal(10, bill.getTotalAmount());
                pstmt.setBigDecimal(11, bill.getAmountPaid());
                pstmt.setBigDecimal(12, bill.getBalance());
                pstmt.setString(13, bill.getRemarks());
                pstmt.setString(14, bill.getCreatedBy());

                pstmt.executeUpdate();
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        billId = generatedKeys.getInt(1);
                    }
                }

                String insertItem = "INSERT INTO sales_bill_items (bill_id, product_id, batch_id, quantity, loose_qty, rate, mrp, discount_percent, discount_amount, amount, margin_percent) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement itemStmt = conn.prepareStatement(insertItem)) {
                    for (SalesBillItem item : items) {
                        itemStmt.setInt(1, billId);
                        itemStmt.setInt(2, item.getProductId());
                        itemStmt.setInt(3, item.getBatchId());
                        itemStmt.setInt(4, item.getQuantity());
                        itemStmt.setInt(5, item.getLooseQty());
                        itemStmt.setBigDecimal(6, item.getRate());
                        itemStmt.setBigDecimal(7, item.getMrp());
                        itemStmt.setBigDecimal(8, item.getDiscountPercent());
                        itemStmt.setBigDecimal(9, item.getDiscountAmount());
                        itemStmt.setBigDecimal(10, item.getAmount());
                        itemStmt.setBigDecimal(11, item.getMarginPercent());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
        return billId;
    }

    public List<SalesBill> getAllBills(Date from, Date to) throws SQLException {
        return new ArrayList<>(); // Stub
    }

    public List<SalesBillItem> getBillItems(int billId) throws SQLException {
        return new ArrayList<>(); // Stub
    }

    public void updateBillPaymentStatus(int billId, String status) throws SQLException {
        // Stub
    }

    public List<SalesBill> searchBills(String query) throws SQLException {
        return new ArrayList<>(); // Stub
    }

    public boolean deleteBill(int billId) throws SQLException {
        return false; // Stub
    }
}
