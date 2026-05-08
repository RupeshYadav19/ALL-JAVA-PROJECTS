package vendor;

import dao.VendorDAO;
import dao.UserDAO;
import models.User;
import models.Vendor;
import utils.UIUtils;
import utils.ValidationUtils;
import javax.swing.*;
import java.awt.*;

public class VendorProfilePanel extends JPanel {
    private final User user;
    private Vendor vendor;
    private final VendorDAO vdao = new VendorDAO();
    private final UserDAO udao = new UserDAO();

    public VendorProfilePanel(User user, Vendor vendor) {
        this.user = user; this.vendor = vendor;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("👤  My Business Profile"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(6,6,6,6); gc.gridwidth=2;

        JTextField businessF = addField(form,gc,0,"Business Name *");
        JTextField descF     = addField(form,gc,1,"Description");
        JTextField cityF     = addField(form,gc,2,"City *");
        JTextField localityF = addField(form,gc,3,"Locality / Area");
        JTextField priceF    = addField(form,gc,4,"Starting Price (₹) *");
        JTextField specialF  = addField(form,gc,5,"Specialties (comma-separated)");

        if (vendor!=null){
            businessF.setText(vendor.getBusinessName());
            descF.setText(vendor.getDescription()!=null?vendor.getDescription():"");
            cityF.setText(vendor.getCity());
            localityF.setText(vendor.getLocality()!=null?vendor.getLocality():"");
            priceF.setText(String.valueOf((int)vendor.getStartingPrice()));
            specialF.setText(vendor.getSpecialties()!=null?vendor.getSpecialties():"");
        }

        gc.gridy=12;
        JButton saveBtn = UIUtils.primaryButton("💾 Save Profile");
        form.add(saveBtn,gc);

        saveBtn.addActionListener(e -> {
            if (vendor==null){JOptionPane.showMessageDialog(this,"Vendor profile not found.");return;}
            if (!ValidationUtils.isNotEmpty(businessF.getText())){JOptionPane.showMessageDialog(this,"Business name required.");return;}
            vendor.setBusinessName(businessF.getText().trim());
            vendor.setDescription(descF.getText().trim());
            vendor.setCity(cityF.getText().trim());
            vendor.setLocality(localityF.getText().trim());
            try { vendor.setStartingPrice(Double.parseDouble(priceF.getText().trim().isEmpty()?"0":priceF.getText().trim())); }
            catch(NumberFormatException ex){ JOptionPane.showMessageDialog(this,"Invalid price."); return; }
            vendor.setSpecialties(specialF.getText().trim());
            try { vdao.updateVendor(vendor); JOptionPane.showMessageDialog(this,"Profile updated! ✔"); }
            catch (Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });

        add(UIUtils.scrollPane(form),BorderLayout.CENTER);
    }

    private JTextField addField(JPanel p,GridBagConstraints gc,int row,String label){
        gc.gridy=row*2; p.add(new JLabel(label),gc);
        gc.gridy=row*2+1; JTextField f=UIUtils.styledField(30); p.add(f,gc); return f;
    }
}
