package com.pharmacypro.ui.panels;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setLayout(new BorderLayout());
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(200);
        
        // Sidebar
        JList<String> list = new JList<>(new String[]{"Expiry Alert", "Short Book"});
        list.setBackground(Color.DARK_GRAY);
        list.setForeground(Color.WHITE);
        split.setLeftComponent(new JScrollPane(list));
        
        // Main view
        JPanel main = new JPanel(new BorderLayout());
        JPanel filterRow = new JPanel(new FlowLayout());
        filterRow.add(new JLabel("From:")); filterRow.add(new JTextField(10));
        filterRow.add(new JLabel("To:")); filterRow.add(new JTextField(10));
        filterRow.add(new JButton("Apply"));
        main.add(filterRow, BorderLayout.NORTH);
        
        JTable table = new JTable(new DefaultTableModel(new String[]{"Product", "Batch", "Expiry Date", "Days Left", "Stock", "MRP"}, 0));
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(new JButton("Print"));
        footer.add(new JButton("Export CSV"));
        main.add(footer, BorderLayout.SOUTH);
        
        split.setRightComponent(main);
        add(split, BorderLayout.CENTER);
    }
}
