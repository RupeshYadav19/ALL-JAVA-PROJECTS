package com.pharmacypro.ui.components;

import com.pharmacypro.utils.AppColors;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

public class ExpiryAlertsWidget extends JPanel {
    private int alertCount = 0;

    public ExpiryAlertsWidget(int count) {
        this.alertCount = count;
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // In a real app this opens Expiry Alert report
                JOptionPane.showMessageDialog(ExpiryAlertsWidget.this, "Opening Expiry Alerts");
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Orange background
        g2.setColor(AppColors.ORANGE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        // Draw calendar icon on left
        g2.setColor(Color.WHITE);
        g2.drawRect(20, 15, 20, 20);
        g2.fillRect(24, 10, 4, 8);
        g2.fillRect(32, 10, 4, 8);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString("31", 24, 30);

        // Draw text
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("Expiry Alerts", 60, 25);

        // Draw badge if count > 0
        if (alertCount > 0) {
            g2.setColor(Color.RED);
            g2.fillOval(getWidth() - 30, 15, 20, 20);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(String.valueOf(alertCount), getWidth() - 24, 30);
        }

        g2.dispose();
    }
}
