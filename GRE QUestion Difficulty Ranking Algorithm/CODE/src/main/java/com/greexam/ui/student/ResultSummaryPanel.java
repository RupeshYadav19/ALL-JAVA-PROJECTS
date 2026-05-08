package com.greexam.ui.student;

import com.greexam.dao.SubmissionDAO;
import com.greexam.model.StudentAnswer;
import com.greexam.model.Submission;
import com.greexam.service.AuthService;
import com.greexam.util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResultSummaryPanel extends JPanel {

    private SubmissionDAO submissionDAO = new SubmissionDAO();
    private AuthService authService = AuthService.getInstance();

    private JComboBox<SubmissionItem> cmbSubmissions;
    private JLabel lblResultOverview;
    private JTable tblAnswers;
    private DefaultTableModel tModel;

    public ResultSummaryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadSubmissions();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Test Result:"));
        cmbSubmissions = new JComboBox<>();
        cmbSubmissions.addActionListener(e -> displayResult());
        topPanel.add(cmbSubmissions);
        
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        lblResultOverview = new JLabel("Select a result to view details.");
        lblResultOverview.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblResultOverview.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        centerPanel.add(lblResultOverview, BorderLayout.NORTH);

        String[] cols = {"Q No.", "Question", "Your Answer", "Status", "Marks", "Time Spent (s)"};
        tModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAnswers = new JTable(tModel);
        tblAnswers.setRowHeight(25);
        tblAnswers.getColumnModel().getColumn(1).setPreferredWidth(300);

        centerPanel.add(new JScrollPane(tblAnswers), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadSubmissions() {
        cmbSubmissions.removeAllItems();
        int stuId = authService.getCurrentUser().getId();
        List<Submission> subs = submissionDAO.findByStudent(stuId);
        for (Submission s : subs) {
            cmbSubmissions.addItem(new SubmissionItem(s.getId(), s.getTestTitle() + " (" + DateTimeUtil.formatDate(s.getEndTime()) + ")"));
        }
    }

    private void displayResult() {
        SubmissionItem item = (SubmissionItem) cmbSubmissions.getSelectedItem();
        if (item == null) return;

        Submission sub = submissionDAO.findById(item.id);
        if (sub == null) return;

        // Note: Real passing marks needed from Test entity
        String overText = String.format("<html><b>Test:</b> %s<br><b>Total Marks Obtained:</b> %.1f<br><b>Time Taken:</b> %d mins<br><b>Status:</b> %s</html>",
                sub.getTestTitle(),
                sub.getTotalMarksObtained(),
                sub.getTimeTakenMinutes(),
                "Completed"
        );
        lblResultOverview.setText(overText);

        tModel.setRowCount(0);
        List<StudentAnswer> answers = sub.getAnswers();
        int qNum = 1;
        for (StudentAnswer a : answers) {
            String qText = a.getQuestionText() != null ? a.getQuestionText().replace("\n", " ") : "";
            if (qText.length() > 50) qText = qText.substring(0, 50) + "...";
            tModel.addRow(new Object[]{
                    qNum++,
                    qText,
                    a.getAnswerText(),
                    a.getStatusText(),
                    a.getMarksObtained(),
                    a.getTimeSpentSeconds()
            });
        }
    }

    static class SubmissionItem {
        int id;
        String display;
        public SubmissionItem(int id, String display) { this.id = id; this.display = display; }
        @Override public String toString() { return display; }
    }
}
