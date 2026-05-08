package com.greexam.ui.teacher;

import com.greexam.service.AuthService;
import com.greexam.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class TeacherDashboard extends JFrame {

    private AuthService authService = AuthService.getInstance();
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public TeacherDashboard() {
        setTitle("Teacher Dashboard - " + authService.getCurrentUser().getName());
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
        sidebar.setBackground(new Color(44, 62, 80));
        
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
        // Ensure to create these classes next
        contentPanel.add(new StudentsListPanel(), "STUDENTS");
        contentPanel.add(new QuestionBankPanel(), "QUESTIONS");
        contentPanel.add(new ScheduleTestPanel(), "SCHEDULE");
        contentPanel.add(new MyTestsPanel(), "MY_TESTS");
        contentPanel.add(new AnalyticsPanel(), "ANALYTICS");

        // Sidebar Buttons
        addMenuButton(sidebar, "Students List", "STUDENTS");
        addMenuButton(sidebar, "Question Bank", "QUESTIONS");
        addMenuButton(sidebar, "Schedule Test", "SCHEDULE");
        addMenuButton(sidebar, "My Tests", "MY_TESTS");
        addMenuButton(sidebar, "Analytics", "ANALYTICS");

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
        btn.setBackground(new Color(52, 73, 94));
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
