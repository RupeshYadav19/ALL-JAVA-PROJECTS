package admin;

import dao.NotificationDAO;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class NotificationsPanel extends JPanel {
    private final User admin;
    private final NotificationDAO dao = new NotificationDAO();
    private JPanel listPanel;

    public NotificationsPanel(User admin) {
        this.admin = admin;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,8));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("🔔  Notification Center"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton markAllBtn = UIUtils.primaryButton("✔ Mark All Read");
        JButton refreshBtn = UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(markAllBtn); toolbar.add(refreshBtn);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UIUtils.WHITE);
        JScrollPane sp = UIUtils.scrollPane(listPanel);

        markAllBtn.addActionListener(e -> {
            try { dao.markAllRead(admin.getUserId()); loadNotifs(); }
            catch(Exception ex){ JOptionPane.showMessageDialog(this,ex.getMessage()); }
        });
        refreshBtn.addActionListener(e -> loadNotifs());

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar, BorderLayout.NORTH);
        body.add(sp, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
        loadNotifs();
    }

    private void loadNotifs() {
        listPanel.removeAll();
        try {
            java.util.List<java.util.Map<String,Object>> notifs = dao.getByUser(admin.getUserId());
            if (notifs.isEmpty()) {
                JLabel empty = new JLabel("No notifications", SwingConstants.CENTER);
                empty.setFont(UIUtils.FONT_BODY);
                empty.setForeground(Color.GRAY);
                listPanel.add(empty);
            }
            for (java.util.Map<String,Object> n : notifs) {
                boolean read = (boolean) n.get("isRead");
                JPanel card = new JPanel(new BorderLayout(8,0));
                card.setBackground(read ? UIUtils.WHITE : UIUtils.ACCENT_LIGHT);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,0,1,0,new Color(0xEE,0xDD,0xCC)),
                    BorderFactory.createEmptyBorder(10,14,10,14)));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

                JPanel info = new JPanel();
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                info.setOpaque(false);
                JLabel title = new JLabel((String)n.get("title"));
                title.setFont(read ? UIUtils.FONT_BODY : UIUtils.FONT_SUBHEAD);
                title.setForeground(UIUtils.DEEP_BROWN);
                JLabel msg = new JLabel((String)n.get("message"));
                msg.setFont(UIUtils.FONT_SMALL);
                msg.setForeground(Color.GRAY);
                info.add(title); info.add(msg);

                JLabel time = new JLabel(n.get("createdAt")!=null?n.get("createdAt").toString().substring(0,16):"");
                time.setFont(UIUtils.FONT_SMALL);
                time.setForeground(Color.GRAY);

                JLabel dot = new JLabel(read ? "" : "●");
                dot.setForeground(UIUtils.ROSE_GOLD);
                dot.setFont(new Font("Segoe UI",Font.BOLD,18));

                card.add(dot, BorderLayout.WEST);
                card.add(info, BorderLayout.CENTER);
                card.add(time, BorderLayout.EAST);
                listPanel.add(card);
            }
        } catch (Exception ex) {
            listPanel.add(new JLabel("Error: "+ex.getMessage()));
        }
        listPanel.revalidate(); listPanel.repaint();
    }
}
