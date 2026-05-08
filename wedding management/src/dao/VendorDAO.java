package dao;

import db.DBConnection;
import models.Vendor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendorDAO {

    public int insertVendor(Vendor v) throws SQLException {
        String sql = "INSERT INTO vendors(user_id,business_name,category,city,locality,description,starting_price,is_verified,specialties) VALUES(?,?,?,?,?,?,?,0,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getUserId());      ps.setString(2, v.getBusinessName());
            ps.setString(3, v.getCategory()); ps.setString(4, v.getCity());
            ps.setString(5, v.getLocality()); ps.setString(6, v.getDescription());
            ps.setDouble(7, v.getStartingPrice()); ps.setString(8, v.getSpecialties());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public Vendor findByUserId(int userId) throws SQLException {
        String sql = "SELECT v.*, u.full_name AS owner_name, u.email, u.phone FROM vendors v JOIN users u ON v.user_id=u.user_id WHERE v.user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public Vendor findById(int vendorId) throws SQLException {
        String sql = "SELECT v.*, u.full_name AS owner_name, u.email, u.phone FROM vendors v JOIN users u ON v.user_id=u.user_id WHERE v.vendor_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public List<Vendor> getAll() throws SQLException {
        return search("", "", 0, Integer.MAX_VALUE, 0, "rating");
    }

    public List<Vendor> search(String category, String city, double minPrice, double maxPrice, double minRating, String sortBy) throws SQLException {
        List<Vendor> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT v.*, u.full_name AS owner_name, u.email, u.phone FROM vendors v JOIN users u ON v.user_id=u.user_id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (!category.isEmpty() && !category.equals("All")) { sql.append(" AND v.category=?"); params.add(category); }
        if (!city.isEmpty() && !city.equals("All"))          { sql.append(" AND v.city LIKE ?"); params.add("%" + city + "%"); }
        if (minPrice > 0)   { sql.append(" AND v.starting_price>=?"); params.add(minPrice); }
        if (maxPrice < Integer.MAX_VALUE) { sql.append(" AND v.starting_price<=?"); params.add(maxPrice); }
        if (minRating > 0)  { sql.append(" AND v.rating>=?"); params.add(minRating); }
        switch (sortBy) {
            case "price_asc"  -> sql.append(" ORDER BY v.starting_price ASC");
            case "price_desc" -> sql.append(" ORDER BY v.starting_price DESC");
            case "reviews"    -> sql.append(" ORDER BY v.review_count DESC");
            default           -> sql.append(" ORDER BY v.rating DESC, v.is_featured DESC");
        }
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) ps.setString(i + 1, s);
                else if (p instanceof Double d) ps.setDouble(i + 1, d);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void updateVendor(Vendor v) throws SQLException {
        String sql = "UPDATE vendors SET business_name=?,category=?,city=?,locality=?,description=?,starting_price=?,specialties=?,portfolio_path=? WHERE vendor_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, v.getBusinessName()); ps.setString(2, v.getCategory());
            ps.setString(3, v.getCity());          ps.setString(4, v.getLocality());
            ps.setString(5, v.getDescription());   ps.setDouble(6, v.getStartingPrice());
            ps.setString(7, v.getSpecialties());   ps.setString(8, v.getPortfolioPath());
            ps.setInt(9, v.getVendorId());
            ps.executeUpdate();
        }
    }

    public void setVerified(int vendorId, boolean val) throws SQLException {
        exec("UPDATE vendors SET is_verified=? WHERE vendor_id=?", val, vendorId);
    }
    public void setFeatured(int vendorId, boolean val) throws SQLException {
        exec("UPDATE vendors SET is_featured=? WHERE vendor_id=?", val, vendorId);
    }
    public void setAward(int vendorId, boolean val) throws SQLException {
        exec("UPDATE vendors SET award_winner=? WHERE vendor_id=?", val, vendorId);
    }

    public void updateRating(int vendorId) throws SQLException {
        String sql = "UPDATE vendors SET rating=COALESCE((SELECT AVG(rating) FROM reviews WHERE vendor_id=? AND is_approved=1),0), review_count=(SELECT COUNT(*) FROM reviews WHERE vendor_id=? AND is_approved=1) WHERE vendor_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vendorId); ps.setInt(2, vendorId); ps.setInt(3, vendorId);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM vendors")) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public void deleteVendor(int vendorId) throws SQLException {
        String sql = "DELETE FROM vendors WHERE vendor_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vendorId); ps.executeUpdate();
        }
    }

    private void exec(String sql, boolean b, int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, b); ps.setInt(2, id); ps.executeUpdate();
        }
    }

    private Vendor mapRow(ResultSet rs) throws SQLException {
        Vendor v = new Vendor();
        v.setVendorId(rs.getInt("vendor_id"));
        v.setUserId(rs.getInt("user_id"));
        v.setBusinessName(rs.getString("business_name"));
        v.setCategory(rs.getString("category"));
        v.setCity(rs.getString("city"));
        v.setLocality(rs.getString("locality"));
        v.setDescription(rs.getString("description"));
        v.setStartingPrice(rs.getDouble("starting_price"));
        v.setRating(rs.getDouble("rating"));
        v.setReviewCount(rs.getInt("review_count"));
        v.setPortfolioPath(rs.getString("portfolio_path"));
        v.setVerified(rs.getBoolean("is_verified"));
        v.setFeatured(rs.getBoolean("is_featured"));
        v.setAwardWinner(rs.getBoolean("award_winner"));
        v.setSpecialties(rs.getString("specialties"));
        try { v.setOwnerName(rs.getString("owner_name")); } catch (SQLException ignored) {}
        try { v.setEmail(rs.getString("email")); }         catch (SQLException ignored) {}
        try { v.setPhone(rs.getString("phone")); }         catch (SQLException ignored) {}
        return v;
    }
}
