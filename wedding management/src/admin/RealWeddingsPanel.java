package admin;

import db.DBConnection;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RealWeddingsPanel extends JPanel {
    private final User admin;
    private DefaultTableModel model;
    private JTable table;

    public RealWeddingsPanel(User admin) {
        this.admin = admin;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("📖  Real Wedding Stories"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton addBtn     = UIUtils.primaryButton("＋ Add Story");
        JButton featBtn    = UIUtils.secondaryButton("⭐ Toggle Featured");
        JButton deleteBtn  = UIUtils.dangerButton("🗑 Delete");
        JButton refreshBtn = UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(addBtn); toolbar.add(featBtn); toolbar.add(deleteBtn); toolbar.add(refreshBtn);

        String[] cols = {"ID","Couple","City","Venue","Date","Views","Featured"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        UIUtils.styleTable(table);
        loadData();

        addBtn.addActionListener(e -> openAddDialog());
        refreshBtn.addActionListener(e -> loadData());
        featBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row<0) return;
            int id = (int) model.getValueAt(row,0);
            boolean cur = "⭐".equals(model.getValueAt(row,6));
            try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE real_weddings SET is_featured=? WHERE story_id=?")) {
                ps.setBoolean(1,!cur); ps.setInt(2,id); ps.executeUpdate();
            } catch (Exception ex){ JOptionPane.showMessageDialog(this,ex.getMessage()); }
            loadData();
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow(); if(row<0) return;
            int id = (int) model.getValueAt(row,0);
            if(JOptionPane.showConfirmDialog(this,"Delete this story?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                try(Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement("DELETE FROM real_weddings WHERE story_id=?")){
                    ps.setInt(1,id); ps.executeUpdate();
                }catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage());}
                loadData();
            }
        });

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        try(Connection c=DBConnection.getConnection(); Statement st=c.createStatement();
            ResultSet rs=st.executeQuery("SELECT * FROM real_weddings ORDER BY is_featured DESC, story_id DESC")){
            while(rs.next()){
                model.addRow(new Object[]{rs.getInt("story_id"),rs.getString("couple_names"),
                    rs.getString("city"),rs.getString("venue"),rs.getString("date"),
                    rs.getInt("views"),rs.getBoolean("is_featured")?"⭐":"—"});
            }
        }catch(Exception ex){JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage());}
    }

    private void openAddDialog() {
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Add Real Wedding Story",true);
        dlg.setSize(480,440);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(18,28,18,28));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(5,5,5,5); gc.gridwidth=2;

        JTextField couple= field(p,gc,0,"Couple Names *");
        JTextField city  = field(p,gc,1,"City");
        JTextField venue = field(p,gc,2,"Venue");
        JTextField date  = field(p,gc,3,"Date (YYYY-MM-DD)");

        gc.gridy=8; p.add(new JLabel("Story Text"),gc);
        gc.gridy=9;
        JTextArea story = new JTextArea(4,30);
        story.setFont(UIUtils.FONT_BODY);
        p.add(UIUtils.scrollPane(story),gc);

        gc.gridy=10;
        JButton save = UIUtils.primaryButton("Add Story");
        save.addActionListener(e -> {
            if(couple.getText().trim().isEmpty()){JOptionPane.showMessageDialog(dlg,"Couple names required.");return;}
            try(Connection c=DBConnection.getConnection();
                PreparedStatement ps=c.prepareStatement("INSERT INTO real_weddings(couple_names,city,venue,date,story_text,is_featured,views) VALUES(?,?,?,?,?,0,0)")){
                ps.setString(1,couple.getText().trim()); ps.setString(2,city.getText().trim());
                ps.setString(3,venue.getText().trim());
                ps.setString(4,date.getText().trim().isEmpty()?null:date.getText().trim());
                ps.setString(5,story.getText());
                ps.executeUpdate();
                dlg.dispose(); loadData();
            }catch(Exception ex){JOptionPane.showMessageDialog(dlg,"Error: "+ex.getMessage());}
        });
        p.add(save,gc);
        dlg.setContentPane(new JScrollPane(p));
        dlg.setVisible(true);
    }

    private JTextField field(JPanel p,GridBagConstraints gc,int row,String label){
        gc.gridy=row*2; p.add(new JLabel(label),gc);
        gc.gridy=row*2+1; JTextField f=UIUtils.styledField(20); p.add(f,gc); return f;
    }
}
