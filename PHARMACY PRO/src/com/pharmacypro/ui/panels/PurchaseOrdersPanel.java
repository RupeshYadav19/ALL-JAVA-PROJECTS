package com.pharmacypro.ui.panels;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.math.BigDecimal;
import com.pharmacypro.db.DBConnection;
import com.pharmacypro.ui.dialogs.EditPurchaseDialog;

public class PurchaseOrdersPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;

    public PurchaseOrdersPanel() {
        setLayout(new BorderLayout());
        
        // Filter Row (Simplified)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> loadData());
        topPanel.add(btnRefresh);
        add(topPanel, BorderLayout.NORTH);
        
        // Table
        String[] cols = {"ID", "Distributor", "Order Date", "Items Requested", "Purchase Amount (\u20B9)"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        JPanel actPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnEdit = new JButton("Edit Purchase Amount");
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                Object amtObj = model.getValueAt(row, 4);
                BigDecimal amt = BigDecimal.ZERO;
                if (amtObj != null) {
                    try {
                        amt = new BigDecimal(amtObj.toString());
                    } catch (Exception ex) {
                        amt = BigDecimal.ZERO;
                    }
                }
                Window win = SwingUtilities.getWindowAncestor(this);
                if (win instanceof Frame) {
                    new EditPurchaseDialog((Frame)win, id, amt).setVisible(true);
                    loadData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an order to edit.");
            }
        });
        
        actPanel.add(btnEdit);
        actPanel.add(new JButton("Print Manifest"));
        footer.add(actPanel, BorderLayout.WEST);
        
        add(footer, BorderLayout.SOUTH);

        // Auto-refresh when shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadData();
            }
        });

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        String sql = "SELECT pb.id, d.name AS distributor, pb.bill_date, " +
                     "(SELECT GROUP_CONCAT(CONCAT(pbi.source_product_name, ' (', pbi.quantity, ')') SEPARATOR ', ') " +
                     " FROM purchase_bill_items pbi WHERE pbi.purchase_id = pb.id) AS items, " +
                     "pb.total_amount " +
                     "FROM purchase_bills pb " +
                     "LEFT JOIN distributors d ON pb.distributor_id = d.id " +
                     "ORDER BY pb.id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("distributor") != null ? rs.getString("distributor") : "N/A",
                    rs.getDate("bill_date"),
                    rs.getString("items") != null ? rs.getString("items") : "None",
                    rs.getBigDecimal("total_amount")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
