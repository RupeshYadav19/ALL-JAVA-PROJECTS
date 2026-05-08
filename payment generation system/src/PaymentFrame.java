import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentFrame extends JFrame {
    private JComboBox<String> methodCombo;
    private JTextField recipientField;
    private JTextField nameField;
    private JTextField amountField;
    private JTextArea statusArea;
    private double currentBalance = 0.00;
    private double maxSend = 0.00;
    private int isFrozen = 0;
    private String loggedInUser;
    private int loggedInUserId;

    public PaymentFrame(String username) {
        this.loggedInUser = username;
        setTitle("Payment System - Secure Transfer");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        fetchUserData();

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 243, 244));
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Make a Payment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Balance & Status
        String statusText = (isFrozen == 1) ? " [ACCOUNT FROZEN]" : "";
        JLabel balLabel = new JLabel("Available Balance: ₹" + String.format("%.2f", currentBalance) + statusText);
        balLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        balLabel.setForeground((isFrozen == 1) ? Color.RED : new Color(39, 174, 96));
        gbc.gridy = 1;
        mainPanel.add(balLabel, gbc);

        // Payment Method
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Payment Method:"), gbc);

        String[] methods = { "Pay with mobile number", "Pay with UPI ID", "Pay with other methods" };
        methodCombo = new JComboBox<>(methods);
        gbc.gridx = 1;
        mainPanel.add(methodCombo, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Recipient Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Recipient Detail
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Mobile/UPI ID:"), gbc);
        recipientField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(recipientField, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Amount (₹):"), gbc);
        amountField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(amountField, gbc);

        // Pay Button
        JButton payBtn = new JButton("PROCEED TO PAY");
        payBtn.setBackground(new Color(41, 128, 185));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payBtn.setFocusPainted(false);
        payBtn.addActionListener(e -> processPayment());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        mainPanel.add(payBtn, gbc);

        // Status Area
        statusArea = new JTextArea(6, 20);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        statusArea.setBackground(new Color(236, 240, 241));
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(new JScrollPane(statusArea), gbc);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            new DashboardFrame(loggedInUser).setVisible(true);
            this.dispose();
        });
        gbc.gridy = 8;
        mainPanel.add(backBtn, gbc);

        add(mainPanel);
    }

    private void fetchUserData() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, balance, max_send, is_frozen FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, loggedInUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                loggedInUserId = rs.getInt("id");
                currentBalance = rs.getDouble("balance");
                maxSend = rs.getDouble("max_send");
                isFrozen = rs.getInt("is_frozen");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logPayment(String type, String recipient, double amount, String status, String error) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO payments (user_id, payment_type, recipient, amount, status, error_message) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, loggedInUserId);
            ps.setString(2, type);
            ps.setString(3, recipient);
            ps.setDouble(4, amount);
            ps.setString(5, status);
            ps.setString(6, error);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void freezeAccount(int userId, String reason) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET is_frozen = 1 WHERE id = ?");
            ps.setInt(1, userId);
            ps.executeUpdate();
            if (userId == loggedInUserId)
                isFrozen = 1;
            JOptionPane.showMessageDialog(this, "ACCOUNT FROZEN: " + reason, "Security Alert",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processPayment() {
        if (isFrozen == 1) {
            showError("Account Frozen", "Your account is frozen and cannot perform transactions. Contact Admin.");
            return;
        }

        String method = (String) methodCombo.getSelectedItem();
        String recipient = recipientField.getText().trim();
        String name = nameField.getText().trim();
        String amountStr = amountField.getText().trim();

        statusArea.setText("Status: Processing...");
        statusArea.setForeground(Color.BLACK);

        if (amountStr.isEmpty() || recipient.isEmpty() || name.isEmpty()) {
            showError("Incomplete fields", "Please fill all details.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Invalid Amount", "Please enter a positive numeric value.");
            return;
        }

        // Limit Check - Sender
        if (amount > maxSend) {
            freezeAccount(loggedInUserId, "Send limit (₹" + maxSend + ") exceeded.");
            logPayment(method, name, amount, "Frozen", "Limit exceeded");
            return;
        }

        if (amount > currentBalance) {
            showError("Insufficient balance", "Your balance is ₹" + String.format("%.2f", currentBalance));
            return;
        }

        boolean isValidRecipient = false;
        int recipientId = -1;
        double recipientMaxReceive = 0;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "";
            if (method.equals("Pay with UPI ID"))
                sql = "SELECT id, full_name, max_receive FROM users WHERE upi_id = ?";
            else if (method.equals("Pay with mobile number"))
                sql = "SELECT id, full_name, max_receive FROM users WHERE mobile = ?";
            else
                sql = "SELECT id, full_name, max_receive FROM users WHERE full_name = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, recipient);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbName = rs.getString("full_name");
                if (dbName.equalsIgnoreCase(name)) {
                    isValidRecipient = true;
                    recipientId = rs.getInt("id");
                    recipientMaxReceive = rs.getDouble("max_receive");
                } else {
                    showError("Recipient name mismatch", "The name does not match our records.");
                    return;
                }
            } else {
                showError("Recipient not found", "Could not find user.");
                return;
            }

            // Limit Check - Receiver
            if (amount > recipientMaxReceive) {
                freezeAccount(recipientId, "Receive limit (₹" + recipientMaxReceive + ") exceeded.");
                showError("Payment Blocked", "The recipient's account has been frozen due to receiving limit excess.");
                logPayment(method, name, amount, "Blocked", "Recipient limit exceeded");
                return;
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
            return;
        }

        if (isValidRecipient) {
            updateBalance(amount);
            // Also update recipient balance
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance = balance + ? WHERE id = ?");
                ps.setDouble(1, amount);
                ps.setInt(2, recipientId);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            logPayment(method, name, amount, "Success", null);
            statusArea.setForeground(new Color(39, 174, 96));
            statusArea.setText("Payment Successful!\nPaid ₹" + amount + " to " + name);
            JOptionPane.showMessageDialog(this, "Payment Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            fetchUserData();
        }
    }

    private void showError(String error, String suggestion) {
        statusArea.setForeground(Color.RED);
        statusArea.setText("ERROR: " + error + "\nFIX: " + suggestion);
        JOptionPane.showMessageDialog(this, error + "\n" + suggestion, "Payment Failed", JOptionPane.WARNING_MESSAGE);
    }

    private void updateBalance(double amount) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE users SET balance = balance - ? WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, amount);
            ps.setString(2, loggedInUser);
            ps.executeUpdate();
            currentBalance -= amount;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
