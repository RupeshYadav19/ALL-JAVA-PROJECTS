package com.pharmacypro.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchableTable extends JPanel {
    private JTable table;
    private PlaceholderTextField filterField;
    private TableRowSorter<DefaultTableModel> sorter;

    public SearchableTable(DefaultTableModel model) {
        setLayout(new BorderLayout());

        filterField = new PlaceholderTextField("Filter records...");
        add(filterField, BorderLayout.NORTH);

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = filterField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
    }

    public JTable getTable() {
        return table;
    }
}
