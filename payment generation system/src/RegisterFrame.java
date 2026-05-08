import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private JTextField userField, nameField, mobileField, upiField, balanceField, sendLimitField, receiveLimitField;
    private JPasswordField passField;
    private JButton registerButton, backButton;

    public RegisterFrame() {
        setTitle("Modern Banking - Register");
        setSize(450, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(28, 40, 51));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        addLabelAndField(mainPanel, "Full Name:", nameField = new JTextField(15), 1, gbc);
        addLabelAndField(mainPanel, "Username:", userField = new JTextField(15), 2, gbc);
        addLabelAndField(mainPanel, "Password:", passField = new JPasswordField(15), 3, gbc);
        addLabelAndField(mainPanel, "Mobile:", mobileField = new JTextField(15), 4, gbc);
        addLabelAndField(mainPanel, "UPI ID:", upiField = new JTextField(15), 5, gbc);
        addLabelAndField(mainPanel, "Initial Deposit (₹):", balanceField = new JTextField("1000", 15), 6, gbc);
        addLabelAndField(mainPanel, "Max Send Limit (₹):", sendLimitField = new JTextField("5000", 15), 7, gbc);
        addLabelAndField(mainPanel, "Max Receive Limit (₹):", receiveLimitField = new JTextField("10000", 15), 8, gbc);

        registerButton = new JButton("REGISTER");
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.addActionListener(e -> handleRegistration());
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 5, 10);
        mainPanel.add(registerButton, gbc);

        backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(28, 40, 51));
        backButton.setForeground(new Color(171, 178, 185));
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        gbc.gridy = 10;
        mainPanel.add(backButton, gbc);

        add(mainPanel);
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField field, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(171, 178, 185));
        panel.add(label, gbc);

        field.setBackground(new Color(44, 62, 80));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void handleRegistration() {
        String name = nameField.getText().trim();
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String mobile = mobileField.getText().trim();
        String upi = upiField.getText().trim();
        String balanceStr = balanceField.getText().trim();
        String sendLimitStr = sendLimitField.getText().trim();
        String receiveLimitStr = receiveLimitField.getText().trim();

        if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || mobile.isEmpty() || upi.isEmpty()
                || balanceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double balance = Double.parseDouble(balanceStr);
            double sendLimit = Double.parseDouble(sendLimitStr);
            double receiveLimit = Double.parseDouble(receiveLimitStr);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO users (username, password, full_name, mobile, upi_id, balance, max_send, max_receive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user);
                ps.setString(2, pass);
                ps.setString(3, name);
                ps.setString(4, mobile);
                ps.setString(5, upi);
                ps.setDouble(6, balance);
                ps.setDouble(7, sendLimit);
                ps.setDouble(8, receiveLimit);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Account Created!\nSend Limit: ₹" + sendLimit + "\nReceive Limit: ₹" + receiveLimit, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid numeric values!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
