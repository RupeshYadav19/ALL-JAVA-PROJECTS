package auth;

import dao.UserDAO;
import dao.VendorDAO;
import dao.ChecklistDAO;
import models.User;
import models.Vendor;
import utils.UIUtils;
import utils.PasswordUtils;
import utils.ValidationUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

/**
 * RegisterFrame — JTabbedPane with "Register as Couple" and "Register as Vendor" tabs.
 * Includes password strength meter, field validation.
 */
public class RegisterFrame extends JFrame {
    // Shared
    private JProgressBar strengthBar;
    private JLabel strengthLabel;

    public RegisterFrame() {
        setTitle("WeddingGenie — Register");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.CREAM);

        // Header
        JPanel header = UIUtils.createHeaderBar("🌸  WeddingGenie — Create Account");
        root.add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.FONT_SUBHEAD);
        tabs.setBackground(UIUtils.CREAM);
        tabs.addTab("👫 Register as Couple", buildCouplePanel());
        tabs.addTab("🏪 Register as Vendor", buildVendorPanel());
        root.add(tabs, BorderLayout.CENTER);

        // Back to login
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(UIUtils.CREAM);
        JButton backBtn = UIUtils.secondaryButton("← Back to Login");
        backBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        bottom.add(backBtn);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JScrollPane buildCouplePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(8, 5, 2, 5);

        int row = 0;
        JTextField fullName = addField(p, gc, row++, "Full Name *", 30);
        JTextField partnerName = addField(p, gc, row++, "Partner's Name", 30);
        JTextField email = addField(p, gc, row++, "Email Address *", 30);
        JTextField phone = addField(p, gc, row++, "Phone Number *", 30);
        JTextField city = addField(p, gc, row++, "City", 30);
        JTextField weddingDate = addField(p, gc, row++, "Wedding Date (YYYY-MM-DD, optional)", 30);

        // Password
        gc.insets = new Insets(12, 5, 2, 5);
        gc.gridx = 0; gc.gridy = row * 2; gc.gridwidth = 2;
        JLabel p1l = new JLabel("Password *"); p1l.setFont(UIUtils.FONT_BODY);
        p.add(p1l, gc);
        gc.gridy = row * 2 + 1;
        JPasswordField pass1 = UIUtils.styledPasswordField(30);
        pass1.setMaximumSize(new Dimension(400, 36));
        stylePasswordListener(pass1);
        p.add(pass1, gc);
        row++;

        gc.gridy = row * 2;
        JLabel p2l = new JLabel("Confirm Password *"); p2l.setFont(UIUtils.FONT_BODY);
        p.add(p2l, gc);
        gc.gridy = row * 2 + 1;
        JPasswordField pass2 = UIUtils.styledPasswordField(30);
        p.add(pass2, gc);
        row++;

        // Strength bar
        gc.insets = new Insets(5, 5, 2, 5);
        gc.gridy = row * 2;
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setPreferredSize(new Dimension(400, 8));
        strengthBar.setBorderPainted(false);
        p.add(strengthBar, gc);
        gc.gridy = row * 2 + 1;
        strengthLabel = new JLabel("Password strength");
        strengthLabel.setFont(UIUtils.FONT_SMALL);
        p.add(strengthLabel, gc);
        row++;

        // Register button
        gc.insets = new Insets(20, 5, 10, 5);
        gc.gridy = row * 2; gc.gridwidth = 2;
        JButton regBtn = UIUtils.primaryButton("Create Couple Account");
        regBtn.setPreferredSize(new Dimension(400, 40));
        regBtn.addActionListener(e -> {
            if (!validate(fullName.getText(), email.getText(), phone.getText(), pass1, pass2)) return;
            try {
                User u = new User();
                u.setFullName(fullName.getText().trim() + (partnerName.getText().trim().isEmpty() ? "" : " & " + partnerName.getText().trim()));
                u.setEmail(email.getText().trim());
                u.setPasswordHash(PasswordUtils.hash(new String(pass1.getPassword())));
                u.setPhone(phone.getText().trim());
                u.setCity(city.getText().trim());
                u.setRole("user");
                int uid = new UserDAO().insertUser(u);
                if (uid <= 0) throw new SQLException("Failed to create user account.");
                new ChecklistDAO().createDefaultChecklist(uid);
                JOptionPane.showMessageDialog(this, "Account created! Please login.", "Success 🎉", JOptionPane.INFORMATION_MESSAGE);
                dispose(); new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(regBtn, gc);
        return new JScrollPane(p);
    }

    private JScrollPane buildVendorPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(8, 5, 2, 5);

        int row = 0;
        JTextField businessName = addField(p, gc, row++, "Business Name *", 30);
        JTextField ownerName = addField(p, gc, row++, "Owner Name *", 30);
        JTextField email = addField(p, gc, row++, "Email Address *", 30);
        JTextField phone = addField(p, gc, row++, "Phone Number *", 30);
        JTextField city = addField(p, gc, row++, "City *", 30);
        JTextField locality = addField(p, gc, row++, "Locality / Area", 30);

        String[] cats = {"Photographer","Makeup Artist","Caterer","Decorator","Mehndi Artist","DJ","Choreographer","Venue","Bridal Wear","Groom Wear","Invitation","Jewellery","Wedding Planner","Trousseau Packer","Transport","Wedding Cake"};
        gc.gridx = 0; gc.gridy = row * 2; gc.gridwidth = 2;
        JLabel cl = new JLabel("Category *"); cl.setFont(UIUtils.FONT_BODY);
        p.add(cl, gc);
        gc.gridy = row * 2 + 1;
        JComboBox<String> catCombo = UIUtils.styledCombo(cats);
        p.add(catCombo, gc);
        row++;

        JTextField startingPrice = addField(p, gc, row++, "Starting Price (₹) *", 30);

        // Password
        gc.insets = new Insets(12, 5, 2, 5);
        gc.gridx = 0; gc.gridy = row * 2; gc.gridwidth = 2;
        JLabel p1l = new JLabel("Password *"); p1l.setFont(UIUtils.FONT_BODY);
        p.add(p1l, gc);
        gc.gridy = row * 2 + 1;
        JPasswordField pass1 = UIUtils.styledPasswordField(30);
        stylePasswordListener(pass1);
        p.add(pass1, gc);
        row++;

        gc.gridy = row * 2;
        JLabel p2l = new JLabel("Confirm Password *"); p2l.setFont(UIUtils.FONT_BODY);
        p.add(p2l, gc);
        gc.gridy = row * 2 + 1;
        JPasswordField pass2 = UIUtils.styledPasswordField(30);
        p.add(pass2, gc);
        row++;

        // Strength bar
        gc.insets = new Insets(5, 5, 2, 5);
        gc.gridy = row * 2;
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setPreferredSize(new Dimension(400, 8));
        strengthBar.setBorderPainted(false);
        p.add(strengthBar, gc);
        gc.gridy = row * 2 + 1;
        strengthLabel = new JLabel("Password strength");
        strengthLabel.setFont(UIUtils.FONT_SMALL);
        p.add(strengthLabel, gc);
        row++;

        gc.insets = new Insets(20, 5, 10, 5);
        gc.gridy = row * 2; gc.gridwidth = 2;
        JButton regBtn = UIUtils.primaryButton("Register My Business");
        regBtn.setPreferredSize(new Dimension(400, 40));
        regBtn.addActionListener(e -> {
            if (!validate(ownerName.getText(), email.getText(), phone.getText(), pass1, pass2)) return;
            if (!ValidationUtils.isNotEmpty(businessName.getText())) {
                JOptionPane.showMessageDialog(this, "Business name is required."); return;
            }
            try {
                User u = new User();
                u.setFullName(ownerName.getText().trim());
                u.setEmail(email.getText().trim());
                u.setPasswordHash(PasswordUtils.hash(new String(pass1.getPassword())));
                u.setPhone(phone.getText().trim());
                u.setCity(city.getText().trim());
                u.setRole("vendor");
                int uid = new UserDAO().insertUser(u);

                Vendor v = new Vendor();
                v.setUserId(uid);
                v.setBusinessName(businessName.getText().trim());
                v.setCategory((String) catCombo.getSelectedItem());
                v.setCity(city.getText().trim());
                v.setLocality(locality.getText().trim());
                v.setStartingPrice(Double.parseDouble(startingPrice.getText().isEmpty() ? "0" : startingPrice.getText()));
                new VendorDAO().insertVendor(v);

                JOptionPane.showMessageDialog(this, "Vendor account created! Awaiting admin verification.", "Success 🎊", JOptionPane.INFORMATION_MESSAGE);
                dispose(); new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(regBtn, gc);
        return new JScrollPane(p);
    }

    private JTextField addField(JPanel p, GridBagConstraints gc, int row, String label, int cols) {
        gc.gridx = 0; gc.gridy = row * 2; gc.gridwidth = 2;
        gc.insets = new Insets(8, 5, 2, 5); // Label insets
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_BODY);
        p.add(lbl, gc);
        gc.gridy = row * 2 + 1;
        gc.insets = new Insets(0, 5, 5, 5); // Field insets
        JTextField field = UIUtils.styledField(cols);
        p.add(field, gc);
        return field;
    }


    private void stylePasswordListener(JPasswordField pf) {
        pf.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            void update() {
                String pw = new String(pf.getPassword());
                int score = PasswordUtils.strength(pw);
                strengthBar.setValue(score);
                strengthBar.setForeground(PasswordUtils.strengthColor(score));
                strengthLabel.setText("Strength: " + PasswordUtils.strengthLabel(score));
                strengthLabel.setForeground(PasswordUtils.strengthColor(score));
            }
        });
    }

    private boolean validate(String name, String email, String phone, JPasswordField p1, JPasswordField p2) {
        if (!ValidationUtils.isNotEmpty(name))   { JOptionPane.showMessageDialog(this, "Full name is required."); return false; }
        if (!ValidationUtils.isValidEmail(email)){ JOptionPane.showMessageDialog(this, "Invalid email address."); return false; }
        if (!ValidationUtils.isValidPhone(phone)){ JOptionPane.showMessageDialog(this, "Invalid phone number (10 digits starting with 6-9)."); return false; }
        String pw = new String(p1.getPassword());
        if (pw.length() < 6)                    { JOptionPane.showMessageDialog(this, "Password must be at least 6 characters."); return false; }
        if (!pw.equals(new String(p2.getPassword()))) { JOptionPane.showMessageDialog(this, "Passwords do not match."); return false; }
        try {
            if (new UserDAO().emailExists(email)) { JOptionPane.showMessageDialog(this, "Email already registered."); return false; }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage()); return false; }
        return true;
    }
}
