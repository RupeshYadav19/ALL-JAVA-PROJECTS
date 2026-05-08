package utils;

import utils.UIUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Colored statistic card with big number and label.
 */
public class StatCard extends JPanel {
    private String number;
    private String label;
    private Color accentColor;
    private String icon;

    public StatCard(String icon, String number, String label, Color accentColor) {
        this.icon = icon; this.number = number; this.label = label; this.accentColor = accentColor;
        setPreferredSize(new Dimension(180, 110));
        setBackground(UIUtils.CARD_BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    public void update(String number, String label) {
        this.number = number; this.label = label; repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card background
        g2.setColor(UIUtils.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

        // Left accent strip
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 6, getHeight(), 4, 4);

        // Icon circle
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40));
        g2.fillOval(getWidth() - 54, 10, 44, 44);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        g2.setColor(accentColor);
        g2.drawString(icon, getWidth() - 44, 40);

        // Number
        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2.setColor(UIUtils.DEEP_BROWN);
        g2.drawString(number, 16, 54);

        // Label
        g2.setFont(UIUtils.FONT_SMALL);
        g2.setColor(Color.GRAY);
        g2.drawString(label, 16, 74);

        // Border
        g2.setColor(new Color(0xE8, 0xD5, 0xBF));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
    }
}
