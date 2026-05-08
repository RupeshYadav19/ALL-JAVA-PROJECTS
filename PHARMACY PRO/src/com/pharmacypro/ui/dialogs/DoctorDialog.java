package com.pharmacypro.ui.dialogs;

import com.pharmacypro.dao.DoctorDAO;
import com.pharmacypro.models.Doctor;
import javax.swing.*;
import java.awt.*;

public class DoctorDialog extends JDialog {
    private JTextField nameField, mobileField, emailField, specializationField, addressField;

    public DoctorDialog(Frame owner) {
        super(owner, "Doctor Details", true);
        setSize(500, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(owner);
        
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        nameField = new JTextField();
        mobileField = new JTextField();
        emailField = new JTextField();
        specializationField = new JTextField();
        addressField = new JTextField();

        form.add(new JLabel("Name*:")); form.add(nameField);
        form.add(new JLabel("Mobile (+Code):")); form.add(mobileField);
        form.add(new JLabel("Email (@gmail):")); form.add(emailField);
        form.add(new JLabel("Specialization:")); form.add(specializationField);
        form.add(new JLabel("Address:")); form.add(addressField);
        
        add(form, BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> saveDoctor());
        cancelBtn.addActionListener(e -> dispose());
        
        footer.add(saveBtn);
        footer.add(cancelBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void saveDoctor() {
        String name = nameField.getText().trim();
        String mobile = mobileField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!mobile.isEmpty() && !mobile.matches("^\\+\\d{1,3}\\s?\\d{10}$")) {
            JOptionPane.showMessageDialog(this, "Mobile number must be exactly 10 digits with country code (e.g., +91 9876543210)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.isEmpty() && !email.endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Email must be a valid @gmail.com address", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Doctor d = new Doctor();
        d.setName(name);
        d.setMobile(mobile);
        d.setEmail(email);
        d.setSpecialization(specializationField.getText().trim());
        d.setAddress(addressField.getText().trim());

        try {
            new DoctorDAO().addDoctor(d);
            JOptionPane.showMessageDialog(this, "Doctor saved successfully!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving doctor: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
