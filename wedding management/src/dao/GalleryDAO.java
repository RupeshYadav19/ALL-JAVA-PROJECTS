package dao;

import db.DBConnection;
import models.Gallery;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GalleryDAO {

    public int insert(Gallery g) throws SQLException {
        String sql = "INSERT INTO gallery(title,type,image_path,tags,uploaded_by,is_featured) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getTitle());    ps.setString(2, g.getType());
            ps.setString(3, g.getImagePath()); ps.setString(4, g.getTags());
            ps.setInt(5, g.getUploadedBy());  ps.setBoolean(6, g.isFeatured());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public List<Gallery> getAll() throws SQLException {
        return getByType(null, null);
    }

    public List<Gallery> getByType(String type, String tag) throws SQLException {
        List<Gallery> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM gallery WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (type != null && !type.equals("All")) { sql.append(" AND type=?"); params.add(type); }
        if (tag != null && !tag.isEmpty()) { sql.append(" AND tags LIKE ?"); params.add("%" + tag + "%"); }
        sql.append(" ORDER BY is_featured DESC, gallery_id DESC");
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setString(i + 1, (String) params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Gallery> getFeatured() throws SQLException {
        List<Gallery> list = new ArrayList<>();
        String sql = "SELECT * FROM gallery WHERE is_featured=1 ORDER BY gallery_id DESC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void setFeatured(int galleryId, boolean featured) throws SQLException {
        String sql = "UPDATE gallery SET is_featured=? WHERE gallery_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, featured); ps.setInt(2, galleryId); ps.executeUpdate();
        }
    }

    public void delete(int galleryId) throws SQLException {
        String sql = "DELETE FROM gallery WHERE gallery_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, galleryId); ps.executeUpdate();
        }
    }

    private Gallery mapRow(ResultSet rs) throws SQLException {
        Gallery g = new Gallery();
        g.setGalleryId(rs.getInt("gallery_id"));
        g.setTitle(rs.getString("title"));
        g.setType(rs.getString("type"));
        g.setImagePath(rs.getString("image_path"));
        g.setTags(rs.getString("tags"));
        g.setUploadedBy(rs.getInt("uploaded_by"));
        g.setFeatured(rs.getBoolean("is_featured"));
        return g;
    }
}
