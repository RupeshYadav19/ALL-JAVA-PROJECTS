package com.pharmacypro.ui.components;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FontMetrics;

public class RoundedButton extends JButton {
    private Color bgColor;
    private Color fgColor;
    private int cornerRadius;
    private Color hoverColor;
    private Color pressColor;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public RoundedButton(String text, Color bgColor, Color fgColor, int radius) {
        super(text);
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.cornerRadius = radius;
        this.hoverColor = darken(bgColor, 0.15f);
        this.pressColor = darken(bgColor, 0.25f);

        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(fgColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            @Override
            public void mousePressed(MouseEvent e) { isPressed = true; repaint(); }
            @Override
            public void mouseReleased(MouseEvent e) { isPressed = false; repaint(); }
        });
    }

    private Color darken(Color color, float fraction) {
        int r = Math.max(0, (int) (color.getRed() * (1 - fraction)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - fraction)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - fraction)));
        return new Color(r, g, b);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color c = isPressed ? pressColor : (isHovered ? hoverColor : bgColor);
        g2.setColor(c);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        g2.setColor(fgColor);
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
