package admin;

import dao.VendorDAO;
import models.User;
import models.Vendor;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ManageVendorsPanel extends JPanel {
    private final VendorDAO dao = new VendorDAO();
    private DefaultTableModel model;
    private JTable table;
    private List<Vendor> vendors;

    public ManageVendorsPanel(User admin) {
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("🏢  Manage Vendors"), BorderLayout.NORTH);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterBar.setOpaque(false);
        String[] cats = {"All","Photographer","Makeup Artist","Caterer","Decorator","Mehndi Artist","DJ","Choreographer","Venue","Bridal Wear","Groom Wear"};
        JComboBox<String> catFilter = UIUtils.styledCombo(cats);
        JTextField cityFilter = UIUtils.styledField(12);
        cityFilter.setMaximumSize(new Dimension(140, 36));
        JButton searchBtn  = UIUtils.primaryButton("Search");
        JButton verifyBtn  = UIUtils.successButton("✔ Verify");
        JButton featureBtn = UIUtils.secondaryButton("⭐ Feature");
        JButton awardBtn   = UIUtils.secondaryButton("🏆 Award");
        JButton delBtn     = UIUtils.dangerButton("🗑 Remove");
        JButton refreshBtn = UIUtils.secondaryButton("↻ Refresh");

        filterBar.add(new JLabel("Category:")); filterBar.add(catFilter);
        filterBar.add(new JLabel("City:"));     filterBar.add(cityFilter);
        filterBar.add(searchBtn);
        filterBar.add(Box.createRigidArea(new Dimension(20,0)));
        filterBar.add(verifyBtn); filterBar.add(featureBtn); filterBar.add(awardBtn); filterBar.add(delBtn); filterBar.add(refreshBtn);

        String[] cols = {"ID","Business Name","Category","City","Rating","Reviews","Verified","Featured","Award"};
        model = new DefaultTableModel(cols, 0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadData("All","");

        searchBtn.addActionListener(e -> loadData((String)catFilter.getSelectedItem(), cityFilter.getText().trim()));
        refreshBtn.addActionListener(e -> loadData("All",""));

        verifyBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row<0) return;
            Vendor v = vendors.get(row);
            try { dao.setVerified(v.getVendorId(), !v.isVerified()); loadData("All",""); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });
        featureBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row<0) return;
            Vendor v = vendors.get(row);
            try { dao.setFeatured(v.getVendorId(), !v.isFeatured()); loadData("All",""); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });
        awardBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row<0) return;
            Vendor v = vendors.get(row);
            try { dao.setAward(v.getVendorId(), !v.isAwardWinner()); loadData("All",""); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row<0) return;
            if (JOptionPane.showConfirmDialog(this,"Remove vendor permanently?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                try { dao.deleteVendor(vendors.get(row).getVendorId()); loadData("All",""); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
            }
        });

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(filterBar, BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private void loadData(String category, String city) {
        model.setRowCount(0);
        try {
            vendors = dao.search(category, city, 0, Integer.MAX_VALUE, 0, "rating");
            for (Vendor v : vendors) {
                model.addRow(new Object[]{
                    v.getVendorId(), v.getBusinessName(), v.getCategory(), v.getCity(),
                    String.format("%.1f",v.getRating()), v.getReviewCount(),
                    v.isVerified()?"✔":"✗", v.isFeatured()?"⭐":"—", v.isAwardWinner()?"🏆":"—"
                });
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage()); }
    }
}
