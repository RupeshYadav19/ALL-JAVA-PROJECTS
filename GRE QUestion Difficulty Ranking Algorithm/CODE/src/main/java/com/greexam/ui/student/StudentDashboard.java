package com.greexam.ui.student;

import com.greexam.service.AuthService;
import com.greexam.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private AuthService authService = AuthService.getInstance();
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public StudentDashboard() {
        setTitle("Student Dashboard - " + authService.getCurrentUser().getName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(39, 174, 96)); // Green theme for student
        
        JLabel lblMenu = new JLabel("MENU");
        lblMenu.setForeground(Color.WHITE);
        lblMenu.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMenu.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(lblMenu);

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add Panels
        contentPanel.add(new TestListPanel(), "TESTS");
        // Placeholders
        contentPanel.add(new ResultSummaryPanel(), "RESULTS");
        contentPanel.add(new NotificationsPanel(), "NOTIFICATIONS");

        // Sidebar Buttons
        addMenuButton(sidebar, "My Tests", "TESTS");
        addMenuButton(sidebar, "My Results", "RESULTS");
        addMenuButton(sidebar, "Notifications", "NOTIFICATIONS");

        sidebar.add(Box.createVerticalGlue());
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(180, 40));
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addMenuButton(JPanel sidebar, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(new Color(46, 204, 113));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        sidebar.add(btn);
    }

    private void logout() {
        authService.logout();
        new LoginFrame().setVisible(true);
        this.dispose();
    }
}
