package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void insert(int userId, String title, String message, String type) throws SQLException {
        String sql = "INSERT INTO notifications(user_id,title,message,type,is_read) VALUES(?,?,?,?,0)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setString(2, title);
            ps.setString(3, message); ps.setString(4, type);
            ps.executeUpdate();
        }
    }

    public List<java.util.Map<String,Object>> getByUser(int userId) throws SQLException {
        List<java.util.Map<String,Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id=? ORDER BY created_at DESC LIMIT 50";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                java.util.Map<String,Object> m = new java.util.LinkedHashMap<>();
                m.put("notifId", rs.getInt("notif_id"));
                m.put("title",   rs.getString("title"));
                m.put("message", rs.getString("message"));
                m.put("type",    rs.getString("type"));
                m.put("isRead",  rs.getBoolean("is_read"));
                m.put("createdAt", rs.getTimestamp("created_at"));
                list.add(m);
            }
        }
        return list;
    }

    public int unreadCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id=? AND is_read=0";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public void markAllRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read=1 WHERE user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.executeUpdate();
        }
    }

    public void markRead(int notifId) throws SQLException {
        String sql = "UPDATE notifications SET is_read=1 WHERE notif_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, notifId); ps.executeUpdate();
        }
    }
}
