package user;

import dao.BookingDAO;
import dao.ServiceDAO;
import models.Booking;
import models.Event;
import models.User;
import models.Service;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Booking form opened as a dialog from BrowseEventsPanel.
 */
public class BookingFormDialog extends JDialog {
    private final User user;
    private final Event event;
    private JSpinner guestSpinner;
    private JCheckBox[] ceremonyChecks;
    private JTextArea specialReqs;
    private JLabel totalLabel;
    private DefaultTableModel serviceModel;
    private List<Service> services;
    private double basePrice = 0;

    public BookingFormDialog(Frame parent, User user, Event event) {
        super(parent, "Book: " + event.getEventName(), true);
        this.user=user; this.event=event;
        setSize(600,580);
        setLocationRelativeTo(parent);
        build();
    }

    private void build() {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,24,16,24));

        // Event header
        JPanel eventHeader = UIUtils.createHeaderBar("📅  " + event.getEventName() + " — " + event.getEventType());
        p.add(eventHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(6,6,6,6); gc.gridwidth=2;

        // Event details
        gc.gridy=0; form.add(new JLabel("📍 Venue: " + event.getVenue() + ", " + event.getCity()), gc);
        gc.gridy=1; form.add(new JLabel("📅 Event Date: " + event.getDate() + "  |  Capacity: " + event.getCapacity()), gc);

        // Guest count
        gc.gridy=2; form.add(UIUtils.headingLabel("Number of Guests"), gc);
        gc.gridy=3;
        guestSpinner = new JSpinner(new SpinnerNumberModel(50, 1, event.getCapacity(), 1));
        guestSpinner.setFont(UIUtils.FONT_BODY);
        form.add(guestSpinner, gc);

        // Ceremonies
        gc.gridy=4; form.add(UIUtils.headingLabel("Select Ceremonies"), gc);
        gc.gridy=5;
        JPanel cerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        cerPanel.setOpaque(false);
        String[] ceremonies = {"Haldi","Mehendi","Sangeet","Wedding","Reception","Engagement"};
        ceremonyChecks = new JCheckBox[ceremonies.length];
        for (int i=0;i<ceremonies.length;i++) {
            JCheckBox cb = new JCheckBox(ceremonies[i]);
            cb.setFont(UIUtils.FONT_BODY);
            cb.setBackground(UIUtils.WHITE);
            cb.addActionListener(e -> updateTotal());
            ceremonyChecks[i]=cb; cerPanel.add(cb);
        }
        form.add(cerPanel, gc);

        // Special requests
        gc.gridy=6; form.add(UIUtils.headingLabel("Special Requests"), gc);
        gc.gridy=7;
        specialReqs = new JTextArea(3, 30);
        specialReqs.setFont(UIUtils.FONT_BODY);
        specialReqs.setLineWrap(true);
        form.add(UIUtils.scrollPane(specialReqs), gc);

        // Price
        gc.gridy=8;
        basePrice = event.getTotalPrice();
        totalLabel = new JLabel("💰 Total: ₹" + String.format("%,.0f", basePrice));
        totalLabel.setFont(UIUtils.FONT_HEADING);
        totalLabel.setForeground(UIUtils.SUCCESS);
        form.add(totalLabel, gc);

        // Buttons
        gc.gridy=9;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        btnRow.setOpaque(false);
        JButton submitBtn = UIUtils.primaryButton("📋 Confirm Booking");
        JButton cancelBtn = UIUtils.secondaryButton("Cancel");
        submitBtn.addActionListener(e -> submitBooking());
        cancelBtn.addActionListener(e -> dispose());
        btnRow.add(submitBtn); btnRow.add(cancelBtn);
        form.add(btnRow, gc);

        p.add(UIUtils.scrollPane(form), BorderLayout.CENTER);
        setContentPane(p);
    }

    private void updateTotal() {
        double t = basePrice;
        totalLabel.setText("💰 Total: ₹" + String.format("%,.0f", t));
    }

    private void submitBooking() {
        if (user.getUserId() <= 0) { JOptionPane.showMessageDialog(this,"Please login first."); return; }
        List<String> cers = new ArrayList<>();
        for (JCheckBox cb : ceremonyChecks) if (cb.isSelected()) cers.add(cb.getText());
        Booking b = new Booking();
        b.setUserId(user.getUserId());
        b.setEventId(event.getEventId());
        b.setEventDate(event.getDate());
        b.setGuestCount((int)guestSpinner.getValue());
        b.setCeremonyTypes(String.join(",", cers));
        b.setTotalPrice(basePrice);
        b.setSpecialRequests(specialReqs.getText().trim());
        try {
            int bookingId = new BookingDAO().insert(b);
            // Notify admin
            new dao.NotificationDAO().insert(1,"New Booking #"+bookingId,
                user.getFullName()+" booked "+event.getEventName(),"booking");
            JOptionPane.showMessageDialog(this,"Booking submitted! ID: #"+bookingId+"\nStatus: Pending approval.","Success 🎉",JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }
}
