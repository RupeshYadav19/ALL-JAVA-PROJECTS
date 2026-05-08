package dao;

import db.DBConnection;
import models.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public int insert(Review r) throws SQLException {
        String sql = "INSERT INTO reviews(user_id,vendor_id,booking_id,rating,review_text,is_approved) VALUES(?,?,?,?,?,0)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getUserId()); ps.setInt(2, r.getVendorId());
            ps.setInt(3, r.getBookingId()); ps.setInt(4, r.getRating());
            ps.setString(5, r.getReviewText());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public List<Review> getByVendor(int vendorId, boolean approvedOnly) throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS reviewer_name FROM reviews r JOIN users u ON r.user_id=u.user_id WHERE r.vendor_id=?" + (approvedOnly ? " AND r.is_approved=1" : "") + " ORDER BY r.created_at DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Review rev = mapRow(rs);
                rev.setReviewerName(rs.getString("reviewer_name"));
                list.add(rev);
            }
        }
        return list;
    }

    public List<Review> getAll() throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS reviewer_name FROM reviews r JOIN users u ON r.user_id=u.user_id ORDER BY r.created_at DESC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Review rev = mapRow(rs);
                rev.setReviewerName(rs.getString("reviewer_name"));
                list.add(rev);
            }
        }
        return list;
    }

    public void approve(int reviewId) throws SQLException {
        exec("UPDATE reviews SET is_approved=1 WHERE review_id=?", reviewId);
    }

    public void delete(int reviewId) throws SQLException {
        exec("DELETE FROM reviews WHERE review_id=?", reviewId);
    }

    /** Returns count per rating (index 0=unused, 1-5 = count) */
    public int[] getRatingDistribution(int vendorId) throws SQLException {
        int[] dist = new int[6];
        String sql = "SELECT rating, COUNT(*) AS cnt FROM reviews WHERE vendor_id=? AND is_approved=1 GROUP BY rating";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) dist[rs.getInt("rating")] = rs.getInt("cnt");
        }
        return dist;
    }

    private void exec(String sql, int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewId(rs.getInt("review_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setVendorId(rs.getInt("vendor_id"));
        r.setBookingId(rs.getInt("booking_id"));
        r.setRating(rs.getInt("rating"));
        r.setReviewText(rs.getString("review_text"));
        r.setPhotosPath(rs.getString("photos_path"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setApproved(rs.getBoolean("is_approved"));
        return r;
    }
}
