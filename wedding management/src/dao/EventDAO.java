package dao;

import db.DBConnection;
import models.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    public List<Event> getAll() throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY date DESC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Event> getActive() throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status='active' ORDER BY date ASC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Event> search(String type, String city, double minPrice, double maxPrice) throws SQLException {
        List<Event> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM events WHERE status='active'");
        List<Object> params = new ArrayList<>();
        if (!type.isEmpty() && !type.equals("All"))  { sql.append(" AND event_type=?"); params.add(type); }
        if (!city.isEmpty() && !city.equals("All"))  { sql.append(" AND city LIKE ?"); params.add("%" + city + "%"); }
        if (minPrice > 0) { sql.append(" AND total_price>=?"); params.add(minPrice); }
        if (maxPrice > 0) { sql.append(" AND total_price<=?"); params.add(maxPrice); }
        sql.append(" ORDER BY date ASC");
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

    public Event findById(int eventId) throws SQLException {
        String sql = "SELECT * FROM events WHERE event_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public int insert(Event e, int createdBy) throws SQLException {
        String sql = "INSERT INTO events(event_name,event_type,description,venue,city,date,time,capacity,price_per_head,total_price,status,image_path,created_by) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getEventName());   ps.setString(2, e.getEventType());
            ps.setString(3, e.getDescription()); ps.setString(4, e.getVenue());
            ps.setString(5, e.getCity());        ps.setDate(6, e.getDate());
            ps.setString(7, e.getTime());        ps.setInt(8, e.getCapacity());
            ps.setDouble(9, e.getPricePerHead());ps.setDouble(10, e.getTotalPrice());
            ps.setString(11, e.getStatus());     ps.setString(12, e.getImagePath());
            ps.setInt(13, createdBy);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public void update(Event e) throws SQLException {
        String sql = "UPDATE events SET event_name=?,event_type=?,description=?,venue=?,city=?,date=?,time=?,capacity=?,price_per_head=?,total_price=?,status=?,image_path=? WHERE event_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getEventName());   ps.setString(2, e.getEventType());
            ps.setString(3, e.getDescription()); ps.setString(4, e.getVenue());
            ps.setString(5, e.getCity());        ps.setDate(6, e.getDate());
            ps.setString(7, e.getTime());        ps.setInt(8, e.getCapacity());
            ps.setDouble(9, e.getPricePerHead());ps.setDouble(10, e.getTotalPrice());
            ps.setString(11, e.getStatus());     ps.setString(12, e.getImagePath());
            ps.setInt(13, e.getEventId());
            ps.executeUpdate();
        }
    }

    public void toggleStatus(int eventId, String status) throws SQLException {
        String sql = "UPDATE events SET status=? WHERE event_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, eventId); ps.executeUpdate();
        }
    }

    public void delete(int eventId) throws SQLException {
        String sql = "DELETE FROM events WHERE event_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, eventId); ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM events")) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setEventId(rs.getInt("event_id"));
        e.setEventName(rs.getString("event_name"));
        e.setEventType(rs.getString("event_type"));
        e.setDescription(rs.getString("description"));
        e.setVenue(rs.getString("venue"));
        e.setCity(rs.getString("city"));
        e.setDate(rs.getDate("date"));
        e.setTime(rs.getString("time"));
        e.setCapacity(rs.getInt("capacity"));
        e.setPricePerHead(rs.getDouble("price_per_head"));
        e.setTotalPrice(rs.getDouble("total_price"));
        e.setStatus(rs.getString("status"));
        e.setImagePath(rs.getString("image_path"));
        e.setCreatedBy(rs.getInt("created_by"));
        return e;
    }
}
