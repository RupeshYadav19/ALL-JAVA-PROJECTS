package vendor;

import dao.NotificationDAO;
import models.User;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;

/** Vendor notifications panel — reuses same logic as admin's */
public class VendorNotificationsPanel extends JPanel {
    private final User user;
    private final NotificationDAO dao = new NotificationDAO();
    private final JPanel listPanel = new JPanel();

    public VendorNotificationsPanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,8));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("🔔  Notifications"), BorderLayout.NORTH);
        JPanel toolbar=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton markAll=UIUtils.primaryButton("✔ Mark All Read");
        JButton refresh=UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(markAll); toolbar.add(refresh);

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UIUtils.WHITE);

        markAll.addActionListener(e -> {
            try{ dao.markAllRead(user.getUserId()); load(); }
            catch(Exception ex){ JOptionPane.showMessageDialog(this,ex.getMessage()); }
        });
        refresh.addActionListener(e -> load());

        JPanel body=new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(listPanel),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
        load();
    }

    private void load() {
        listPanel.removeAll();
        try{
            var notifs=dao.getByUser(user.getUserId());
            if(notifs.isEmpty()){
                JLabel e=new JLabel("No notifications.",SwingConstants.CENTER);
                e.setFont(UIUtils.FONT_BODY); e.setForeground(Color.GRAY);
                listPanel.add(e);
            }
            for(var n:notifs){
                boolean read=(boolean)n.get("isRead");
                JPanel card=new JPanel(new BorderLayout(8,0));
                card.setBackground(read?UIUtils.WHITE:UIUtils.ACCENT_LIGHT);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0,0,1,0,new Color(0xEE,0xDD,0xCC)),
                    BorderFactory.createEmptyBorder(10,14,10,14)));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE,65));
                JLabel title=new JLabel((String)n.get("title"));
                title.setFont(read?UIUtils.FONT_BODY:UIUtils.FONT_SUBHEAD);
                title.setForeground(UIUtils.DEEP_BROWN);
                JLabel msg=new JLabel((String)n.get("message"));
                msg.setFont(UIUtils.FONT_SMALL); msg.setForeground(Color.GRAY);
                JPanel info=new JPanel(); info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS)); info.setOpaque(false);
                info.add(title); info.add(msg);
                JLabel dot=new JLabel(read?"":" ●"); dot.setForeground(UIUtils.ROSE_GOLD);
                card.add(dot,BorderLayout.WEST); card.add(info,BorderLayout.CENTER);
                listPanel.add(card);
            }
        }catch(Exception ex){listPanel.add(new JLabel("Error: "+ex.getMessage()));}
        listPanel.revalidate(); listPanel.repaint();
    }
}
