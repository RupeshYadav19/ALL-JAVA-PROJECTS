package vendor;

import models.User;
import models.Vendor;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PortfolioPanel extends JPanel {
    private final User user;
    private final Vendor vendor;
    private JPanel gridPanel;

    public PortfolioPanel(User user, Vendor vendor) {
        this.user=user; this.vendor=vendor;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,8));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("🖼  My Portfolio"), BorderLayout.NORTH);

        JPanel toolbar=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton uploadBtn=UIUtils.primaryButton("＋ Upload Photo");
        JButton refreshBtn=UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(uploadBtn); toolbar.add(refreshBtn);

        gridPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        gridPanel.setBackground(UIUtils.WHITE);

        uploadBtn.addActionListener(e -> {
            JFileChooser fc=new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images","jpg","jpeg","png","gif"));
            fc.setMultiSelectionEnabled(true);
            if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                for(File f:fc.getSelectedFiles()) addImageCard(f);
            }
        });
        refreshBtn.addActionListener(e -> { gridPanel.removeAll(); gridPanel.revalidate(); gridPanel.repaint(); });

        JPanel body=new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(gridPanel),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);

        // Add placeholder cards
        for(int i=1;i<=6;i++){
            JPanel card=createPlaceholderCard("Photo "+i);
            gridPanel.add(card);
        }
    }

    private void addImageCard(File file){
        JPanel card=new JPanel(new BorderLayout(0,4));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(UIUtils.ROSE_GOLD,1));
        card.setPreferredSize(new Dimension(150,130));

        JLabel img=new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon=new ImageIcon(new ImageIcon(file.getAbsolutePath()).getImage().getScaledInstance(150,100,Image.SCALE_SMOOTH));
            img.setIcon(icon);
        } catch(Exception e){ img.setText("🖼"); img.setFont(new Font("Segoe UI Emoji",Font.PLAIN,32)); }

        JLabel name=new JLabel(file.getName().length()>18?file.getName().substring(0,16)+"…":file.getName(),SwingConstants.CENTER);
        name.setFont(UIUtils.FONT_SMALL);
        card.add(img,BorderLayout.CENTER);
        card.add(name,BorderLayout.SOUTH);
        gridPanel.add(card);
        gridPanel.revalidate(); gridPanel.repaint();
    }

    private JPanel createPlaceholderCard(String label){
        JPanel card=new JPanel(new BorderLayout());
        card.setBackground(UIUtils.ACCENT_LIGHT);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xDD,0xCC,0xBB),1));
        card.setPreferredSize(new Dimension(150,130));
        JLabel ico=new JLabel("📷",SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji",Font.PLAIN,36));
        JLabel lbl=new JLabel(label,SwingConstants.CENTER);
        lbl.setFont(UIUtils.FONT_SMALL);
        card.add(ico,BorderLayout.CENTER);
        card.add(lbl,BorderLayout.SOUTH);
        return card;
    }
}
