package utils;

import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

// ─────────────────────────────────────────────────────────────────────────────
// StarRatingPanel — clickable or display-only 5-star rating
// ─────────────────────────────────────────────────────────────────────────────
public class StarRatingPanel extends JPanel {
    private int rating;
    private final boolean editable;
    private final int starSize;

    public StarRatingPanel(int initialRating, boolean editable) {
        this.rating = initialRating;
        this.editable = editable;
        this.starSize = editable ? 28 : 18;
        setOpaque(false);
        setPreferredSize(new Dimension(starSize * 5 + 8, starSize + 4));
        if (editable) {
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    int clicked = (int) Math.ceil((double) e.getX() / starSize);
                    if (clicked >= 1 && clicked <= 5) { rating = clicked; repaint(); }
                }
            });
        }
    }

    public int getRating() { return rating; }
    public void setRating(int r) { this.rating = r; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 1; i <= 5; i++) {
            g2.setColor(i <= rating ? new Color(0xFF, 0xC1, 0x07) : new Color(0xCC, 0xCC, 0xCC));
            drawStar(g2, (i - 1) * starSize + 2, 2, starSize - 4);
        }
    }

    private void drawStar(Graphics2D g2, int x, int y, int size) {
        int cx = x + size / 2, cy = y + size / 2;
        int r = size / 2;
        int[] xp = new int[10], yp = new int[10];
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + i * Math.PI / 5;
            int radius = (i % 2 == 0) ? r : r / 2;
            xp[i] = cx + (int) (radius * Math.cos(angle));
            yp[i] = cy - (int) (radius * Math.sin(angle));
        }
        g2.fillPolygon(xp, yp, 10);
    }
}
