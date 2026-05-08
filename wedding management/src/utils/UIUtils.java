package utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Central UI utilities — colors, fonts, and component factory for WeddingGenie.
 */
public class UIUtils {

    // ─── Color Palette ──────────────────────────────────────────────────────
    public static final Color ROSE_GOLD     = new Color(0xC9, 0x95, 0x6C); // #C9956C
    public static final Color CREAM         = new Color(0xFD, 0xEB, 0xD0); // #FDEBD0
    public static final Color DEEP_BROWN    = new Color(0x5D, 0x40, 0x37); // #5D4037
    public static final Color WHITE         = Color.WHITE;
    public static final Color SUCCESS       = new Color(0x27, 0xAE, 0x60); // green
    public static final Color WARNING       = new Color(0xF3, 0x9C, 0x12); // amber
    public static final Color DANGER        = new Color(0xE7, 0x4C, 0x3C); // red
    public static final Color LIGHT_GRAY    = new Color(0xF5, 0xF5, 0xF5);
    public static final Color SIDEBAR_BG    = new Color(0x4A, 0x31, 0x28);
    public static final Color SIDEBAR_HOVER = new Color(0xC9, 0x95, 0x6C);
    public static final Color CARD_BG       = new Color(0xFF, 0xFC, 0xF7);
    public static final Color ACCENT_LIGHT  = new Color(0xFB, 0xE4, 0xCA);

    // ─── Fonts ───────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,   24);
    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD,   18);
    public static final Font FONT_SUBHEAD  = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN,  11);
    public static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD,   13);
    public static final Font FONT_MONO     = new Font("Consolas",  Font.PLAIN,  12);

    // ─── Button Factory ───────────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(ROSE_GOLD);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color orig = btn.getBackground();
            public void mouseEntered(java.awt.event.MouseEvent e)  { btn.setBackground(DEEP_BROWN);  btn.setForeground(Color.WHITE); }
            public void mouseExited(java.awt.event.MouseEvent e)   { btn.setBackground(orig);        btn.setForeground(Color.BLACK); }
        });
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(WHITE);
        btn.setForeground(DEEP_BROWN);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(ROSE_GOLD, 1));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ROSE_GOLD, 1),
            BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT_LIGHT); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(WHITE); }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(0xC0, 0x39, 0x2B)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(DANGER); }
        });
        return btn;
    }

    public static JButton successButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(0x1E, 0x87, 0x49)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(SUCCESS); }
        });
        return btn;
    }

    // ─── Label Helpers ────────────────────────────────────────────────────────
    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(DEEP_BROWN);
        return lbl;
    }

    public static JLabel headingLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(DEEP_BROWN);
        return lbl;
    }

    public static JLabel bodyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(Color.DARK_GRAY);
        return lbl;
    }

    // ─── Field Factory ────────────────────────────────────────────────────────
    public static JTextField styledField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDD, 0xCC, 0xBB), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setBackground(WHITE);
        return field;
    }

    public static JPasswordField styledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDD, 0xCC, 0xBB), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setBackground(WHITE);
        return field;
    }

    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setBackground(WHITE);
        return combo;
    }

    // ─── Panel / Border ───────────────────────────────────────────────────────
    public static JPanel createCardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE8, 0xD5, 0xBF), 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        return p;
    }

    public static JPanel createHeaderBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(DEEP_BROWN);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(WHITE);
        bar.add(lbl, BorderLayout.WEST);
        return bar;
    }

    // ─── Table styling ────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.getTableHeader().setFont(FONT_SUBHEAD);
        table.getTableHeader().setBackground(ROSE_GOLD);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(ACCENT_LIGHT);
        table.setSelectionForeground(DEEP_BROWN);
        table.setGridColor(new Color(0xEE, 0xDD, 0xCC));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
    }

    // ─── Frame centering ──────────────────────────────────────────────────────
    public static void centerFrame(JFrame frame) {
        frame.setLocationRelativeTo(null);
    }

    public static void centerDialog(JDialog dlg) {
        dlg.setLocationRelativeTo(null);
    }

    // ─── Scrollpane ───────────────────────────────────────────────────────────
    public static JScrollPane scrollPane(Component comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xE0, 0xD0, 0xBF), 1));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // ─── Separator ───────────────────────────────────────────────────────────
    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xE8, 0xD5, 0xBF));
        return sep;
    }
}
