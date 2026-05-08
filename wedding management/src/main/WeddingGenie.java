package main;

import auth.SplashScreen;
import javax.swing.*;

/**
 * WeddingGenie — Main entry point.
 * Sets Look & Feel, then shows the animated splash screen.
 */
public class WeddingGenie {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            // Nice font rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            new SplashScreen().showSplash();
        });
    }
}
