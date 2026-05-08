package com.restaurant.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.restaurant.model.*;
import com.restaurant.database.DatabaseManager;

public class UserDashboard extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private List<com.restaurant.model.MenuItem> cart = new ArrayList<>();

    public UserDashboard(User user) {
        this.currentUser = user;
        setTitle("Restaurant Management - User Dashboard (" + user.getName() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 700));
        sidebar.setBackground(new Color(20, 20, 20));
        sidebar.setLayout(new GridLayout(6, 1, 10, 10));

        JButton resBtn = createSidebarBtn("Reservations");
        JButton menuBtn = createSidebarBtn("Order Food");
        JButton profileBtn = createSidebarBtn("Profile");
        JButton logoutBtn = createSidebarBtn("Logout");

        sidebar.add(resBtn);
        sidebar.add(menuBtn);
        sidebar.add(profileBtn);
        sidebar.add(new JLabel("")); // Spacer
        sidebar.add(new JLabel("")); // Spacer
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // Content Area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(45, 45, 45));
        add(contentPanel, BorderLayout.CENTER);

        // Action Listeners
        resBtn.addActionListener(e -> showReservationPanel());
        menuBtn.addActionListener(e -> showMenuPanel());
        profileBtn.addActionListener(e -> showProfilePanel());
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // Default view
        showReservationPanel();
    }

    private JButton createSidebarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }

    private void showReservationPanel() {
        contentPanel.removeAll();
        JPanel resPanel = new JPanel(new BorderLayout());
        resPanel.setBackground(new Color(45, 45, 45));

        JLabel title = new JLabel("Online Table Reservation", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        resPanel.add(title, BorderLayout.NORTH);

        JPanel tableGrid = new JPanel(new GridLayout(5, 4, 15, 15)); // 5 rows, 4 columns for 20 tables
        tableGrid.setBackground(new Color(45, 45, 45));
        tableGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<TableSlot> tables = DatabaseManager.getTables();
        for (TableSlot table : tables) {
            JButton tBtn = new JButton(table.getLabel() + " (" + table.getCapacity() + " seats)");
            if (table.isReserved()) {
                tBtn.setBackground(Color.RED);
                tBtn.setText(table.getLabel() + " (Reserved)");
                tBtn.setEnabled(false);
            } else {
                tBtn.setBackground(Color.GREEN);
                tBtn.addActionListener(e -> {
                    int choice = JOptionPane.showConfirmDialog(this, "Book table " + table.getLabel() + "?", "Reserve",
                            JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        DatabaseManager.reserveTable(table.getId(), true);
                        showReservationPanel();
                    }
                });
            }
            tBtn.setForeground(Color.BLACK);
            tableGrid.add(tBtn);
        }

        resPanel.add(new JScrollPane(tableGrid), BorderLayout.CENTER);
        contentPanel.add(resPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMenuPanel() {
        contentPanel.removeAll();
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(45, 45, 45));

        JLabel title = new JLabel("Digital Menu / Online Food Ordering", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        menuPanel.add(title, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(new Color(45, 45, 45));

        List<com.restaurant.model.MenuItem> items = DatabaseManager.getMenu();
        for (com.restaurant.model.MenuItem item : items) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(new Dimension(800, 50));
            row.setBackground(new Color(60, 60, 60));
            row.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            JLabel label = new JLabel(
                    " " + item.getName() + " - ₹" + item.getPrice() + " [" + item.getCategory() + "]");
            label.setForeground(Color.WHITE);
            row.add(label, BorderLayout.WEST);

            JButton orderBtn = new JButton("Add to Cart");
            orderBtn.addActionListener(e -> {
                cart.add(item);
                JOptionPane.showMessageDialog(this, item.getName() + " added to cart!");
            });
            row.add(orderBtn, BorderLayout.EAST);

            itemsPanel.add(row);
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JButton checkoutBtn = new JButton("Checkout (" + cart.size() + " items)");
        checkoutBtn.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty!");
                return;
            }
            double total = 0;
            StringBuilder sb = new StringBuilder();
            for (com.restaurant.model.MenuItem mi : cart) {
                total += mi.getPrice();
                sb.append(mi.getName()).append(", ");
            }
            int choice = JOptionPane.showConfirmDialog(this, "Total: ₹" + total + "\nPlace Order?", "Checkout",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                DatabaseManager.placeOrder(currentUser.getId(), sb.toString(), total);
                cart.clear();
                JOptionPane.showMessageDialog(this, "Order Placed Successfully!");
                showMenuPanel();
            }
        });
        menuPanel.add(checkoutBtn, BorderLayout.SOUTH);

        menuPanel.add(new JScrollPane(itemsPanel), BorderLayout.CENTER);
        contentPanel.add(menuPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfilePanel() {
        contentPanel.removeAll();
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(new Color(45, 45, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel addrLabel = new JLabel("Delivery Address:");
        addrLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        profilePanel.add(addrLabel, gbc);

        JTextArea addrArea = new JTextArea(5, 20);
        addrArea.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        gbc.gridx = 1;
        profilePanel.add(new JScrollPane(addrArea), gbc);

        JButton saveBtn = new JButton("Update Address");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        profilePanel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            String newAddr = addrArea.getText();
            DatabaseManager.updateAddress(currentUser.getId(), newAddr);
            currentUser.setAddress(newAddr);
            JOptionPane.showMessageDialog(this, "Profile Updated!");
        });

        contentPanel.add(profilePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
