package vendor;

import models.User;
import models.Vendor;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class RevenuePanel extends JPanel {
    public RevenuePanel(User user, Vendor vendor) {
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("💰  Revenue Report"), BorderLayout.NORTH);

        JPanel toolbar=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton exportBtn=UIUtils.secondaryButton("📥 Export CSV");
        toolbar.add(exportBtn);

        String[] cols={"Month","Completed Bookings","Revenue (₹)","Avg per Booking"};
        DefaultTableModel model=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable table=new JTable(model);
        UIUtils.styleTable(table);
        // Sample revenue data
        model.addRow(new Object[]{"March 2026","4","₹1,40,000","₹35,000"});
        model.addRow(new Object[]{"February 2026","3","₹1,05,000","₹35,000"});
        model.addRow(new Object[]{"January 2026","5","₹1,75,000","₹35,000"});

        JLabel totalLbl=new JLabel("Total Revenue: ₹4,20,000",SwingConstants.RIGHT);
        totalLbl.setFont(UIUtils.FONT_HEADING);
        totalLbl.setForeground(UIUtils.SUCCESS);

        exportBtn.addActionListener(e -> {
            JFileChooser fc=new JFileChooser();
            fc.setSelectedFile(new java.io.File("vendor_revenue.csv"));
            if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
                try(java.io.PrintWriter pw=new java.io.PrintWriter(fc.getSelectedFile())){
                    pw.println("Month,Bookings,Revenue");
                    for(int i=0;i<model.getRowCount();i++){
                        pw.println(model.getValueAt(i,0)+","+model.getValueAt(i,1)+","+model.getValueAt(i,2));
                    }
                    JOptionPane.showMessageDialog(this,"Exported: "+fc.getSelectedFile().getName());
                } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
            }
        });

        JPanel body=new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table),BorderLayout.CENTER);
        body.add(totalLbl,BorderLayout.SOUTH);
        add(body,BorderLayout.CENTER);
    }
}
