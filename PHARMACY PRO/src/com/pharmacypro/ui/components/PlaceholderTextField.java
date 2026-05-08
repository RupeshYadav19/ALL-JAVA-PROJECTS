package com.pharmacypro.ui.components;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PlaceholderTextField extends JTextField {
    private String placeholder;
    private Color placeholderColor = Color.LIGHT_GRAY;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight() - 1);
        
        // draw bottom underline
        g2.setColor(Color.GRAY);
        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        
        super.paintComponent(g);

        if (getText().isEmpty() && !isFocusOwner() && placeholder != null) {
            g2.setColor(placeholderColor);
            g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
        }
        g2.dispose();
    }
}
