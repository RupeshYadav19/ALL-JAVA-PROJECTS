package dao;

import db.DBConnection;
import models.Budget;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public int insert(Budget b) throws SQLException {
        String sql = "INSERT INTO budget(user_id,category,item_name,estimated_amount,actual_amount,paid_amount,vendor_id,notes) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getUserId());         ps.setString(2, b.getCategory());
            ps.setString(3, b.getItemName());    ps.setDouble(4, b.getEstimatedAmount());
            ps.setDouble(5, b.getActualAmount()); ps.setDouble(6, b.getPaidAmount());
            if (b.getVendorId() > 0) ps.setInt(7, b.getVendorId()); else ps.setNull(7, Types.INTEGER);
            ps.setString(8, b.getNotes());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public List<Budget> getByUser(int userId) throws SQLException {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT b.*, v.business_name AS vendor_name FROM budget b LEFT JOIN vendors v ON b.vendor_id=v.vendor_id WHERE b.user_id=? ORDER BY b.category, b.item_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Budget bud = mapRow(rs);
                bud.setVendorName(rs.getString("vendor_name"));
                list.add(bud);
            }
        }
        return list;
    }

    public void update(Budget b) throws SQLException {
        String sql = "UPDATE budget SET category=?,item_name=?,estimated_amount=?,actual_amount=?,paid_amount=?,vendor_id=?,notes=? WHERE budget_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getCategory());     ps.setString(2, b.getItemName());
            ps.setDouble(3, b.getEstimatedAmount()); ps.setDouble(4, b.getActualAmount());
            ps.setDouble(5, b.getPaidAmount());
            if (b.getVendorId() > 0) ps.setInt(6, b.getVendorId()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, b.getNotes());        ps.setInt(8, b.getBudgetId());
            ps.executeUpdate();
        }
    }

    public void delete(int budgetId) throws SQLException {
        String sql = "DELETE FROM budget WHERE budget_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, budgetId); ps.executeUpdate();
        }
    }

    /** Returns [totalEstimated, totalActual, totalPaid] */
    public double[] getSummary(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(estimated_amount),0), COALESCE(SUM(actual_amount),0), COALESCE(SUM(paid_amount),0) FROM budget WHERE user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new double[]{rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)};
        }
        return new double[]{0, 0, 0};
    }

    /** Returns category -> total estimated, for pie chart */
    public java.util.Map<String,Double> getCategoryTotals(int userId) throws SQLException {
        java.util.LinkedHashMap<String,Double> map = new java.util.LinkedHashMap<>();
        String sql = "SELECT category, SUM(estimated_amount) total FROM budget WHERE user_id=? GROUP BY category ORDER BY total DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getString("category"), rs.getDouble("total"));
        }
        return map;
    }

    private Budget mapRow(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setBudgetId(rs.getInt("budget_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setCategory(rs.getString("category"));
        b.setItemName(rs.getString("item_name"));
        b.setEstimatedAmount(rs.getDouble("estimated_amount"));
        b.setActualAmount(rs.getDouble("actual_amount"));
        b.setPaidAmount(rs.getDouble("paid_amount"));
        b.setVendorId(rs.getInt("vendor_id"));
        b.setNotes(rs.getString("notes"));
        return b;
    }
}
