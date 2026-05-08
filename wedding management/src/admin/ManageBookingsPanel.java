package admin;

import dao.BookingDAO;
import models.Booking;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ManageBookingsPanel extends JPanel {
    private final BookingDAO dao = new BookingDAO();
    private DefaultTableModel model;
    private JTable table;
    private List<Booking> bookings;

    public ManageBookingsPanel(User admin) {
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("📋  Manage Bookings"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setOpaque(false);
        String[] statusOpts = {"All","pending","approved","rejected","cancelled","completed"};
        JComboBox<String> statusFilter = UIUtils.styledCombo(statusOpts);
        JButton filterBtn   = UIUtils.primaryButton("Filter");
        JButton approveBtn  = UIUtils.successButton("✔ Approve");
        JButton rejectBtn   = UIUtils.dangerButton("✘ Reject");
        JButton detailBtn   = UIUtils.secondaryButton("👁 Details");
        JButton refreshBtn  = UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(new JLabel("Status:")); toolbar.add(statusFilter);
        toolbar.add(filterBtn); toolbar.add(approveBtn); toolbar.add(rejectBtn); toolbar.add(detailBtn); toolbar.add(refreshBtn);

        String[] cols = {"ID","Couple","Event","Type","Date","Guests","Amount","Payment","Status"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.setDefaultRenderer(Object.class, new StatusRenderer());
        loadAll();

        filterBtn.addActionListener(e -> {
            String s = (String)statusFilter.getSelectedItem();
            try {
                bookings = s.equals("All") ? dao.getAll() : dao.getByStatus(s);
                populateTable();
            } catch(Exception ex) { JOptionPane.showMessageDialog(this,ex.getMessage()); }
        });
        refreshBtn.addActionListener(e -> loadAll());
        approveBtn.addActionListener(e -> changeStatus("approved",null));
        rejectBtn.addActionListener(e -> {
            String reason = JOptionPane.showInputDialog(this,"Rejection reason (optional):");
            changeStatus("rejected", reason);
        });
        detailBtn.addActionListener(e -> showDetails());

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar, BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private void loadAll() {
        try { bookings = dao.getAll(); populateTable(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage()); }
    }

    private void populateTable() {
        model.setRowCount(0);
        for (Booking b : bookings) {
            model.addRow(new Object[]{
                b.getBookingId(), b.getUserName(), b.getEventName(), b.getEventType(),
                b.getEventDate(), b.getGuestCount(),
                "₹"+String.format("%,.0f",b.getTotalPrice()),
                b.getPaymentStatus(), b.getStatus().toUpperCase()
            });
        }
    }

    private void changeStatus(String status, String reason) {
        int row = table.getSelectedRow(); if (row<0) return;
        try {
            dao.updateStatus(bookings.get(row).getBookingId(), status, reason);
            loadAll();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void showDetails() {
        int row = table.getSelectedRow(); if (row<0) return;
        Booking b = bookings.get(row);
        String msg = "Booking ID: " + b.getBookingId() +
            "\nCouple: " + b.getUserName() +
            "\nEvent: " + b.getEventName() + " (" + b.getEventType() + ")" +
            "\nEvent Date: " + b.getEventDate() +
            "\nGuests: " + b.getGuestCount() +
            "\nCeremonies: " + b.getCeremonyTypes() +
            "\nTotal Price: ₹" + String.format("%,.0f",b.getTotalPrice()) +
            "\nAdvance Paid: ₹" + String.format("%,.0f",b.getAdvancePaid()) +
            "\nPayment: " + b.getPaymentStatus() +
            "\nStatus: " + b.getStatus() +
            "\nSpecial Requests: " + (b.getSpecialRequests()!=null?b.getSpecialRequests():"None");
        JOptionPane.showMessageDialog(this,msg,"Booking Details #"+b.getBookingId(),JOptionPane.INFORMATION_MESSAGE);
    }

    static class StatusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            if (c==8 && v!=null) {
                String s = v.toString().toLowerCase();
                setForeground(switch(s){
                    case "approved"  -> UIUtils.SUCCESS;
                    case "rejected"  -> UIUtils.DANGER;
                    case "pending"   -> UIUtils.WARNING;
                    case "completed" -> new Color(0x27,0x6E,0xBC);
                    default          -> Color.DARK_GRAY;
                });
                setFont(UIUtils.FONT_SUBHEAD);
            } else { setForeground(Color.DARK_GRAY); setFont(UIUtils.FONT_BODY); }
            return this;
        }
    }
}
