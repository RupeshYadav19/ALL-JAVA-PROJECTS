package com.greexam.ui;

import com.greexam.model.User;
import com.greexam.service.AuthService;
import com.greexam.ui.teacher.TeacherDashboard;
import com.greexam.ui.student.StudentDashboard;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLoginTeacher;
    private JButton btnLoginStudent;
    private JButton btnRegister;
    private JButton btnForgotPassword;

    private AuthService authService = AuthService.getInstance();

    public LoginFrame() {
        setTitle("GRE Online Exam Management System - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        
        // Auto-open registration if no users exist
        SwingUtilities.invokeLater(() -> {
            if (!com.greexam.db.DBConnection.getInstance().hasUsers()) {
                JOptionPane.showMessageDialog(this, "Welcome! No users found. Please register an admin teacher first.");
                openRegistration();
            }
        });
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Title
        JLabel lblTitle = new JLabel("GRE Exam Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy++;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        mainPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        mainPanel.add(txtPassword, gbc);

        // Teacher Login Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnLoginTeacher = new JButton("Login as Teacher");
        btnLoginTeacher.setBackground(new Color(41, 128, 185)); // Blueish
        btnLoginTeacher.setForeground(Color.WHITE);
        btnLoginTeacher.addActionListener(e -> attemptLogin("teacher"));
        mainPanel.add(btnLoginTeacher, gbc);

        // Student Login Button
        gbc.gridy++;
        btnLoginStudent = new JButton("Login as Student");
        btnLoginStudent.setBackground(new Color(39, 174, 96)); // Greenish
        btnLoginStudent.setForeground(Color.WHITE);
        btnLoginStudent.addActionListener(e -> attemptLogin("student"));
        mainPanel.add(btnLoginStudent, gbc);

        // Options Panel
        gbc.gridy++;
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRegister = new JButton("Create New Account");
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setForeground(Color.BLUE);
        btnRegister.addActionListener(e -> openRegistration());

        btnForgotPassword = new JButton("Forgot Password?");
        btnForgotPassword.setContentAreaFilled(false);
        btnForgotPassword.setBorderPainted(false);
        btnForgotPassword.setForeground(Color.RED);
        btnForgotPassword.addActionListener(e -> handleForgotPassword());

        optionsPanel.add(btnRegister);
        optionsPanel.add(btnForgotPassword);
        mainPanel.add(optionsPanel, gbc);
    }

    private void attemptLogin(String expectedRole) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authService.login(username, password);

        if (user != null) {
            if (!user.getRole().equalsIgnoreCase(expectedRole)) {
                JOptionPane.showMessageDialog(this, "Invalid role. Please select the correct login button.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                authService.logout(); // reset
                return;
            }

            try {
                // Success - Attempt to open appropriate dashboard
                if (user.isTeacher()) {
                     new TeacherDashboard().setVisible(true);
                } else {
                     new StudentDashboard().setVisible(true);
                }
                this.dispose(); // Close login frame
            } catch (Throwable t) {
                t.printStackTrace();
                String errorMsg = "Failed to launch dashboard:\n" + t.toString();
                if (t.getCause() != null) errorMsg += "\nCause: " + t.getCause().toString();
                JOptionPane.showMessageDialog(this, errorMsg, "System Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistration() {
        // Will implement RegistrationFrame
        new RegistrationFrame().setVisible(true);
        this.dispose();
    }

    private void handleForgotPassword() {
        String username = JOptionPane.showInputDialog(this, "Enter your username:");
        if (username != null && !username.trim().isEmpty()) {
            String question = authService.getSecretQuestion(username.trim());
            if (question != null) {
                String answer = JOptionPane.showInputDialog(this, "Secret Question: " + question + "\nEnter your answer:");
                if (answer != null && !answer.trim().isEmpty()) {
                    JPasswordField pf = new JPasswordField();
                    int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter New Password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (okCxl == JOptionPane.OK_OPTION) {
                        String newPassword = new String(pf.getPassword());
                        if (newPassword.length() >= 8) {
                            boolean success = authService.resetPassword(username.trim(), answer, newPassword);
                            if (success) {
                                JOptionPane.showMessageDialog(this, "Password reset successfully. Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this, "Incorrect secret answer. Password reset failed.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username not found or no secret question set.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
