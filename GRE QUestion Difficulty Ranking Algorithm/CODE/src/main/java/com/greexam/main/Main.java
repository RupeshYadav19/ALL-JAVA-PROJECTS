package com.greexam.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.greexam.db.DBConnection;
import com.greexam.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set modern Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Optional: Customize FlatLaf here (colors, fonts, etc.)
            UIManager.put( "Button.arc", 10 );
            UIManager.put( "Component.arc", 10 );
            UIManager.put( "ProgressBar.arc", 10 );
            UIManager.put( "TextComponent.arc", 10 );
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // Initialize Database asynchronously to not block UI thread initially
        SwingUtilities.invokeLater(() -> {
            DBConnection.getInstance().initializeDatabase();
            
            // Open Login Frame
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
