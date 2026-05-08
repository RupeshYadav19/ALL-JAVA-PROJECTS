package Hotel.Management.System;

import javax.swing.*;
import java.awt.*;

public class Splash extends JFrame {
    Splash() {
        getContentPane().setBackground(new Color(245, 247, 250));
        JLabel label = new JLabel("WELCOME TO HOTEL MANAGEMENT SYSTEM");
        label.setBounds(100, 200, 700, 50);
        label.setFont(new Font("Tahoma", Font.BOLD, 30));
        label.setForeground(new Color(33, 37, 41));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);

        setLayout(null);
        setLocation(300, 80);
        setSize(858, 680);
        setVisible(true);

        try {
            Thread.sleep(5000);
            new Login();
            setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Splash();
    }
}