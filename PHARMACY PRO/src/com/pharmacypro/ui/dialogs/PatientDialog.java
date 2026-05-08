package com.pharmacypro.ui.dialogs;

import com.pharmacypro.dao.PatientDAO;
import com.pharmacypro.models.Patient;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PatientDialog extends JDialog {
    private JTextField nameField, mobileField, emailField, addressField, dobField, identifierField;
    private JComboBox<String> genderCombo;

    public PatientDialog(Frame owner) {
        super(owner, "Patient Details", true);
        setSize(600, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(owner);
        
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        nameField = new JTextField();
        mobileField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();
        dobField = new JTextField();
        identifierField = new JTextField();
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        form.add(new JLabel("Name*:")); form.add(nameField);
        form.add(new JLabel("Mobile:")); form.add(mobileField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Address:")); form.add(addressField);
        form.add(new JLabel("Date of Birth (YYYY-MM-DD):")); form.add(dobField);
        form.add(new JLabel("Gender:")); form.add(genderCombo);
        form.add(new JLabel("Identifier:")); form.add(identifierField);
        
        add(form, BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> savePatient());
        cancelBtn.addActionListener(e -> dispose());
        
        footer.add(saveBtn);
        footer.add(cancelBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void savePatient() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Patient p = new Patient();
        p.setName(name);
        p.setMobile(mobileField.getText().trim());
        p.setEmail(emailField.getText().trim());
        p.setAddress(addressField.getText().trim());
        p.setIdentifier(identifierField.getText().trim());
        p.setGender((String) genderCombo.getSelectedItem());
        p.setOutstanding(BigDecimal.ZERO);

        try {
            String dobStr = dobField.getText().trim();
            if (!dobStr.isEmpty()) {
                p.setDateOfBirth(LocalDate.parse(dobStr));
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            new PatientDAO().addPatient(p);
            JOptionPane.showMessageDialog(this, "Patient saved successfully!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving patient: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
