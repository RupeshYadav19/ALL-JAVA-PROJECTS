package com.pharmacypro.ui.panels;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import com.pharmacypro.ui.components.RoundedButton;
import com.pharmacypro.utils.AppColors;

public class MailsPanel extends JPanel {
    public MailsPanel() {
        setLayout(new BorderLayout());
        
        // Filters
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterRow.add(new JLabel("Date Filter:"));
        filterRow.add(new JFormattedTextField());
        filterRow.add(new JTextField(10)); // Sender
        filterRow.add(new JTextField(15)); // Subject
        add(filterRow, BorderLayout.NORTH);
        
        // Table
        String[] cols = {"Date", "Sender", "Subject", "Attachment"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Bottom Action Bar
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new RoundedButton("Alt O Columns", Color.DARK_GRAY, Color.WHITE, 10));
        
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(new RoundedButton("Alt P Process", AppColors.PRIMARY_PURPLE, Color.WHITE, 10));
        center.add(new RoundedButton("Del Delete", Color.RED, Color.WHITE, 10));
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(new JButton("Refresh"));
        
        bottom.add(left, BorderLayout.WEST);
        bottom.add(center, BorderLayout.CENTER);
        bottom.add(right, BorderLayout.EAST);
        
        add(bottom, BorderLayout.SOUTH);
    }
}
