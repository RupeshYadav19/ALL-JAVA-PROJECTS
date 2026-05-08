import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * GeminiChatbot - Enhanced version with Auto-Fallback and Diagnostics.
 */
public class GeminiChatbot extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton diagButton;
    private JLabel statusLabel;

    // API CONFIGURATION - DO NOT CHANGE THESE MANUALLY UNLESS NECESSARY
    // The app will try to find a working combination automatically.
    private static final String API_KEY = "AIzaSyC_IMceC3ygUWNtEmT9ijKwEAr7VrQDamA";

    private String currentVersion = "v1";
    private String currentModel = "gemini-2.5-flash";

    // Fallback lists - includes newest models
    private final String[] VERSIONS = { "v1", "v1beta" };
    private final String[] MODELS = {
            "gemini-2.5-flash",
            "gemini-2.0-flash",
            "gemini-1.5-flash",
            "gemini-1.5-pro",
            "gemini-1.5-flash-8b",
            "gemini-pro"
    };

    public GeminiChatbot() {
        setTitle("Gemini AI Chatbot (Robust)");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color bgColor = new Color(245, 245, 250);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(mainFont);
        chatArea.setBackground(bgColor);
        chatArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);

        inputField = new JTextField();
        inputField.setFont(mainFont);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        sendButton = new JButton("Send");
        styleButton(sendButton, new Color(66, 133, 244));

        diagButton = new JButton("Check Models");
        styleButton(diagButton, new Color(52, 168, 83));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(diagButton);
        buttonPanel.add(sendButton);

        statusLabel = new JLabel("Using: " + currentVersion + "/" + currentModel);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        inputPanel.add(statusLabel, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        diagButton.addActionListener(e -> checkAvailableModels());

        appendMessage("System", "Welcome! I will automatically try multiple Gemini models if the default fails.");
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty())
            return;

        appendMessage("You", text);
        inputField.setText("");
        setLoading(true);

        CompletableFuture.runAsync(() -> {
            try {
                String response = tryAllCombinations(text);
                SwingUtilities.invokeLater(() -> {
                    appendMessage("Bot", response);
                    setLoading(false);
                    statusLabel.setText("Working set: " + currentVersion + "/" + currentModel);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    appendMessage("Error", "All attempts failed. Please check your API key or click 'Check Models'.");
                    setLoading(false);
                });
            }
        });
    }

    private String tryAllCombinations(String prompt) throws Exception {
        // First try current working model
        try {
            return callGeminiAPI(currentVersion, currentModel, prompt);
        } catch (Exception e) {
            String lastError = e.getMessage();
            if (!lastError.contains("404"))
                throw e; // If not 404, might be network/key issue

            // Start Fallback Loop
            for (String v : VERSIONS) {
                for (String m : MODELS) {
                    if (v.equals(currentVersion) && m.equals(currentModel))
                        continue;

                    try {
                        SwingUtilities.invokeLater(() -> statusLabel.setText("Trying fallback: " + v + "/" + m));
                        String res = callGeminiAPI(v, m, prompt);
                        currentVersion = v;
                        currentModel = m;
                        return res;
                    } catch (Exception ex) {
                        // Continue to next combination
                    }
                }
            }
            throw new Exception("No working model found.");
        }
    }

    private void checkAvailableModels() {
        setLoading(true);
        statusLabel.setText("Listing models...");
        CompletableFuture.runAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models?key=" + API_KEY))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String result = response.body();

                SwingUtilities.invokeLater(() -> {
                    chatArea.append("\n--- AVAILABLE MODELS ---\n" + formatModelList(result)
                            + "\n------------------------\n\n");
                    setLoading(false);
                    statusLabel.setText("Diagnostics complete.");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    appendMessage("Error", "Failed to list models: " + ex.getMessage());
                    setLoading(false);
                });
            }
        });
    }

    private String formatModelList(String json) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while ((index = json.indexOf("\"name\": \"models/", index)) != -1) {
            int start = index + 16;
            int end = json.indexOf("\"", start);
            sb.append("- ").append(json.substring(start, end)).append("\n");
            index = end;
        }
        return sb.length() == 0 ? "No models found in response." : sb.toString();
    }

    private void appendMessage(String sender, String message) {
        chatArea.append("[" + sender + "]: " + message + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void setLoading(boolean loading) {
        sendButton.setEnabled(!loading);
        diagButton.setEnabled(!loading);
        inputField.setEnabled(!loading);
        if (loading)
            statusLabel.setText("Gemini is processing...");
    }

    private String callGeminiAPI(String version, String model, String prompt) throws Exception {
        String url = "https://generativelanguage.googleapis.com/" + version + "/models/" + model
                + ":generateContent?key=" + API_KEY;
        String jsonPayload = "{\"contents\": [{\"parts\": [{\"text\": \"" + escapeJson(prompt) + "\"}]}]}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseGeminiResponse(response.body());
        } else {
            throw new Exception("HTTP " + response.statusCode() + ": " + response.body());
        }
    }

    private String parseGeminiResponse(String json) {
        try {
            int textStart = json.indexOf("\"text\": \"") + 9;
            int textEnd = json.indexOf("\"", textStart);
            if (textStart > 8 && textEnd > textStart) {
                String result = json.substring(textStart, textEnd);
                return result.replace("\\n", "\n").replace("\\\"", "\"");
            }
        } catch (Exception e) {
        }
        return "Sorry, I couldn't parse the response correctly.";
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new GeminiChatbot().setVisible(true);
        });
    }
}
