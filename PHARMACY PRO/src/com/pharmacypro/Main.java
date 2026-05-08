package com.pharmacypro;

import com.pharmacypro.ui.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                try (java.sql.Connection c = com.pharmacypro.db.DBConnection.getConnection();
                     java.sql.Statement s = c.createStatement()) {
                    try { s.execute("ALTER TABLE doctors ADD COLUMN email VARCHAR(100)"); } catch(Exception ignored) {}
                    try { s.execute("ALTER TABLE sales_bills ADD COLUMN amount_paid DECIMAL(10,2) DEFAULT 0"); } catch(Exception ignored) {}
                    try { s.execute("ALTER TABLE sales_bills ADD COLUMN balance DECIMAL(10,2) DEFAULT 0"); } catch(Exception ignored) {}
                    try { s.execute("ALTER TABLE products ADD COLUMN expiry_date DATE"); } catch(Exception ignored) {}
                    try { s.execute("DELETE FROM purchase_bill_items"); } catch(Exception ignored) {}
                    try { s.execute("DELETE FROM purchase_bills"); } catch(Exception ignored) {}
                    try { s.execute("DELETE FROM sales_bill_items"); } catch(Exception ignored) {}
                    try { s.execute("DELETE FROM sales_bills"); } catch(Exception ignored) {}
                } catch(Exception ignored) {}
                
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("Panel.background", com.pharmacypro.utils.AppColors.WHITE);
                UIManager.put("Table.alternateRowColor", com.pharmacypro.utils.AppColors.TABLE_ALT_ROW);
                UIManager.put("Table.selectionBackground", com.pharmacypro.utils.AppColors.TABLE_SELECTED);
                UIManager.put("Table.selectionForeground", com.pharmacypro.utils.AppColors.BLACK);
                UIManager.put("TableHeader.background", com.pharmacypro.utils.AppColors.TABLE_ALT_ROW);
                UIManager.put("TableHeader.font", new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                UIManager.put("Table.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
                UIManager.put("TextField.margin", new java.awt.Insets(5, 10, 5, 10));
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
            new LoginFrame().setVisible(true);
        });
    }
}
