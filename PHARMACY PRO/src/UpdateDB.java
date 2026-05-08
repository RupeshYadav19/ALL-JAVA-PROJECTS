import com.pharmacypro.db.DBConnection;
import java.sql.*;
public class UpdateDB {
    public static void main(String[] args) {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement()) {
            s.execute("ALTER TABLE doctors ADD COLUMN email VARCHAR(100)");
            System.out.println("DB Updated");
        } catch(Exception e) { e.printStackTrace(); }
    }
}
