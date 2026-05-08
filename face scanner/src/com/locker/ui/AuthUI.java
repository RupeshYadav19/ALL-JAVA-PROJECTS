package com.locker.ui;

import com.locker.db.DatabaseManager;
import com.locker.util.FaceRecognitionHelper;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class AuthUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private FaceRecognitionHelper faceHelper;

    public AuthUI() {
        setTitle("Face Locker - Secure Diary");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        faceHelper = new FaceRecognitionHelper();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createAuthPanel(true), "LOGIN");
        mainPanel.add(createAuthPanel(false), "SIGNUP");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createAuthPanel(boolean isLogin) {
        JPanel panel = new GradientPanel();
        panel.setLayout(null);

        JPanel glassPanel = new RoundedPanel(30, new Color(255, 255, 255, 220));
        glassPanel.setBounds(300, 100, 400, 500);
        glassPanel.setLayout(null);

        JLabel titleLbl = new JLabel(isLogin ? "Welcome Back" : "Create Account", JLabel.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLbl.setForeground(new Color(33, 33, 33));
        titleLbl.setBounds(0, 40, 400, 40);
        glassPanel.add(titleLbl);

        JTextField userTxt = new JTextField();
        userTxt.setBorder(BorderFactory.createTitledBorder("Username"));
        userTxt.setBounds(50, 120, 300, 50);
        glassPanel.add(userTxt);

        JPasswordField passTxt = new JPasswordField();
        passTxt.setBorder(BorderFactory.createTitledBorder("Password"));
        passTxt.setBounds(50, 200, 300, 50);
        glassPanel.add(passTxt);

        JButton actionBtn = new JButton(isLogin ? "NEXT" : "SIGN UP");
        actionBtn.setBounds(50, 300, 300, 50);
        actionBtn.setBackground(new Color(66, 133, 244));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionBtn.setFocusPainted(false);
        glassPanel.add(actionBtn);

        JButton toggleBtn = new JButton(isLogin ? "Don't have an account? Sign Up" : "Already have an account? Login");
        toggleBtn.setBounds(50, 370, 300, 30);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setForeground(new Color(66, 133, 244));
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        glassPanel.add(toggleBtn);

        toggleBtn.addActionListener(e -> cardLayout.show(mainPanel, isLogin ? "SIGNUP" : "LOGIN"));

        actionBtn.addActionListener(e -> {
            String user = userTxt.getText();
            String pass = new String(passTxt.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            if (isLogin) {
                int userId = DatabaseManager.loginUser(user, pass);
                if (userId != -1) {
                    // Password correct, now Verify Face
                    FaceScannerUI scanner = new FaceScannerUI(this, faceHelper);
                    scanner.setVisible(true);
                    Mat currentFace = scanner.getCapturedFace();
                    if (currentFace != null) {
                        byte[] storedFaceBytes = DatabaseManager.getUserFaceData(userId);
                        Mat storedFace = faceHelper.bytesToMat(storedFaceBytes);
                        double score = faceHelper.compareFaces(currentFace, storedFace);

                        if (score > 0.75) { // Stricter threshold (was 0.5)
                            JOptionPane.showMessageDialog(this, "Face Verified! Welcome.");
                            this.dispose();
                            new com.locker.ui.DiaryUI(userId);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Face recognition failed! (Score: " + String.format("%.2f", score) + ")");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!");
                }
            } else {
                // Signup
                FaceScannerUI scanner = new FaceScannerUI(this, faceHelper);
                scanner.setVisible(true);
                Mat face = scanner.getCapturedFace();
                if (face != null) {
                    byte[] faceData = faceHelper.matToBytes(face);
                    if (DatabaseManager.registerUser(user, pass, faceData)) {
                        JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
                        cardLayout.show(mainPanel, "LOGIN");
                    } else {
                        JOptionPane.showMessageDialog(this, "Registration Failed! Username might exist.");
                    }
                }
            }
        });

        panel.add(glassPanel);
        return panel;
    }

    // Custom UI Components
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            Color color1 = new Color(18, 113, 255);
            Color color2 = new Color(203, 0, 255);
            GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(backgroundColor);
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        }
    }
}
