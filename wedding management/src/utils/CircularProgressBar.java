package utils;

import javax.swing.*;
import java.awt.*;

/**
 * Circular progress arc drawn with Graphics2D.
 */
public class CircularProgressBar extends JPanel {
    private int value = 0;   // 0-100
    private String label = "";
    private Color arcColor = UIUtils.ROSE_GOLD;

    public CircularProgressBar(int size) {
        setPreferredSize(new Dimension(size, size));
        setOpaque(false);
    }

    public void setValue(int v, String lbl) {
        this.value = Math.min(100, Math.max(0, v));
        this.label = lbl;
        repaint();
    }

    public void setArcColor(Color c) { this.arcColor = c; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        int pad = 8, arc = (int)(value / 100.0 * 360);

        // Background circle
        g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(0xEE, 0xDD, 0xCC));
        g2.drawOval(pad, pad, w - 2 * pad, h - 2 * pad);

        // Progress arc
        g2.setColor(arcColor);
        g2.drawArc(pad, pad, w - 2 * pad, h - 2 * pad, 90, -arc);

        // Center text
        g2.setFont(UIUtils.FONT_SUBHEAD);
        g2.setColor(UIUtils.DEEP_BROWN);
        String pct = value + "%";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(pct, (w - fm.stringWidth(pct)) / 2, h / 2 + 2);

        if (!label.isEmpty()) {
            g2.setFont(UIUtils.FONT_SMALL);
            g2.setColor(Color.GRAY);
            g2.drawString(label, (w - g2.getFontMetrics().stringWidth(label)) / 2, h / 2 + 16);
        }
    }
}
