package vendor;

import dao.ServiceDAO;
import models.Service;
import models.User;
import models.Vendor;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ServiceListingPanel extends JPanel {
    private final ServiceDAO dao = new ServiceDAO();
    private final Vendor vendor;
    private DefaultTableModel model;
    private JTable table;
    private List<Service> services;

    public ServiceListingPanel(User user, Vendor vendor) {
        this.vendor = vendor;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("📦  My Services"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton addBtn    = UIUtils.primaryButton("＋ Add Service");
        JButton editBtn   = UIUtils.secondaryButton("✏ Edit");
        JButton deleteBtn = UIUtils.dangerButton("🗑 Delete");
        JButton refreshBtn= UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(addBtn); toolbar.add(editBtn); toolbar.add(deleteBtn); toolbar.add(refreshBtn);

        String[] cols = {"ID","Service Name","Category","Price (₹)","Type","Available"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        UIUtils.styleTable(table);
        loadData();

        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0){JOptionPane.showMessageDialog(this,"Select a service.");return;}
            openDialog(services.get(row));
        });
        deleteBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0) return;
            if(JOptionPane.showConfirmDialog(this,"Delete service?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                try{ dao.delete(services.get(row).getServiceId()); loadData(); }
                catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
            }
        });
        refreshBtn.addActionListener(e->loadData());

        JPanel body=new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
    }

    private void loadData(){
        model.setRowCount(0);
        if(vendor==null) return;
        try{
            services=dao.getByVendor(vendor.getVendorId());
            for(Service s:services){
                model.addRow(new Object[]{s.getServiceId(),s.getServiceName(),s.getCategory(),
                    String.format("%,.0f",s.getPrice()),s.getPriceType(),s.isAvailable()?"Yes":"No"});
            }
        }catch(Exception ex){ JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage()); }
    }

    private void openDialog(Service existing){
        JDialog dlg=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
            existing==null?"Add Service":"Edit Service",true);
        dlg.setSize(440,400); dlg.setLocationRelativeTo(this);

        JPanel p=new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(18,28,18,28));
        GridBagConstraints gc=new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(5,5,5,5); gc.gridwidth=2;

        JTextField nameF=field(p,gc,0,"Service Name *");
        JTextField catF =field(p,gc,1,"Category");
        JTextField descF=field(p,gc,2,"Description");
        JTextField priceF=field(p,gc,3,"Price (₹) *");
        String[] ptypes={"fixed","per_head","per_day"};
        JComboBox<String> typeC=combo(p,gc,4,"Price Type",ptypes);
        gc.gridy=10;
        JCheckBox availCheck=new JCheckBox("Service Available",true);
        availCheck.setBackground(UIUtils.WHITE);
        availCheck.setFont(UIUtils.FONT_BODY);
        p.add(availCheck,gc);

        if(existing!=null){
            nameF.setText(existing.getServiceName());
            catF.setText(existing.getCategory()!=null?existing.getCategory():"");
            descF.setText(existing.getDescription()!=null?existing.getDescription():"");
            priceF.setText(String.valueOf((int)existing.getPrice()));
            typeC.setSelectedItem(existing.getPriceType());
            availCheck.setSelected(existing.isAvailable());
        }

        gc.gridy=12;
        JButton save=UIUtils.primaryButton(existing==null?"Add":"Save");
        save.addActionListener(e->{
            try{
                Service s=existing!=null?existing:new Service();
                s.setServiceName(nameF.getText().trim()); s.setCategory(catF.getText().trim());
                s.setDescription(descF.getText().trim());
                s.setPrice(Double.parseDouble(priceF.getText().trim().isEmpty()?"0":priceF.getText().trim()));
                s.setPriceType((String)typeC.getSelectedItem());
                s.setAvailable(availCheck.isSelected());
                if(vendor!=null) s.setVendorId(vendor.getVendorId());
                if(existing==null) dao.insert(s); else dao.update(s);
                dlg.dispose(); loadData();
            }catch(Exception ex){ JOptionPane.showMessageDialog(dlg,"Error: "+ex.getMessage()); }
        });
        p.add(save,gc);
        dlg.setContentPane(new JScrollPane(p));
        dlg.setVisible(true);
    }

    private JTextField field(JPanel p,GridBagConstraints gc,int row,String lbl){
        gc.gridy=row*2; p.add(new JLabel(lbl),gc);
        gc.gridy=row*2+1; JTextField f=UIUtils.styledField(22); p.add(f,gc); return f;
    }
    private JComboBox<String> combo(JPanel p,GridBagConstraints gc,int row,String lbl,String[] items){
        gc.gridy=row*2; p.add(new JLabel(lbl),gc);
        gc.gridy=row*2+1; JComboBox<String> c=UIUtils.styledCombo(items); p.add(c,gc); return c;
    }
}
