package user;

import dao.BookingDAO;
import dao.ReviewDAO;
import models.Booking;
import models.Review;
import models.User;
import utils.UIUtils;
import utils.StarRatingPanel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MyBookingsPanel extends JPanel {
    private final User user;
    private final BookingDAO dao = new BookingDAO();

    public MyBookingsPanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("📋  My Bookings"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.FONT_SUBHEAD);
        tabs.addTab("⏳ Pending",   buildTab("pending"));
        tabs.addTab("✔ Approved",   buildTab("approved"));
        tabs.addTab("🏁 Completed", buildTab("completed"));
        tabs.addTab("✘ Cancelled",  buildTab("cancelled"));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTab(String status) {
        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setBackground(UIUtils.CREAM);
        p.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

        String[] cols = {"ID","Event","Type","Date","Guests","Amount","Payment","Status"};
        DefaultTableModel model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable table = new JTable(model);
        UIUtils.styleTable(table);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton detailBtn  = UIUtils.secondaryButton("👁 Details");
        JButton cancelBtn  = UIUtils.dangerButton("✘ Cancel");
        JButton reviewBtn  = UIUtils.primaryButton("⭐ Write Review");
        JButton refreshBtn = UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(detailBtn); toolbar.add(refreshBtn);
        if ("pending".equals(status))   toolbar.add(cancelBtn);
        if ("completed".equals(status)) toolbar.add(reviewBtn);

        List<Booking>[] bookings = new List[]{null};
        Runnable load = () -> {
            model.setRowCount(0);
            try {
                bookings[0] = dao.getByUser(user.getUserId()).stream()
                    .filter(b -> status.equals(b.getStatus())).collect(Collectors.toList());
                for(Booking b : bookings[0]) {
                    model.addRow(new Object[]{b.getBookingId(),b.getEventName(),b.getEventType(),
                        b.getEventDate(),b.getGuestCount(),
                        "₹"+String.format("%,.0f",b.getTotalPrice()),
                        b.getPaymentStatus(),b.getStatus().toUpperCase()});
                }
            } catch(Exception ex){ JOptionPane.showMessageDialog(p,"Error: "+ex.getMessage()); }
        };
        load.run();

        refreshBtn.addActionListener(e -> load.run());
        detailBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if(row<0||bookings[0]==null) return;
            Booking b = bookings[0].get(row);
            JOptionPane.showMessageDialog(p,
                "Booking #"+b.getBookingId()+"\nEvent: "+b.getEventName()+
                " ("+b.getEventType()+")\nDate: "+b.getEventDate()+
                "\nGuests: "+b.getGuestCount()+"\nCeremonies: "+b.getCeremonyTypes()+
                "\nTotal: ₹"+String.format("%,.0f",b.getTotalPrice())+
                "\nStatus: "+b.getStatus()+
                (b.getRejectionReason()!=null&&!b.getRejectionReason().isEmpty()?"\nReason: "+b.getRejectionReason():""),
                "Booking Details",JOptionPane.INFORMATION_MESSAGE);
        });
        cancelBtn.addActionListener(e -> {
            if(bookings[0]==null) return;
            int row = table.getSelectedRow(); if(row<0) return;
            if(JOptionPane.showConfirmDialog(p,"Cancel this booking?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                try{ dao.updateStatus(bookings[0].get(row).getBookingId(),"cancelled",null); load.run(); }
                catch(Exception ex){ JOptionPane.showMessageDialog(p,"Error: "+ex.getMessage()); }
            }
        });
        reviewBtn.addActionListener(e -> {
            if(bookings[0]==null) return;
            int row = table.getSelectedRow(); if(row<0) return;
            openReviewDialog(bookings[0].get(row));
        });

        p.add(toolbar, BorderLayout.NORTH);
        p.add(UIUtils.scrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void openReviewDialog(Booking booking) {
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Write a Review",true);
        dlg.setSize(440,340); dlg.setLocationRelativeTo(this);
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,22,16,22));

        JLabel hdr = UIUtils.headingLabel("Rate your experience");
        StarRatingPanel stars = new StarRatingPanel(5, true);
        stars.setPreferredSize(new Dimension(160,32));
        JTextArea reviewText = new JTextArea(4,30);
        reviewText.setFont(UIUtils.FONT_BODY); reviewText.setLineWrap(true);
        JButton submitBtn = UIUtils.primaryButton("Submit Review");
        submitBtn.addActionListener(e -> {
            if(stars.getRating()==0){JOptionPane.showMessageDialog(dlg,"Please select a rating.");return;}
            Review r = new Review();
            r.setUserId(user.getUserId());
            r.setVendorId(1); // default – in production link to vendor
            r.setBookingId(booking.getBookingId());
            r.setRating(stars.getRating());
            r.setReviewText(reviewText.getText().trim());
            try {
                new ReviewDAO().insert(r);
                JOptionPane.showMessageDialog(dlg,"Review submitted! Awaiting approval.","Thank You",JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } catch(Exception ex){ JOptionPane.showMessageDialog(dlg,"Error: "+ex.getMessage()); }
        });
        p.add(hdr,BorderLayout.NORTH);
        JPanel center = new JPanel(); center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(stars); center.add(Box.createRigidArea(new Dimension(0,8)));
        center.add(new JLabel("Your Review:")); center.add(UIUtils.scrollPane(reviewText));
        center.add(Box.createRigidArea(new Dimension(0,10))); center.add(submitBtn);
        p.add(center,BorderLayout.CENTER);
        dlg.setContentPane(p); dlg.setVisible(true);
    }
}
