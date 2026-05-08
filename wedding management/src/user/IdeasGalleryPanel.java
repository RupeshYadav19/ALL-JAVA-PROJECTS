package user;

import dao.GalleryDAO;
import models.Gallery;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IdeasGalleryPanel extends JPanel {
    private final GalleryDAO dao = new GalleryDAO();
    private JPanel gridPanel;
    private JComboBox<String> typeCombo;
    private JTextField searchF;

    public IdeasGalleryPanel(User user) {
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,10));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("🖼  Style & Ideas Gallery"), BorderLayout.NORTH);

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        filter.setBackground(UIUtils.WHITE);
        filter.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(0xE0,0xD0,0xBF)));
        String[] types = {"All","mehndi","lehenga","decor","ceremony","venue","makeup","invitation"};
        typeCombo = UIUtils.styledCombo(types);
        searchF = UIUtils.styledField(16);
        searchF.setPreferredSize(new Dimension(150,32));
        JButton filterBtn = UIUtils.primaryButton("Show");
        JButton refreshBtn = UIUtils.secondaryButton("↻ All");
        filter.add(new JLabel("Category:")); filter.add(typeCombo);
        filter.add(new JLabel("Search:")); filter.add(searchF);
        filter.add(filterBtn); filter.add(refreshBtn);

        gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,12));
        gridPanel.setBackground(UIUtils.WHITE);

        filterBtn.addActionListener(e -> loadGallery((String)typeCombo.getSelectedItem(), searchF.getText().trim()));
        refreshBtn.addActionListener(e -> loadGallery("All",""));

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(filter,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(gridPanel),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
        loadGallery("All","");
    }

    private void loadGallery(String type, String keyword) {
        gridPanel.removeAll();
        String t = "All".equals(type) ? null : type;
        try {
            List<Gallery> list = dao.getByType(t, keyword.isEmpty()?null:keyword);
            if (list.isEmpty()) {
                // Show placeholder tiles
                String[] styles = {"🌺 Rose Decor","👗 Bridal Lehenga","🎨 Mehndi Patterns","🏛 Palace Venue","💄 Bridal Makeup","✉ Invitation Cards"};
                for (String s : styles) { gridPanel.add(placeholderCard(s)); }
            } else {
                for (Gallery g : list) { gridPanel.add(buildCard(g)); }
            }
        } catch (Exception ex) {
            // Show placeholder tiles on error
            String[] styles = {"🌺 Rose Decor","👗 Bridal Lehenga","🎨 Mehndi Patterns","🏛 Palace Venue","💄 Bridal Makeup","✉ Invitations"};
            for (String s : styles) { gridPanel.add(placeholderCard(s)); }
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    private JPanel buildCard(Gallery g) {
        JPanel card = new JPanel(new BorderLayout(0,4));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(UIUtils.ROSE_GOLD,1));
        card.setPreferredSize(new Dimension(160,140));

        JLabel imgL = new JLabel("🖼",SwingConstants.CENTER);
        imgL.setFont(new Font("Segoe UI Emoji",Font.PLAIN,40));
        imgL.setOpaque(true); imgL.setBackground(UIUtils.ACCENT_LIGHT);

        if (g.getImagePath()!=null&&!g.getImagePath().isEmpty()) {
            try {
                java.io.File f = new java.io.File(g.getImagePath());
                if (f.exists()) {
                    ImageIcon ic=new ImageIcon(new ImageIcon(f.getAbsolutePath()).getImage().getScaledInstance(160,100,Image.SCALE_SMOOTH));
                    imgL.setIcon(ic); imgL.setText("");
                }
            } catch (Exception ignored) {}
        }
        JLabel title=new JLabel(g.getTitle()!=null?g.getTitle():"—",SwingConstants.CENTER);
        title.setFont(UIUtils.FONT_SMALL);
        JLabel type=new JLabel(g.getType(),SwingConstants.CENTER);
        type.setFont(UIUtils.FONT_SMALL); type.setForeground(UIUtils.ROSE_GOLD);
        JPanel info=new JPanel(new GridLayout(2,1));
        info.setOpaque(false); info.add(title); info.add(type);
        card.add(imgL,BorderLayout.CENTER);
        card.add(info,BorderLayout.SOUTH);
        return card;
    }

    private JPanel placeholderCard(String label) {
        JPanel card=new JPanel(new BorderLayout());
        card.setBackground(UIUtils.ACCENT_LIGHT);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xDD,0xCC,0xBB)));
        card.setPreferredSize(new Dimension(160,140));
        JLabel ico=new JLabel(label,SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji",Font.PLAIN,13));
        ico.setForeground(UIUtils.DEEP_BROWN);
        ico.setVerticalAlignment(SwingConstants.CENTER);
        card.add(ico,BorderLayout.CENTER);
        return card;
    }
}
