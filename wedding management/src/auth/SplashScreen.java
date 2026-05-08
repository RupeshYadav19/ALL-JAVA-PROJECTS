package auth;

import utils.UIUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Animated splash screen with rose-gold WeddingGenie branding.
 * Shows for ~3 seconds with a progress bar, then opens LoginFrame.
 */
public class SplashScreen extends JWindow {
    private JProgressBar progressBar;
    private Timer timer;
    private int progress = 0;

    public SplashScreen() {
        setSize(640, 400);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Cream-to-rose-gold gradient
                GradientPaint gp = new GradientPaint(0, 0, UIUtils.CREAM, getWidth(), getHeight(), UIUtils.ROSE_GOLD);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        content.setOpaque(false);

        // ── Center panel ─────────────────────────────────────────────────────
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(50, 60, 0, 60));

        // Rose icon
        JLabel icon = new JLabel("🌸", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // App name
        JLabel appName = new JLabel("WeddingGenie", SwingConstants.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 42));
        appName.setForeground(UIUtils.DEEP_BROWN);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline
        JLabel tagline = new JLabel("Plan Your Perfect Indian Wedding", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        tagline.setForeground(new Color(0x7D, 0x60, 0x52));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Decorative divider
        JLabel divider = new JLabel("✦ ─────────────── ✦ ─────────────── ✦", SwingConstants.CENTER);
        divider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        divider.setForeground(UIUtils.ROSE_GOLD);
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(icon);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(appName);
        center.add(Box.createRigidArea(new Dimension(0, 6)));
        center.add(tagline);
        center.add(Box.createRigidArea(new Dimension(0, 16)));
        center.add(divider);

        // ── Bottom progress bar ───────────────────────────────────────────────
        JPanel bottom = new JPanel(new BorderLayout(0, 6));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 60, 30, 60));

        JLabel loading = new JLabel("Loading WeddingGenie…", SwingConstants.CENTER);
        loading.setFont(UIUtils.FONT_SMALL);
        loading.setForeground(UIUtils.DEEP_BROWN);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(0xE8, 0xD5, 0xBF));
        progressBar.setForeground(UIUtils.ROSE_GOLD);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(400, 6));

        bottom.add(loading, BorderLayout.NORTH);
        bottom.add(progressBar, BorderLayout.CENTER);

        // Border decoration
        JPanel dec = new JPanel(new BorderLayout());
        dec.setOpaque(false);
        dec.setBorder(BorderFactory.createLineBorder(new Color(0xC9, 0x95, 0x6C, 100), 3));

        content.add(center, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
        dec.add(content);
        getContentPane().add(dec);

        // ── Timer: ~3 seconds ─────────────────────────────────────────────────
        timer = new Timer(30, e -> {
            progress += 1;
            progressBar.setValue(progress);
            if (progress >= 100) {
                timer.stop();
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new LoginFrame().setVisible(true);
                });
            }
        });
        timer.setInitialDelay(200);
    }

    public void showSplash() {
        setVisible(true);
        timer.start();
    }
}
