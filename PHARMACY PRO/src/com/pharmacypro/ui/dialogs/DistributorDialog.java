package com.pharmacypro.ui.dialogs;

import com.pharmacypro.dao.DistributorDAO;
import com.pharmacypro.models.Distributor;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DistributorDialog extends JDialog {
    private JTextField nameField, mobileField, emailField, addressField, gstField, drugLicenseField;

    public DistributorDialog(Frame owner) {
        super(owner, "Distributor Details", true);
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(owner);
        
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        nameField = new JTextField();
        mobileField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();
        gstField = new JTextField();
        drugLicenseField = new JTextField();

        form.add(new JLabel("Name*:")); form.add(nameField);
        form.add(new JLabel("Mobile:")); form.add(mobileField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Address:")); form.add(addressField);
        form.add(new JLabel("GST No:")); form.add(gstField);
        form.add(new JLabel("Drug License:")); form.add(drugLicenseField);
        
        add(form, BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> saveDistributor());
        cancelBtn.addActionListener(e -> dispose());
        
        footer.add(saveBtn);
        footer.add(cancelBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void saveDistributor() {
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

        Distributor d = new Distributor();
        d.setName(name);
        d.setMobile(mobile);
        d.setEmail(email);
        d.setAddress(addressField.getText().trim());
        d.setGstNo(gstField.getText().trim());
        d.setDrugLicense(drugLicenseField.getText().trim());
        d.setPendingAmount(BigDecimal.ZERO);
        d.setCreditCycleDays(30);

        try {
            new DistributorDAO().addDistributor(d);
            JOptionPane.showMessageDialog(this, "Distributor saved successfully!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving distributor: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
