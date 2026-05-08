import java.sql.Connection;
import java.sql.Statement;
import com.pharmacypro.db.DBConnection;

public class PurgeDB {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            System.out.println("Purging dummy data from transactions...");
            stmt.execute("DELETE FROM sales_bill_items");
            stmt.execute("DELETE FROM sales_bills");
            stmt.execute("DELETE FROM purchase_bill_items");
            stmt.execute("DELETE FROM purchase_bills");
            System.out.println("Purge Complete!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
