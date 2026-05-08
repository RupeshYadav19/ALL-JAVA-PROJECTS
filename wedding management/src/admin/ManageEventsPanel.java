package admin;

import dao.EventDAO;
import models.Event;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.util.List;

/**
 * ManageEventsPanel — full CRUD for events with JTable toolbar.
 */
public class ManageEventsPanel extends JPanel {
    private final User admin;
    private final EventDAO dao = new EventDAO();
    private DefaultTableModel model;
    private JTable table;
    private List<Event> events;

    public ManageEventsPanel(User admin) {
        this.admin = admin;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("📅  Manage Events"), BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setOpaque(false);
        JButton addBtn    = UIUtils.primaryButton("＋ Add Event");
        JButton editBtn   = UIUtils.secondaryButton("✏ Edit");
        JButton deleteBtn = UIUtils.dangerButton("🗑 Delete");
        JButton toggleBtn = UIUtils.secondaryButton("🔄 Toggle Status");
        JButton refreshBtn = UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(addBtn); toolbar.add(editBtn); toolbar.add(deleteBtn);
        toolbar.add(toggleBtn); toolbar.add(refreshBtn);

        // Table
        String[] cols = {"ID","Name","Type","City","Date","Capacity","Price (₹)","Status"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadData();

        // Actions
        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this,"Select an event first."); return; }
            openDialog(events.get(row));
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            if (JOptionPane.showConfirmDialog(this,"Delete this event?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                try { dao.delete(events.get(row).getEventId()); loadData(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
            }
        });
        toggleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            Event ev = events.get(row);
            String newStatus = ev.getStatus().equals("active") ? "inactive" : "active";
            try { dao.toggleStatus(ev.getEventId(), newStatus); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });
        refreshBtn.addActionListener(e -> loadData());

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setOpaque(false);
        body.add(toolbar, BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            events = dao.getAll();
            for (Event e : events) {
                model.addRow(new Object[]{
                    e.getEventId(), e.getEventName(), e.getEventType(), e.getCity(),
                    e.getDate(), e.getCapacity(),
                    String.format("%,.0f", e.getTotalPrice()), e.getStatus().toUpperCase()
                });
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage()); }
    }

    private void openDialog(Event existing) {
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add New Event" : "Edit Event", true);
        dlg.setSize(520, 560);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(5,5,5,5); gc.gridwidth = 2;

        JTextField nameF = field(p,gc,0,"Event Name *");
        String[] types = {"Wedding","Sangeet","Haldi","Mehendi","Reception","Engagement","Tilak","Cocktail"};
        JComboBox<String> typeC = combo(p,gc,1,"Event Type", types);
        JTextField venueF = field(p,gc,2,"Venue");
        JTextField cityF  = field(p,gc,3,"City");
        JTextField dateF  = field(p,gc,4,"Date (YYYY-MM-DD)");
        JTextField capF   = field(p,gc,5,"Capacity");
        JTextField priceF = field(p,gc,6,"Total Price (₹)");
        String[] statuses = {"active","inactive","completed"};
        JComboBox<String> statC = combo(p,gc,7,"Status", statuses);

        if (existing != null) {
            nameF.setText(existing.getEventName());
            typeC.setSelectedItem(existing.getEventType());
            venueF.setText(existing.getVenue());
            cityF.setText(existing.getCity());
            if (existing.getDate() != null) dateF.setText(existing.getDate().toString());
            capF.setText(String.valueOf(existing.getCapacity()));
            priceF.setText(String.valueOf(existing.getTotalPrice()));
            statC.setSelectedItem(existing.getStatus());
        }

        gc.gridy = 16; gc.gridwidth = 2;
        JButton saveBtn = UIUtils.primaryButton(existing==null?"Add Event":"Save Changes");
        saveBtn.addActionListener(e -> {
            try {
                Event ev = existing != null ? existing : new Event();
                ev.setEventName(nameF.getText().trim());
                ev.setEventType((String)typeC.getSelectedItem());
                ev.setVenue(venueF.getText().trim());
                ev.setCity(cityF.getText().trim());
                ev.setDate(Date.valueOf(dateF.getText().trim()));
                ev.setCapacity(Integer.parseInt(capF.getText().trim().isEmpty()?"0":capF.getText().trim()));
                ev.setTotalPrice(Double.parseDouble(priceF.getText().trim().isEmpty()?"0":priceF.getText().trim()));
                ev.setStatus((String)statC.getSelectedItem());
                if (existing == null) dao.insert(ev, admin.getUserId()); else dao.update(ev);
                dlg.dispose(); loadData();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg,"Error: "+ex.getMessage()); }
        });
        p.add(saveBtn, gc);
        dlg.setContentPane(new JScrollPane(p));
        dlg.setVisible(true);
    }

    private JTextField field(JPanel p, GridBagConstraints gc, int row, String label) {
        gc.gridy = row*2; p.add(new JLabel(label), gc);
        gc.gridy = row*2+1;
        JTextField f = UIUtils.styledField(20); p.add(f, gc); return f;
    }
    private JComboBox<String> combo(JPanel p, GridBagConstraints gc, int row, String label, String[] items) {
        gc.gridy = row*2; p.add(new JLabel(label), gc);
        gc.gridy = row*2+1;
        JComboBox<String> c = UIUtils.styledCombo(items); p.add(c, gc); return c;
    }
}
