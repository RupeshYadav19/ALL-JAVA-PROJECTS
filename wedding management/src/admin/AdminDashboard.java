package admin;

import dao.*;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Admin Dashboard — 1200×750 with collapsible sidebar and CardLayout content area.
 */
public class AdminDashboard extends JFrame {
    private final User admin;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;
    private boolean sidebarExpanded = true;

    private static final String[][] NAV = {
        {"🏠", "Home"},
        {"📅", "Events"},
        {"🏢", "Vendors"},
        {"📋", "Bookings"},
        {"👥", "Users"},
        {"🖼", "Gallery"},
        {"📖", "Real Weddings"},
        {"📊", "Reports"},
        {"🔔", "Notifications"},
        {"🚪", "Logout"}
    };

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("WeddingGenie — Admin Dashboard");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.CREAM);

        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIUtils.DEEP_BROWN);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 20));
        topBar.setPreferredSize(new Dimension(1200, 54));

        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topLeft.setOpaque(false);
        JButton hamburger = new JButton("☰");
        hamburger.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        hamburger.setForeground(Color.WHITE);
        hamburger.setBackground(UIUtils.DEEP_BROWN);
        hamburger.setBorderPainted(false);
        hamburger.setFocusPainted(false);
        hamburger.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hamburger.addActionListener(e -> toggleSidebar());
        JLabel logo = new JLabel("🌸 WeddingGenie — Admin Panel");
        logo.setFont(UIUtils.FONT_HEADING);
        logo.setForeground(Color.WHITE);
        topLeft.add(hamburger); topLeft.add(logo);

        JLabel adminName = new JLabel("👑 " + admin.getFullName());
        adminName.setFont(UIUtils.FONT_BODY);
        adminName.setForeground(UIUtils.ACCENT_LIGHT);

        topBar.add(topLeft, BorderLayout.WEST);
        topBar.add(adminName, BorderLayout.EAST);

        // ── Sidebar ──────────────────────────────────────────────────────────
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtils.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 700));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Profile section
        JPanel profile = new JPanel();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.setOpaque(false);
        profile.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        JLabel avatar = new JLabel("👑", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel name = new JLabel(admin.getFullName(), SwingConstants.CENTER);
        name.setFont(UIUtils.FONT_SMALL);
        name.setForeground(UIUtils.ACCENT_LIGHT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel role = new JLabel("Administrator", SwingConstants.CENTER);
        role.setFont(UIUtils.FONT_SMALL);
        role.setForeground(UIUtils.ROSE_GOLD);
        role.setAlignmentX(Component.CENTER_ALIGNMENT);
        profile.add(avatar); profile.add(Box.createRigidArea(new Dimension(0, 4)));
        profile.add(name); profile.add(role);
        sidebar.add(profile);
        sidebar.add(createSeparator());

        // Navigation buttons
        ButtonGroup bg = new ButtonGroup();
        for (String[] nav : NAV) {
            JToggleButton btn = createNavButton(nav[0], nav[1]);
            bg.add(btn);
            btn.addActionListener(e -> navigate(nav[1]));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        // ── Content area ─────────────────────────────────────────────────────
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIUtils.CREAM);
        contentPanel.add(new AdminHomePanel(admin), "Home");
        contentPanel.add(new ManageEventsPanel(admin), "Events");
        contentPanel.add(new ManageVendorsPanel(admin), "Vendors");
        contentPanel.add(new ManageBookingsPanel(admin), "Bookings");
        contentPanel.add(new ManageUsersPanel(admin), "Users");
        contentPanel.add(new GalleryManagerPanel(admin), "Gallery");
        contentPanel.add(new RealWeddingsPanel(admin), "Real Weddings");
        contentPanel.add(new ReportsPanel(admin), "Reports");
        contentPanel.add(new NotificationsPanel(admin), "Notifications");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, contentPanel);
        split.setDividerSize(0);
        split.setEnabled(false);

        root.add(topBar, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        setContentPane(root);
        cardLayout.show(contentPanel, "Home");
    }

    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        sidebar.setPreferredSize(new Dimension(sidebarExpanded ? 200 : 60, 700));
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JToggleButton btn) {
                if (!sidebarExpanded) {
                    String t = btn.getText(); int space = t.indexOf(' ');
                    btn.setText(space>0 ? t.substring(0,space) : t);
                }
            }
        }
        sidebar.revalidate(); sidebar.repaint();
    }

    private void navigate(String label) {
        if ("Logout".equals(label)) {
            int c = JOptionPane.showConfirmDialog(this, "Logout from WeddingGenie?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { dispose(); new auth.LoginFrame().setVisible(true); }
        } else {
            cardLayout.show(contentPanel, label);
        }
    }

    private JToggleButton createNavButton(String icon, String label) {
        JToggleButton btn = new JToggleButton(icon + "  " + label);
        btn.setFont(UIUtils.FONT_BODY);
        btn.setForeground(new Color(220, 200, 180));
        btn.setBackground(UIUtils.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 10));
        btn.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                btn.setBackground(UIUtils.ROSE_GOLD);
                btn.setForeground(Color.BLACK);
            } else {
                btn.setBackground(UIUtils.SIDEBAR_BG);
                btn.setForeground(new Color(220, 200, 180));
            }
        });
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (!btn.isSelected()) btn.setBackground(new Color(0x6D, 0x50, 0x45)); }
            public void mouseExited(MouseEvent e)  { if (!btn.isSelected()) btn.setBackground(UIUtils.SIDEBAR_BG); }
        });
        return btn;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(100, 70, 60));
        sep.setMaximumSize(new Dimension(200, 1));
        return sep;
    }
}
