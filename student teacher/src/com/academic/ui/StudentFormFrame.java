package com.academic.ui;

import com.academic.dao.StudentDAO;
import com.academic.engine.DecisionEngine;
import com.academic.model.Student;
import com.academic.model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StudentFormFrame extends JFrame {

    private final User loggedInUser;

    // Personal Information fields
    private JTextField fullNameField;
    private JTextField parentNameField;
    private JTextField dobField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;

    // Academic Information fields
    private JTextField sgpaField;
    private JTextField creditsField;
    private JTextField attendanceField;
    private JCheckBox conductCheckBox;
    private JComboBox<String> conductTypeCombo;
    private JLabel conductTypeLabel;

    // New Academic fields
    private JComboBox<String> streamCombo;
    private JComboBox<Integer> yearCombo;
    private JComboBox<Integer> semCombo;
    private JTextField cgpa1stYearField;
    private JTextField sgpa3rdSemField;
    private JLabel cgpa1stYearLabel;
    private JLabel sgpa3rdSemLabel;
    private JLabel sgpaMainLabel;

    private JComboBox<String> semStatusCombo;
    private JLabel semStatusLabel;

    private StudentDAO studentDAO = new StudentDAO();
    private DecisionEngine engine = new DecisionEngine();

    private static final Color PRIMARY_COLOR = new Color(67, 56, 202);
    private static final Color PRIMARY_DARK = new Color(49, 46, 129);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public StudentFormFrame(User user) {
        this.loggedInUser = user;

        setTitle("Student Details Form — Logged in as: " + user.getUsername());
        setSize(650, 760);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // ---- TITLE ----
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel titleLabel = new JLabel("Student Enrollment Dashboard");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ---- MAIN SCROLL PANEL ----
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        mainPanel.add(buildPersonalSection());
        mainPanel.add(Box.createVerticalStrut(18));
        mainPanel.add(buildAcademicSection());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // ---- BUTTONS ----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 14));
        btnPanel.setBackground(BG_COLOR);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 230)));

        JButton checkBtn = createButton("Check Eligibility", PRIMARY_COLOR);
        JButton clearBtn = createButton("Reset Form", new Color(55, 65, 81));

        checkBtn.addActionListener(e -> handleCheckEligibility());
        clearBtn.addActionListener(e -> clearForm());

        btnPanel.add(checkBtn);
        btnPanel.add(clearBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Window close confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(StudentFormFrame.this,
                        "Are you sure you want to exit?", "Confirm Exit",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
    }

    // ======================================================
    // Section builders
    // ======================================================

    private JPanel buildPersonalSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createSectionBorder("Personal Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 12, 9, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fullNameField = addFormRow(panel, gbc, 0, "Full Name *:", new JTextField(20));
        parentNameField = (JTextField) addFormRowGeneric(panel, gbc, 1, "Parent/Guardian Name:", new JTextField(20));
        dobField = (JTextField) addFormRowGeneric(panel, gbc, 2, "Date of Birth (YYYY-MM-DD):", new JTextField(20));
        phoneField = (JTextField) addFormRowGeneric(panel, gbc, 3, "Phone Number (10 digits):", new JTextField(20));
        restrictPhoneField(phoneField);
        emailField = (JTextField) addFormRowGeneric(panel, gbc, 4, "Email Address:", new JTextField(20));

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        JLabel addrLabel = new JLabel("Address:");
        addrLabel.setFont(LABEL_FONT);
        panel.add(addrLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.7;
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(LABEL_FONT);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addrScroll = new JScrollPane(addressArea);
        addrScroll.setBorder(BorderFactory.createLineBorder(new Color(180, 190, 210)));
        panel.add(addrScroll, gbc);

        return panel;
    }

    private JPanel buildAcademicSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createSectionBorder("Academic Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 12, 9, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Stream
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        panel.add(new JLabel("Stream:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        streamCombo = new JComboBox<>(new String[] { "B.Tech", "BBA", "BCA", "MBA", "MCA" });
        streamCombo.setBackground(Color.WHITE);
        panel.add(streamCombo, gbc);

        // Year
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.35;
        panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        yearCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        yearCombo.setBackground(Color.WHITE);
        panel.add(yearCombo, gbc);

        // Semester
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.35;
        panel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        semCombo = new JComboBox<>();
        updateSemOptions();
        semCombo.setBackground(Color.WHITE);
        panel.add(semCombo, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.35;
        semStatusLabel = new JLabel("Status:");
        panel.add(semStatusLabel, gbc);
        gbc.gridx = 1;
        semStatusCombo = new JComboBox<>(new String[] { "Completed", "Ongoing" });
        semStatusCombo.setBackground(Color.WHITE);
        panel.add(semStatusCombo, gbc);

        sgpaMainLabel = new JLabel("SGPA (0.0 – 10.0) *:");
        sgpaField = (JTextField) addFormRowGenericWithLabel(panel, gbc, 4, sgpaMainLabel, new JTextField(20));
        creditsField = (JTextField) addFormRowGeneric(panel, gbc, 5, "Credits Completed *:", new JTextField(20));
        attendanceField = (JTextField) addFormRowGeneric(panel, gbc, 6, "Attendance % (0–100) *:", new JTextField(20));

        // Conditional fields for Sem 3
        cgpa1stYearLabel = new JLabel("CGPA 1st Year:");
        cgpa1stYearField = new JTextField(20);
        sgpa3rdSemLabel = new JLabel("SGPA 3rd Semester:");
        sgpa3rdSemField = new JTextField(20);

        cgpa1stYearLabel.setVisible(false);
        cgpa1stYearField.setVisible(false);
        sgpa3rdSemLabel.setVisible(false);
        sgpa3rdSemField.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.35;
        panel.add(cgpa1stYearLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        styleTextField(cgpa1stYearField);
        panel.add(cgpa1stYearField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.35;
        panel.add(sgpa3rdSemLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        styleTextField(sgpa3rdSemField);
        panel.add(sgpa3rdSemField, gbc);

        yearCombo.addActionListener(e -> {
            updateSemOptions();
            updateAcademicLabels();
        });

        semCombo.addActionListener(e -> {
            updateAcademicLabels();
            boolean isSem3 = Integer.valueOf(3).equals(semCombo.getSelectedItem());
            cgpa1stYearLabel.setVisible(isSem3);
            cgpa1stYearField.setVisible(isSem3);
            sgpa3rdSemLabel.setVisible(isSem3);
            sgpa3rdSemField.setVisible(isSem3);
            panel.revalidate();
            panel.repaint();
        });

        semStatusCombo.addActionListener(e -> updateAcademicLabels());

        // Conduct Violation checkbox
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.3;
        JLabel condLabel = new JLabel("Conduct Violation?:");
        condLabel.setFont(LABEL_FONT);
        panel.add(condLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 0.7;
        conductCheckBox = new JCheckBox();
        conductCheckBox.setBackground(PANEL_BG);
        conductCheckBox.setFont(LABEL_FONT);
        panel.add(conductCheckBox, gbc);

        // Conduct Type (hidden by default)
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.weightx = 0.3;
        conductTypeLabel = new JLabel("Conduct Type:");
        conductTypeLabel.setFont(LABEL_FONT);
        conductTypeLabel.setVisible(false);
        panel.add(conductTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.weightx = 0.7;
        conductTypeCombo = new JComboBox<>(new String[] { "Reward", "Urgent", "Warning" });
        conductTypeCombo.setFont(LABEL_FONT);
        conductTypeCombo.setVisible(false);
        conductTypeCombo.setBackground(Color.WHITE);
        panel.add(conductTypeCombo, gbc);

        conductCheckBox.addActionListener(e -> {
            boolean checked = conductCheckBox.isSelected();
            conductTypeLabel.setVisible(checked);
            conductTypeCombo.setVisible(checked);
            panel.revalidate();
            panel.repaint();
        });

        return panel;
    }

    // ======================================================
    // Event handlers
    // ======================================================

    private void handleCheckEligibility() {
        // Validate personal fields
        String fullName = fullNameField.getText().trim();
        if (fullName.isEmpty()) {
            showError("Full Name is required.");
            return;
        }

        // Validate and parse SGPA
        double sgpa;
        String sgpaText = sgpaField.getText().trim();
        try {
            sgpa = Double.parseDouble(sgpaText);
            if (sgpa < 0) {
                showError("SGPA cannot be negative.");
                return;
            }
            // ACAD_009 cap handled inside engine, but warn user
            if (sgpa > 10.0) {
                JOptionPane.showMessageDialog(this,
                        "SGPA " + sgpa + " exceeds maximum. It will be capped at 10.0 (ACAD_009).",
                        "Out-of-Bounds SGPA", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            // ACAD_008: empty or zero SGPA
            if (sgpaText.isEmpty()) {
                sgpa = 0.0;
                JOptionPane.showMessageDialog(this,
                        "SGPA not entered. Default value 0.0 will be assigned (ACAD_008).",
                        "Missing SGPA", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("SGPA must be a valid decimal number (e.g. 8.75).");
                return;
            }
        }

        // Validate credits
        int credits;
        try {
            credits = Integer.parseInt(creditsField.getText().trim());
            if (credits < 0) {
                showError("Credits cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Credits Completed must be a whole number.");
            return;
        }

        // Validate attendance
        double attendance;
        try {
            attendance = Double.parseDouble(attendanceField.getText().trim());
            if (attendance < 0 || attendance > 100) {
                showError("Attendance % must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Attendance % must be a valid number.");
            return;
        }

        // Validate date of birth (simple YYYY-MM-DD check)
        String dob = dobField.getText().trim();
        if (!dob.isEmpty() && !dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
            showError("Date of Birth must be in YYYY-MM-DD format (e.g., 2005-05-15).");
            return;
        }

        // Conditional fields validation if Semester 3
        double cgpa1stYear = 0;
        double sgpa3rdSem = 0;
        if (Integer.valueOf(3).equals(semCombo.getSelectedItem())) {
            try {
                cgpa1stYear = Double.parseDouble(cgpa1stYearField.getText().trim());
                sgpa3rdSem = Double.parseDouble(sgpa3rdSemField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("CGPA 1st Year and SGPA 3rd Sem must be valid numbers.");
                return;
            }
        }

        // Build Student object
        Student student = new Student();
        student.setUserId(loggedInUser.getId());
        student.setFullName(fullName);
        student.setParentName(parentNameField.getText().trim());
        student.setDateOfBirth(dob);
        student.setPhone(phoneField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setAddress(addressArea.getText().trim());
        student.setSgpa(sgpa);
        student.setCredits(credits);
        student.setAttendancePercent(attendance);
        student.setConductViolation(conductCheckBox.isSelected());
        student.setConductType(conductCheckBox.isSelected()
                ? (String) conductTypeCombo.getSelectedItem()
                : "");
        student.setStream((String) streamCombo.getSelectedItem());
        student.setYear((Integer) yearCombo.getSelectedItem());
        student.setSemester((Integer) semCombo.getSelectedItem());
        student.setSemStatus((String) semStatusCombo.getSelectedItem());
        student.setCgpaFirstYear(cgpa1stYear);
        student.setSgpaThirdSem(sgpa3rdSem);

        // Save to DB
        int studentId = studentDAO.saveStudent(student);
        if (studentId == -1) {
            showError("Failed to save student record to database.");
            return;
        }
        student.setId(studentId);

        // Run decision engine
        List<String[]> results = engine.evaluate(student);

        // Save results
        studentDAO.saveResults(studentId, results);

        // Open Results Frame
        new EligibilityResultFrame(student, results, this).setVisible(true);
        setVisible(false);
    }

    private void clearForm() {
        fullNameField.setText("");
        parentNameField.setText("");
        dobField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        sgpaField.setText("");
        creditsField.setText("");
        attendanceField.setText("");
        streamCombo.setSelectedIndex(0);
        yearCombo.setSelectedIndex(0);
        semCombo.setSelectedIndex(0);
        cgpa1stYearField.setText("");
        sgpa3rdSemField.setText("");
        cgpa1stYearLabel.setVisible(false);
        cgpa1stYearField.setVisible(false);
        sgpa3rdSemLabel.setVisible(false);
        sgpa3rdSemField.setVisible(false);
        conductCheckBox.setSelected(false);
        conductTypeCombo.setVisible(false);
        conductTypeLabel.setVisible(false);
        conductTypeCombo.setSelectedIndex(0);
    }

    // ======================================================
    // Helper methods
    // ======================================================

    private JTextField addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        styleTextField(field);
        panel.add(field, gbc);
        return field;
    }

    private JComponent addFormRowGeneric(JPanel panel, GridBagConstraints gbc, int row, String label,
            JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(field, gbc);
        return field;
    }

    private void restrictPhoneField(JTextField f) {
        ((AbstractDocument) f.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = current.substring(0, offset) + text + current.substring(offset + length);
                if (next.length() <= 10 && next.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private JComponent addFormRowGenericWithLabel(JPanel panel, GridBagConstraints gbc, int row, JLabel lbl,
            JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        panel.add(field, gbc);
        return field;
    }

    private void styleTextField(JTextField f) {
        f.setFont(LABEL_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    private void updateSemOptions() {
        int year = (Integer) yearCombo.getSelectedItem();
        semCombo.removeAllItems();
        semCombo.addItem(year * 2 - 1);
        semCombo.addItem(year * 2);
    }

    private void updateAcademicLabels() {
        Object semObj = semCombo.getSelectedItem();
        Object yearObj = yearCombo.getSelectedItem();
        if (semObj == null || yearObj == null)
            return;

        int sem = (Integer) semObj;
        int year = (Integer) yearObj;
        String status = (String) semStatusCombo.getSelectedItem();

        // Reveal/Hide status for even semesters
        boolean isEven = (sem % 2 == 0);
        semStatusLabel.setVisible(isEven);
        semStatusCombo.setVisible(isEven);

        // Reset visibility
        cgpa1stYearLabel.setVisible(false);
        cgpa1stYearField.setVisible(false);
        sgpa3rdSemLabel.setVisible(false);
        sgpa3rdSemField.setVisible(false);

        // Refined Logic
        if (year == 1) {
            if (sem == 2) {
                cgpa1stYearLabel.setVisible(true);
                cgpa1stYearField.setVisible(true);
                if ("Ongoing".equals(status)) {
                    cgpa1stYearLabel.setText("1st Sem SGPA:");
                    sgpaMainLabel.setText("2nd Sem SGPA (Expected):");
                } else {
                    cgpa1stYearLabel.setText("1st Year CGPA:");
                    sgpaMainLabel.setText("SGPA (0.0 - 10.0) *:");
                }
            } else {
                sgpaMainLabel.setText("1st Sem SGPA:");
            }
        } else if (year == 2 && sem == 3) {
            cgpa1stYearLabel.setVisible(true);
            cgpa1stYearField.setVisible(true);
            cgpa1stYearLabel.setText("CGPA 1st Year:");
            sgpaMainLabel.setText("3rd Sem SGPA:");
        } else if (year == 2 && sem == 4) {
            cgpa1stYearLabel.setVisible(true);
            cgpa1stYearField.setVisible(true);
            sgpa3rdSemLabel.setVisible(true);
            sgpa3rdSemField.setVisible(true);
            cgpa1stYearLabel.setText("CGPA 1st Year:");
            sgpa3rdSemLabel.setText("3rd Sem SGPA:");
            sgpaMainLabel.setText(status.equals("Ongoing") ? "4th Sem SGPA (Exp):" : "4th Sem SGPA:");
        } else {
            sgpaMainLabel.setText("SGPA (0.0 - 10.0) *:");
        }

        revalidate();
        repaint();
    }

    private TitledBorder createSectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                title, TitledBorder.LEFT, TitledBorder.TOP, SECTION_FONT, PRIMARY_COLOR);
        return border;
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

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}
