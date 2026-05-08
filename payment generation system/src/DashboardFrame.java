import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class DashboardFrame extends JFrame {
    private String username;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public DashboardFrame(String username) {
        this.username = username;
        setTitle("Modern Banking - Dashboard (" + username + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 700));
        sidebar.setBackground(new Color(33, 47, 61));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        addSidebarButton(sidebar, "Overview", "OVERVIEW");
        addSidebarButton(sidebar, "Payments", "PAYMENTS");
        addSidebarButton(sidebar, "History", "HISTORY");
        addSidebarButton(sidebar, "Settings", "SETTINGS");

        if (username.equals("admin")) {
            addSidebarButton(sidebar, "Admin Panel", "ADMIN");
        }

        sidebar.add(Box.createVerticalGlue());
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        mainPanel.add(sidebar, BorderLayout.WEST);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(1000, 80));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel welcomeLabel = new JLabel(
                "  Secure Banking Portal - " + (username.equals("admin") ? "Administrator" : username));
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(welcomeLabel, BorderLayout.WEST);

        mainPanel.add(header, BorderLayout.NORTH);

        // Content Panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        contentPanel.add(createOverviewPanel(), "OVERVIEW");
        contentPanel.add(createHistoryPanel(), "HISTORY");
        contentPanel.add(createSettingsPanel(), "SETTINGS");
        if (username.equals("admin")) {
            contentPanel.add(createAdminPanel(), "ADMIN");
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void addSidebarButton(JPanel panel, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(33, 47, 61));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btn.addActionListener(e -> {
            if (cardName.equals("PAYMENTS")) {
                new PaymentFrame(username).setVisible(true);
                this.dispose();
            } else {
                refreshPanel(cardName);
                cardLayout.show(contentPanel, cardName);
            }
        });
        panel.add(btn);
    }

    private void refreshPanel(String cardName) {
        if (cardName.equals("OVERVIEW")) {
            contentPanel.add(createOverviewPanel(), "OVERVIEW");
        } else if (cardName.equals("HISTORY")) {
            contentPanel.add(createHistoryPanel(), "HISTORY");
        } else if (cardName.equals("SETTINGS")) {
            contentPanel.add(createSettingsPanel(), "SETTINGS");
        } else if (cardName.equals("ADMIN")) {
            contentPanel.add(createAdminPanel(), "ADMIN");
        }
        cardLayout.show(contentPanel, cardName);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setName("OVERVIEW");
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        double balance = 0;
        int frozen = 0;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT balance, is_frozen FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("balance");
                frozen = rs.getInt("is_frozen");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JLabel balTitle = new JLabel("Your Account Balance");
        balTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(balTitle, gbc);

        JLabel balVal = new JLabel("₹" + String.format("%.2f", balance));
        balVal.setFont(new Font("Segoe UI", Font.BOLD, 48));
        balVal.setForeground(new Color(41, 128, 185));
        gbc.gridy = 1;
        panel.add(balVal, gbc);

        if (frozen == 1) {
            JLabel frozenLabel = new JLabel("ACCOUNT FROZEN");
            frozenLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            frozenLabel.setForeground(Color.RED);
            gbc.gridy = 2;
            panel.add(frozenLabel, gbc);
        }

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("HISTORY");
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = { "Date", "Type", "Details", "Amount", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT created_at, payment_type, recipient, amount, status, error_message FROM payments " +
                            "WHERE user_id = (SELECT id FROM users WHERE username = ?) ORDER BY created_at DESC");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getTimestamp("created_at").toString());
                row.add(rs.getString("payment_type"));
                String detail = rs.getString("recipient");
                if (rs.getString("error_message") != null)
                    detail += " (" + rs.getString("error_message") + ")";
                row.add(detail);
                row.add("₹" + String.format("%.2f", rs.getDouble("amount")));
                row.add(rs.getString("status"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setName("SETTINGS");
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT full_name, mobile, upi_id, max_send, max_receive FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                addSettingRow(panel, "Full Name:", rs.getString("full_name"), 0, gbc);
                addSettingRow(panel, "Mobile:", rs.getString("mobile"), 1, gbc);
                addSettingRow(panel, "UPI ID:", rs.getString("upi_id"), 2, gbc);
                addSettingRow(panel, "Send Limit:", "₹" + rs.getDouble("max_send"), 3, gbc);
                addSettingRow(panel, "Receive Limit:", "₹" + rs.getDouble("max_receive"), 4, gbc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return panel;
    }

    private void addSettingRow(JPanel panel, String label, String value, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(val, gbc);
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("ADMIN");
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("User Management Console");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = { "ID", "User", "Name", "Balance", "Send Lim", "Recv Lim", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Load users
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT id, username, full_name, balance, max_send, max_receive, is_frozen FROM users");
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("full_name"));
                row.add(rs.getDouble("balance"));
                row.add(rs.getDouble("max_send"));
                row.add(rs.getDouble("max_receive"));
                row.add(rs.getInt("is_frozen") == 1 ? "FROZEN" : "ACTIVE");
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Action Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton freezeBtn = new JButton("Toggle Freeze");
        JButton editBtn = new JButton("Edit User Data");

        freezeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1)
                return;
            int id = (int) model.getValueAt(row, 0);
            String currentStatus = (String) model.getValueAt(row, 6);
            int newStatus = currentStatus.equals("FROZEN") ? 0 : 1;

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE users SET is_frozen = ? WHERE id = ?");
                ps.setInt(1, newStatus);
                ps.setInt(2, id);
                ps.executeUpdate();
                refreshPanel("ADMIN");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1)
                return;
            int id = (int) model.getValueAt(row, 0);
            String user = (String) model.getValueAt(row, 1);

            String newBal = JOptionPane.showInputDialog(this, "Enter new balance for " + user,
                    model.getValueAt(row, 3));
            if (newBal != null) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance = ? WHERE id = ?");
                    ps.setDouble(1, Double.parseDouble(newBal));
                    ps.setInt(2, id);
                    ps.executeUpdate();
                    refreshPanel("ADMIN");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        actions.add(freezeBtn);
        actions.add(editBtn);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }
}
