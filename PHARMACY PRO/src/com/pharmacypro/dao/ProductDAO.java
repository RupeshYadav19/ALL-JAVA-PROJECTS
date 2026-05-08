package com.pharmacypro.dao;

import com.pharmacypro.models.Product;
import com.pharmacypro.models.ProductBatch;
import com.pharmacypro.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> searchProducts(String query) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product p = extractProduct(rs);
                    list.add(p);
                }
            }
        }
        return list;
    }

    public List<ProductBatch> getBatchesForProduct(int productId) throws SQLException {
        List<ProductBatch> list = new ArrayList<>();
        String sql = "SELECT * FROM product_batches WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductBatch b = extractBatch(rs);
                    list.add(b);
                }
            }
        }
        return list;
    }

    public ProductBatch getAutoBatch(int productId) throws SQLException {
        String sql = "SELECT * FROM product_batches WHERE product_id = ? AND is_auto = 1 LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractBatch(rs);
                }
            }
        }
        return null;
    }

    public List<Product> getSubstitutes(int productId) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE composition = (SELECT composition FROM products WHERE id = ?) AND id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractProduct(rs));
                }
            }
        }
        return list;
    }

    public void updateBatchStock(int batchId, int quantitySold) throws SQLException {
        String sql = "UPDATE product_batches SET quantity = quantity - ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantitySold);
            pstmt.setInt(2, batchId);
            pstmt.executeUpdate();
        }
    }

    public List<Product> getExpiringProducts(int withinDays) throws SQLException {
        return new ArrayList<>();
    }

    public List<Product> getLowStockProducts() throws SQLException {
        return new ArrayList<>();
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setManufacturer(rs.getString("manufacturer"));
        p.setComposition(rs.getString("composition"));
        p.setHsnCode(rs.getString("hsn_code"));
        p.setDefaultMrp(rs.getBigDecimal("default_mrp"));
        p.setGstPercent(rs.getBigDecimal("gst_percent"));
        p.setCgst(rs.getBigDecimal("cgst"));
        p.setSgst(rs.getBigDecimal("sgst"));
        p.setPackSize(rs.getInt("pack_size"));
        p.setScheduleH(rs.getBoolean("is_schedule_h"));
        if(rs.getDate("expiry_date") != null) {
            p.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        }
        return p;
    }

    private ProductBatch extractBatch(ResultSet rs) throws SQLException {
        ProductBatch b = new ProductBatch();
        b.setId(rs.getInt("id"));
        b.setProductId(rs.getInt("product_id"));
        b.setBatchNo(rs.getString("batch_no"));
        if (rs.getDate("expiry_date") != null) {
            b.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        }
        b.setQuantity(rs.getInt("quantity"));
        b.setLooseQty(rs.getInt("loose_qty"));
        b.setCostPrice(rs.getBigDecimal("cost_price"));
        b.setMrp(rs.getBigDecimal("mrp"));
        b.setMarginPercent(rs.getBigDecimal("margin_percent"));
        b.setAuto(rs.getBoolean("is_auto"));
        b.setDistributorId(rs.getInt("distributor_id"));
        return b;
    }

    public void addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, manufacturer, composition, hsn_code, default_mrp, gst_percent, cgst, sgst, pack_size, is_schedule_h, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getName());
            pstmt.setString(2, p.getManufacturer());
            pstmt.setString(3, p.getComposition());
            pstmt.setString(4, p.getHsnCode());
            pstmt.setBigDecimal(5, p.getDefaultMrp() != null ? p.getDefaultMrp() : java.math.BigDecimal.ZERO);
            pstmt.setBigDecimal(6, p.getGstPercent() != null ? p.getGstPercent() : java.math.BigDecimal.ZERO);
            pstmt.setBigDecimal(7, p.getCgst() != null ? p.getCgst() : java.math.BigDecimal.ZERO);
            pstmt.setBigDecimal(8, p.getSgst() != null ? p.getSgst() : java.math.BigDecimal.ZERO);
            pstmt.setInt(9, p.getPackSize());
            pstmt.setBoolean(10, p.isScheduleH());
            if (p.getExpiryDate() != null) {
                pstmt.setDate(11, java.sql.Date.valueOf(p.getExpiryDate()));
            } else {
                pstmt.setNull(11, java.sql.Types.DATE);
            }
            pstmt.executeUpdate();
        }
    }

    public void deleteProduct(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
