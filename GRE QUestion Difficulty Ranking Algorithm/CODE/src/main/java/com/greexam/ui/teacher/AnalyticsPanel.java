package com.greexam.ui.teacher;

import com.greexam.dao.TestDAO;
import com.greexam.model.Test;
import com.greexam.service.AnalyticsService;
import com.greexam.service.AuthService;
import com.greexam.util.ExcelExporter;
import com.greexam.util.PDFExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class AnalyticsPanel extends JPanel {

    private TestDAO testDAO = new TestDAO();
    private AnalyticsService analyticsService = new AnalyticsService();
    private AuthService authService = AuthService.getInstance();

    private JComboBox<TestItem> cmbTestSelect;
    private JLabel lblOverview;
    private JTable tblQuestionAnalytics;
    private DefaultTableModel qModel;
    
    private JLabel lblEasyCount, lblModCount, lblDiffCount, lblNeedsAttn;

    public AnalyticsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        // Top Panel: Test selection & Export buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Test:"));
        cmbTestSelect = new JComboBox<>();
        loadPublishedTests();
        cmbTestSelect.addActionListener(e -> loadAnalyticsData());
        topPanel.add(cmbTestSelect);

        JButton btnPDF = new JButton("Export PDF Report");
        btnPDF.addActionListener(e -> exportData(true));
        topPanel.add(btnPDF);

        JButton btnExcel = new JButton("Export Excel");
        btnExcel.addActionListener(e -> exportData(false));
        topPanel.add(btnExcel);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));

        // 1. Overview Dashboard (North of Center)
        JPanel pnlDashboard = new JPanel(new GridLayout(2, 1, 5, 5));
        lblOverview = new JLabel("Select a test to view performance overview.");
        lblOverview.setFont(new Font("SansSerif", Font.BOLD, 14));
        pnlDashboard.add(lblOverview);

        JPanel pnlDifficultySummary = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        pnlDifficultySummary.setBorder(BorderFactory.createTitledBorder("Quick Difficulty Ranking Summary"));
        lblEasyCount = new JLabel("Easy: 0");
        lblModCount = new JLabel("Moderate: 0");
        lblDiffCount = new JLabel("Difficult: 0");
        lblNeedsAttn = new JLabel("Needs Attention: 0");
        
        lblEasyCount.setForeground(new Color(39, 174, 96));
        lblModCount.setForeground(new Color(243, 156, 18));
        lblDiffCount.setForeground(new Color(192, 57, 43));
        lblNeedsAttn.setForeground(new Color(142, 68, 173));

        pnlDifficultySummary.add(lblEasyCount);
        pnlDifficultySummary.add(lblModCount);
        pnlDifficultySummary.add(lblDiffCount);
        pnlDifficultySummary.add(lblNeedsAttn);
        pnlDashboard.add(pnlDifficultySummary);

        centerPanel.add(pnlDashboard, BorderLayout.NORTH);

        // 2. Main Question Analytics Table (Center of Center)
        String[] cols = {"Q No.", "Text Snippet", "Type", "Avg Time (s)", "Correct Rate", "Skip Rate", "Difficulty Rank"};
        qModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblQuestionAnalytics = new JTable(qModel);
        tblQuestionAnalytics.setRowHeight(25);
        centerPanel.add(new JScrollPane(tblQuestionAnalytics), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        
        loadAnalyticsData();
    }

    public void loadPublishedTests() {
        cmbTestSelect.removeAllItems();
        List<Test> tests = testDAO.findPublishedByTeacher(authService.getCurrentUser().getId());
        for (Test t : tests) {
            cmbTestSelect.addItem(new TestItem(t.getId(), t.getTitle()));
        }
    }

    private void loadAnalyticsData() {
        try {
            TestItem item = (TestItem) cmbTestSelect.getSelectedItem();
            if (item == null) {
                lblOverview.setText("No tests available yet.");
                qModel.setRowCount(0);
                resetSummary();
                return;
            }
            int testId = item.id;

            // Force recalculation
            analyticsService.refreshDifficultyRankings(testId);

            // Load Overview
            Map<String, Object> ov = analyticsService.getTestOverview(testId);
            String ovText = String.format("Test: %s | Students: %s | Submissions: %s | Avg Marks: %.1f",
                    item.title,
                    ov.getOrDefault("totalStudents", "0"),
                    ov.getOrDefault("totalSubmissions", "0"),
                    ov.getOrDefault("avgMarks", 0.0)
            );
            lblOverview.setText(ovText);

            // Difficulty Counts
            Map<String, Integer> dist = analyticsService.getDifficultyDistribution(testId);
            lblEasyCount.setText("Easy: " + dist.getOrDefault("EASY", 0));
            lblModCount.setText("Moderate: " + dist.getOrDefault("MODERATE", 0));
            lblDiffCount.setText("Difficult: " + dist.getOrDefault("DIFFICULT", 0));
            lblNeedsAttn.setText("Needs Attention: " + dist.getOrDefault("NEEDS_ATTENTION", 0));

            // Populate Table
            qModel.setRowCount(0);
            List<Map<String, Object>> qStats = analyticsService.getPerQuestionAnalytics(testId);
            int no = 1;
            for (Map<String, Object> q : qStats) {
                String text = (String) q.get("questionText");
                if (text != null && text.length() > 40) text = text.substring(0, 40) + "...";
                
                // Safe numeric extraction with doubleValue()
                double avgTime = ((Number) q.getOrDefault("avgTime", 0.0)).doubleValue();
                double correctRate = ((Number) q.getOrDefault("correctRate", 0.0)).doubleValue();
                double skipRate = ((Number) q.getOrDefault("skipRate", 0.0)).doubleValue();

                qModel.addRow(new Object[]{
                        no++,
                        text,
                        q.get("questionType"),
                        String.format("%.1f", avgTime),
                        String.format("%.1f %%", correctRate),
                        String.format("%.1f %%", skipRate),
                        q.getOrDefault("difficultyLevel", "MODERATE")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Error loading analytics: " + e.getMessage();
            lblOverview.setText("<html><font color='red'>" + errorMsg + "</font></html>");
            
            // Show detailed error dialog
            StringBuilder sb = new StringBuilder(errorMsg + "\n\nStack Trace:\n");
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append(ste.toString()).append("\n");
                if (sb.length() > 500) break; // limit size
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Analytics Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetSummary() {
        lblEasyCount.setText("Easy: 0");
        lblModCount.setText("Moderate: 0");
        lblDiffCount.setText("Difficult: 0");
        lblNeedsAttn.setText("Needs Attention: 0");
    }

    private void exportData(boolean asPdf) {
        TestItem item = (TestItem) cmbTestSelect.getSelectedItem();
        if (item == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(item.title.replaceAll("[^a-zA-Z0-9_-]", "") + (asPdf ? "_Report.pdf" : "_Report.xlsx")));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            Map<String, Object> overview = analyticsService.getTestOverview(item.id);
            List<Map<String, Object>> questions = analyticsService.getPerQuestionAnalytics(item.id);

            boolean success = asPdf ? PDFExporter.exportAnalytics(path, item.title, overview, questions)
                                   : ExcelExporter.exportAnalytics(path, item.title, overview, questions);

            if (success) JOptionPane.showMessageDialog(this, "Exported: " + path);
            else JOptionPane.showMessageDialog(this, "Export failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class TestItem {
        int id;
        String title;
        public TestItem(int id, String title) { this.id = id; this.title = title; }
        @Override public String toString() { return title; }
    }
}
