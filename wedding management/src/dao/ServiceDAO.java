package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public int insert(models.Service s) throws SQLException {
        String sql = "INSERT INTO services(vendor_id,service_name,category,description,price,price_type,is_available,images_path) VALUES(?,?,?,?,?,?,1,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getVendorId());       ps.setString(2, s.getServiceName());
            ps.setString(3, s.getCategory());    ps.setString(4, s.getDescription());
            ps.setDouble(5, s.getPrice());       ps.setString(6, s.getPriceType());
            ps.setString(7, s.getImagesPath());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public List<models.Service> getByVendor(int vendorId) throws SQLException {
        List<models.Service> list = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE vendor_id=? ORDER BY service_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<models.Service> getAvailable() throws SQLException {
        List<models.Service> list = new ArrayList<>();
        String sql = "SELECT s.*, v.business_name AS vendor_name FROM services s JOIN vendors v ON s.vendor_id=v.vendor_id WHERE s.is_available=1";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                models.Service svc = mapRow(rs);
                svc.setVendorName(rs.getString("vendor_name"));
                list.add(svc);
            }
        }
        return list;
    }

    public void update(models.Service s) throws SQLException {
        String sql = "UPDATE services SET service_name=?,category=?,description=?,price=?,price_type=?,is_available=?,images_path=? WHERE service_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getServiceName()); ps.setString(2, s.getCategory());
            ps.setString(3, s.getDescription()); ps.setDouble(4, s.getPrice());
            ps.setString(5, s.getPriceType());   ps.setBoolean(6, s.isAvailable());
            ps.setString(7, s.getImagesPath());  ps.setInt(8, s.getServiceId());
            ps.executeUpdate();
        }
    }

    public void delete(int serviceId) throws SQLException {
        String sql = "DELETE FROM services WHERE service_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, serviceId); ps.executeUpdate();
        }
    }

    public int countByVendor(int vendorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM services WHERE vendor_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private models.Service mapRow(ResultSet rs) throws SQLException {
        models.Service s = new models.Service();
        s.setServiceId(rs.getInt("service_id"));
        s.setVendorId(rs.getInt("vendor_id"));
        s.setServiceName(rs.getString("service_name"));
        s.setCategory(rs.getString("category"));
        s.setDescription(rs.getString("description"));
        s.setPrice(rs.getDouble("price"));
        s.setPriceType(rs.getString("price_type"));
        s.setAvailable(rs.getBoolean("is_available"));
        s.setImagesPath(rs.getString("images_path"));
        return s;
    }
}
