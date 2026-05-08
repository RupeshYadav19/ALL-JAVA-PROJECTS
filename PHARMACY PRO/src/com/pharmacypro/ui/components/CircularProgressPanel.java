package com.pharmacypro.ui.components;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

public class CircularProgressPanel extends JPanel {
    private int percent;

    public CircularProgressPanel(int percent) {
        this.percent = Math.min(100, Math.max(0, percent));
        setOpaque(false);
    }

    public void setPercent(int percent) {
        this.percent = Math.min(100, Math.max(0, percent));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diam = Math.min(getWidth(), getHeight()) - 20;
        int x = (getWidth() - diam) / 2;
        int y = (getHeight() - diam) / 2;

        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Background arc
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawArc(x, y, diam, diam, 0, 360);

        // Progress arc
        g2.setColor(new Color(30, 136, 229)); // #1E88E5
        int angle = -(int)(percent * 3.6);
        g2.drawArc(x, y, diam, diam, 90, angle);

        // Text
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String text = percent + "%";
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (diam - fm.stringWidth(text)) / 2;
        int ty = y + (diam - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);

        g2.dispose();
    }
}
