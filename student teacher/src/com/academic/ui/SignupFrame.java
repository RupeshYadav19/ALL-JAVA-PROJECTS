package com.academic.ui;

import com.academic.dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignupFrame extends JFrame {

    private JTextField fullNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private UserDAO userDAO = new UserDAO();

    private static final Color PRIMARY_COLOR = new Color(67, 56, 202);
    private static final Color PRIMARY_DARK = new Color(49, 46, 129);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);

    public SignupFrame() {
        setTitle("Create New Account");
        setSize(480, 440);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel titleLabel = new JLabel("Student Enrollment Form");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 60, 10, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 8, 9, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fullNameField = addRow(formPanel, gbc, 0, "Full Name:", new JTextField(18));
        usernameField = (JTextField) addRowGeneric(formPanel, gbc, 1, "Username:", new JTextField(18));
        passwordField = (JPasswordField) addRowGeneric(formPanel, gbc, 2, "Password:", new JPasswordField(18));
        confirmPasswordField = (JPasswordField) addRowGeneric(formPanel, gbc, 3, "Confirm Password:",
                new JPasswordField(18));

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setBackground(BG_COLOR);

        JButton registerBtn = createButton("Register", PRIMARY_COLOR);
        JButton backBtn = createButton("Back to Login", new Color(55, 65, 81));

        registerBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm = new String(confirmPasswordField.getPassword()).trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already taken. Please choose another.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hashed = LoginFrame.hashSHA256(password);
        boolean success = userDAO.register(username, hashed);

        if (success) {
            JOptionPane.showMessageDialog(this, "Registration Successful! Please login.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(field, gbc);
        return field;
    }

    private JComponent addRowGeneric(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(field, gbc);
        return field;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.RED);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 2),
                BorderFactory.createEmptyBorder(12, 32, 12, 32)));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY_DARK);
                if (bg.equals(new Color(55, 65, 81))) {
                    btn.setBackground(new Color(31, 41, 55));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }
}
