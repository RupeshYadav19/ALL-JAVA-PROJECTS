package com.greexam.ui.teacher;

import com.greexam.dao.SubmissionDAO;
import com.greexam.model.StudentAnswer;
import com.greexam.model.Submission;
import com.greexam.service.NotificationService;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GradingDialog extends JDialog {

    private Submission submission;
    private SubmissionDAO submissionDAO = new SubmissionDAO();
    private NotificationService notificationService = NotificationService.getInstance();
    
    // Store inputs for each answer
    private Map<Integer, JTextField> marksFields = new HashMap<>();
    private Map<Integer, JTextArea> commentFields = new HashMap<>();

    public GradingDialog(JDialog owner, Submission sub) {
        super(owner, "Grading: " + sub.getStudentName() + " - " + sub.getTestTitle(), true);
        this.submission = sub;
        
        setSize(800, 600);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlHeader = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlHeader.add(new JLabel("Student: " + submission.getStudentName()));
        pnlHeader.add(new JLabel("Test: " + submission.getTestTitle()));
        pnlHeader.add(new JLabel("Marks Obtained: " + String.format("%.1f", submission.getTotalMarksObtained())));
        pnlHeader.add(new JLabel("Status: Completed"));
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        
        for (StudentAnswer sa : submission.getAnswers()) {
            pnlContent.add(createAnswerPanel(sa));
            pnlContent.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scroll = new JScrollPane(pnlContent);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel();
        JButton btnSave = new JButton("Save Grading & Notify Student");
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveGrading());
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        pnlBottom.add(btnSave);
        pnlBottom.add(btnCancel);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private JPanel createAnswerPanel(StudentAnswer sa) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder("Question ID: " + sa.getQuestionId()));
        p.setMaximumSize(new Dimension(750, 200));

        JPanel pnlDetails = new JPanel(new GridLayout(0, 1));
        pnlDetails.add(new JLabel("<html><b>Question:</b> " + sa.getQuestionText() + "</html>"));
        pnlDetails.add(new JLabel("<html><b>Student Answer:</b> <font color='blue'>" + (sa.getAnswerText() == null ? "[Skipped]" : sa.getAnswerText()) + "</font></html>"));
        pnlDetails.add(new JLabel("Max Marks: " + sa.getQuestionMarks() + " | Auto-graded: " + sa.getMarksObtained()));
        
        p.add(pnlDetails, BorderLayout.NORTH);

        JPanel pnlInput = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        gbc.gridx = 0; gbc.gridy = 0;
        pnlInput.add(new JLabel("Final Marks:"), gbc);
        gbc.gridx = 1;
        JTextField txtMarks = new JTextField(String.valueOf(sa.getMarksObtained()), 5);
        pnlInput.add(txtMarks, gbc);
        marksFields.put(sa.getQuestionId(), txtMarks);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlInput.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1;
        JTextArea txtComment = new JTextArea(submissionDAO.getFeedbackComment(submission.getId(), sa.getQuestionId()), 2, 40);
        txtComment.setLineWrap(true);
        pnlInput.add(new JScrollPane(txtComment), gbc);
        commentFields.put(sa.getQuestionId(), txtComment);

        p.add(pnlInput, BorderLayout.CENTER);
        return p;
    }

    private void saveGrading() {
        try {
            for (StudentAnswer sa : submission.getAnswers()) {
                double marks = Double.parseDouble(marksFields.get(sa.getQuestionId()).getText().trim());
                String comment = commentFields.get(sa.getQuestionId()).getText().trim();
                
                submissionDAO.saveFeedback(submission.getId(), sa.getQuestionId(), comment, marks);
            }

            // Send Notification
            String msg = String.format("Teacher has provided feedback on your test '%s'. Check your results for details.", submission.getTestTitle());
            notificationService.notify(submission.getStudentId(), msg);

            JOptionPane.showMessageDialog(this, "Feedback saved and student notified!");
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid marks for all questions.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving feedback: " + e.getMessage());
        }
    }
}
