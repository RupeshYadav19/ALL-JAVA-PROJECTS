package com.pharmacypro.ui.components;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius;
    private Color borderColor;

    public RoundedPanel(Color bgColor, int radius) {
        this(bgColor, radius, null);
    }

    public RoundedPanel(Color bgColor, int radius, Color borderColor) {
        setOpaque(false);
        this.backgroundColor = bgColor;
        this.cornerRadius = radius;
        this.borderColor = borderColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundColor != null) {
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
