
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connect {
    static Connection con = null;

    public static Connection ConnectToDB() {
        try {
            // Loading the MySQL JDBC Driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "type your password here");
        } catch (ClassNotFoundException ex) {
            System.err.println("MySQL Driver not found! Please add mysql-connector-j-x.x.x.jar to your classpath.");
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, "Driver not found", ex);
        } catch (SQLException ex) {
            System.err.println(
                    "Failed to connect to the database. Check credentials and ensure library database exists.");
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, "SQL Connection error", ex);
        }
        return con;
    }
}
