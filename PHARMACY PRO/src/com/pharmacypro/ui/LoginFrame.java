package com.pharmacypro.ui;

import com.pharmacypro.utils.AppColors;
import com.pharmacypro.utils.AppFonts;
import com.pharmacypro.ui.components.RoundedButton;
import com.pharmacypro.ui.components.PlaceholderTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Pharmacy Pro Software");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        add(createLeftPanel(), BorderLayout.WEST);
        add(createRightPanel(), BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(420, 650));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(null);

        // Logo
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Overlapping circles for infinity
                g2.setColor(new Color(34, 197, 94, 200)); 
                g2.fillOval(0, 5, 30, 30);
                g2.setColor(new Color(249, 115, 22, 200));
                g2.fillOval(15, 5, 30, 30);

                // Text
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2.drawString("Pharmacy", 55, 28);

                // Badge
                g2.setColor(AppColors.SUCCESS_GREEN);
                g2.fillRoundRect(165, 10, 45, 20, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString("PRO", 175, 25);

                g2.dispose();
            }
        };
        logoPanel.setBounds(40, 40, 300, 50);
        logoPanel.setOpaque(false);
        leftPanel.add(logoPanel);

        // Form Fields
        PlaceholderTextField txtUser = new PlaceholderTextField("admin");
        txtUser.setBounds(40, 150, 340, 40);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        leftPanel.add(txtUser);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(40, 220, 300, 40);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtPass.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Custom simple bottom border for password field
        JPanel passPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.GRAY);
                g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        passPanel.setBounds(40, 220, 340, 40);
        passPanel.setBackground(Color.WHITE);
        passPanel.add(txtPass, BorderLayout.CENTER);
        
        JButton btnEye = new JButton("O"); // Eye icon placeholder
        btnEye.setBorderPainted(false);
        btnEye.setContentAreaFilled(false);
        btnEye.setFocusPainted(false);
        btnEye.addActionListener(e -> {
            if (txtPass.getEchoChar() != '\u0000') {
                txtPass.setEchoChar('\u0000');
            } else {
                txtPass.setEchoChar('•'); // Default or asterisk
            }
        });
        passPanel.add(btnEye, BorderLayout.EAST);
        leftPanel.add(passPanel);

        PlaceholderTextField txtRetailer = new PlaceholderTextField("RETAILER CODE");
        txtRetailer.setBounds(40, 290, 340, 40);
        txtRetailer.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        leftPanel.add(txtRetailer);

        JCheckBox chkTerms = new JCheckBox("<html>By clicking Login, you agree to the <font color='blue'><u>Terms of Use</u></font> of our software</html>");
        chkTerms.setBounds(40, 360, 340, 40);
        chkTerms.setBackground(Color.WHITE);
        chkTerms.setSelected(true);
        leftPanel.add(chkTerms);

        RoundedButton btnLogin = new RoundedButton("Login", AppColors.PRIMARY_PURPLE, Color.WHITE, 8);
        btnLogin.setBounds(40, 430, 120, 40);
        btnLogin.setFont(AppFonts.BUTTON);
        btnLogin.addActionListener(e -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            dispose();
        });
        leftPanel.add(btnLogin);

        RoundedButton btnEnter = new RoundedButton("Enter", Color.WHITE, AppColors.PRIMARY_PURPLE, 8) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(AppColors.PRIMARY_PURPLE);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            }
        };
        btnEnter.setBounds(180, 430, 120, 40);
        btnEnter.setFont(AppFonts.BUTTON);
        leftPanel.add(btnEnter);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, AppColors.LIGHT_PURPLE, getWidth(), getHeight(), AppColors.PRIMARY_PURPLE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Text
                g2.setColor(Color.WHITE);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.drawString("Smart Inventory and", 50, 80);
                
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                g2.drawString("Purchase", 50, 125);
                g2.drawString("Suggestions", 50, 165);

                // Purple underline
                g2.setColor(AppColors.DARK_PURPLE);
                g2.fillRect(50, 185, 120, 4);

                // Body text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.drawString("Increase your business potential by managing", 50, 230);
                g2.drawString("your inventory and purchases efficiently.", 50, 250);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString("Maximize your profits with automated technology.", 50, 290);

                // Draw pharmacy shelf
                int shelfX = 350;
                int shelfY = 200;
                g2.setColor(new Color(255, 255, 255, 100));
                for(int i=0; i<4; i++) {
                    g2.fillRect(shelfX, shelfY + (i*60), 250, 5); // Shelf plank
                    // Draw some boxes
                    g2.setColor(Color.RED); g2.fillRect(shelfX + 20, shelfY + (i*60) - 30, 20, 30);
                    g2.setColor(Color.GREEN); g2.fillRect(shelfX + 50, shelfY + (i*60) - 40, 30, 40);
                    g2.setColor(Color.YELLOW); g2.fillRect(shelfX + 90, shelfY + (i*60) - 25, 25, 25);
                    g2.setColor(Color.BLUE); g2.fillRect(shelfX + 130, shelfY + (i*60) - 35, 15, 35);
                    g2.setColor(new Color(255, 255, 255, 100)); // Reset
                }

                // Silhouette
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillOval(shelfX - 80, shelfY + 80, 40, 40); // Head
                g2.fillRoundRect(shelfX - 100, shelfY + 130, 80, 150, 40, 40); // Body

                g2.dispose();
            }
        };
        rightPanel.setLayout(null);

        // Bottom dots
        int dotY = 600;
        int dotX = 300;
        for (int i = 0; i < 3; i++) {
            JPanel dot = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getBackground() == Color.RED) {
                        g2.setColor(Color.RED);
                    } else {
                        g2.setColor(new Color(255,255,255,100));
                    }
                    g2.fillOval(0, 0, 10, 10);
                }
            };
            dot.setBounds(dotX + (i*20), dotY, 10, 10);
            dot.setOpaque(false);
            if(i==0) dot.setBackground(Color.RED);
            rightPanel.add(dot);
        }

        return rightPanel;
    }
}
