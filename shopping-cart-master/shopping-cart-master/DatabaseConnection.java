import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/shopping_cart";
    private static final String USER = "root";
    private static final String PASSWORD = "Rupesh@#202007"; // User should change this as needed

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
