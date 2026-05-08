package com.restaurant.ui;

import javax.swing.*;
import java.awt.*;
import com.restaurant.database.DatabaseManager;

public class SignupFrame extends JFrame {
    private JTextField userField, nameField;
    private JPasswordField passField;
    private JButton signupBtn, backBtn;

    public SignupFrame() {
        setTitle("Restaurant Management System - Sign Up");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 1;
        mainPanel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(userLabel, gbc);

        userField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passLabel, gbc);

        passField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passField, gbc);

        signupBtn = new JButton("Sign Up");
        signupBtn.setBackground(new Color(46, 139, 87));
        signupBtn.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(signupBtn, gbc);

        backBtn = new JButton("Back to Login");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(Color.CYAN);
        gbc.gridy = 5;
        mainPanel.add(backBtn, gbc);

        add(mainPanel);

        signupBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String name = nameField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (DatabaseManager.register(username, password, name)) {
                JOptionPane.showMessageDialog(this, "Account Created Successfully!");
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }
}
