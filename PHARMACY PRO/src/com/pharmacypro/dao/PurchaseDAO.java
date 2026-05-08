package com.pharmacypro.dao;

import com.pharmacypro.models.PurchaseBill;
import com.pharmacypro.models.PurchaseBillItem;
import com.pharmacypro.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class PurchaseDAO {

    public String generatePurchaseInvoiceNumber() throws SQLException {
        String sql = "SELECT MAX(id) as max_id FROM purchase_bills";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int nextId = rs.getInt("max_id") + 1;
                    return "PU-" + nextId + "F";
                }
            }
        }
        return "PU-1F";
    }

    public int savePurchaseBill(PurchaseBill bill, List<PurchaseBillItem> items) throws SQLException {
        String insertBill = "INSERT INTO purchase_bills (invoice_no, distributor_id, bill_date, billing_mode, extra_discount, cd_percent, credit_note_amount, mrp_value, total_amount, tax_amount, tcs_applied, pay_status, pending_amount, due_days, remarks, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int billId = -1;
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertBill, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, bill.getInvoiceNo());
                if (bill.getDistributorId() > 0) pstmt.setInt(2, bill.getDistributorId()); else pstmt.setNull(2, Types.INTEGER);
                pstmt.setDate(3, java.sql.Date.valueOf(bill.getBillDate()));
                pstmt.setString(4, bill.getBillingMode());
                pstmt.setBigDecimal(5, bill.getExtraDiscount());
                pstmt.setBigDecimal(6, bill.getCdPercent());
                pstmt.setBigDecimal(7, bill.getCreditNoteAmount());
                pstmt.setBigDecimal(8, bill.getMrpValue());
                pstmt.setBigDecimal(9, bill.getTotalAmount());
                pstmt.setBigDecimal(10, bill.getTaxAmount());
                pstmt.setBoolean(11, bill.isTcsApplied());
                pstmt.setString(12, bill.getPayStatus());
                pstmt.setBigDecimal(13, bill.getPendingAmount());
                pstmt.setInt(14, bill.getDueDays());
                pstmt.setString(15, bill.getRemarks());
                pstmt.setString(16, bill.getCreatedBy());

                pstmt.executeUpdate();
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        billId = generatedKeys.getInt(1);
                    }
                }

                String insertItem = "INSERT INTO purchase_bill_items (purchase_id, product_id, source_product_name, batch_no, expiry_date, quantity, free_qty, cost_price, mrp, discount_percent, discount_amount, cgst, sgst, net_gst, amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement itemStmt = conn.prepareStatement(insertItem)) {
                    for (PurchaseBillItem item : items) {
                        itemStmt.setInt(1, billId);
                        itemStmt.setInt(2, item.getProductId());
                        itemStmt.setString(3, item.getSourceProductName());
                        itemStmt.setString(4, item.getBatchNo());
                        itemStmt.setDate(5, item.getExpiryDate() != null ? java.sql.Date.valueOf(item.getExpiryDate()) : null);
                        itemStmt.setInt(6, item.getQuantity());
                        itemStmt.setInt(7, item.getFreeQty());
                        itemStmt.setBigDecimal(8, item.getCostPrice());
                        itemStmt.setBigDecimal(9, item.getMrp());
                        itemStmt.setBigDecimal(10, item.getDiscountPercent());
                        itemStmt.setBigDecimal(11, item.getDiscountAmount());
                        itemStmt.setBigDecimal(12, item.getCgst());
                        itemStmt.setBigDecimal(13, item.getSgst());
                        itemStmt.setBigDecimal(14, item.getNetGst());
                        itemStmt.setBigDecimal(15, item.getAmount());
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

    public void updatePayStatus(int purchaseId, String status) throws SQLException {
        // Stub
    }

    public List<PurchaseBill> getPurchaseOrders(Date from, Date to) throws SQLException {
        return new ArrayList<>();
    }

    public void importFromCSV(String filePath, int distributorId) throws SQLException {
        // Stub to handle CSV import logic outside, maybe just use dao inserted
    }
}
