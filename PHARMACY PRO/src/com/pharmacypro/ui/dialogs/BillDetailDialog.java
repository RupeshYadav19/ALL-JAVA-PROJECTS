package com.pharmacypro.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class BillDetailDialog extends JDialog {
    public BillDetailDialog(Frame owner, int billId) {
        super(owner, "Bill Details", true);
        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Color.LIGHT_GRAY);
        header.add(new JLabel("Distributor: meher distributor | Invoice: 7479 | Total Amt: \u20B91,287.00"));
        add(header, BorderLayout.NORTH);
        
        // Details table
        String[] cols = {"Product", "Batch", "Exp", "MRP", "CP", "Disc \u20B9", "Qty", "Scheme", "Free", "Net GST"};
        JTable table = new JTable(new javax.swing.table.DefaultTableModel(cols, 0));
        add(new JScrollPane(table), BorderLayout.CENTER);
        
    }
}
