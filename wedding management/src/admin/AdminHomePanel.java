package admin;

import dao.*;
import models.Booking;
import models.User;
import utils.UIUtils;
import utils.StatCard;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin home panel — stats dashboard with recent bookings and pending approvals.
 */
public class AdminHomePanel extends JPanel {
    private final User admin;

    public AdminHomePanel(User admin) {
        this.admin = admin;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        build();
    }

    private void build() {
        // ── Header ───────────────────────────────────────────────────────────
        JLabel title = UIUtils.titleLabel("📊  Admin Dashboard");
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // ── Stat cards row ────────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        statsRow.setOpaque(false);

        try {
            UserDAO userDAO       = new UserDAO();
            VendorDAO vendorDAO   = new VendorDAO();
            EventDAO eventDAO     = new EventDAO();
            BookingDAO bookingDAO = new BookingDAO();

            int totalUsers    = userDAO.countByRole("user");
            int totalVendors  = vendorDAO.count();
            int totalEvents   = eventDAO.count();
            int totalBookings = bookingDAO.totalCount();
            int pendingBk     = bookingDAO.countByStatus("pending");
            double revenue    = bookingDAO.revenueThisMonth();
            int newToday      = userDAO.countNewToday();

            statsRow.add(new StatCard("👥", String.valueOf(totalUsers),   "Total Couples",         UIUtils.ROSE_GOLD));
            statsRow.add(new StatCard("🏪", String.valueOf(totalVendors), "Total Vendors",          new Color(0x8E, 0x44, 0xAD)));
            statsRow.add(new StatCard("📅", String.valueOf(totalEvents),  "Total Events",           new Color(0x27, 0x6E, 0xBC)));
            statsRow.add(new StatCard("📋", String.valueOf(totalBookings), "Total Bookings",        UIUtils.DEEP_BROWN));
            statsRow.add(new StatCard("⏳", String.valueOf(pendingBk),    "Pending Approvals",      UIUtils.WARNING));
            statsRow.add(new StatCard("💰", "₹" + String.format("%,.0f", revenue), "Revenue (Month)", UIUtils.SUCCESS));
            statsRow.add(new StatCard("🆕", String.valueOf(newToday),     "New Users Today",        new Color(0x16, 0xA0, 0x85)));
        } catch (Exception ex) {
            statsRow.add(new JLabel("Could not load stats: " + ex.getMessage()));
        }

        center.add(statsRow);
        center.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Recent bookings ───────────────────────────────────────────────────
        JLabel bookLbl = UIUtils.headingLabel("Recent Bookings");
        center.add(bookLbl);
        center.add(Box.createRigidArea(new Dimension(0, 8)));

        String[] cols = {"ID", "Couple", "Event", "Type", "Date", "Guests", "Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        table.setDefaultRenderer(Object.class, new StatusCellRenderer());

        try {
            List<Booking> bookings = new BookingDAO().getAll();
            int limit = Math.min(10, bookings.size());
            for (int i = 0; i < limit; i++) {
                Booking b = bookings.get(i);
                model.addRow(new Object[]{
                    b.getBookingId(), b.getUserName(), b.getEventName(), b.getEventType(),
                    b.getEventDate(), b.getGuestCount(),
                    "₹" + String.format("%,.0f", b.getTotalPrice()), b.getStatus().toUpperCase()
                });
            }
        } catch (Exception ex) { model.addRow(new Object[]{"Error loading bookings: " + ex.getMessage()}); }

        JScrollPane sp = UIUtils.scrollPane(table);
        sp.setPreferredSize(new Dimension(1100, 220));
        center.add(sp);
        center.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Pending approvals quick action ────────────────────────────────────
        JLabel pendLbl = UIUtils.headingLabel("⏳  Pending Approvals");
        center.add(pendLbl);
        center.add(Box.createRigidArea(new Dimension(0, 8)));

        String[] cols2 = {"Booking ID", "Couple", "Event", "Event Date", "Amount", "Action"};
        DefaultTableModel model2 = new DefaultTableModel(cols2, 0) { public boolean isCellEditable(int r, int c) { return c == 5; } };
        JTable pendTable = new JTable(model2);
        UIUtils.styleTable(pendTable);

        try {
            List<Booking> pending = new BookingDAO().getByStatus("pending");
            BookingDAO bd = new BookingDAO();
            for (Booking b : pending) {
                model2.addRow(new Object[]{b.getBookingId(), b.getUserName(), b.getEventName(), b.getEventDate(), "₹"+String.format("%,.0f",b.getTotalPrice()), "Approve"});
            }
            pendTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
            pendTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), pendTable, model2, bd));
        } catch (Exception ex) { ex.printStackTrace(); }

        JScrollPane sp2 = UIUtils.scrollPane(pendTable);
        sp2.setPreferredSize(new Dimension(1100, 160));
        center.add(sp2);

        add(new JScrollPane(center), BorderLayout.CENTER);
    }

    // ── Status color renderer ─────────────────────────────────────────────────
    static class StatusCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            if (c == 7 && v != null) {
                String s = v.toString();
                setForeground(switch (s.toLowerCase()) {
                    case "approved"  -> UIUtils.SUCCESS;
                    case "rejected"  -> UIUtils.DANGER;
                    case "pending"   -> UIUtils.WARNING;
                    case "completed" -> new Color(0x27, 0x6E, 0xBC);
                    default          -> Color.DARK_GRAY;
                });
                setFont(UIUtils.FONT_SUBHEAD);
            } else {
                setForeground(Color.DARK_GRAY);
                setFont(UIUtils.FONT_BODY);
            }
            return this;
        }
    }

    // ── Inline approve button renderer ────────────────────────────────────────
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setText(v != null ? v.toString() : "Approve");
            setBackground(UIUtils.SUCCESS);
            setForeground(Color.WHITE);
            setFont(UIUtils.FONT_SMALL);
            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private String label;
        private final JTable table;
        private final DefaultTableModel model;
        private final BookingDAO bd;
        public ButtonEditor(JCheckBox cb, JTable t, DefaultTableModel m, BookingDAO bd) {
            super(cb); this.table = t; this.model = m; this.bd = bd;
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            label = v != null ? v.toString() : "Approve";
            JButton btn = UIUtils.successButton(label);
            btn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = (int) model.getValueAt(row, 0);
                    try { bd.updateStatus(id, "approved", null); model.removeRow(row); } catch (Exception ex) { ex.printStackTrace(); }
                }
                fireEditingStopped();
            });
            return btn;
        }
        public Object getCellEditorValue() { return label; }
    }
}
