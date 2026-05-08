package admin;

import dao.BookingDAO;
import models.Booking;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * ReportsPanel — 4-tab reports with custom bar chart painting.
 */
public class ReportsPanel extends JPanel {
    private final User admin;

    public ReportsPanel(User admin) {
        this.admin = admin;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("📊  Reports & Analytics"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.FONT_SUBHEAD);
        tabs.addTab("Booking Summary",    buildBookingSummary());
        tabs.addTab("Revenue Report",     buildRevenueReport());
        tabs.addTab("Vendor Performance", buildVendorPerformance());
        tabs.addTab("User Activity",      buildUserActivity());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildBookingSummary() {
        JPanel p = new JPanel(new BorderLayout(0,12));
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        // Stats row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));
        statsRow.setOpaque(false);
        try {
            BookingDAO bd = new BookingDAO();
            int total   = bd.totalCount();
            int pending = bd.countByStatus("pending");
            int approved= bd.countByStatus("approved");
            int rejected= bd.countByStatus("rejected");
            String[] labels = {"Total","Pending","Approved","Rejected"};
            int[] vals      = {total,pending,approved,rejected};
            Color[] colors  = {UIUtils.ROSE_GOLD,UIUtils.WARNING,UIUtils.SUCCESS,UIUtils.DANGER};
            for (int i=0;i<4;i++) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(UIUtils.CARD_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colors[i],2),
                    BorderFactory.createEmptyBorder(12,18,12,18)));
                card.setPreferredSize(new Dimension(160,80));
                JLabel num = new JLabel(String.valueOf(vals[i]),SwingConstants.CENTER);
                num.setFont(new Font("Segoe UI",Font.BOLD,28));
                num.setForeground(colors[i]);
                JLabel lbl = new JLabel(labels[i],SwingConstants.CENTER);
                lbl.setFont(UIUtils.FONT_SMALL);
                card.add(num,BorderLayout.CENTER);
                card.add(lbl,BorderLayout.SOUTH);
                statsRow.add(card);
            }
        } catch (Exception ex) { statsRow.add(new JLabel("Error: "+ex.getMessage())); }
        p.add(statsRow,BorderLayout.NORTH);

        // Bar chart
        JPanel chart = new BarChartPanel() {
            { setTitle("Booking Status Distribution"); }
            @Override Map<String,Double> getData() {
                try {
                    BookingDAO bd = new BookingDAO();
                    Map<String,Double> m = new LinkedHashMap<>();
                    m.put("Pending", (double)bd.countByStatus("pending"));
                    m.put("Approved", (double)bd.countByStatus("approved"));
                    m.put("Rejected", (double)bd.countByStatus("rejected"));
                    m.put("Cancelled", (double)bd.countByStatus("cancelled"));
                    m.put("Completed", (double)bd.countByStatus("completed"));
                    return m;
                } catch (Exception e){ return new LinkedHashMap<>();}
            }
        };
        chart.setPreferredSize(new Dimension(800,280));
        p.add(chart,BorderLayout.CENTER);

        JButton exportBtn = UIUtils.secondaryButton("📥 Export Report");
        exportBtn.addActionListener(e -> exportToFile("booking_summary_report.txt", buildBookingText()));
        p.add(exportBtn,BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildRevenueReport() {
        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        try {
            double monthly = new BookingDAO().revenueThisMonth();
            JLabel revLbl = new JLabel("Revenue This Month: ₹" + String.format("%,.2f",monthly));
            revLbl.setFont(UIUtils.FONT_HEADING);
            revLbl.setForeground(UIUtils.SUCCESS);
            p.add(revLbl,BorderLayout.NORTH);
        } catch(Exception ex){ p.add(new JLabel("Error: "+ex.getMessage()),BorderLayout.NORTH); }

        String[] cols = {"Month","Bookings","Revenue (₹)","Avg per Booking"};
        DefaultTableModel m = new DefaultTableModel(cols,0);
        JTable t = new JTable(m);
        UIUtils.styleTable(t);
        // Dummy data rows
        m.addRow(new Object[]{"March 2026","12","₹4,20,000","₹35,000"});
        m.addRow(new Object[]{"February 2026","9","₹3,15,000","₹35,000"});
        m.addRow(new Object[]{"January 2026","15","₹5,25,000","₹35,000"});
        p.add(UIUtils.scrollPane(t),BorderLayout.CENTER);
        JButton export = UIUtils.secondaryButton("📥 Export Revenue CSV");
        export.addActionListener(e -> exportToFile("revenue_report.csv","Month,Bookings,Revenue\nMarch 2026,12,420000"));
        p.add(export,BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildVendorPerformance() {
        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        String[] cols = {"Vendor","Category","City","Rating","Reviews","Verified"};
        DefaultTableModel m = new DefaultTableModel(cols,0);
        JTable t = new JTable(m);
        UIUtils.styleTable(t);
        try {
            dao.VendorDAO vd = new dao.VendorDAO();
            for (models.Vendor v : vd.getAll()) {
                m.addRow(new Object[]{v.getBusinessName(),v.getCategory(),v.getCity(),
                    String.format("%.1f",v.getRating()),v.getReviewCount(),v.isVerified()?"✔":"✗"});
            }
        } catch (Exception ex){ m.addRow(new Object[]{"Error: "+ex.getMessage()}); }
        p.add(UIUtils.scrollPane(t),BorderLayout.CENTER);
        return p;
    }

    private JPanel buildUserActivity() {
        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        try {
            dao.UserDAO ud = new dao.UserDAO();
            int couples = ud.countByRole("user");
            int vendors = ud.countByRole("vendor");
            int newToday = ud.countNewToday();

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT,16,0));
            row.setOpaque(false);
            row.add(statChip("Total Couples", String.valueOf(couples), UIUtils.ROSE_GOLD));
            row.add(statChip("Total Vendors", String.valueOf(vendors), new Color(0x8E,0x44,0xAD)));
            row.add(statChip("New Today", String.valueOf(newToday), UIUtils.SUCCESS));
            p.add(row,BorderLayout.NORTH);
        } catch (Exception ex){ p.add(new JLabel("Error: "+ex.getMessage()),BorderLayout.NORTH); }

        String[] cols = {"User","Role","City","Bookings","Registered"};
        DefaultTableModel m = new DefaultTableModel(cols,0);
        JTable t = new JTable(m);
        UIUtils.styleTable(t);
        try {
            for (models.User u : new dao.UserDAO().getAllByRole("user")) {
                m.addRow(new Object[]{u.getFullName(),"Couple",u.getCity(),"-",u.getCreatedAt()});
            }
        } catch (Exception ex){ m.addRow(new Object[]{"Error"}); }
        p.add(UIUtils.scrollPane(t),BorderLayout.CENTER);
        return p;
    }

    private JPanel statChip(String label, String value, Color c) {
        JPanel card = new JPanel(new GridLayout(2,1,0,2));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(c,1),
            BorderFactory.createEmptyBorder(8,14,8,14)));
        JLabel num = new JLabel(value,SwingConstants.CENTER);
        num.setFont(new Font("Segoe UI",Font.BOLD,22));
        num.setForeground(c);
        JLabel lbl = new JLabel(label,SwingConstants.CENTER);
        lbl.setFont(UIUtils.FONT_SMALL);
        card.add(num); card.add(lbl);
        return card;
    }

    private void exportToFile(String filename, String content) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File(filename));
        if (fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                pw.print(content);
                JOptionPane.showMessageDialog(this,"Report saved: "+fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        }
    }

    private String buildBookingText() {
        try {
            BookingDAO bd = new BookingDAO();
            return "=== BOOKING SUMMARY REPORT ===\n" +
                "Generated: " + new java.util.Date() + "\n\n" +
                "Total Bookings:    " + bd.totalCount() + "\n" +
                "Pending:           " + bd.countByStatus("pending") + "\n" +
                "Approved:          " + bd.countByStatus("approved") + "\n" +
                "Rejected:          " + bd.countByStatus("rejected") + "\n" +
                "Revenue (Month):   ₹" + String.format("%,.2f",bd.revenueThisMonth()) + "\n";
        } catch (Exception e){ return "Error generating report"; }
    }

    /** Abstract bar chart panel */
    static abstract class BarChartPanel extends JPanel {
        protected String title = "Chart";
        void setTitle(String t) { this.title = t; }
        abstract Map<String,Double> getData();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UIUtils.WHITE);
            g2.fillRect(0,0,getWidth(),getHeight());

            Map<String,Double> data = getData();
            if (data==null || data.isEmpty()) { g2.drawString("No data",20,40); return; }
            double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
            if (max==0) max=1;

            int barW = (getWidth()-80)/(data.size()>0?data.size():1) - 10;
            int maxBarH = getHeight() - 80;
            int x = 40;
            Color[] colors = {UIUtils.ROSE_GOLD, UIUtils.SUCCESS, UIUtils.DANGER, UIUtils.WARNING, new Color(0x27,0x6E,0xBC)};
            int ci = 0;
            for (Map.Entry<String,Double> entry : data.entrySet()) {
                int barH = (int)(entry.getValue()/max*maxBarH);
                int y = getHeight()-40-barH;
                g2.setColor(colors[ci % colors.length]);
                g2.fillRoundRect(x, y, barW, barH, 6, 6);
                g2.setColor(Color.BLACK);
                g2.setFont(UIUtils.FONT_SMALL);
                g2.drawString(entry.getKey(), x+(barW-g2.getFontMetrics().stringWidth(entry.getKey()))/2, getHeight()-22);
                g2.drawString(String.format("%.0f",entry.getValue()), x+(barW-g2.getFontMetrics().stringWidth(String.format("%.0f",entry.getValue())))/2, y-4);
                x += barW+10; ci++;
            }
            g2.setFont(UIUtils.FONT_SUBHEAD);
            g2.setColor(UIUtils.DEEP_BROWN);
            g2.drawString(title, 40, 20);
        }
    }
}
