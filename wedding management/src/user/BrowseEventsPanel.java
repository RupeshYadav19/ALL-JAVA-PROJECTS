package user;

import dao.EventDAO;
import dao.ServiceDAO;
import dao.BookingDAO;
import models.Booking;
import models.Event;
import models.Service;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class BrowseEventsPanel extends JPanel {
    private final User user;
    private final EventDAO dao = new EventDAO();
    private JPanel cardsPanel;
    private JComboBox<String> typeCombo;
    private JTextField cityField;
    private List<Event> events;

    public BrowseEventsPanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,10));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("📅  Browse Events"), BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,6));
        filterBar.setBackground(UIUtils.WHITE);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0,0xD0,0xBF)),
            BorderFactory.createEmptyBorder(8,12,8,12)));

        String[] types = {"All","Wedding","Sangeet","Haldi","Mehendi","Reception","Engagement","Tilak","Cocktail"};
        typeCombo = UIUtils.styledCombo(types);
        cityField = UIUtils.styledField(14);
        cityField.setPreferredSize(new Dimension(130,32));
        JButton searchBtn = UIUtils.primaryButton("🔍 Search");
        JButton refreshBtn = UIUtils.secondaryButton("↻ All Events");

        filterBar.add(new JLabel("Type:"));  filterBar.add(typeCombo);
        filterBar.add(new JLabel("City:"));  filterBar.add(cityField);
        filterBar.add(searchBtn); filterBar.add(refreshBtn);

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,14,14));
        cardsPanel.setBackground(UIUtils.WHITE);

        searchBtn.addActionListener(e -> {
            try {
                String t = (String) typeCombo.getSelectedItem();
                events = dao.search(t, cityField.getText().trim(), 0, 0);
                renderCards();
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });
        refreshBtn.addActionListener(e -> loadAll());

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(filterBar, BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(cardsPanel), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
        loadAll();
    }

    private void loadAll() {
        try { events = dao.getActive(); renderCards(); }
        catch(Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void renderCards() {
        cardsPanel.removeAll();
        if (events.isEmpty()) {
            cardsPanel.add(new JLabel("No events found."));
            cardsPanel.revalidate(); return;
        }
        for (Event e : events) {
            JPanel card = new JPanel(new BorderLayout(0,6));
            card.setBackground(UIUtils.CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0,0xD0,0xBF),1),
                BorderFactory.createEmptyBorder(14,16,14,16)));
            card.setPreferredSize(new Dimension(280,200));

            // Type badge
            JLabel type = new JLabel("  " + e.getEventType() + "  ");
            type.setFont(UIUtils.FONT_SMALL);
            type.setForeground(Color.WHITE);
            type.setBackground(UIUtils.ROSE_GOLD);
            type.setOpaque(true);

            JLabel name = new JLabel(e.getEventName());
            name.setFont(UIUtils.FONT_SUBHEAD); name.setForeground(UIUtils.DEEP_BROWN);
            JLabel venue = new JLabel("📍 " + e.getVenue() + ", " + e.getCity());
            venue.setFont(UIUtils.FONT_SMALL);
            JLabel date = new JLabel("📅 " + e.getDate() + "  ⏰ " + e.getTime());
            date.setFont(UIUtils.FONT_SMALL);
            JLabel cap = new JLabel("👥 Capacity: " + e.getCapacity());
            cap.setFont(UIUtils.FONT_SMALL);
            JLabel price = new JLabel("💰 ₹" + String.format("%,.0f", e.getTotalPrice()));
            price.setFont(UIUtils.FONT_SUBHEAD); price.setForeground(UIUtils.SUCCESS);

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));
            info.setOpaque(false);
            info.add(type); info.add(Box.createRigidArea(new Dimension(0,4)));
            info.add(name); info.add(venue); info.add(date); info.add(cap);
            info.add(Box.createRigidArea(new Dimension(0,4))); info.add(price);

            JButton bookBtn = UIUtils.primaryButton(user.getUserId() > 0 ? "Book Now" : "Login to Book");
            bookBtn.addActionListener(ev -> openBooking(e));
            card.add(info, BorderLayout.CENTER);
            card.add(bookBtn, BorderLayout.SOUTH);
            cardsPanel.add(card);
        }
        cardsPanel.revalidate(); cardsPanel.repaint();
    }

    private void openBooking(Event event) {
        if (user.getUserId() <= 0) {
            JOptionPane.showMessageDialog(this,"Please login to book an event."); return;
        }
        BookingFormDialog dlg = new BookingFormDialog((Frame)SwingUtilities.getWindowAncestor(this), user, event);
        dlg.setVisible(true);
    }
}
