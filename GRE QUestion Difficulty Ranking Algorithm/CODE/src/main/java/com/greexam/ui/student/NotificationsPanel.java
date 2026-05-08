package com.greexam.ui.student;

import com.greexam.model.Notification;
import com.greexam.service.AuthService;
import com.greexam.service.NotificationService;
import com.greexam.util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NotificationsPanel extends JPanel {

    private NotificationService notifService = NotificationService.getInstance();
    private AuthService authService = AuthService.getInstance();

    private JTable table;
    private DefaultTableModel model;

    public NotificationsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnMarkAll = new JButton("Mark All as Read");
        btnMarkAll.addActionListener(e -> {
            notifService.markAllAsRead(authService.getCurrentUser().getId());
            loadNotifications();
        });
        topPanel.add(btnMarkAll);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Date", "Message", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int id = (int) model.getValueAt(row, 0);
                        String status = (String) model.getValueAt(row, 3);
                        if ("Unread".equals(status)) {
                            notifService.markAsRead(id);
                            loadNotifications();
                        }
                    }
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        
        loadNotifications();
    }

    public void loadNotifications() {
        model.setRowCount(0);
        List<Notification> notifs = notifService.getNotifications(authService.getCurrentUser().getId());
        for (Notification n : notifs) {
            model.addRow(new Object[]{
                    n.getId(),
                    DateTimeUtil.formatDisplay(DateTimeUtil.fromTimestamp(n.getCreatedAt())),
                    n.getMessage(),
                    n.isRead() ? "Read" : "Unread"
            });
        }
    }
}
