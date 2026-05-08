package com.restaurant.main;

import com.restaurant.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class RestaurantApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
