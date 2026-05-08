package user;

import dao.BookingDAO;
import dao.ChecklistDAO;
import models.User;
import utils.UIUtils;
import utils.StatCard;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * User home panel with live countdown timer.
 */
public class UserHomePanel extends JPanel {
    private final User user;
    private JLabel countdownLabel;

    public UserHomePanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,16));
        setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        build();
    }

    private void build() {
        // ── Welcome banner ────────────────────────────────────────────────────
        JPanel banner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,UIUtils.DEEP_BROWN,getWidth(),getHeight(),new Color(0xC9,0x60,0x40));
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
            }
        };
        banner.setLayout(new BorderLayout(16,0));
        banner.setBorder(BorderFactory.createEmptyBorder(20,28,20,28));
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(1100, 120));

        JPanel bannerLeft=new JPanel(new GridLayout(3,1,0,4));
        bannerLeft.setOpaque(false);
        JLabel welcome=new JLabel("Welcome back, " + user.getFullName() + "! 🌸");
        welcome.setFont(new Font("Segoe UI",Font.BOLD,22));
        welcome.setForeground(Color.WHITE);
        JLabel sub=new JLabel("Your perfect Indian wedding is in the making ✨");
        sub.setFont(UIUtils.FONT_BODY); sub.setForeground(UIUtils.ACCENT_LIGHT);
        countdownLabel=new JLabel("🗓️ Loading countdown…");
        countdownLabel.setFont(UIUtils.FONT_HEADING);
        countdownLabel.setForeground(UIUtils.ROSE_GOLD);
        bannerLeft.add(welcome); bannerLeft.add(sub); bannerLeft.add(countdownLabel);
        banner.add(bannerLeft,BorderLayout.CENTER);

        JLabel floral=new JLabel("🌺🌸💐",SwingConstants.CENTER);
        floral.setFont(new Font("Segoe UI Emoji",Font.PLAIN,40));
        banner.add(floral,BorderLayout.EAST);

        add(banner,BorderLayout.NORTH);
        startCountdown();

        // ── Stats row ────────────────────────────────────────────────────────
        JPanel center=new JPanel();
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JPanel statsRow=new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));
        statsRow.setOpaque(false);
        
        if (user.getUserId() > 0) {
            try {
                BookingDAO bd=new BookingDAO();
                ChecklistDAO cd=new ChecklistDAO();
                int totalBk = bd.getByUser(user.getUserId()).size();
                int approvedBk = (int) bd.getByUser(user.getUserId()).stream().filter(b->"approved".equals(b.getStatus())).count();
                int[] prog = cd.getProgress(user.getUserId());
                int donePct = prog[1]>0 ? (int)(100.0*prog[0]/prog[1]) : 0;
                statsRow.add(new StatCard("📋",String.valueOf(totalBk),  "Total Bookings",    UIUtils.ROSE_GOLD));
                statsRow.add(new StatCard("✔", String.valueOf(approvedBk),"Approved Bookings",UIUtils.SUCCESS));
                statsRow.add(new StatCard("✅",prog[0]+"/"+prog[1],      "Checklist Done",    new Color(0x27,0x6E,0xBC)));
                statsRow.add(new StatCard("📊",donePct+"%",              "Wedding Progress",  UIUtils.DEEP_BROWN));
            } catch(Exception ex){ statsRow.add(new JLabel("Error: "+ex.getMessage())); }
        } else {
            JLabel joinMsg = new JLabel("Join WeddingGenie to start managing your checklist, budget, and guest list! 🌸");
            joinMsg.setFont(UIUtils.FONT_SUBHEAD);
            joinMsg.setForeground(UIUtils.DEEP_BROWN);
            statsRow.add(joinMsg);
        }
        center.add(statsRow);
        center.add(Box.createRigidArea(new Dimension(0,20)));

        // ── Quick actions ─────────────────────────────────────────────────────
        JLabel actHdr=UIUtils.headingLabel("Quick Actions");
        center.add(actHdr);
        center.add(Box.createRigidArea(new Dimension(0,10)));
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));
        actions.setOpaque(false);
        String[][] acts={{"🔍 Find Vendors","Find Vendors"},{"📅 Browse Events","Browse Events"},
            {"✅ My Checklist","Checklist"},{"💰 Budget Tracker","Budget"},
            {"💌 Create E-Invite","E-Invite"},{"👥 Guest Manager","Guests"}};
        for(String[] a : acts){
            JButton btn = UIUtils.primaryButton(a[0]);
            btn.setPreferredSize(new Dimension(160,40));
            btn.addActionListener(e -> {
                if (user.getUserId() <= 0 && !"Find Vendors".equals(a[1]) && !"Browse Events".equals(a[1]) && !"Ideas Gallery".equals(a[1])) {
                    JOptionPane.showMessageDialog(this, "Please Login or Register to use " + a[1] + " features. 🌸", "Member Feature", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Container parent = getParent();
                    while (parent != null && !(parent instanceof UserDashboard)) parent = parent.getParent();
                    if (parent != null) ((UserDashboard)parent).navigate(a[1]);
                }
            });
            actions.add(btn);
        }
        center.add(actions);
        add(new JScrollPane(center),BorderLayout.CENTER);
    }

    private void startCountdown() {
        // Default wedding target: 180 days from today if not set
        LocalDate weddingDay = LocalDate.now().plusDays(180);
        Timer t = new Timer(1000, e -> {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), weddingDay);
            if (days > 0) countdownLabel.setText("⏳ " + days + " days to your Wedding Day!");
            else if (days == 0) countdownLabel.setText("🎊 Your Wedding Day is TODAY! Congratulations!");
            else countdownLabel.setText("💍 Wedding Complete! Relive the memories ✨");
        });
        t.start();
    }
}
