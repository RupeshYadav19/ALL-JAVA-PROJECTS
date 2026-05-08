package com.pharmacypro.ui.dialogs;

import com.pharmacypro.dao.ProductDAO;
import com.pharmacypro.models.Product;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ProductDialog extends JDialog {
    private JTextField nameField, mfgField, compField, hsnField, mrpField, packField, gstField, expiryField;
    private JCheckBox scheduleHCheck;

    public ProductDialog(Frame owner) {
        super(owner, "Product Details", true);
        setSize(500, 450);
        setLayout(new BorderLayout());
        setLocationRelativeTo(owner);
        
        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        nameField = new JTextField();
        mfgField = new JTextField();
        compField = new JTextField();
        hsnField = new JTextField();
        mrpField = new JTextField();
        packField = new JTextField("1");
        gstField = new JTextField("12.0");
        expiryField = new JTextField();
        scheduleHCheck = new JCheckBox("Is Schedule H");

        form.add(new JLabel("Product Name*:")); form.add(nameField);
        form.add(new JLabel("Manufacturer:")); form.add(mfgField);
        form.add(new JLabel("Composition:")); form.add(compField);
        form.add(new JLabel("HSN Code:")); form.add(hsnField);
        form.add(new JLabel("Default MRP:")); form.add(mrpField);
        form.add(new JLabel("Pack Size:")); form.add(packField);
        form.add(new JLabel("GST %:")); form.add(gstField);
        form.add(new JLabel("Expiry (YYYY-MM-DD):")); form.add(expiryField);
        form.add(new JLabel("Options:")); form.add(scheduleHCheck);
        
        add(form, BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> saveProduct());
        cancelBtn.addActionListener(e -> dispose());
        
        footer.add(saveBtn);
        footer.add(cancelBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void saveProduct() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product p = new Product();
        p.setName(name);
        p.setManufacturer(mfgField.getText().trim());
        p.setComposition(compField.getText().trim());
        p.setHsnCode(hsnField.getText().trim());
        p.setScheduleH(scheduleHCheck.isSelected());
        
        String expiryStr = expiryField.getText().trim();
        if(!expiryStr.isEmpty()) {
            try {
                p.setExpiryDate(java.time.LocalDate.parse(expiryStr));
            } catch(Exception dateEx) {
                JOptionPane.showMessageDialog(this, "Expiry date must be in YYYY-MM-DD format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        try {
            String mrpStr = mrpField.getText().trim();
            p.setDefaultMrp(mrpStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(mrpStr));
            
            p.setPackSize(Integer.parseInt(packField.getText().trim()));
            
            String gstStr = gstField.getText().trim();
            BigDecimal gst = gstStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(gstStr);
            p.setGstPercent(gst);
            p.setCgst(gst.divide(new BigDecimal(2)));
            p.setSgst(gst.divide(new BigDecimal(2)));

            new ProductDAO().addProduct(p);
            JOptionPane.showMessageDialog(this, "Product saved successfully!");
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for MRP, Pack Size, or GST!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving product: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
