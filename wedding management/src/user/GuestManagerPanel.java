package user;

import dao.GuestDAO;
import models.Guest;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class GuestManagerPanel extends JPanel {
    private final User user;
    private final GuestDAO dao = new GuestDAO();
    private DefaultTableModel model;
    private JTable table;
    private List<Guest> guests;
    private JLabel countLbl;

    public GuestManagerPanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("👥  Guest Manager"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton addBtn    = UIUtils.primaryButton("＋ Add Guest");
        JButton deleteBtn = UIUtils.dangerButton("🗑 Remove");
        JButton rsvpBtn   = UIUtils.secondaryButton("✔ Mark RSVP");
        JButton exportBtn = UIUtils.secondaryButton("📥 Export CSV");
        JButton refreshBtn= UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(addBtn); toolbar.add(deleteBtn); toolbar.add(rsvpBtn); toolbar.add(exportBtn); toolbar.add(refreshBtn);

        countLbl = new JLabel();
        countLbl.setFont(UIUtils.FONT_SUBHEAD);
        countLbl.setForeground(UIUtils.DEEP_BROWN);
        toolbar.add(Box.createRigidArea(new Dimension(20,0)));
        toolbar.add(countLbl);

        String[] cols = {"ID","Name","Phone","Email","Relation","Side","Status","Meal"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        UIUtils.styleTable(table);
        loadData();

        addBtn.addActionListener(e -> openAddDialog());
        deleteBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0||guests==null) return;
            if(JOptionPane.showConfirmDialog(this,"Remove guest?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                try{ dao.delete(guests.get(row).getGuestId()); loadData(); }
                catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
            }
        });
        rsvpBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row<0||guests==null) return;
            try{ dao.updateRsvp(guests.get(row).getGuestId(),"confirmed"); loadData(); }
            catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });
        exportBtn.addActionListener(e -> exportCsv());
        refreshBtn.addActionListener(e -> loadData());

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
            guests=dao.getByUser(user.getUserId());
            long confirmed=guests.stream().filter(g->"confirmed".equalsIgnoreCase(g.getStatus())).count();
            for(Guest g:guests){
                model.addRow(new Object[]{g.getGuestId(),g.getGuestName(),
                    g.getPhone()!=null?g.getPhone():"—",g.getEmail()!=null?g.getEmail():"—",
                    g.getRelation()!=null?g.getRelation():"",g.getSide()!=null?g.getSide():"",
                    g.getStatus()!=null?g.getStatus().toUpperCase():"PENDING",
                    g.getMealPreference()!=null?g.getMealPreference():""});
            }
            countLbl.setText("Total: "+guests.size()+"  |  Confirmed: "+confirmed);
        }catch(Exception ex){JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage());}
    }

    private void openAddDialog() {
        JDialog dlg=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Add Guest",true);
        dlg.setSize(440,380); dlg.setLocationRelativeTo(this);
        JPanel p=new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,22,16,22));
        GridBagConstraints gc=new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(5,5,5,5); gc.gridwidth=2;

        gc.gridy=0; p.add(new JLabel("Guest Name *"),gc);
        gc.gridy=1; JTextField nameF=UIUtils.styledField(22); p.add(nameF,gc);
        gc.gridy=2; p.add(new JLabel("Phone"),gc);
        gc.gridy=3; JTextField phoneF=UIUtils.styledField(14); p.add(phoneF,gc);
        gc.gridy=4; p.add(new JLabel("Email"),gc);
        gc.gridy=5; JTextField emailF=UIUtils.styledField(22); p.add(emailF,gc);
        gc.gridy=6; p.add(new JLabel("Relation"),gc);
        gc.gridy=7; JTextField relF=UIUtils.styledField(14); p.add(relF,gc);
        String[] sides={"Bride","Groom"};
        gc.gridy=8; p.add(new JLabel("Side"),gc);
        gc.gridy=9; JComboBox<String> sideC=UIUtils.styledCombo(sides); p.add(sideC,gc);
        String[] meals={"Veg","Non-Veg","Jain","No Preference"};
        gc.gridy=10; p.add(new JLabel("Meal Preference"),gc);
        gc.gridy=11; JComboBox<String> mealC=UIUtils.styledCombo(meals); p.add(mealC,gc);
        gc.gridy=12;
        JButton save=UIUtils.primaryButton("Add Guest");
        save.addActionListener(e->{
            if(nameF.getText().trim().isEmpty()){JOptionPane.showMessageDialog(dlg,"Name required.");return;}
            Guest g=new Guest();
            g.setUserId(user.getUserId());
            g.setGuestName(nameF.getText().trim()); g.setPhone(phoneF.getText().trim());
            g.setEmail(emailF.getText().trim());    g.setRelation(relF.getText().trim());
            g.setSide((String)sideC.getSelectedItem()); g.setMealPreference((String)mealC.getSelectedItem());
            g.setStatus("pending");
            try{ dao.insert(g); dlg.dispose(); loadData(); }
            catch(Exception ex){ JOptionPane.showMessageDialog(dlg,"Error: "+ex.getMessage()); }
        });
        p.add(save,gc);
        dlg.setContentPane(new JScrollPane(p)); dlg.setVisible(true);
    }

    private void exportCsv() {
        JFileChooser fc=new JFileChooser();
        fc.setSelectedFile(new java.io.File("wedding_guests.csv"));
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            try(java.io.PrintWriter pw=new java.io.PrintWriter(fc.getSelectedFile())){
                pw.println("Name,Phone,Email,Relation,Side,Status,Meal");
                for(int i=0;i<model.getRowCount();i++){
                    pw.println(model.getValueAt(i,1)+","+model.getValueAt(i,2)+","+model.getValueAt(i,3)+","+
                        model.getValueAt(i,4)+","+model.getValueAt(i,5)+","+model.getValueAt(i,6)+","+model.getValueAt(i,7));
                }
                JOptionPane.showMessageDialog(this,"CSV exported!");
            }catch(Exception ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
        }
    }
}
