package user;

import dao.ChecklistDAO;
import models.ChecklistItem;
import models.User;
import utils.UIUtils;
import utils.CircularProgressBar;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * ChecklistPanel — Indian wedding checklist with categories and progress ring.
 */
public class ChecklistPanel extends JPanel {
    private final User user;
    private final ChecklistDAO dao = new ChecklistDAO();
    private DefaultTableModel model;
    private JTable table;
    private List<ChecklistItem> items;
    private CircularProgressBar ring;
    private JLabel progressLbl;

    public ChecklistPanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("✅  Wedding Checklist"), BorderLayout.NORTH);

        // Progress ring
        ring = new CircularProgressBar(110);
        progressLbl = new JLabel("0 / 0 tasks done",SwingConstants.CENTER);
        progressLbl.setFont(UIUtils.FONT_SUBHEAD);
        JPanel ringPanel = new JPanel(new BorderLayout(0,4));
        ringPanel.setOpaque(false);
        ringPanel.setPreferredSize(new Dimension(130,130));
        ringPanel.add(ring,BorderLayout.CENTER);
        ringPanel.add(progressLbl,BorderLayout.SOUTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton addBtn    = UIUtils.primaryButton("＋ Add Task");
        JButton doneBtn   = UIUtils.successButton("✔ Mark Done");
        JButton undoneBtn = UIUtils.secondaryButton("↩ Unmark");
        JButton deleteBtn = UIUtils.dangerButton("🗑 Delete");
        JButton refreshBtn= UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(addBtn); toolbar.add(doneBtn); toolbar.add(undoneBtn); toolbar.add(deleteBtn); toolbar.add(refreshBtn);
        toolbar.add(Box.createRigidArea(new Dimension(30,0)));
        toolbar.add(ringPanel);

        String[] cols = {"ID","Category","Task","Due Date","Done","Priority"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.setDefaultRenderer(Object.class, new ChecklistRenderer());
        loadData();

        addBtn.addActionListener(e -> openAddDialog());
        doneBtn.addActionListener(e -> markDone(true));
        undoneBtn.addActionListener(e -> markDone(false));
        deleteBtn.addActionListener(e -> deleteTask());
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
        try {
            items = dao.getByUser(user.getUserId());
            long done = items.stream().filter(i->i.isDone()).count();
            for(ChecklistItem i:items) {
                model.addRow(new Object[]{i.getItemId(),i.getCategory(),i.getTaskName(),
                    i.getDueDate()!=null?i.getDueDate().toString():"—",
                    i.isDone()?"✔ Done":"",i.getPriority()!=null?i.getPriority():"Normal"});
            }
            int pct = items.isEmpty()?0:(int)(100.0*done/items.size());
            ring.setValue(pct,"");
            progressLbl.setText(done+"/"+items.size()+" done");
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage()); }
    }

    private void openAddDialog() {
        JDialog dlg=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Add Checklist Task",true);
        dlg.setSize(420,340); dlg.setLocationRelativeTo(this);
        JPanel p=new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16,22,16,22));
        GridBagConstraints gc=new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(5,5,5,5); gc.gridwidth=2;

        String[] cats={"Venue","Catering","Photography","Decoration","Mehndi","Attire","Invitation","Jewellery","Entertainment","Transport","Accommodation","Budget","Other"};
        gc.gridy=0; p.add(new JLabel("Category *"),gc);
        gc.gridy=1; JComboBox<String> catC=UIUtils.styledCombo(cats); p.add(catC,gc);
        gc.gridy=2; p.add(new JLabel("Task Name *"),gc);
        gc.gridy=3; JTextField taskF=UIUtils.styledField(24); p.add(taskF,gc);
        gc.gridy=4; p.add(new JLabel("Due Date (YYYY-MM-DD)"),gc);
        gc.gridy=5; JTextField dateF=UIUtils.styledField(16); p.add(dateF,gc);
        String[] prios={"High","Normal","Low"};
        gc.gridy=6; p.add(new JLabel("Priority"),gc);
        gc.gridy=7; JComboBox<String> prioC=UIUtils.styledCombo(prios); p.add(prioC,gc);
        gc.gridy=8;
        JButton save=UIUtils.primaryButton("Add Task");
        save.addActionListener(e -> {
            String task=taskF.getText().trim();
            if(task.isEmpty()){JOptionPane.showMessageDialog(dlg,"Task name required.");return;}
            ChecklistItem item=new ChecklistItem();
            item.setUserId(user.getUserId());
            item.setCategory((String)catC.getSelectedItem());
            item.setTaskName(task);
            if(!dateF.getText().trim().isEmpty()) { try{item.setDueDate(Date.valueOf(dateF.getText().trim()));}catch(Exception ex){} }
            item.setPriority((String)prioC.getSelectedItem());
            try{ dao.insert(item); dlg.dispose(); loadData(); }
            catch(Exception ex){JOptionPane.showMessageDialog(dlg,"Error: "+ex.getMessage());}
        });
        p.add(save,gc);
        dlg.setContentPane(p); dlg.setVisible(true);
    }

    private void markDone(boolean done) {
        int row=table.getSelectedRow(); if(row<0||items==null) return;
        try{ dao.setDone(items.get(row).getItemId(),done); loadData(); }
        catch(Exception ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
    }

    private void deleteTask() {
        int row=table.getSelectedRow(); if(row<0||items==null) return;
        if(JOptionPane.showConfirmDialog(this,"Delete this task?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{ dao.delete(items.get(row).getItemId()); loadData(); }
            catch(Exception ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
        }
    }

    static class ChecklistRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
            super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            String done=(String)t.getModel().getValueAt(r,4);
            if("✔ Done".equals(done)){
                if(c==2){ setText("<html><s>"+v+"</s></html>"); }
                setForeground(Color.GRAY);
            } else { setForeground(UIUtils.DEEP_BROWN); }
            if(c==5){ // priority
                String pri=(String)v;
                if("High".equals(pri)) setForeground(UIUtils.DANGER);
                else if("Low".equals(pri)) setForeground(Color.LIGHT_GRAY);
            }
            return this;
        }
    }
}
