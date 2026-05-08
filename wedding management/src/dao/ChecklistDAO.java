package dao;

import db.DBConnection;
import models.ChecklistItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChecklistDAO {

    // Pre-populated wedding checklist tasks
    private static final String[][] DEFAULT_TASKS = {
        {"Venue","Book wedding venue","high"},
        {"Venue","Confirm seating arrangement","medium"},
        {"Photography","Book photographer","high"},
        {"Photography","Book videographer","medium"},
        {"Photography","Pre-wedding shoot","low"},
        {"Catering","Finalize menu","high"},
        {"Catering","Confirm catering company","high"},
        {"Catering","Arrange cake","medium"},
        {"Invitations","Design invitation cards","high"},
        {"Invitations","Send digital invites","medium"},
        {"Invitations","Send physical invites","medium"},
        {"Bridal Wear","Book bridal lehenga","high"},
        {"Bridal Wear","Book bridal jewellery","high"},
        {"Bridal Wear","Book bridal makeup artist","high"},
        {"Groom Wear","Book sherwani / suit","high"},
        {"Mehndi","Book mehndi artist","high"},
        {"Music","Book DJ","medium"},
        {"Music","Prepare song playlist","low"},
        {"Transport","Book car for bride","high"},
        {"Transport","Arrange guest transport","medium"},
        {"Decor","Book decorator","high"},
        {"Decor","Finalize floral theme","medium"},
        {"Honeymoon","Book honeymoon flights","medium"},
        {"Honeymoon","Book honeymoon hotel","medium"},
        {"Legal","Apply for marriage certificate","high"},
    };

    public void createDefaultChecklist(int userId) throws SQLException {
        String sql = "INSERT INTO checklist(user_id,category,task_name,priority,is_done) VALUES(?,?,?,?,0)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (String[] task : DEFAULT_TASKS) {
                ps.setInt(1, userId); ps.setString(2, task[0]);
                ps.setString(3, task[1]); ps.setString(4, task[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public int insert(ChecklistItem item) throws SQLException {
        String sql = "INSERT INTO checklist(user_id,category,task_name,due_date,priority,notes,is_done) VALUES(?,?,?,?,?,?,0)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getUserId());       ps.setString(2, item.getCategory());
            ps.setString(3, item.getTaskName());  ps.setDate(4, item.getDueDate());
            ps.setString(5, item.getPriority());  ps.setString(6, item.getNotes());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public List<ChecklistItem> getByUser(int userId) throws SQLException {
        List<ChecklistItem> list = new ArrayList<>();
        String sql = "SELECT * FROM checklist WHERE user_id=? ORDER BY FIELD(priority,'high','medium','low'), category, task_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<ChecklistItem> getPending(int userId) throws SQLException {
        List<ChecklistItem> list = new ArrayList<>();
        String sql = "SELECT * FROM checklist WHERE user_id=? AND is_done=0 ORDER BY FIELD(priority,'high','medium','low')";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void setDone(int itemId, boolean done) throws SQLException {
        String sql = "UPDATE checklist SET is_done=? WHERE item_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, done); ps.setInt(2, itemId); ps.executeUpdate();
        }
    }

    public void update(ChecklistItem item) throws SQLException {
        String sql = "UPDATE checklist SET task_name=?,category=?,due_date=?,priority=?,notes=?,is_done=? WHERE item_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getTaskName()); ps.setString(2, item.getCategory());
            ps.setDate(3, item.getDueDate());    ps.setString(4, item.getPriority());
            ps.setString(5, item.getNotes());    ps.setBoolean(6, item.isDone());
            ps.setInt(7, item.getItemId());
            ps.executeUpdate();
        }
    }

    public void delete(int itemId) throws SQLException {
        String sql = "DELETE FROM checklist WHERE item_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, itemId); ps.executeUpdate();
        }
    }

    public int[] getProgress(int userId) throws SQLException {
        // returns [done, total]
        String sql = "SELECT COUNT(*) total, SUM(is_done) done FROM checklist WHERE user_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new int[]{rs.getInt("done"), rs.getInt("total")};
        }
        return new int[]{0, 0};
    }

    private ChecklistItem mapRow(ResultSet rs) throws SQLException {
        ChecklistItem item = new ChecklistItem();
        item.setItemId(rs.getInt("item_id"));
        item.setUserId(rs.getInt("user_id"));
        item.setCategory(rs.getString("category"));
        item.setTaskName(rs.getString("task_name"));
        item.setDueDate(rs.getDate("due_date"));
        item.setDone(rs.getBoolean("is_done"));
        item.setPriority(rs.getString("priority"));
        item.setNotes(rs.getString("notes"));
        return item;
    }
}
