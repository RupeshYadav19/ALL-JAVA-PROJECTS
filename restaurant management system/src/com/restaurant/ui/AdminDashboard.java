package com.restaurant.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.restaurant.model.*;
import com.restaurant.database.DatabaseManager;

public class AdminDashboard extends JFrame {
    private User admin;
    private JPanel contentPanel;

    public AdminDashboard(User user) {
        this.admin = user;
        setTitle("Restaurant Management - Admin Panel (" + admin.getName() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel(new GridLayout(6, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(200, 700));
        sidebar.setBackground(new Color(40, 0, 0)); // Dark red for admin

        JButton menuMgmtBtn = createSidebarBtn("Manage Menu");
        JButton tableMgmtBtn = createSidebarBtn("Manage Tables");
        JButton orderMgmtBtn = createSidebarBtn("Order Management");
        JButton logoutBtn = createSidebarBtn("Logout");

        sidebar.add(menuMgmtBtn);
        sidebar.add(tableMgmtBtn);
        sidebar.add(orderMgmtBtn);
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(30, 30, 30));
        add(contentPanel, BorderLayout.CENTER);

        menuMgmtBtn.addActionListener(e -> showMenuManagement());
        tableMgmtBtn.addActionListener(e -> showTableManagement());
        orderMgmtBtn.addActionListener(e -> showOrderManagement());
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        showMenuManagement();
    }

    private JButton createSidebarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(60, 20, 20));
        btn.setFocusPainted(false);
        return btn;
    }

    private void showMenuManagement() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Menu Management", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(30, 30, 30));

        List<com.restaurant.model.MenuItem> items = DatabaseManager.getMenu();
        for (com.restaurant.model.MenuItem item : items) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBackground(new Color(50, 50, 50));
            row.setMaximumSize(new Dimension(800, 40));

            JTextField nameField = new JTextField(item.getName(), 15);
            JTextField priceField = new JTextField(String.valueOf(item.getPrice()), 7);
            JButton updateBtn = new JButton("Update");
            JButton delBtn = new JButton("Delete");

            updateBtn.addActionListener(e -> {
                DatabaseManager.updateMenuItem(item.getId(), nameField.getText(),
                        Double.parseDouble(priceField.getText()));
                JOptionPane.showMessageDialog(this, "Updated!");
            });

            delBtn.addActionListener(e -> {
                DatabaseManager.deleteMenuItem(item.getId());
                showMenuManagement();
            });

            row.add(nameField);
            row.add(new JLabel("Price:"));
            row.add(priceField);
            row.add(updateBtn);
            row.add(delBtn);
            listPanel.add(row);
        }

        // Add new item panel
        JPanel addPanel = new JPanel(new FlowLayout());
        addPanel.setBackground(new Color(40, 40, 40));
        JTextField nName = new JTextField(10);
        JTextField nPrice = new JTextField(5);
        JButton addBtn = new JButton("Add New Item");
        addBtn.addActionListener(e -> {
            DatabaseManager.addMenuItem(nName.getText(), "General", Double.parseDouble(nPrice.getText()));
            showMenuManagement();
        });
        addPanel.add(new JLabel("Name:"));
        addPanel.add(nName);
        addPanel.add(new JLabel("Price:"));
        addPanel.add(nPrice);
        addPanel.add(addBtn);

        panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        panel.add(addPanel, BorderLayout.SOUTH);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showTableManagement() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Table Reservation Management", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(5, 4, 10, 10));
        grid.setBackground(new Color(30, 30, 30));

        List<TableSlot> tables = DatabaseManager.getTables();
        for (TableSlot table : tables) {
            JButton btn = new JButton(table.getLabel() + (table.isReserved() ? " (OCCUPIED)" : " (FREE)"));
            btn.setBackground(table.isReserved() ? Color.RED : Color.GRAY);
            btn.addActionListener(e -> {
                DatabaseManager.reserveTable(table.getId(), !table.isReserved());
                showTableManagement();
            });
            grid.add(btn);
        }

        panel.add(new JScrollPane(grid), BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOrderManagement() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Kitchen / Order Management", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(30, 30, 30));

        List<Order> orders = DatabaseManager.getOrders();
        for (Order order : orders) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(new Color(50, 50, 50));
            row.setMaximumSize(new Dimension(800, 60));
            row.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            String text = "<html><b>Order #" + order.getId() + "</b> - ₹" + order.getTotalPrice() +
                    "<br>Items: " + order.getItems() +
                    "<br>Status: " + order.getStatus() + "</html>";
            JLabel label = new JLabel(text);
            label.setForeground(Color.WHITE);
            row.add(label, BorderLayout.WEST);

            JPanel btnPanel = new JPanel(new FlowLayout());
            btnPanel.setOpaque(false);

            if (order.getStatus().equals("PENDING")) {
                JButton completeBtn = new JButton("Complete");
                JButton cancelBtn = new JButton("Cancel");

                completeBtn.addActionListener(e -> {
                    DatabaseManager.updateOrderStatus(order.getId(), "COMPLETED");
                    showOrderManagement();
                });

                cancelBtn.addActionListener(e -> {
                    DatabaseManager.updateOrderStatus(order.getId(), "CANCELLED");
                    showOrderManagement();
                });

                btnPanel.add(completeBtn);
                btnPanel.add(cancelBtn);
            }
            row.add(btnPanel, BorderLayout.EAST);

            listPanel.add(row);
            listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
