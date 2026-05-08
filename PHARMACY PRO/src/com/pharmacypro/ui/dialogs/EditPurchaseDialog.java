package com.pharmacypro.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import com.pharmacypro.db.DBConnection;

public class EditPurchaseDialog extends JDialog {
    private JTextField amountField;
    private int purchaseId;

    public EditPurchaseDialog(Frame owner, int purchaseId, BigDecimal currentAmount) {
        super(owner, "Edit Purchase Amount - ID: " + purchaseId, true);
        this.purchaseId = purchaseId;
        setSize(300, 180);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(2, 1, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        amountField = new JTextField(currentAmount != null ? currentAmount.toString() : "0.00");
        
        form.add(new JLabel("Actual Purchase Amount (\u20B9):"));
        form.add(amountField);

        add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Update Amount");
        saveBtn.addActionListener(e -> updateAmount());
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void updateAmount() {
        try {
            BigDecimal amt = new BigDecimal(amountField.getText().trim());
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE purchase_bills SET total_amount = ? WHERE id = ?")) {
                
                pstmt.setBigDecimal(1, amt);
                pstmt.setInt(2, purchaseId);
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Purchase Amount Updated!");
                dispose();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
