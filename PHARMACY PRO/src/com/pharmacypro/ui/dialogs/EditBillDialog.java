package com.pharmacypro.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import com.pharmacypro.db.DBConnection;

public class EditBillDialog extends JDialog {
    private JTextField paidField;
    private JTextField balanceField;
    private JTextField modeField;
    private String invoiceNo;

    public EditBillDialog(Frame owner, String invoiceNo, BigDecimal paidAmt, BigDecimal balanceAmt) {
        super(owner, "Edit Financials - " + invoiceNo, true);
        this.invoiceNo = invoiceNo;
        setSize(300, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        paidField = new JTextField(paidAmt != null ? paidAmt.toString() : "0.0");
        balanceField = new JTextField(balanceAmt != null ? balanceAmt.toString() : "0.0");
        modeField = new JTextField("CASH");

        form.add(new JLabel("Amount Paid:")); form.add(paidField);
        form.add(new JLabel("Balance:")); form.add(balanceField);
        form.add(new JLabel("Payment Mode:")); form.add(modeField);

        add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Update Bill");
        saveBtn.addActionListener(e -> updateBill());
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void updateBill() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE sales_bills SET amount_paid = ?, balance = ?, payment_mode = ? WHERE invoice_no = ?")) {
            
            pstmt.setBigDecimal(1, new BigDecimal(paidField.getText().trim()));
            pstmt.setBigDecimal(2, new BigDecimal(balanceField.getText().trim()));
            pstmt.setString(3, modeField.getText().trim());
            pstmt.setString(4, invoiceNo);
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bill Updated Successfully!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating bill: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
