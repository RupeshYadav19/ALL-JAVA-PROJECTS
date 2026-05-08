import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages conversion history.
 */
public class ConversionHistory {
    private List<String> history;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public ConversionHistory() {
        this.history = new ArrayList<>();
    }

    public void addEntry(double amount, String from, String to, double result) {
        String timestamp = dtf.format(LocalDateTime.now());
        String entry = String.format("[%s] %.2f %s = %.2f %s", timestamp, amount, from, result, to);
        history.add(0, entry); // Add to beginning of list
        
        // Keep only last 10 conversions
        if (history.size() > 10) {
            history.remove(history.size() - 1);
        }
    }

    public List<String> getHistory() {
        return history;
    }

    public void clear() {
        history.clear();
    }
}
