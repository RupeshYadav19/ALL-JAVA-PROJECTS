package dao;

import db.DBConnection;
import models.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public int insert(Guest g) throws SQLException {
        String sql = "INSERT INTO guests(booking_id,user_id,guest_name,phone,email,relation,side,rsvp_status,meal_preference,table_no) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, g.getBookingId());    ps.setInt(2, g.getUserId());
            ps.setString(3, g.getGuestName()); ps.setString(4, g.getPhone());
            ps.setString(5, g.getEmail());     ps.setString(6, g.getRelation());
            ps.setString(7, g.getSide());      ps.setString(8, g.getRsvpStatus());
            ps.setString(9, g.getMealPreference()); ps.setInt(10, g.getTableNo());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public List<Guest> getByUser(int userId) throws SQLException {
        return getByUser(userId, null);
    }

    public List<Guest> getByUser(int userId, String side) throws SQLException {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE user_id=?" + (side != null ? " AND side=?" : "") + " ORDER BY guest_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            if (side != null) ps.setString(2, side);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void update(Guest g) throws SQLException {
        String sql = "UPDATE guests SET guest_name=?,phone=?,email=?,relation=?,side=?,rsvp_status=?,meal_preference=?,table_no=? WHERE guest_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, g.getGuestName()); ps.setString(2, g.getPhone());
            ps.setString(3, g.getEmail());     ps.setString(4, g.getRelation());
            ps.setString(5, g.getSide());      ps.setString(6, g.getRsvpStatus());
            ps.setString(7, g.getMealPreference()); ps.setInt(8, g.getTableNo());
            ps.setInt(9, g.getGuestId());
            ps.executeUpdate();
        }
    }

    public void updateRsvp(int guestId, String status) throws SQLException {
        String sql = "UPDATE guests SET rsvp_status=? WHERE guest_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, guestId); ps.executeUpdate();
        }
    }

    public void delete(int guestId) throws SQLException {
        String sql = "DELETE FROM guests WHERE guest_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, guestId); ps.executeUpdate();
        }
    }

    public int[] getRsvpCounts(int userId) throws SQLException {
        // [0]=total, [1]=attending, [2]=not_attending, [3]=pending
        int[] counts = new int[4];
        String sql = "SELECT rsvp_status, COUNT(*) cnt FROM guests WHERE user_id=? GROUP BY rsvp_status";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int cnt = rs.getInt("cnt");
                counts[0] += cnt;
                switch (rs.getString("rsvp_status")) {
                    case "attending"     -> counts[1] += cnt;
                    case "not_attending" -> counts[2] += cnt;
                    case "pending"       -> counts[3] += cnt;
                }
            }
        }
        return counts;
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setGuestId(rs.getInt("guest_id"));
        g.setBookingId(rs.getInt("booking_id"));
        g.setUserId(rs.getInt("user_id"));
        g.setGuestName(rs.getString("guest_name"));
        g.setPhone(rs.getString("phone"));
        g.setEmail(rs.getString("email"));
        g.setRelation(rs.getString("relation"));
        g.setSide(rs.getString("side"));
        g.setRsvpStatus(rs.getString("rsvp_status"));
        g.setMealPreference(rs.getString("meal_preference"));
        g.setTableNo(rs.getInt("table_no"));
        return g;
    }
}
