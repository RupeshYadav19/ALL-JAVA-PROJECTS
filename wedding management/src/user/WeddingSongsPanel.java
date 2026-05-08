package user;

import models.User;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * WeddingSongsPanel — curated Indian wedding song playlist manager.
 */
public class WeddingSongsPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;

    // Built-in Indian wedding songs across ceremonies
    private static final String[][] BUILTIN_SONGS = {
        {"Haldi","Kesariya","Brahmastra","Arijit Singh,Pritam"},
        {"Haldi","Haldi Wali Rasam","Pawandeep Rajan","Folk"},
        {"Mehndi","Mehndi Hai Rachne Wali","Vibha Sharma","90s Classic"},
        {"Mehndi","Aaj Mere Yaar Ki Shaadi","Udit Narayan","90s Classic"},
        {"Sangeet","Nagada Sang Dhol","Ram-Leela","Shreya Ghoshal,Osman Mir"},
        {"Sangeet","Ghoomar","Padmaavat","Shreya Ghoshal"},
        {"Sangeet","Gallan Goodiyaan","Dil Dhadakne Do","Multiple Singers"},
        {"Sangeet","London Thumakda","Queen","Sonu Kakkar"},
        {"Baraat","Sehra Bandha","Wedding Classic","Folk"},
        {"Baraat","Ban Ja Tu Meri Rani","Guru Randhawa","Guru Randhawa"},
        {"Wedding","Tujh Mein Rab Dikhta Hai","Rab Ne Bana Di Jodi","Roop Kumar Rathod"},
        {"Wedding","Saaj Ho Tum","Wedding Classic","Asha Bhosle"},
        {"Girls Sangeet","Bole Chudiyan","K3G","Kavita Krishnamurthy"},
        {"Reception","Lamborghini","Doorbeen ft. Ragini","Doorbeen"},
        {"Reception","Morni Banke","Badhaai Ho","Neha Kakkar,Gurnam Bhullar"},
        {"Reception","Kala Chashma","Baar Baar Dekho","Badshah"},
        {"Reception","Nashe Si Chadh Gayi","Befikre","Arijit Singh"},
        {"Kids Dance","Baby Doll","Ragini MMS 2","Sunny Leone"},
        {"Kids Dance","Chamma Chamma","China Gate","Isha Koppikar"},
    };

    public WeddingSongsPanel(User user) {
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));

        add(UIUtils.createHeaderBar("🎵  Wedding Playlist Manager"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton addBtn    = UIUtils.primaryButton("＋ Add Song");
        JButton deleteBtn = UIUtils.dangerButton("🗑 Remove");
        JComboBox<String> filter = UIUtils.styledCombo(new String[]{"All","Haldi","Mehndi","Sangeet","Baraat","Wedding","Reception","Kids Dance","Girls Sangeet"});
        JButton filterBtn = UIUtils.secondaryButton("Filter");
        toolbar.add(addBtn); toolbar.add(deleteBtn);
        toolbar.add(Box.createRigidArea(new Dimension(16,0)));
        toolbar.add(new JLabel("Ceremony:")); toolbar.add(filter); toolbar.add(filterBtn);

        String[] cols = {"Ceremony","Song Title","Movie / Album","Singer(s)"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.setDefaultRenderer(Object.class, new CeremonyRenderer());
        loadAll();

        addBtn.addActionListener(e -> {
            String[] cers={"Haldi","Mehndi","Sangeet","Baraat","Wedding","Reception","Kids Dance","Girls Sangeet"};
            JComboBox<String> cerC=UIUtils.styledCombo(cers);
            JTextField songF=UIUtils.styledField(20), movieF=UIUtils.styledField(20), singerF=UIUtils.styledField(20);
            JPanel p=new JPanel(new GridLayout(0,1,4,4));
            p.add(new JLabel("Ceremony:")); p.add(cerC);
            p.add(new JLabel("Song Title:")); p.add(songF);
            p.add(new JLabel("Movie/Album:")); p.add(movieF);
            p.add(new JLabel("Singer(s):")); p.add(singerF);
            int r=JOptionPane.showConfirmDialog(this,p,"Add Song",JOptionPane.OK_CANCEL_OPTION);
            if(r==JOptionPane.OK_OPTION&&!songF.getText().trim().isEmpty()){
                model.addRow(new Object[]{cerC.getSelectedItem(),songF.getText().trim(),movieF.getText().trim(),singerF.getText().trim()});
            }
        });
        deleteBtn.addActionListener(e -> {
            int row=table.getSelectedRow(); if(row>=0) model.removeRow(row);
        });
        filterBtn.addActionListener(e -> {
            String sel=(String)filter.getSelectedItem();
            model.setRowCount(0);
            for(String[] s:BUILTIN_SONGS) {
                if("All".equals(sel)||sel.equals(s[0])) model.addRow(s);
            }
        });

        JPanel body=new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
    }

    private void loadAll() {
        model.setRowCount(0);
        for(String[] s:BUILTIN_SONGS) model.addRow(s);
    }

    static class CeremonyRenderer extends DefaultTableCellRenderer {
        private static final java.util.Map<String,Color> COLOR_MAP = new java.util.HashMap<>();
        static {
            COLOR_MAP.put("Haldi",new Color(0xFF,0xE4,0x58));
            COLOR_MAP.put("Mehndi",new Color(0x22,0x8B,0x22,80));
            COLOR_MAP.put("Sangeet",new Color(0x8A,0x2B,0xE2,80));
            COLOR_MAP.put("Baraat",new Color(0xFF,0x6B,0x6B,80));
            COLOR_MAP.put("Wedding",new Color(0xC9,0x60,0x40,80));
            COLOR_MAP.put("Reception",new Color(0x1E,0x90,0xFF,80));
        }
        public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
            super.getTableCellRendererComponent(t,v,sel,foc,r,c);
            if(!sel && c==0 && v!=null){
                Color bg=COLOR_MAP.getOrDefault(v.toString(),UIUtils.CARD_BG);
                setBackground(bg); setForeground(Color.BLACK);
            } else if(!sel){ setBackground(UIUtils.WHITE); setForeground(UIUtils.DEEP_BROWN); }
            return this;
        }
    }
}
