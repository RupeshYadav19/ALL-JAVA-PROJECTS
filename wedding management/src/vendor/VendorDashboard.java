package vendor;

import dao.*;
import models.User;
import models.Vendor;
import utils.UIUtils;
import utils.StatCard;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Vendor Dashboard — 1100×700 with sidebar navigation.
 */
public class VendorDashboard extends JFrame {
    private final User user;
    private Vendor vendor;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private static final String[][] NAV = {
        {"🏠","Home"},{"👤","Profile"},{"📦","Services"},
        {"📋","Booking Requests"},{"🖼","Portfolio"},
        {"⭐","Reviews"},{"💰","Revenue"},{"🔔","Notifications"},{"🚪","Logout"}
    };

    public VendorDashboard(User user) {
        this.user = user;
        try { this.vendor = new VendorDAO().findByUserId(user.getUserId()); } catch(Exception e) { e.printStackTrace(); }
        setTitle("WeddingGenie — Vendor Dashboard");
        setSize(1100,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIUtils.DEEP_BROWN);
        topBar.setBorder(BorderFactory.createEmptyBorder(10,16,10,20));
        topBar.setPreferredSize(new Dimension(1100,54));
        JLabel logo = new JLabel("🌸 WeddingGenie — Vendor Panel");
        logo.setFont(UIUtils.FONT_HEADING); logo.setForeground(Color.WHITE);
        JLabel vName = new JLabel("🏪 " + (vendor!=null?vendor.getBusinessName():user.getFullName()));
        vName.setFont(UIUtils.FONT_BODY); vName.setForeground(UIUtils.ACCENT_LIGHT);
        topBar.add(logo, BorderLayout.WEST);
        topBar.add(vName, BorderLayout.EAST);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtils.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(195,700));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16,0,16,0));

        JLabel avatar = new JLabel("🏪", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji",Font.PLAIN,36));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel name = new JLabel(vendor!=null?vendor.getBusinessName():"—", SwingConstants.CENTER);
        name.setFont(UIUtils.FONT_SMALL); name.setForeground(UIUtils.ACCENT_LIGHT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel cat = new JLabel(vendor!=null?vendor.getCategory():"Vendor", SwingConstants.CENTER);
        cat.setFont(UIUtils.FONT_SMALL); cat.setForeground(UIUtils.ROSE_GOLD);
        cat.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(avatar); sidebar.add(Box.createRigidArea(new Dimension(0,4)));
        sidebar.add(name); sidebar.add(cat);
        sidebar.add(Box.createRigidArea(new Dimension(0,10)));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(100,70,60));
        sep.setMaximumSize(new Dimension(200,1));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0,6)));

        ButtonGroup bg = new ButtonGroup();
        for (String[] nav : NAV) {
            JToggleButton btn = createNavBtn(nav[0], nav[1]);
            bg.add(btn);
            btn.addActionListener(e -> navigate(nav[1]));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0,2)));
        }

        // Content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIUtils.CREAM);
        contentPanel.add(new VendorHomePanel(user, vendor),    "Home");
        contentPanel.add(new VendorProfilePanel(user, vendor), "Profile");
        contentPanel.add(new ServiceListingPanel(user, vendor),"Services");
        contentPanel.add(new BookingRequestsPanel(user, vendor),"Booking Requests");
        contentPanel.add(new PortfolioPanel(user, vendor),     "Portfolio");
        contentPanel.add(new ReviewsReceivedPanel(vendor),     "Reviews");
        contentPanel.add(new RevenuePanel(user, vendor),       "Revenue");
        contentPanel.add(new VendorNotificationsPanel(user),   "Notifications");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, contentPanel);
        split.setDividerSize(0); split.setEnabled(false);
        root.add(topBar, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        setContentPane(root);
        cardLayout.show(contentPanel,"Home");
    }

    private void navigate(String label) {
        if ("Logout".equals(label)) {
            int c = JOptionPane.showConfirmDialog(this,"Logout?","Confirm",JOptionPane.YES_NO_OPTION);
            if (c==JOptionPane.YES_OPTION){ dispose(); new auth.LoginFrame().setVisible(true); }
        } else cardLayout.show(contentPanel,label);
    }

    private JToggleButton createNavBtn(String icon, String label) {
        JToggleButton btn = new JToggleButton(icon+"  "+label);
        btn.setFont(UIUtils.FONT_BODY);
        btn.setForeground(new Color(220,200,180));
        btn.setBackground(UIUtils.SIDEBAR_BG);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(195,42));
        btn.setBorder(BorderFactory.createEmptyBorder(8,18,8,10));
        btn.addItemListener(e -> {
            if (e.getStateChange()==ItemEvent.SELECTED){ btn.setBackground(UIUtils.ROSE_GOLD); btn.setForeground(Color.BLACK); }
            else { btn.setBackground(UIUtils.SIDEBAR_BG); btn.setForeground(new Color(220,200,180)); }
        });
        btn.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){if(!btn.isSelected())btn.setBackground(new Color(0x6D,0x50,0x45));}
            public void mouseExited(MouseEvent e) {if(!btn.isSelected())btn.setBackground(UIUtils.SIDEBAR_BG);}
        });
        return btn;
    }
}
