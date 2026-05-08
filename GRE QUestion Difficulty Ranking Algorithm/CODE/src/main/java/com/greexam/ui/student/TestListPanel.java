package com.greexam.ui.student;

import com.greexam.dao.SubmissionDAO;
import com.greexam.dao.TestDAO;
import com.greexam.model.Test;
import com.greexam.service.AuthService;
import com.greexam.service.ExamService;
import com.greexam.util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class TestListPanel extends JPanel {

    private TestDAO testDAO = new TestDAO();
    private SubmissionDAO submissionDAO = new SubmissionDAO();
    private ExamService examService = new ExamService();
    private AuthService authService = AuthService.getInstance();

    private JTable table;
    private DefaultTableModel tableModel;

    public TestListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTests());
        topPanel.add(btnRefresh);

        JButton btnStart = new JButton("Start / Resume Test");
        btnStart.setBackground(new Color(41, 128, 185));
        btnStart.setForeground(Color.WHITE);
        btnStart.addActionListener(e -> startTest());
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(btnStart);

        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Duration", "Start Window", "End Window", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadTests();
    }

    public void loadTests() {
        tableModel.setRowCount(0);
        int studentId = authService.getCurrentUser().getId();
        List<Test> tests = testDAO.findTestsForStudent(studentId);
        
        LocalDateTime now = LocalDateTime.now();

        for (Test t : tests) {
            String status = "Upcoming";
            if (testDAO.hasStudentSubmitted(t.getId(), studentId)) {
                status = "Completed";
            } else if (now.isAfter(t.getStartTime()) && now.isBefore(t.getEndTime())) {
                status = "Active";
            } else if (now.isAfter(t.getEndTime())) {
                status = "Missed";
            }

            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getTitle(),
                    t.getDurationMinutes() + " m",
                    DateTimeUtil.formatDisplay(t.getStartTime()),
                    DateTimeUtil.formatDisplay(t.getEndTime()),
                    status
            });
        }
    }

    private void startTest() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a test.");
            return;
        }

        String status = (String) tableModel.getValueAt(row, 5);
        if ("Completed".equals(status)) {
            JOptionPane.showMessageDialog(this, "You have already completed this test.");
            return;
        }
        if ("Upcoming".equals(status)) {
            JOptionPane.showMessageDialog(this, "This test has not started yet.");
            return;
        }
        if ("Missed".equals(status)) {
            JOptionPane.showMessageDialog(this, "The time window for this test has closed.");
            return;
        }

        int testId = (int) tableModel.getValueAt(row, 0);
        int studentId = authService.getCurrentUser().getId();

        int submissionId = examService.startTest(testId, studentId);
        if (submissionId > 0) {
             // Close dashboard and open Test Taking Screen
             Window win = SwingUtilities.getWindowAncestor(this);
             if (win != null) win.dispose();
             
             new TestTakingFrame(submissionId, testId).setVisible(true);
        } else {
             JOptionPane.showMessageDialog(this, "Failed to start test or already submitted.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
