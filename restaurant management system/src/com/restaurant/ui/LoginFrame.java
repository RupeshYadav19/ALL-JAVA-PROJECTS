package com.restaurant.ui;

import java.awt.*;
import javax.swing.*;
import com.restaurant.database.DatabaseManager;
import com.restaurant.model.User;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn, signupBtn;

    public LoginFrame() {
        setTitle("Restaurant Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 1;
        mainPanel.add(userLabel, gbc);

        userField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passLabel, gbc);

        passField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passField, gbc);

        loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(loginBtn, gbc);

        signupBtn = new JButton("Don't have an account? Create one");
        signupBtn.setBorderPainted(false);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setForeground(Color.CYAN);
        gbc.gridy = 4;
        mainPanel.add(signupBtn, gbc);

        add(mainPanel);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            User user = DatabaseManager.login(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                if (user.getRole().equals("ADMIN")) {
                    new AdminDashboard(user).setVisible(true);
                } else {
                    new UserDashboard(user).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or user does not exist.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        signupBtn.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            dispose();
        });
    }
}
