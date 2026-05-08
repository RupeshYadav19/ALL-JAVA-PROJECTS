package com.pharmacypro.ui.panels;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import com.pharmacypro.db.DBConnection;

public class SalesOrdersPanel extends JPanel {
    public SalesOrdersPanel() {
        setLayout(new BorderLayout());
        
        // Table
        String[] cols = {"Invoice No", "Patient Name", "Doctor Name", "Medicines Issued", "Paid Amt \u20B9", "Balance \u20B9", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        
        loadData(model);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadData(model);
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Bottom action bar
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel leftActs = new JPanel(new FlowLayout());
        
        JButton btnEdit = new JButton("Edit Selected Bill");
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String inv = model.getValueAt(row, 0).toString();
                
                Object pObj = model.getValueAt(row, 4);
                java.math.BigDecimal paid = java.math.BigDecimal.ZERO;
                if(pObj != null) {
                    try { paid = new java.math.BigDecimal(pObj.toString()); } catch(Exception ex) {}
                }
                
                Object bObj = model.getValueAt(row, 5);
                java.math.BigDecimal bal = java.math.BigDecimal.ZERO;
                if(bObj != null) {
                    try { bal = new java.math.BigDecimal(bObj.toString()); } catch(Exception ex) {}
                }

                Window win = SwingUtilities.getWindowAncestor(this);
                if(win instanceof Frame) {
                    new com.pharmacypro.ui.dialogs.EditBillDialog((Frame)win, inv, paid, bal).setVisible(true);
                    loadData(model);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an invoice to edit.");
            }
        });
        
        leftActs.add(btnEdit);
        leftActs.add(new JButton("Ctrl P Print"));
        leftActs.add(new JButton("Ctrl R Reorder"));
        leftActs.add(new JButton("Del Delete"));
        leftActs.add(new JButton("Enter / Ctrl I Info"));
        bottom.add(leftActs, BorderLayout.WEST);
        
        JPanel rightActs = new JPanel(new FlowLayout());
        rightActs.add(new JButton("Alt F Filter"));
        bottom.add(rightActs, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }
    
    private void loadData(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT sb.invoice_no, p.name AS patient, d.name AS doctor, " +
                 "(SELECT GROUP_CONCAT(pr.name SEPARATOR ', ') FROM sales_bill_items sbi " +
                 "JOIN products pr ON sbi.product_id = pr.id WHERE sbi.bill_id = sb.id) AS medicines, " +
                 "sb.amount_paid, sb.balance, sb.bill_date " +
                 "FROM sales_bills sb " +
                 "LEFT JOIN patients p ON sb.patient_id = p.id " +
                 "LEFT JOIN doctors d ON sb.doctor_id = d.id " +
                 "ORDER BY sb.bill_date DESC")) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("invoice_no"),
                    rs.getString("patient") != null ? rs.getString("patient") : "Walk-in",
                    rs.getString("doctor") != null ? rs.getString("doctor") : "N/A",
                    rs.getString("medicines") != null ? rs.getString("medicines") : "",
                    rs.getBigDecimal("amount_paid"),
                    rs.getBigDecimal("balance"),
                    rs.getDate("bill_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
