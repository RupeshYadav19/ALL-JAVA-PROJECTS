package vendor;

import dao.*;
import models.User;
import models.Vendor;
import utils.UIUtils;
import utils.StatCard;
import utils.StarRatingPanel;
import javax.swing.*;
import java.awt.*;

public class VendorHomePanel extends JPanel {
    public VendorHomePanel(User user, Vendor vendor) {
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,16));
        setBorder(BorderFactory.createEmptyBorder(20,24,20,24));

        add(UIUtils.createHeaderBar("🏠  Vendor Dashboard — " + (vendor!=null?vendor.getBusinessName():"")), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // Stat cards
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));
        statsRow.setOpaque(false);
        try {
            ServiceDAO sd = new ServiceDAO();
            BookingDAO bd = new BookingDAO();
            ReviewDAO rd  = new ReviewDAO();
            int services  = vendor!=null ? sd.countByVendor(vendor.getVendorId()) : 0;
            int pendingBk = vendor!=null ? new dao.BookingDAO().countByStatus("pending") : 0;
            double rating = vendor!=null ? vendor.getRating() : 0;
            int revCount  = vendor!=null ? vendor.getReviewCount() : 0;

            statsRow.add(new StatCard("📦", String.valueOf(services),  "Services Listed",     UIUtils.ROSE_GOLD));
            statsRow.add(new StatCard("📋", String.valueOf(pendingBk), "Pending Requests",    UIUtils.WARNING));
            statsRow.add(new StatCard("⭐", String.format("%.1f",rating), "Avg Rating",       new Color(0xFF,0xA0,0x00)));
            statsRow.add(new StatCard("💬", String.valueOf(revCount),  "Total Reviews",       new Color(0x27,0x6E,0xBC)));
        } catch (Exception ex) { statsRow.add(new JLabel("Error: "+ex.getMessage())); }

        center.add(statsRow);
        center.add(Box.createRigidArea(new Dimension(0,20)));

        // Rating breakdown
        JLabel ratingHdr = UIUtils.headingLabel("⭐ Rating Breakdown");
        center.add(ratingHdr);
        center.add(Box.createRigidArea(new Dimension(0,8)));
        if (vendor != null) {
            try {
                int[] dist = new ReviewDAO().getRatingDistribution(vendor.getVendorId());
                int total = vendor.getReviewCount() > 0 ? vendor.getReviewCount() : 1;
                String[] stars = {"5 ★","4 ★","3 ★","2 ★","1 ★"};
                Color[] colors = {UIUtils.SUCCESS,new Color(0x27,0xAE,0x60),UIUtils.WARNING,new Color(0xE6,0x7E,0x22),UIUtils.DANGER};
                JPanel ratingPanel = new JPanel();
                ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.Y_AXIS));
                ratingPanel.setOpaque(false);
                for (int i=5;i>=1;i--) {
                    JPanel row = new JPanel(new BorderLayout(8,0));
                    row.setOpaque(false);
                    row.setMaximumSize(new Dimension(400,22));
                    JLabel lbl = new JLabel(stars[5-i]);
                    lbl.setFont(UIUtils.FONT_SMALL);
                    lbl.setPreferredSize(new Dimension(36,20));
                    JProgressBar bar = new JProgressBar(0,total);
                    bar.setValue(dist[i]);
                    bar.setForeground(colors[5-i]);
                    bar.setBackground(new Color(0xEE,0xDD,0xCC));
                    bar.setBorderPainted(false);
                    JLabel cnt = new JLabel("("+dist[i]+")");
                    cnt.setFont(UIUtils.FONT_SMALL);
                    cnt.setPreferredSize(new Dimension(36,20));
                    row.add(lbl,BorderLayout.WEST);
                    row.add(bar,BorderLayout.CENTER);
                    row.add(cnt,BorderLayout.EAST);
                    ratingPanel.add(row);
                    ratingPanel.add(Box.createRigidArea(new Dimension(0,4)));
                }
                center.add(ratingPanel);
            } catch (Exception ex) { center.add(new JLabel("Could not load ratings")); }
        }

        add(new JScrollPane(center), BorderLayout.CENTER);
    }
}
