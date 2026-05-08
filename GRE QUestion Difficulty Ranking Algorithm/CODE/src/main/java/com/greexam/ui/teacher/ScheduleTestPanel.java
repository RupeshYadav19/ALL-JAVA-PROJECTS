package com.greexam.ui.teacher;

import com.greexam.dao.QuestionDAO;
import com.greexam.dao.TestDAO;
import com.greexam.dao.UserDAO;
import com.greexam.model.Question;
import com.greexam.model.Test;
import com.greexam.model.User;
import com.greexam.service.AuthService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ScheduleTestPanel extends JPanel {

    private TestDAO testDAO = new TestDAO();
    private QuestionDAO questionDAO = new QuestionDAO();
    private UserDAO userDAO = new UserDAO();
    private AuthService authService = AuthService.getInstance();

    private JTextField txtTitle;
    private JSpinner spinDuration;
    private JSpinner spinPassingMarks;
    private JDateChooser dateStart;
    private JSpinner spinStartTime;
    private JDateChooser dateEnd;
    private JSpinner spinEndTime;
    private JCheckBox chkShowOneAtTime;

    private DefaultTableModel studentsModel;
    private JTable studentsTable;

    private DefaultTableModel questionsModel;
    private JTable questionsTable;

    private java.util.Set<Integer> selectedStudentIds = new java.util.HashSet<>();
    private java.util.Set<Integer> selectedQuestionIds = new java.util.HashSet<>();

    public ScheduleTestPanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: General Details
        JPanel pnlGeneral = new JPanel(new GridBagLayout());
        pnlGeneral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        pnlGeneral.add(new JLabel("Test Title*:"), gbc);
        gbc.gridx = 1;
        txtTitle = new JTextField(20);
        pnlGeneral.add(txtTitle, gbc);

        gbc.gridx = 0; gbc.gridy++;
        pnlGeneral.add(new JLabel("Duration (minutes)*:"), gbc);
        gbc.gridx = 1;
        spinDuration = new JSpinner(new SpinnerNumberModel(60, 1, 300, 5));
        pnlGeneral.add(spinDuration, gbc);

        gbc.gridx = 0; gbc.gridy++;
        pnlGeneral.add(new JLabel("Passing Marks:"), gbc);
        gbc.gridx = 1;
        spinPassingMarks = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        pnlGeneral.add(spinPassingMarks, gbc);

        // Start DateTime
        gbc.gridx = 0; gbc.gridy++;
        pnlGeneral.add(new JLabel("Start Date*:"), gbc);
        gbc.gridx = 1;
        JPanel pnlStart = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateStart = new JDateChooser(new java.util.Date());
        spinStartTime = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinStartTime, "HH:mm");
        spinStartTime.setEditor(timeEditor);
        pnlStart.add(dateStart);
        pnlStart.add(new JLabel(" Time: "));
        pnlStart.add(spinStartTime);
        pnlGeneral.add(pnlStart, gbc);

        // End DateTime
        gbc.gridx = 0; gbc.gridy++;
        pnlGeneral.add(new JLabel("End Date*:"), gbc);
        gbc.gridx = 1;
        JPanel pnlEnd = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateEnd = new JDateChooser(new java.util.Date());
        spinEndTime = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor2 = new JSpinner.DateEditor(spinEndTime, "HH:mm");
        spinEndTime.setEditor(timeEditor2);
        pnlEnd.add(dateEnd);
        pnlEnd.add(new JLabel(" Time: "));
        pnlEnd.add(spinEndTime);
        pnlGeneral.add(pnlEnd, gbc);

        // Add Listeners to sync End Time automatically
        javax.swing.event.ChangeListener syncEndTime = e -> {
            if (dateStart.getDate() != null && spinStartTime.getValue() != null) {
                LocalDateTime startDt = combineDateAndTime(dateStart.getDate(), (java.util.Date) spinStartTime.getValue());
                LocalDateTime endDt = startDt.plusMinutes(((Number) spinDuration.getValue()).longValue());
                dateEnd.setDate(java.util.Date.from(endDt.atZone(ZoneId.systemDefault()).toInstant()));
                spinEndTime.setValue(java.util.Date.from(endDt.atZone(ZoneId.systemDefault()).toInstant()));
            }
        };
        spinDuration.addChangeListener(syncEndTime);
        spinStartTime.addChangeListener(syncEndTime);
        dateStart.getDateEditor().addPropertyChangeListener(e -> syncEndTime.stateChanged(null));

        gbc.gridx = 0; gbc.gridy++;
        pnlGeneral.add(new JLabel("Options:"), gbc);
        gbc.gridx = 1;
        chkShowOneAtTime = new JCheckBox("Show one question at a time");
        chkShowOneAtTime.setSelected(true);
        pnlGeneral.add(chkShowOneAtTime, gbc);

        tabbedPane.addTab("1. Details", pnlGeneral);

        // Tab 2: Select Students
        JPanel pnlStudents = new JPanel(new BorderLayout(5, 5));
        pnlStudents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        studentsModel = new DefaultTableModel(new String[]{"Select", "ID", "Name", "Username"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        studentsTable = new JTable(studentsModel);
        studentsModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 0) {
                int row = e.getFirstRow();
                if (row >= 0 && row < studentsModel.getRowCount()) {
                    boolean isSelected = (Boolean) studentsModel.getValueAt(row, 0);
                    int id = (Integer) studentsModel.getValueAt(row, 1);
                    if (isSelected) selectedStudentIds.add(id);
                    else selectedStudentIds.remove(id);
                }
            }
        });
        loadStudents();
        pnlStudents.add(new JScrollPane(studentsTable), BorderLayout.CENTER);
        
        JPanel pnlStudAct = new JPanel();
        JButton btnSelectAllStud = new JButton("Select All");
        btnSelectAllStud.addActionListener(e -> setAllTableCheckboxes(studentsModel, true));
        pnlStudAct.add(btnSelectAllStud);
        pnlStudents.add(pnlStudAct, BorderLayout.SOUTH);
        
        tabbedPane.addTab("2. Students", pnlStudents);

        // Tab 3: Select Questions
        JPanel pnlQuestions = new JPanel(new BorderLayout(5, 5));
        pnlQuestions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        questionsModel = new DefaultTableModel(new String[]{"Select", "ID", "Type", "Topic", "Marks", "Text"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // only selection editable
            }
        };
        questionsTable = new JTable(questionsModel);
        questionsModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 0) {
                int row = e.getFirstRow();
                if (row >= 0 && row < questionsModel.getRowCount()) {
                    boolean isSelected = (Boolean) questionsModel.getValueAt(row, 0);
                    int id = (Integer) questionsModel.getValueAt(row, 1);
                    if (isSelected) selectedQuestionIds.add(id);
                    else selectedQuestionIds.remove(id);
                }
            }
        });
        loadQuestions();
        pnlQuestions.add(new JScrollPane(questionsTable), BorderLayout.CENTER);
        
        JPanel pnlQuesAct = new JPanel();
        JButton btnSelectAllQ = new JButton("Select All");
        btnSelectAllQ.addActionListener(e -> setAllTableCheckboxes(questionsModel, true));
        pnlQuesAct.add(btnSelectAllQ);
        pnlQuestions.add(pnlQuesAct, BorderLayout.SOUTH);

        tabbedPane.addTab("3. Questions", pnlQuestions);

        tabbedPane.addChangeListener(e -> {
            loadStudents();
            loadQuestions();
        });

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom publish area
        JPanel pnlBottom = new JPanel();
        JButton btnSaveDraft = new JButton("Save as Draft");
        btnSaveDraft.addActionListener(e -> saveTest(false));
        
        JButton btnPublish = new JButton("Publish Test");
        btnPublish.setBackground(new Color(41, 128, 185));
        btnPublish.setForeground(Color.WHITE);
        btnPublish.addActionListener(e -> saveTest(true));

        pnlBottom.add(btnSaveDraft);
        pnlBottom.add(btnPublish);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadStudents() {
        studentsModel.setRowCount(0);
        List<User> students = userDAO.findAllStudents();
        for (User u : students) {
            boolean isSelected = selectedStudentIds.contains(u.getId());
            studentsModel.addRow(new Object[]{isSelected, u.getId(), u.getName(), u.getUsername()});
        }
    }

    private void loadQuestions() {
        questionsModel.setRowCount(0);
        List<Question> questions = questionDAO.findByTeacher(authService.getCurrentUser().getId());
        for (Question q : questions) {
            String snippet = q.getQuestionText().replace("\n", " ");
            if (snippet.length() > 40) snippet = snippet.substring(0, 40) + "...";
            boolean isSelected = selectedQuestionIds.contains(q.getId());
            questionsModel.addRow(new Object[]{isSelected, q.getId(), q.getQuestionType().name(), q.getTopic(), q.getMarks(), snippet});
        }
    }

    private void setAllTableCheckboxes(DefaultTableModel model, boolean val) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(val, i, 0);
        }
    }

    private void saveTest(boolean publish) {
        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.");
            return;
        }

        if (dateStart.getDate() == null || dateEnd.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Valid Start and End dates are required.");
            return;
        }

        // Combine Date + Time
        LocalDateTime startDt = combineDateAndTime(dateStart.getDate(), (java.util.Date) spinStartTime.getValue());
        LocalDateTime endDt = combineDateAndTime(dateEnd.getDate(), (java.util.Date) spinEndTime.getValue());

        if (startDt.isAfter(endDt) || startDt.isEqual(endDt)) {
            JOptionPane.showMessageDialog(this, "End time must be after start time.");
            return;
        }

        Test test = new Test();
        test.setTeacherId(authService.getCurrentUser().getId());
        test.setTitle(title);
        test.setDurationMinutes((Integer) spinDuration.getValue());
        test.setPassingMarks((Integer) spinPassingMarks.getValue());
        test.setStartTime(startDt);
        test.setEndTime(endDt);
        test.setShowOneAtTime(chkShowOneAtTime.isSelected());
        test.setPublished(publish);

        // Gather Students
        List<Integer> studentIds = new ArrayList<>();
        for (int i = 0; i < studentsModel.getRowCount(); i++) {
            if ((Boolean) studentsModel.getValueAt(i, 0)) {
                studentIds.add((Integer) studentsModel.getValueAt(i, 1));
            }
        }
        
        if (studentIds.isEmpty() && publish) {
            JOptionPane.showMessageDialog(this, "Please select at least one student to publish.");
            return;
        }
        test.setAssignedStudentIds(studentIds);

        // Gather Questions
        List<Test.TestQuestion> testQ = new ArrayList<>();
        int order = 1;
        for (int i = 0; i < questionsModel.getRowCount(); i++) {
            if ((Boolean) questionsModel.getValueAt(i, 0)) {
                Test.TestQuestion tq = new Test.TestQuestion();
                tq.setQuestionId((Integer) questionsModel.getValueAt(i, 1));
                tq.setOrderNumber(order++);
                tq.setMarks((Integer) questionsModel.getValueAt(i, 4)); // use original marks for now
                testQ.add(tq);
            }
        }

        if (testQ.isEmpty() && publish) {
            JOptionPane.showMessageDialog(this, "Please select at least one question to publish.");
            return;
        }
        test.setTestQuestions(testQ);

        if (testDAO.insert(test)) {
             JOptionPane.showMessageDialog(this, publish ? "Test published successfully!" : "Test saved as draft.");
             // Clear form
             txtTitle.setText("");
             setAllTableCheckboxes(studentsModel, false);
             setAllTableCheckboxes(questionsModel, false);
        } else {
             JOptionPane.showMessageDialog(this, "Error saving test.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDateTime combineDateAndTime(java.util.Date datePortion, java.util.Date timePortion) {
        java.time.LocalDate ld = datePortion.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        java.time.LocalTime lt = timePortion.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        return LocalDateTime.of(ld, lt);
    }
}
