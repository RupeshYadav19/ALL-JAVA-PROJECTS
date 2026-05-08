package com.greexam.service;

import com.greexam.db.DBConnection;
import com.greexam.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing in-app notifications (Observer pattern).
 */
public class NotificationService {

    private static NotificationService instance;

    private NotificationService() {}

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    /**
     * Send a notification to a specific user.
     */
    public void notify(int userId, String message) {
        String sql = "INSERT INTO Notifications (user_id, message) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify a teacher.
     */
    public void notifyTeacher(int teacherId, String message) {
        notify(teacherId, message);
    }

    /**
     * Notify multiple students.
     */
    public void notifyStudents(List<Integer> studentIds, String message) {
        String sql = "INSERT INTO Notifications (user_id, message) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            for (int id : studentIds) {
                ps.setInt(1, id);
                ps.setString(2, message);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all notifications for a user.
     */
    public List<Notification> getNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setMessage(rs.getString("message"));
                n.setRead(rs.getBoolean("is_read"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                notifications.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Get unread count for a user.
     */
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE user_id = ? AND is_read = FALSE";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Mark a specific notification as read.
     */
    public void markAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET is_read = TRUE WHERE id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mark all notifications as read for a user.
     */
    public void markAllAsRead(int userId) {
        String sql = "UPDATE Notifications SET is_read = TRUE WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
