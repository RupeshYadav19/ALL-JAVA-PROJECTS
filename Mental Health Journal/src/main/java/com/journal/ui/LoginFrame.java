package com.journal.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Mental Health Journal - Login");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel with Gradient Background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), 0, getHeight(),
                        new Color(109, 213, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subLabel = new JLabel("Please login to your journal", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(new Color(236, 240, 241));
        gbc.gridy = 1;
        mainPanel.add(subLabel, gbc);

        // Username Label & Field
        JLabel uLabel = new JLabel("Username");
        uLabel.setForeground(Color.WHITE);
        uLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(uLabel, gbc);

        userField = new JTextField(20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridy = 3;
        mainPanel.add(userField, gbc);

        // Password Label & Field
        JLabel pLabel = new JLabel("Password");
        pLabel.setForeground(Color.WHITE);
        pLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 4;
        mainPanel.add(pLabel, gbc);

        passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridy = 5;
        mainPanel.add(passField, gbc);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(30, 10, 10, 10);
        mainPanel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if (username.equals("admin") && password.equals("12345")) {
                    dispose();
                    new JournalFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid credentials!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(mainPanel);
    }
}
