package com.academic.ui;

import com.academic.dao.StudentDAO;
import com.academic.engine.DecisionEngine;
import com.academic.model.Student;
import com.academic.service.GeminiService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class EligibilityResultFrame extends JFrame {

    private final Student student;
    private List<String[]> results;
    private final StudentFormFrame formFrame;
    private StudentDAO studentDAO = new StudentDAO();
    private DecisionEngine engine = new DecisionEngine();

    private JPanel mainContentPanel;
    private JLabel summaryLabel;
    private JLabel categoryLabel;

    // Gemini API key applied
    public static String GEMINI_API_KEY = "AIzaSyC_IMceC3ygUWNtEmT9ijKwEAr7VrQDamA";

    private static final Color PRIMARY_COLOR = new Color(67, 56, 202);
    private static final Color PRIMARY_DARK = new Color(49, 46, 129);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);

    public EligibilityResultFrame(Student student, List<String[]> results, StudentFormFrame formFrame) {
        this.student = student;
        this.results = results;
        this.formFrame = formFrame;

        setTitle("Student Academic Summary — " + student.getFullName());
        setSize(850, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 0));

        // ---- TITLE PANEL ----
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel titleLabel = new JLabel("Academic Eligibility Overview");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ---- MAIN CONTENT SCROLL ----
        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(BG_COLOR);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        refreshContent();

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // ---- BOTTOM PANEL ----
        add(buildBottomPanel(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(EligibilityResultFrame.this,
                        "Are you sure you want to exit?", "Confirm Exit",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
    }

    private void refreshContent() {
        mainContentPanel.removeAll();

        // 1. Student Info Box
        mainContentPanel.add(buildInfoBox());
        mainContentPanel.add(Box.createVerticalStrut(20));

        // 2. Eligibility Boxes Grid
        mainContentPanel.add(buildEligibilityGrid());
        mainContentPanel.add(Box.createVerticalStrut(20));

        // 3. Improvement Advice Box
        String advice = getImprovementAdvice();
        if (advice != null && !advice.isEmpty()) {
            mainContentPanel.add(buildAdviceBox(advice));
            mainContentPanel.add(Box.createVerticalStrut(20));
        }

        // 4. Gemini AI Section
        mainContentPanel.add(buildAISection());
        mainContentPanel.add(Box.createVerticalStrut(20));

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private JPanel buildInfoBox() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        panel.add(createDetailLabel("Full Name", student.getFullName()));
        panel.add(createDetailLabel("Stream", student.getStream()));
        panel.add(createDetailLabel("Year", String.valueOf(student.getYear())));
        panel.add(createDetailLabel("Semester", String.valueOf(student.getSemester())));
        panel.add(createDetailLabel("SGPA", String.format("%.2f", student.getSgpa())));
        panel.add(createDetailLabel("Credits", String.valueOf(student.getCredits())));
        panel.add(createDetailLabel("Attendance", String.format("%.1f%%", student.getAttendancePercent())));
        panel.add(createDetailLabel("Violation",
                student.isConductViolation() ? "YES (" + student.getConductType() + ")" : "NO"));

        return panel;
    }

    private JPanel createDetailLabel(String key, String val) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.BOLD, 12));
        k.setForeground(Color.GRAY);
        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        v.setForeground(PRIMARY_COLOR);
        p.add(k, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildEligibilityGrid() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_COLOR);
        JLabel title = new JLabel("Eligibility Status");
        title.setFont(HEADER_FONT);
        title.setForeground(PRIMARY_COLOR);
        outer.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        grid.setBackground(BG_COLOR);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        int passCount = 0;
        for (String[] res : results) {
            if (res[0].startsWith("IMPROVE"))
                continue; // Skip improvement logic row for grid

            boolean isPass = "Pass".equals(res[5]);
            if (isPass)
                passCount++;

            JPanel box = new JPanel(new BorderLayout());
            Color boxBg = new Color(236, 253, 245); // Default PASS_COLOR
            Color boxBorder = new Color(134, 239, 172); // Default PASS_BORDER
            if (!isPass) {
                boxBg = new Color(254, 242, 242); // FAIL_COLOR
                boxBorder = new Color(252, 165, 165); // FAIL_BORDER
            }
            if (res[4].contains("WARNING") || res[4].contains("capped")) {
                boxBg = new Color(255, 251, 235); // WARN_COLOR
                boxBorder = new Color(253, 224, 71); // WARN_BORDER
            }
            box.setBackground(boxBg);
            box.setBorder(BorderFactory.createLineBorder(boxBorder));

            JLabel head = new JLabel(res[1]); // Description
            head.setFont(new Font("Segoe UI", Font.BOLD, 12));
            head.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel status = new JLabel(res[4]); // Actual Result
            status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            status.setHorizontalAlignment(SwingConstants.CENTER);

            box.add(head, BorderLayout.NORTH);
            box.add(status, BorderLayout.CENTER);
            box.setPreferredSize(new Dimension(200, 60));
            grid.add(box);
        }

        outer.add(grid, BorderLayout.CENTER);

        // Update summary info based on pass count
        updateCategoryInfo(passCount, results.size() - (getImprovementAdvice() != null ? 1 : 0));

        return outer;
    }

    private void updateCategoryInfo(int pass, int total) {
        String category;
        Color catColor;
        if (pass >= 6) {
            category = "⭐ BEST PERFORMANCE";
            catColor = new Color(40, 100, 40);
        } else if (pass >= 4) {
            category = "⚠ AVERAGE PERFORMANCE";
            catColor = new Color(150, 100, 0);
        } else {
            category = "❌ POOR PERFORMANCE";
            catColor = new Color(150, 40, 40);
        }

        if (summaryLabel != null) {
            summaryLabel.setText("Total Checks Passed: " + pass + " / " + total);
        }
        if (categoryLabel != null) {
            categoryLabel.setText(category);
            categoryLabel.setForeground(catColor);
        }
    }

    private String getImprovementAdvice() {
        for (String[] res : results) {
            if (res[0].startsWith("IMPROVE")) {
                return res[4]; // The improvement analysis text
            }
        }

        // General advice if not Sem 3
        if (student.getSgpa() < 6.0) {
            return "Your SGPA is currently low. Focus on improving attendance and meeting with your professors for extra support.";
        } else if (student.getSgpa() < 8.0) {
            return "Good progress. To reach the Dean's list, aim for more consistency in core subjects.";
        }
        return "Excellent work! Keep maintaining this level of dedication.";
    }

    private JPanel buildAdviceBox(String advice) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 244, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(199, 210, 254)),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));

        JLabel title = new JLabel("Academic Improvement & Advice");
        title.setFont(HEADER_FONT);
        title.setForeground(PRIMARY_COLOR);

        JTextArea area = new JTextArea(advice);
        area.setFont(LABEL_FONT);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);
        area.setBackground(new Color(240, 244, 255));
        area.setBorder(null);

        panel.add(title, BorderLayout.NORTH);
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 12, 15));

        // Summary row
        JPanel summaryRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 4));
        summaryRow.setBackground(BG_COLOR);
        summaryLabel = new JLabel("Total Qualified: 0 / 0");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        summaryLabel.setForeground(PRIMARY_COLOR);

        categoryLabel = new JLabel("Category: N/A");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        summaryRow.add(summaryLabel);
        summaryRow.add(categoryLabel);
        panel.add(summaryRow);

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        btnRow.setBackground(BG_COLOR);

        JButton backBtn = createButton("Back to Form", PRIMARY_COLOR);
        JButton searchBtn = createButton("Search Student", new Color(55, 65, 81));

        backBtn.addActionListener(e -> {
            formFrame.setVisible(true);
            dispose();
        });
        searchBtn.addActionListener(e -> handleSearch());

        btnRow.add(backBtn);
        btnRow.add(searchBtn);
        panel.add(btnRow);

        return panel;
    }

    private void handleSearch() {
        String name = JOptionPane.showInputDialog(this,
                "Enter student name to search:", "Search Student", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty())
            return;

        Student found = studentDAO.findByName(name.trim());
        if (found == null) {
            JOptionPane.showMessageDialog(this,
                    "No student found with name: " + name,
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Re-run the engine for the found student
        List<String[]> newResults = engine.evaluate(found);
        this.results = newResults;
        // Re-check student object in frame - needs to be updated for refresh to work
        // with found student data
        // Actually, let's just create a new frame or update the local 'student'
        // reference
        // For simplicity, let's update fields and refresh
        try {
            java.lang.reflect.Field field = EligibilityResultFrame.class.getDeclaredField("student");
            field.setAccessible(true);
            field.set(this, found);
        } catch (Exception ex) {
        }

        setTitle("Student Academic Summary — " + found.getFullName());
        refreshContent();
        JOptionPane.showMessageDialog(this,
                "Loaded results for: " + found.getFullName(),
                "Student Found", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel buildAISection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(110, 100, 240), 2),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));

        JLabel title = new JLabel("✨ Gemini AI Academic Insights");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(79, 70, 229));

        JTextArea aiText = new JTextArea("Fetching personalized AI academic feedback...");
        aiText.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        aiText.setWrapStyleWord(true);
        aiText.setLineWrap(true);
        aiText.setEditable(false);
        aiText.setBackground(Color.WHITE);

        panel.add(title, BorderLayout.NORTH);
        panel.add(aiText, BorderLayout.CENTER);

        if (GEMINI_API_KEY.isEmpty()) {
            aiText.setText("AI Feedback unavailable. (Gemini API Key not set)");
            JButton setKeyBtn = new JButton("Set API Key");
            setKeyBtn.addActionListener(e -> {
                String key = JOptionPane.showInputDialog(this, "Enter your Gemini API Key:");
                if (key != null && !key.trim().isEmpty()) {
                    GEMINI_API_KEY = key.trim();
                    refreshContent();
                }
            });
            panel.add(setKeyBtn, BorderLayout.SOUTH);
        } else {
            new Thread(() -> {
                try {
                    GeminiService service = new GeminiService(GEMINI_API_KEY);
                    String feedback = service.getFeedback(student, results);
                    SwingUtilities.invokeLater(() -> {
                        aiText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        aiText.setText(feedback);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> aiText.setText("AI Error: " + ex.getMessage()));
                }
            }).start();
        }
        return panel;
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
