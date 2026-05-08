package utils;

import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Vendor card panel with hover elevation effect.
 */
public class VendorCard extends JPanel {
    private final String businessName;
    private final String category;
    private final String city;
    private final double rating;
    private final double startingPrice;
    private final boolean verified;
    private boolean hovered = false;

    public VendorCard(String businessName, String category, String city, double rating, double startingPrice, boolean verified) {
        this.businessName = businessName; this.category = category; this.city = city;
        this.rating = rating; this.startingPrice = startingPrice; this.verified = verified;
        setPreferredSize(new Dimension(220, 180));
        setBackground(UIUtils.CARD_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        if (hovered) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(4, 4, getWidth() - 2, getHeight() - 2, 14, 14);
        }

        // Card background
        g2.setColor(hovered ? UIUtils.ACCENT_LIGHT : UIUtils.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);

        // Top accent bar
        g2.setColor(UIUtils.ROSE_GOLD);
        g2.fillRoundRect(0, 0, getWidth() - 4, 5, 4, 4);

        int y = 22;
        // Business name
        g2.setFont(UIUtils.FONT_SUBHEAD);
        g2.setColor(UIUtils.DEEP_BROWN);
        g2.drawString(clamp(businessName, 22), 10, y); y += 20;

        // Category badge
        g2.setColor(UIUtils.ROSE_GOLD);
        g2.setFont(UIUtils.FONT_SMALL);
        g2.fillRoundRect(10, y, g2.getFontMetrics().stringWidth(category) + 12, 16, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawString(category, 16, y + 12); y += 26;

        // City
        g2.setColor(Color.GRAY);
        g2.setFont(UIUtils.FONT_SMALL);
        g2.drawString("📍 " + city, 10, y); y += 20;

        // Rating stars (text)
        g2.setColor(new Color(0xFF, 0xA0, 0x00));
        String stars = "★".repeat((int) Math.round(rating)) + "☆".repeat(5 - (int) Math.round(rating));
        g2.drawString(stars + " " + String.format("%.1f", rating), 10, y); y += 20;

        // Price
        g2.setColor(UIUtils.DEEP_BROWN);
        g2.setFont(UIUtils.FONT_BODY);
        g2.drawString("Starting ₹" + String.format("%,.0f", startingPrice), 10, y); y += 20;

        // Verified badge
        if (verified) {
            g2.setColor(UIUtils.SUCCESS);
            g2.setFont(UIUtils.FONT_SMALL);
            g2.drawString("✔ Verified", 10, y);
        }
    }

    private String clamp(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}
