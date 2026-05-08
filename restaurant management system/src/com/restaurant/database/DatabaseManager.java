package com.restaurant.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.restaurant.model.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_db";
    private static final String USER = "root";
    private static final String PASS = "type your password here";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement()) {

            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT," +
                    "name TEXT," +
                    "address TEXT," +
                    "role TEXT)");

            // Menu table
            stmt.execute("CREATE TABLE IF NOT EXISTS menu (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "category TEXT," +
                    "price REAL)");

            // Tables table
            stmt.execute("CREATE TABLE IF NOT EXISTS tables (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "label TEXT," +
                    "capacity INTEGER," +
                    "is_reserved INTEGER DEFAULT 0)");

            // Orders table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "items TEXT," +
                    "total_price REAL," +
                    "status TEXT," +
                    "FOREIGN KEY(user_id) REFERENCES users(id))");

            // Seed Tables if empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tables");
            if (rs.next() && rs.getInt(1) == 0) {
                // 4 groups of 4 tables (16 tables)
                for (int i = 1; i <= 16; i++) {
                    stmt.execute("INSERT INTO tables (label, capacity) VALUES ('G1-T" + i + "', 4)");
                }
                // 2 slots for 2 tables (4 tables)
                for (int i = 1; i <= 4; i++) {
                    stmt.execute("INSERT INTO tables (label, capacity) VALUES ('G2-T" + i + "', 2)");
                }
            }

            // Seed Admin if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role='ADMIN'");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute(
                        "INSERT INTO users (username, password, name, role) VALUES ('admin', 'admin123', 'Administrator', 'ADMIN')");
            }

            // Seed Menu if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM menu");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO menu (name, category, price) VALUES ('Paneer Tikka', 'Starter', 250.0)");
                stmt.execute(
                        "INSERT INTO menu (name, category, price) VALUES ('Chicken Biryani', 'Main Course', 350.0)");
                stmt.execute("INSERT INTO menu (name, category, price) VALUES ('Butter Naan', 'Bread', 40.0)");
                stmt.execute("INSERT INTO menu (name, category, price) VALUES ('Gulab Jamun', 'Dessert', 80.0)");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // User Methods
    public static User login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("name"),
                        rs.getString("address"), rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean register(String username, String password, String name) {
        String query = "INSERT INTO users (username, password, name, role) VALUES (?, ?, ?, 'USER')";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void updateAddress(int userId, String address) {
        String query = "UPDATE users SET address = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, address);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menu Methods
    public static List<MenuItem> getMenu() {
        List<MenuItem> menu = new ArrayList<>();
        String query = "SELECT * FROM menu";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                menu.add(new MenuItem(rs.getInt("id"), rs.getString("name"),
                        rs.getString("category"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menu;
    }

    public static void updateMenuItem(int id, String name, double price) {
        String query = "UPDATE menu SET name = ?, price = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMenuItem(int id) {
        String query = "DELETE FROM menu WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addMenuItem(String name, String category, double price) {
        String query = "INSERT INTO menu (name, category, price) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Table Methods
    public static List<TableSlot> getTables() {
        List<TableSlot> tables = new ArrayList<>();
        String query = "SELECT * FROM tables";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tables.add(new TableSlot(rs.getInt("id"), rs.getString("label"),
                        rs.getInt("capacity"), rs.getInt("is_reserved") == 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public static void reserveTable(int id, boolean status) {
        String query = "UPDATE tables SET is_reserved = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, status ? 1 : 0);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Order Methods
    public static void placeOrder(int userId, String items, double totalPrice) {
        String query = "INSERT INTO orders (user_id, items, total_price, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, items);
            pstmt.setDouble(3, totalPrice);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY id DESC";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                orders.add(new Order(rs.getInt("id"), rs.getInt("user_id"),
                        rs.getString("items"), rs.getDouble("total_price"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static void updateOrderStatus(int orderId, String status) {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
