package com.greexam.ui.teacher;

import com.greexam.dao.SubmissionDAO;
import com.greexam.model.Submission;
import com.greexam.util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TestSubmissionsDialog extends JDialog {

    private int testId;
    private String testTitle;
    private SubmissionDAO submissionDAO = new SubmissionDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    public TestSubmissionsDialog(Frame owner, int testId, String testTitle) {
        super(owner, "Submissions: " + testTitle, true);
        this.testId = testId;
        this.testTitle = testTitle;
        
        setSize(800, 500);
        setLocationRelativeTo(owner);
        initUI();
        loadSubmissions();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Test: " + testTitle));
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Student Name", "Submitted At", "Marks Obtained", "Auto Submitted"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton btnGrade = new JButton("View Answers / Grade / Feedback");
        btnGrade.setBackground(new Color(41, 128, 185));
        btnGrade.setForeground(Color.WHITE);
        btnGrade.addActionListener(e -> openGrading());
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnGrade);
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadSubmissions() {
        tableModel.setRowCount(0);
        List<Submission> subs = submissionDAO.findByTest(testId);
        for (Submission s : subs) {
            if (s.isCompleted()) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getStudentName(),
                        DateTimeUtil.formatDisplay(s.getEndTime()),
                        String.format("%.1f", s.getTotalMarksObtained()),
                        s.isAutoSubmitted() ? "Yes" : "No"
                });
            }
        }
    }

    private void openGrading() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a submission to grade.");
            return;
        }

        int submissionId = (int) tableModel.getValueAt(row, 0);
        Submission sub = submissionDAO.findById(submissionId);
        if (sub != null) {
            new GradingDialog(this, sub).setVisible(true);
            loadSubmissions(); // refresh marks after grading
        }
    }
}
