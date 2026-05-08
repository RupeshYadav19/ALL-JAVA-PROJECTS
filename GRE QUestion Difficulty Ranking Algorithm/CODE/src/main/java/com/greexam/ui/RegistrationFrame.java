package com.greexam.ui;

import com.greexam.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class RegistrationFrame extends JFrame {

    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtEmail;
    private JComboBox<String> cmbSecretQuestion;
    private JComboBox<String> cmbRole;
    private JTextField txtSecretAnswer;
    private JButton btnRegister;
    private JButton btnCancel;

    private AuthService authService = AuthService.getInstance();

    private final String[] SECRET_QUESTIONS = {
            "What is your pet name?",
            "What is your mother's maiden name?",
            "What city were you born in?",
            "What is the name of your first school?",
            "What is your favorite book?"
    };

    public RegistrationFrame() {
        setTitle("Student Registration");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // or DISPOSE_ON_CLOSE if opened as dialog
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel lblTitle = new JLabel("Account Registration", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Full Name
        mainPanel.add(new JLabel("Full Name *:"), gbc);
        gbc.gridx = 1;
        txtFullName = new JTextField(20);
        mainPanel.add(txtFullName, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Username *:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        mainPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Password *:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        mainPanel.add(txtPassword, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Confirm Password *:"), gbc);
        gbc.gridx = 1;
        txtConfirmPassword = new JPasswordField(20);
        mainPanel.add(txtConfirmPassword, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Email *:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        mainPanel.add(txtEmail, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Role *:"), gbc);
        gbc.gridx = 1;
        cmbRole = new JComboBox<>(new String[]{"student", "teacher"});
        mainPanel.add(cmbRole, gbc);

        // Secret Question
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Secret Question *:"), gbc);
        gbc.gridx = 1;
        cmbSecretQuestion = new JComboBox<>(SECRET_QUESTIONS);
        mainPanel.add(cmbSecretQuestion, gbc);

        // Secret Answer
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Secret Answer *:"), gbc);
        gbc.gridx = 1;
        txtSecretAnswer = new JTextField(20);
        mainPanel.add(txtSecretAnswer, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        btnRegister = new JButton("Register");
        btnRegister.setBackground(new Color(46, 204, 113));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.addActionListener(e -> registerUser());
        
        btnCancel = new JButton("Back to Login");
        btnCancel.addActionListener(e -> backToLogin());

        btnPanel.add(btnRegister);
        btnPanel.add(btnCancel);
        mainPanel.add(btnPanel, gbc);
    }

    private void registerUser() {
        String name = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String pwd = new String(txtPassword.getPassword());
        String pwdConfirm = new String(txtConfirmPassword.getPassword());
        String email = txtEmail.getText().trim();
        String role = (String) cmbRole.getSelectedItem();
        String secretQ = (String) cmbSecretQuestion.getSelectedItem();
        String secretA = txtSecretAnswer.getText().trim();

        // Validations
        if (name.isEmpty() || username.isEmpty() || pwd.isEmpty() || pwdConfirm.isEmpty() || email.isEmpty() || secretA.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields marked with * are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pwd.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!pwd.equals(pwdConfirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use selected role from dropdown
        boolean success = authService.register(name, username, pwd, role, email, secretQ, secretA);

        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            backToLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    private void backToLogin() {
        new LoginFrame().setVisible(true);
        this.dispose();
    }
}
