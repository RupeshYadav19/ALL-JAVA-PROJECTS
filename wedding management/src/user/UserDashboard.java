package user;

import dao.*;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * User/Couple Dashboard — 1150×720 with top navigation tab bar.
 */
public class UserDashboard extends JFrame {
    private final User user;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private static final String[][] NAV = {
        {"🏠","Home"},{"🔍","Find Vendors"},{"📅","Browse Events"},
        {"📋","My Bookings"},{"✅","Checklist"},{"💰","Budget"},
        {"👥","Guests"},{"🖼","Ideas Gallery"},{"💌","E-Invite"},
        {"🎵","Songs"},{"🏷","Hashtag"},{"👤","Profile"},{"🚪","Logout"}
    };

    public UserDashboard(User user) {
        this.user = user;
        setTitle("WeddingGenie — " + user.getFullName());
        setSize(1200, 730);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.CREAM);

        // ── Top nav bar ──────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIUtils.DEEP_BROWN);
        topBar.setPreferredSize(new Dimension(1200, 56));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navLeft.setOpaque(false);
        JLabel logo = new JLabel("  🌸 WeddingGenie  ");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logo.setForeground(UIUtils.ROSE_GOLD);
        navLeft.add(logo);

        JPanel navBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 8));
        navBtns.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        for (String[] nav : NAV) {
            JToggleButton btn = createNavButton(nav[0] + " " + nav[1]);
            bg.add(btn);
            String panel = nav[1];
            btn.addActionListener(e -> navigate(panel));
            navBtns.add(btn);
        }

        JLabel userLbl = new JLabel("👰 " + user.getFullName() + "  ");
        userLbl.setFont(UIUtils.FONT_SMALL);
        userLbl.setForeground(UIUtils.ACCENT_LIGHT);

        topBar.add(logo, BorderLayout.WEST);
        topBar.add(navBtns, BorderLayout.CENTER);
        topBar.add(userLbl, BorderLayout.EAST);

        // ── Content ───────────────────────────────────────────────────────────
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIUtils.CREAM);
        contentPanel.add(new UserHomePanel(user),           "Home");
        contentPanel.add(new BrowseVendorsPanel(user),      "Find Vendors");
        contentPanel.add(new BrowseEventsPanel(user),       "Browse Events");
        contentPanel.add(new MyBookingsPanel(user),         "My Bookings");
        contentPanel.add(new ChecklistPanel(user),          "Checklist");
        contentPanel.add(new BudgetTrackerPanel(user),      "Budget");
        contentPanel.add(new GuestManagerPanel(user),       "Guests");
        contentPanel.add(new IdeasGalleryPanel(user),       "Ideas Gallery");
        contentPanel.add(new EInvitePanel(user),            "E-Invite");
        contentPanel.add(new WeddingSongsPanel(user),       "Songs");
        contentPanel.add(new HashtagGeneratorPanel(user),   "Hashtag");
        contentPanel.add(new ProfilePanel(user),            "Profile");

        root.add(topBar, BorderLayout.NORTH);
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
        cardLayout.show(contentPanel, "Home");
    }

    public void navigate(String label) {
        if ("Logout".equals(label)) {
            int c = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { dispose(); new auth.LoginFrame().setVisible(true); }
        } else {
            // Guest mode restrictions
            if (user.getUserId() <= 0) {
                switch (label) {
                    case "My Bookings", "Checklist", "Budget", "Guests", "Profile" -> {
                        JOptionPane.showMessageDialog(this, "Please Login or Register to use " + label + " features. 🌸", "Member Feature", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }
            cardLayout.show(contentPanel, label);
        }
    }

    private JToggleButton createNavButton(String label) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(new Color(210, 190, 170));
        btn.setBackground(UIUtils.DEEP_BROWN);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        btn.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                btn.setBackground(UIUtils.ROSE_GOLD);
                btn.setForeground(Color.BLACK);
            } else {
                btn.setBackground(UIUtils.DEEP_BROWN);
                btn.setForeground(new Color(210, 190, 170));
            }
        });
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (!btn.isSelected()) btn.setBackground(new Color(0x6D,0x50,0x45)); }
            public void mouseExited(MouseEvent e)  { if (!btn.isSelected()) btn.setBackground(UIUtils.DEEP_BROWN); }
        });
        return btn;
    }
}
