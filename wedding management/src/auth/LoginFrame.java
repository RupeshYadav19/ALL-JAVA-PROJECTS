package auth;

import dao.UserDAO;
import dao.VendorDAO;
import models.User;
import utils.UIUtils;
import utils.PasswordUtils;
import utils.ValidationUtils;
import admin.AdminDashboard;
import vendor.VendorDashboard;
import user.UserDashboard;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

/**
 * LoginFrame — 800x500, split left/right design.
 * Left: decorative rose-gold mandala panel.
 * Right: email + password form with role auto-detect.
 */
public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;

    public LoginFrame() {
        setTitle("WeddingGenie — Login");
        setSize(860, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.WHITE);

        // ── LEFT decorative panel ────────────────────────────────────────────
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UIUtils.DEEP_BROWN, getWidth(), getHeight(), new Color(0xC9, 0x60, 0x40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-60, -60, 250, 250);
                g2.fillOval(getWidth() - 120, getHeight() - 120, 200, 200);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(40, getHeight() - 180, 180, 180);
            }
        };
        leftPanel.setPreferredSize(new Dimension(310, 520));

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);

        JLabel icon = new JLabel("🌸", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 54));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("WeddingGenie");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tag = new JLabel("<html><center>Plan Your Perfect<br>Indian Wedding</center></html>");
        tag.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tag.setForeground(UIUtils.ACCENT_LIGHT);
        tag.setAlignmentX(Component.CENTER_ALIGNMENT);
        tag.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel mandala = new JLabel("✦ ─── 🪷 ─── ✦", SwingConstants.CENTER);
        mandala.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        mandala.setForeground(UIUtils.ROSE_GOLD);
        mandala.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("<html><center>Vendors · Events · Bookings<br>Budget · Guest Lists · E-Invites</center></html>");
        sub.setFont(UIUtils.FONT_SMALL);
        sub.setForeground(new Color(255, 255, 255, 160));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setHorizontalAlignment(SwingConstants.CENTER);

        leftContent.add(Box.createRigidArea(new Dimension(0, 40)));
        leftContent.add(icon);
        leftContent.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContent.add(title);
        leftContent.add(Box.createRigidArea(new Dimension(0, 8)));
        leftContent.add(tag);
        leftContent.add(Box.createRigidArea(new Dimension(0, 20)));
        leftContent.add(mandala);
        leftContent.add(Box.createRigidArea(new Dimension(0, 16)));
        leftContent.add(sub);
        leftPanel.add(leftContent);

        // ── RIGHT form panel ─────────────────────────────────────────────────
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UIUtils.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 50));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UIUtils.WHITE);

        JLabel heading = new JLabel("Welcome Back!");
        heading.setFont(UIUtils.FONT_TITLE);
        heading.setForeground(UIUtils.DEEP_BROWN);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub2 = new JLabel("Sign in to your account");
        sub2.setFont(UIUtils.FONT_BODY);
        sub2.setForeground(Color.GRAY);
        sub2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Email
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(UIUtils.FONT_SUBHEAD);
        emailLbl.setForeground(UIUtils.DEEP_BROWN);
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField = UIUtils.styledField(28);
        emailField.setMaximumSize(new Dimension(340, 38));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UIUtils.FONT_SUBHEAD);
        passLbl.setForeground(UIUtils.DEEP_BROWN);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel passRow = new JPanel(new BorderLayout(4, 0));
        passRow.setBackground(UIUtils.WHITE);
        passRow.setMaximumSize(new Dimension(340, 38));
        passRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UIUtils.styledPasswordField(28);
        JButton eyeBtn = new JButton("👁");
        eyeBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        eyeBtn.setPreferredSize(new Dimension(36, 36));
        eyeBtn.setFocusPainted(false);
        eyeBtn.setBorder(BorderFactory.createLineBorder(new Color(0xDD, 0xCC, 0xBB)));
        eyeBtn.setBackground(UIUtils.WHITE);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.addActionListener(e -> {
            if (passwordField.getEchoChar() == 0) { passwordField.setEchoChar('●'); eyeBtn.setText("👁"); }
            else { passwordField.setEchoChar((char) 0); eyeBtn.setText("🙈"); }
        });
        passRow.add(passwordField, BorderLayout.CENTER);
        passRow.add(eyeBtn, BorderLayout.EAST);

        // Remember me + forgot
        JPanel optRow = new JPanel(new BorderLayout());
        optRow.setBackground(UIUtils.WHITE);
        optRow.setMaximumSize(new Dimension(340, 30));
        optRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        rememberMe = new JCheckBox("Remember Me");
        rememberMe.setFont(UIUtils.FONT_SMALL);
        rememberMe.setBackground(UIUtils.WHITE);
        JButton forgot = new JButton("<html><u>Forgot Password?</u></html>");
        forgot.setFont(UIUtils.FONT_SMALL);
        forgot.setForeground(UIUtils.ROSE_GOLD);
        forgot.setBorderPainted(false); forgot.setContentAreaFilled(false);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.addActionListener(e -> forgotPassword());
        optRow.add(rememberMe, BorderLayout.WEST);
        optRow.add(forgot, BorderLayout.EAST);

        // Buttons
        JButton loginBtn = UIUtils.primaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(340, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());
        // Enter key
        getRootPane().setDefaultButton(loginBtn);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        loginBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "login");
        loginBtn.getActionMap().put("login", new AbstractAction() { public void actionPerformed(ActionEvent e) { doLogin(); } });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setBackground(UIUtils.WHITE);
        btnRow.setMaximumSize(new Dimension(340, 40));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton regBtn = UIUtils.secondaryButton("Register");
        regBtn.addActionListener(e -> { dispose(); new RegisterFrame().setVisible(true); });
        JButton guestBtn = UIUtils.secondaryButton("Continue as Guest");
        guestBtn.addActionListener(e -> openGuestMode());
        btnRow.add(regBtn);
        btnRow.add(Box.createRigidArea(new Dimension(8, 0)));
        btnRow.add(guestBtn);

        form.add(heading); form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(sub2);    form.add(Box.createRigidArea(new Dimension(0, 22)));
        form.add(emailLbl); form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(emailField); form.add(Box.createRigidArea(new Dimension(0, 14)));
        form.add(passLbl); form.add(Box.createRigidArea(new Dimension(0, 5)));
        form.add(passRow); form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(optRow);  form.add(Box.createRigidArea(new Dimension(0, 18)));
        form.add(loginBtn); form.add(Box.createRigidArea(new Dimension(0, 12)));
        form.add(btnRow);

        rightPanel.add(form);
        root.add(leftPanel, BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            UserDAO dao = new UserDAO();
            User user = dao.login(email, PasswordUtils.hash(pass));
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials or account inactive.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
            switch (user.getRole()) {
                case "admin"  -> new AdminDashboard(user).setVisible(true);
                case "vendor" -> new VendorDashboard(user).setVisible(true);
                default       -> new UserDashboard(user).setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void forgotPassword() {
        String email = JOptionPane.showInputDialog(this, "Enter your registered email address:", "Forgot Password", JOptionPane.QUESTION_MESSAGE);
        if (email != null && !email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "If " + email + " is registered, a reset link will be sent.\n(Feature requires email server config)", "Password Reset", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openGuestMode() {
        User guest = new User();
        guest.setUserId(-1);
        guest.setFullName("Guest");
        guest.setRole("user");
        dispose();
        new UserDashboard(guest).setVisible(true);
    }
}
