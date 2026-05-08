package admin;

import dao.UserDAO;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {
    private final UserDAO dao = new UserDAO();
    private DefaultTableModel coupleModel, vendorModel;
    private JTable coupleTable, vendorTable;
    private List<User> couples, vendors;

    public ManageUsersPanel(User admin) {
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("👥  Manage Users"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.FONT_SUBHEAD);
        tabs.addTab("👫 Couples", buildTab("user"));
        tabs.addTab("🏪 Vendors", buildTab("vendor"));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTab(String role) {
        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setBackground(UIUtils.CREAM);
        p.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JTextField searchF = UIUtils.styledField(20);
        JButton searchBtn    = UIUtils.primaryButton("Search");
        JButton activateBtn  = UIUtils.successButton("✔ Activate");
        JButton deactivateBtn= UIUtils.dangerButton("✘ Deactivate");
        JButton deleteBtn    = UIUtils.dangerButton("🗑 Delete");
        JButton refreshBtn   = UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(new JLabel("Search (name/email):")); toolbar.add(searchF);
        toolbar.add(searchBtn); toolbar.add(activateBtn); toolbar.add(deactivateBtn);
        toolbar.add(deleteBtn); toolbar.add(refreshBtn);

        String[] cols = {"ID","Full Name","Email","Phone","City","Role","Active","Registered"};
        DefaultTableModel mdl = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable tbl = new JTable(mdl);
        UIUtils.styleTable(tbl);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if ("user".equals(role)) { coupleModel=mdl; coupleTable=tbl; }
        else { vendorModel=mdl; vendorTable=tbl; }

        loadTab(role, mdl, "");

        searchBtn.addActionListener(e -> loadTab(role, mdl, searchF.getText().trim()));
        refreshBtn.addActionListener(e -> loadTab(role, mdl, ""));

        activateBtn.addActionListener(e -> {
            List<User> list = "user".equals(role)?couples:vendors;
            int row = tbl.getSelectedRow(); if (row<0) return;
            try { dao.setActive(list.get(row).getUserId(), true); loadTab(role,mdl,""); }
            catch(Exception ex){ JOptionPane.showMessageDialog(p,ex.getMessage()); }
        });
        deactivateBtn.addActionListener(e -> {
            List<User> list = "user".equals(role)?couples:vendors;
            int row = tbl.getSelectedRow(); if (row<0) return;
            try { dao.setActive(list.get(row).getUserId(), false); loadTab(role,mdl,""); }
            catch(Exception ex){ JOptionPane.showMessageDialog(p,ex.getMessage()); }
        });
        deleteBtn.addActionListener(e -> {
            List<User> list = "user".equals(role)?couples:vendors;
            int row = tbl.getSelectedRow(); if (row<0) return;
            if (JOptionPane.showConfirmDialog(p,"Delete user permanently?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                try { dao.deleteUser(list.get(row).getUserId()); loadTab(role,mdl,""); }
                catch(Exception ex){ JOptionPane.showMessageDialog(p,ex.getMessage()); }
            }
        });

        p.add(toolbar, BorderLayout.NORTH);
        p.add(UIUtils.scrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    private void loadTab(String role, DefaultTableModel mdl, String keyword) {
        mdl.setRowCount(0);
        try {
            List<User> list = keyword.isEmpty() ? dao.getAllByRole(role) : dao.searchUsers(keyword, role);
            if ("user".equals(role)) couples=list; else vendors=list;
            for (User u : list) {
                mdl.addRow(new Object[]{u.getUserId(),u.getFullName(),u.getEmail(),u.getPhone(),
                    u.getCity(),u.getRole(),u.isActive()?"Yes":"No",u.getCreatedAt()});
            }
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage()); }
    }
}
