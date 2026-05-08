package admin;

import dao.GalleryDAO;
import models.Gallery;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GalleryManagerPanel extends JPanel {
    private final GalleryDAO dao = new GalleryDAO();
    private final User admin;
    private JPanel gridPanel;
    private List<Gallery> items;
    private JComboBox<String> typeFilter;

    public GalleryManagerPanel(User admin) {
        this.admin = admin;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,8));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("🖼  Gallery Manager"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        String[] types = {"All","mehndi","lehenga","decor","ceremony","venue","makeup","invitation"};
        typeFilter = UIUtils.styledCombo(types);
        JButton filterBtn = UIUtils.primaryButton("Filter");
        JButton addBtn    = UIUtils.primaryButton("＋ Add Image");
        JButton deleteBtn = UIUtils.dangerButton("🗑 Delete Selected");
        toolbar.add(new JLabel("Type:")); toolbar.add(typeFilter);
        toolbar.add(filterBtn); toolbar.add(addBtn); toolbar.add(deleteBtn);

        gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        gridPanel.setBackground(UIUtils.WHITE);
        JScrollPane sp = UIUtils.scrollPane(gridPanel);

        filterBtn.addActionListener(e -> loadGrid());
        addBtn.addActionListener(e -> addImage());
        deleteBtn.addActionListener(e -> deleteSelected());

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar, BorderLayout.NORTH);
        body.add(sp, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
        loadGrid();
    }

    private void loadGrid() {
        gridPanel.removeAll();
        String type = (String) typeFilter.getSelectedItem();
        try {
            items = dao.getByType(type.equals("All") ? null : type, null);
            for (Gallery g : items) {
                JPanel card = new JPanel(new BorderLayout(0,4));
                card.setBackground(UIUtils.CARD_BG);
                card.setBorder(BorderFactory.createLineBorder(UIUtils.ROSE_GOLD, 1));
                card.setPreferredSize(new Dimension(140, 120));

                JLabel imgLabel = new JLabel("🖼", SwingConstants.CENTER);
                imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
                imgLabel.setOpaque(true);
                imgLabel.setBackground(UIUtils.ACCENT_LIGHT);

                // Try to load actual image
                if (g.getImagePath() != null && !g.getImagePath().isEmpty()) {
                    try {
                        java.io.File f = new java.io.File(g.getImagePath());
                        if (f.exists()) {
                            ImageIcon icon = new ImageIcon(new ImageIcon(g.getImagePath()).getImage().getScaledInstance(140,90,Image.SCALE_SMOOTH));
                            imgLabel.setIcon(icon);
                            imgLabel.setText("");
                        }
                    } catch (Exception ignored) {}
                }

                JLabel titleLbl = new JLabel(g.getTitle()!=null?g.getTitle():"—", SwingConstants.CENTER);
                titleLbl.setFont(UIUtils.FONT_SMALL);
                JLabel typeLbl = new JLabel(g.getType(), SwingConstants.CENTER);
                typeLbl.setFont(UIUtils.FONT_SMALL);
                typeLbl.setForeground(UIUtils.ROSE_GOLD);

                JPanel info = new JPanel(new GridLayout(2,1,0,0));
                info.setOpaque(false);
                info.add(titleLbl); info.add(typeLbl);

                card.add(imgLabel, BorderLayout.CENTER);
                card.add(info, BorderLayout.SOUTH);
                gridPanel.add(card);
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage()); }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    private void addImage() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images","jpg","jpeg","png","gif","bmp"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fc.getSelectedFile();
            String title = JOptionPane.showInputDialog(this,"Enter title for this image:");
            String[] types = {"mehndi","lehenga","decor","ceremony","venue","makeup","invitation"};
            String type = (String) JOptionPane.showInputDialog(this,"Select type:","Type",JOptionPane.PLAIN_MESSAGE,null,types,"decor");
            String tags = JOptionPane.showInputDialog(this,"Enter tags (comma-separated):");
            if (type != null) {
                Gallery g = new Gallery();
                g.setTitle(title!=null?title:file.getName());
                g.setType(type);
                g.setImagePath(file.getAbsolutePath());
                g.setTags(tags);
                g.setUploadedBy(admin.getUserId());
                g.setFeatured(false);
                try { dao.insert(g); loadGrid(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
            }
        }
    }

    private void deleteSelected() {
        // Simple: delete last added (in production, would track selected card)
        JOptionPane.showMessageDialog(this,"Select a gallery item by ID:",null,JOptionPane.INFORMATION_MESSAGE);
        String idStr = JOptionPane.showInputDialog(this,"Enter Gallery ID to delete:");
        if (idStr!=null && !idStr.isEmpty()) {
            try { dao.delete(Integer.parseInt(idStr)); loadGrid(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        }
    }
}
