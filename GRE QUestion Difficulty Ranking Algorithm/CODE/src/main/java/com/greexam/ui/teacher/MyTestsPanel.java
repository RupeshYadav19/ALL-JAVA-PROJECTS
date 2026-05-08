package com.greexam.ui.teacher;

import com.greexam.dao.TestDAO;
import com.greexam.model.Test;
import com.greexam.service.AuthService;
import com.greexam.util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyTestsPanel extends JPanel {

    private TestDAO testDAO = new TestDAO();
    private AuthService authService = AuthService.getInstance();

    private JTable table;
    private DefaultTableModel tableModel;

    public MyTestsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadTests());
        topPanel.add(btnRefresh);

        JButton btnViewDetails = new JButton("View Submissions / Grade / Feedback");
        btnViewDetails.addActionListener(e -> viewDetails());
        topPanel.add(btnViewDetails);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Duration (m)", "Start Time", "End Time", "Status", "Completed / Assigned", "Published"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        loadTests();
    }

    public void loadTests() {
        tableModel.setRowCount(0);
        int teacherId = authService.getCurrentUser().getId();
        List<Test> tests = testDAO.findByTeacher(teacherId);

        for (Test t : tests) {
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getTitle(),
                    t.getDurationMinutes(),
                    DateTimeUtil.formatDisplay(t.getStartTime()),
                    DateTimeUtil.formatDisplay(t.getEndTime()),
                    t.getStatusText(),
                    t.getCompletedCount() + " / " + t.getStudentCount(),
                    t.isPublished() ? "Yes" : "No"
            });
        }
    }

    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a test first.");
            return;
        }

        int testId = (int) tableModel.getValueAt(row, 0);
        String testTitle = (String) tableModel.getValueAt(row, 1);
        
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TestSubmissionsDialog dialog = new TestSubmissionsDialog(topFrame, testId, testTitle);
        dialog.setVisible(true);
        loadTests(); // Refresh counts after potentially grading
    }
}
