package com.greexam.ui.teacher;

import com.greexam.dao.QuestionDAO;
import com.greexam.model.Question;
import com.greexam.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionBankPanel extends JPanel {

    private QuestionDAO questionDAO = new QuestionDAO();
    private AuthService authService = AuthService.getInstance();
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbFilterTopic;
    private JComboBox<String> cmbFilterType;

    public QuestionBankPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadQuestions();
        loadTopics();
    }

    private void initUI() {
        // Top Panel: Filters & Add Button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        topPanel.add(new JLabel("Type:"));
        cmbFilterType = new JComboBox<>(new String[]{"All", "MCQ", "FILL_BLANK", "ONE_WORD", "SHORT_ANSWER", "LONG_ANSWER", "MATCH"});
        cmbFilterType.addActionListener(e -> loadQuestions());
        topPanel.add(cmbFilterType);

        topPanel.add(new JLabel("Topic:"));
        cmbFilterTopic = new JComboBox<>(new String[]{"All"});
        cmbFilterTopic.addActionListener(e -> loadQuestions());
        topPanel.add(cmbFilterTopic);

        JButton btnAdd = new JButton("Add New Question");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> openAddQuestionDialog());
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(btnAdd);

        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedQuestion());
        topPanel.add(btnDelete);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Type", "Topic", "Marks", "Est. Time (s)", "Question snippet"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(300);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTopics() {
        List<String> topics = questionDAO.findDistinctTopics(authService.getCurrentUser().getId());
        cmbFilterTopic.removeAllItems();
        cmbFilterTopic.addItem("All");
        for (String t : topics) {
            if (t != null && !t.trim().isEmpty()) cmbFilterTopic.addItem(t);
        }
    }

    public void loadQuestions() {
        if (cmbFilterType == null || cmbFilterTopic == null) return;
        tableModel.setRowCount(0);
        int teacherId = authService.getCurrentUser().getId();
        
        List<Question> questions = questionDAO.findByTeacher(teacherId);

        String selectedType = (String) cmbFilterType.getSelectedItem();
        String selectedTopic = (String) cmbFilterTopic.getSelectedItem();

        if (!"All".equals(selectedType)) {
            questions = questions.stream()
                .filter(q -> q.getQuestionType().name().equals(selectedType))
                .collect(Collectors.toList());
        }

        if (!"All".equals(selectedTopic)) {
            questions = questions.stream()
                .filter(q -> q.getTopic() != null && q.getTopic().equals(selectedTopic))
                .collect(Collectors.toList());
        }

        for (Question q : questions) {
            String snippet = q.getQuestionText().replace("\n", " ");
            if (snippet.length() > 50) snippet = snippet.substring(0, 50) + "...";
            tableModel.addRow(new Object[]{
                    q.getId(),
                    q.getQuestionType().name(),
                    q.getTopic(),
                    q.getMarks(),
                    q.getExpectedTimeSeconds(),
                    snippet
            });
        }
    }

    private void deleteSelectedQuestion() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this question? It might be part of an exam.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int qId = (int) tableModel.getValueAt(row, 0);
            if (questionDAO.delete(qId)) {
                JOptionPane.showMessageDialog(this, "Deleted successfully.");
                loadQuestions();
                loadTopics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAddQuestionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Question", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Type
        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<Question.QuestionType> cmbType = new JComboBox<>(Question.QuestionType.values());
        p.add(cmbType, gbc);

        // Topic
        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Topic:"), gbc);
        gbc.gridx = 1;
        JTextField txtTopic = new JTextField(20);
        p.add(txtTopic, gbc);

        // Marks
        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Marks:"), gbc);
        gbc.gridx = 1;
        JSpinner spinMarks = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        p.add(spinMarks, gbc);

        // Expected Time
        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Expected Time (s):"), gbc);
        gbc.gridx = 1;
        JSpinner spinTime = new JSpinner(new SpinnerNumberModel(60, 5, 3600, 5));
        p.add(spinTime, gbc);

        // Text
        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("Question Text:"), gbc);
        gbc.gridx = 1;
        JTextArea txtText = new JTextArea(4, 20);
        txtText.setLineWrap(true);
        p.add(new JScrollPane(txtText), gbc);

        // MCQ Options Section (Simplified to 4 fixed options for MCQ)
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        p.add(new JLabel("<html><i>If MCQ, provide options and check the correct one:</i></html>"), gbc);

        JTextField[] optFields = new JTextField[4];
        JRadioButton[] optRadios = new JRadioButton[4];
        ButtonGroup bg = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            gbc.gridy++;
            gbc.gridwidth = 1;
            optRadios[i] = new JRadioButton("O" + (i+1));
            bg.add(optRadios[i]);
            p.add(optRadios[i], gbc);
            
            gbc.gridx = 1;
            optFields[i] = new JTextField(20);
            p.add(optFields[i], gbc);
            gbc.gridx = 0;
        }

        // Add
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton btnSave = new JButton("Save Question");
        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            if (txtText.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Question text cannot be empty.");
                return;
            }
            
            Question q = new Question();
            q.setTeacherId(authService.getCurrentUser().getId());
            q.setQuestionType((Question.QuestionType) cmbType.getSelectedItem());
            q.setTopic(txtTopic.getText().trim().isEmpty() ? "General" : txtTopic.getText().trim());
            q.setMarks((Integer) spinMarks.getValue());
            q.setExpectedTimeSeconds((Integer) spinTime.getValue());
            q.setQuestionText(txtText.getText().trim());

            if (q.getQuestionType() == Question.QuestionType.MCQ) {
                boolean hasCorrect = false;
                for (int i = 0; i < 4; i++) {
                    String optText = optFields[i].getText().trim();
                    if (!optText.isEmpty()) {
                        q.getOptions().add(new Question.QuestionOption(optText, optRadios[i].isSelected()));
                        if (optRadios[i].isSelected()) hasCorrect = true;
                    }
                }
                if (!hasCorrect || q.getOptions().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "MCQ requires options and one must be selected as correct.");
                    return;
                }
            }
            
            if (questionDAO.insert(q)) {
                JOptionPane.showMessageDialog(dialog, "Question added!");
                loadQuestions();
                loadTopics();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Database Error!");
            }
        });
        
        p.add(btnSave, gbc);
        dialog.add(new JScrollPane(p));
        dialog.setVisible(true);
    }
}
