package user;

import dao.BudgetDAO;
import models.Budget;
import models.User;
import utils.UIUtils;
import utils.CircularProgressBar;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * BudgetTrackerPanel — category-wise budget with overall ring and progress bars.
 */
public class BudgetTrackerPanel extends JPanel {
    private final User user;
    private final BudgetDAO dao = new BudgetDAO();
    private DefaultTableModel model;
    private JTable table;
    private List<Budget> items;
    private JLabel totalLbl;
    private CircularProgressBar ring;

    public BudgetTrackerPanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("💰  Wedding Budget Tracker"), BorderLayout.NORTH);

        // Summary header
        JPanel summary = new JPanel(new BorderLayout(16,0));
        summary.setBackground(UIUtils.DEEP_BROWN);
        summary.setBorder(BorderFactory.createEmptyBorder(14,20,14,20));
        summary.setPreferredSize(new Dimension(1100,80));

        totalLbl = new JLabel("Loading…");
        totalLbl.setFont(new Font("Segoe UI",Font.BOLD,18));
        totalLbl.setForeground(Color.WHITE);
        ring = new CircularProgressBar(72);
        ring.setArcColor(UIUtils.SUCCESS);
        summary.add(totalLbl,BorderLayout.CENTER);
        summary.add(ring,BorderLayout.EAST);
        add(summary, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton addBtn    = UIUtils.primaryButton("＋ Add Item");
        JButton editBtn   = UIUtils.secondaryButton("✏ Edit");
        JButton deleteBtn = UIUtils.dangerButton("🗑 Delete");
        JButton clearBtn  = UIUtils.secondaryButton("↻ Refresh");
        JButton exportBtn = UIUtils.secondaryButton("📥 Export CSV");
        toolbar.add(addBtn); toolbar.add(editBtn); toolbar.add(deleteBtn); toolbar.add(clearBtn); toolbar.add(exportBtn);

        String[] cols = {"ID","Category","Item","Estimated (₹)","Actual (₹)","Paid (₹)","Balance","Vendor"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.setDefaultRenderer(Object.class, new BudgetRenderer());
        loadData();

        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0||items==null){JOptionPane.showMessageDialog(this,"Select an item.");return;}
            openDialog(items.get(row));
        });
        deleteBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0||items==null) return;
            if(JOptionPane.showConfirmDialog(this,"Delete?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                try{ dao.delete(items.get(row).getBudgetId()); loadData(); }
                catch(Exception ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
            }
        });
        clearBtn.addActionListener(e -> loadData());
        exportBtn.addActionListener(e -> exportCsv());

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        if(user.getUserId()<=0) return;
        try{
            items=dao.getByUser(user.getUserId());
            double est=0,act=0,paid=0;
            for(Budget b:items){
                model.addRow(new Object[]{b.getBudgetId(),b.getCategory(),b.getItemName(),
                    String.format("%,.0f",b.getEstimatedAmount()),
                    String.format("%,.0f",b.getActualAmount()),
                    String.format("%,.0f",b.getPaidAmount()),
                    String.format("%,.0f",b.getActualAmount()-b.getPaidAmount()),
                    b.getVendorName()!=null?b.getVendorName():"—"});
                est+=b.getEstimatedAmount(); act+=b.getActualAmount(); paid+=b.getPaidAmount();
            }
            totalLbl.setText("Budget: ₹"+String.format("%,.0f",est)+"  |  Spent: ₹"+String.format("%,.0f",act)+"  |  Paid: ₹"+String.format("%,.0f",paid));
            int pct = est>0?(int)(100.0*act/est):0;
            ring.setValue(pct,"");
        }catch(Exception ex){JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage());}
    }

    private void openDialog(Budget existing) {
        JDialog dlg=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
            existing==null?"Add Budget Item":"Edit Budget Item",true);
        dlg.setSize(440,380); dlg.setLocationRelativeTo(this);
        JPanel p=new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,22,16,22));
        GridBagConstraints gc=new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(5,5,5,5); gc.gridwidth=2;

        String[] cats={"Venue","Catering","Photography","Decoration","Mehndi","Attire","Jewellery","Invitation","Entertainment","Transport","Accommodation","Honeymoon","Other"};
        gc.gridy=0; p.add(new JLabel("Category"),gc);
        gc.gridy=1; JComboBox<String> catC=UIUtils.styledCombo(cats);
        if(existing!=null) catC.setSelectedItem(existing.getCategory());
        p.add(catC,gc);
        gc.gridy=2; p.add(new JLabel("Item Name *"),gc);
        gc.gridy=3; JTextField itemF=UIUtils.styledField(22);
        if(existing!=null) itemF.setText(existing.getItemName());
        p.add(itemF,gc);
        gc.gridy=4; p.add(new JLabel("Estimated Amount (₹)"),gc);
        gc.gridy=5; JTextField estF=UIUtils.styledField(14);
        if(existing!=null) estF.setText(String.valueOf((int)existing.getEstimatedAmount()));
        p.add(estF,gc);
        gc.gridy=6; p.add(new JLabel("Actual Amount (₹)"),gc);
        gc.gridy=7; JTextField actF=UIUtils.styledField(14);
        if(existing!=null) actF.setText(String.valueOf((int)existing.getActualAmount()));
        p.add(actF,gc);
        gc.gridy=8; p.add(new JLabel("Amount Paid (₹)"),gc);
        gc.gridy=9; JTextField payF=UIUtils.styledField(14);
        if(existing!=null) payF.setText(String.valueOf((int)existing.getPaidAmount()));
        p.add(payF,gc);

        gc.gridy=10;
        JButton save=UIUtils.primaryButton(existing==null?"Add":"Update");
        save.addActionListener(e -> {
            String name=itemF.getText().trim();
            if(name.isEmpty()){JOptionPane.showMessageDialog(dlg,"Item name required.");return;}
            try{
                Budget b=existing!=null?existing:new Budget();
                b.setUserId(user.getUserId());
                b.setCategory((String)catC.getSelectedItem());
                b.setItemName(name);
                b.setEstimatedAmount(Double.parseDouble(estF.getText().trim().isEmpty()?"0":estF.getText().trim()));
                b.setActualAmount(Double.parseDouble(actF.getText().trim().isEmpty()?"0":actF.getText().trim()));
                b.setPaidAmount(Double.parseDouble(payF.getText().trim().isEmpty()?"0":payF.getText().trim()));
                if(existing==null) dao.insert(b); else dao.update(b);
                dlg.dispose(); loadData();
            }catch(Exception ex){JOptionPane.showMessageDialog(dlg,"Error: "+ex.getMessage());}
        });
        p.add(save,gc);
        dlg.setContentPane(new JScrollPane(p)); dlg.setVisible(true);
    }

    private void exportCsv() {
        JFileChooser fc=new JFileChooser();
        fc.setSelectedFile(new java.io.File("wedding_budget.csv"));
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            try(java.io.PrintWriter pw=new java.io.PrintWriter(fc.getSelectedFile())){
                pw.println("Category,Item,Estimated,Actual,Paid");
                for(int i=0;i<model.getRowCount();i++){
                    pw.println(model.getValueAt(i,1)+","+model.getValueAt(i,2)+","+
                        model.getValueAt(i,3)+","+model.getValueAt(i,4)+","+model.getValueAt(i,5));
                }
                JOptionPane.showMessageDialog(this,"CSV exported!");
            }catch(Exception ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
        }
    }

    static class BudgetRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
            super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            if(c==6 && v!=null){ // Balance col
                double bal=0; try{bal=Double.parseDouble(v.toString().replace(",",""));} catch(Exception ignored){}
                setForeground(bal>0?UIUtils.DANGER:UIUtils.SUCCESS);
            } else { setForeground(UIUtils.DEEP_BROWN); }
            return this;
        }
    }
}
