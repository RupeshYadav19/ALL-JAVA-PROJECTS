package dao;

import db.DBConnection;
import models.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public int insert(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings(user_id,event_id,event_date,guest_count,ceremony_types,total_price,advance_paid,special_requests,status,payment_status) VALUES(?,?,?,?,?,?,?,?,'pending','unpaid')";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getUserId());       ps.setInt(2, b.getEventId());
            ps.setDate(3, b.getEventDate());   ps.setInt(4, b.getGuestCount());
            ps.setString(5, b.getCeremonyTypes()); ps.setDouble(6, b.getTotalPrice());
            ps.setDouble(7, b.getAdvancePaid()); ps.setString(8, b.getSpecialRequests());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public List<Booking> getByUser(int userId) throws SQLException {
        return query("SELECT b.*, u.full_name AS user_name, e.event_name, e.event_type FROM bookings b LEFT JOIN users u ON b.user_id=u.user_id LEFT JOIN events e ON b.event_id=e.event_id WHERE b.user_id=? ORDER BY b.booking_date DESC", userId);
    }

    public List<Booking> getAll() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name AS user_name, e.event_name, e.event_type FROM bookings b LEFT JOIN users u ON b.user_id=u.user_id LEFT JOIN events e ON b.event_id=e.event_id ORDER BY b.booking_date DESC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Booking> getByStatus(String status) throws SQLException {
        return query("SELECT b.*, u.full_name AS user_name, e.event_name, e.event_type FROM bookings b LEFT JOIN users u ON b.user_id=u.user_id LEFT JOIN events e ON b.event_id=e.event_id WHERE b.status=? ORDER BY b.booking_date DESC", status);
    }

    public Booking findById(int bookingId) throws SQLException {
        List<Booking> list = query("SELECT b.*, u.full_name AS user_name, e.event_name, e.event_type FROM bookings b LEFT JOIN users u ON b.user_id=u.user_id LEFT JOIN events e ON b.event_id=e.event_id WHERE b.booking_id=?", bookingId);
        return list.isEmpty() ? null : list.get(0);
    }

    public void updateStatus(int bookingId, String status, String reason) throws SQLException {
        String sql = "UPDATE bookings SET status=?, rejection_reason=? WHERE booking_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setString(2, reason); ps.setInt(3, bookingId);
            ps.executeUpdate();
        }
    }

    public void updatePayment(int bookingId, String payStatus) throws SQLException {
        String sql = "UPDATE bookings SET payment_status=? WHERE booking_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, payStatus); ps.setInt(2, bookingId); ps.executeUpdate();
        }
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int totalCount() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM bookings")) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double revenueThisMonth() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_price),0) FROM bookings WHERE status='approved' AND MONTH(booking_date)=MONTH(CURDATE()) AND YEAR(booking_date)=YEAR(CURDATE())";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    private List<Booking> query(String sql, Object param) throws SQLException {
        List<Booking> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param instanceof Integer i) ps.setInt(1, i);
            else if (param instanceof String s) ps.setString(1, s);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setEventId(rs.getInt("event_id"));
        b.setBookingDate(rs.getTimestamp("booking_date"));
        b.setEventDate(rs.getDate("event_date"));
        b.setGuestCount(rs.getInt("guest_count"));
        b.setCeremonyTypes(rs.getString("ceremony_types"));
        b.setTotalPrice(rs.getDouble("total_price"));
        b.setAdvancePaid(rs.getDouble("advance_paid"));
        b.setSpecialRequests(rs.getString("special_requests"));
        b.setStatus(rs.getString("status"));
        b.setPaymentStatus(rs.getString("payment_status"));
        b.setRejectionReason(rs.getString("rejection_reason"));
        try { b.setUserName(rs.getString("user_name")); }   catch (SQLException ignored) {}
        try { b.setEventName(rs.getString("event_name")); } catch (SQLException ignored) {}
        try { b.setEventType(rs.getString("event_type")); } catch (SQLException ignored) {}
        return b;
    }
}
