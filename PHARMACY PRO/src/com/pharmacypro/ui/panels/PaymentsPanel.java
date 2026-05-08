package com.pharmacypro.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import com.pharmacypro.db.DBConnection;
import com.pharmacypro.utils.AppColors;

public class PaymentsPanel extends JPanel {
    private JPanel pnlSales;
    private JPanel pnlPurchases;

    public PaymentsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 20));
        center.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        center.setBackground(Color.WHITE);

        pnlSales = createMetricCard("TOTAL EARNED (SALES)", "₹ 0.00", AppColors.SUCCESS_GREEN);
        pnlPurchases = createMetricCard("TOTAL WASTED (PURCHASES)", "₹ 0.00", AppColors.EXPIRY_RED);

        center.add(pnlSales);
        center.add(pnlPurchases);

        add(new JLabel("  Pharmacy Analytics Dashboard", JLabel.LEFT), BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        loadData();

        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                loadData();
            }
        });
    }

    private void loadData() {
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalPurchases = BigDecimal.ZERO;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rsSales = stmt.executeQuery("SELECT SUM(amount_paid) FROM sales_bills");
            if (rsSales.next()) {
                totalSales = rsSales.getBigDecimal(1) != null ? rsSales.getBigDecimal(1) : BigDecimal.ZERO;
            }
            ResultSet rsPurchases = stmt.executeQuery("SELECT SUM(total_amount) FROM purchase_bills");
            if (rsPurchases.next()) {
                totalPurchases = rsPurchases.getBigDecimal(1) != null ? rsPurchases.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((JLabel) pnlSales.getComponent(1)).setText("₹ " + totalSales.toString());
        ((JLabel) pnlPurchases.getComponent(1)).setText("₹ " + totalPurchases.toString());
    }
    
    private JPanel createMetricCard(String title, String amount, Color bgColor) {
        JPanel jp = new JPanel(new GridLayout(2, 1));
        jp.setBackground(bgColor);
        jp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JLabel lblAmount = new JLabel(amount, SwingConstants.CENTER);
        lblAmount.setForeground(Color.WHITE);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 36));

        jp.add(lblTitle);
        jp.add(lblAmount);
        return jp;
    }
}
