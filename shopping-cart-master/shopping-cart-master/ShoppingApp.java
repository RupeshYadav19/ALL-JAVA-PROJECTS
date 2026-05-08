import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * SHOPPING CART STANDALONE APPLICATION
 * FLAT STRUCTURE - JDK COMPATIBLE
 */
public class ShoppingApp extends JFrame {

    // THE MAIN METHOD - ENTRY POINT
    public static void main(String[] args) {
        // Standard Java entry point
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            new ShoppingApp().setVisible(true);
        });
    }

    private JPanel mainContainer, navbar;
    private CardLayout cardLayout;
    private String currentUser = null;
    private boolean isAdmin = false;
    private JButton btnLoginNav;

    // UI Colors - Modern Minimalist
    private final Color PRIMARY_COLOR = new Color(0, 0, 0);       // Pure Black
    private final Color SECONDARY_COLOR = new Color(245, 245, 245); // Off-White/Light Gray
    private final Color BG_COLOR = new Color(255, 255, 255);        // Pure White

    public ShoppingApp() {
        setTitle("EKART Shopping App");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setting a global font for readability
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Navbar is persistent but hidden initially
        navbar = createNavbar();
        navbar.setVisible(false);
        getContentPane().add(navbar, BorderLayout.NORTH);
        getContentPane().add(mainContainer, BorderLayout.CENTER);

        initUI();
        showLoginView();
    }

    private void initUI() {
        mainContainer.add(createProductPanel(), "products");
        mainContainer.add(createCartPanel(), "cart");
        mainContainer.add(createAdminPanel(), "admin");
        mainContainer.add(createLoginPanel(), "login");
        mainContainer.add(createRegisterPanel(), "register");
    }

    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(PRIMARY_COLOR);
        nav.setPreferredSize(new Dimension(1000, 70));

        JLabel title = new JLabel("  EKART SHOPPING");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nav.add(title, BorderLayout.WEST);

        JPanel navActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        navActions.setOpaque(false);

        JButton btnHome = createNavButton("HOME");
        JButton btnCart = createNavButton("CART");
        JButton btnAdmin = createNavButton("ADMIN");
        btnLoginNav = createNavButton("LOGIN");

        btnHome.addActionListener(e -> cardLayout.show(mainContainer, "products"));
        btnCart.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Please Login First!");
                showLoginView();
            } else {
                refreshCart();
                cardLayout.show(mainContainer, "cart");
            }
        });

        btnAdmin.addActionListener(e -> {
            if (isAdmin) {
                refreshAdmin();
                cardLayout.show(mainContainer, "admin");
            } else {
                JOptionPane.showMessageDialog(this, "Admin access required!");
            }
        });

        btnLoginNav.addActionListener(e -> {
            if (currentUser == null) {
                showLoginView();
            } else {
                logout();
            }
        });

        navActions.add(btnHome);
        navActions.add(btnCart);
        navActions.add(btnAdmin);
        navActions.add(btnLoginNav);
        nav.add(navActions, BorderLayout.EAST);

        return nav;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.BLACK);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(7, 19, 7, 19)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect for nav buttons
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
        });
        return btn;
    }

    private void updateNavbar() {
        if (currentUser != null) {
            String name = currentUser.split("@")[0];
            btnLoginNav.setText("Logout (" + name + ")");
            navbar.setVisible(true);
        } else {
            btnLoginNav.setText("Login");
            navbar.setVisible(false);
        }
        navbar.revalidate();
        navbar.repaint();
    }

    private void logout() {
        currentUser = null;
        isAdmin = false;
        updateNavbar();
        JOptionPane.showMessageDialog(this, "Logged out successfully!");
        showLoginView();
    }

    private JComboBox<String> comboCategory;
    private JPanel productGrid;

    private JPanel createProductPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Welcome To EKART Store! Browse by Category: "));

        String[] cats = { "All", "mobile", "laptop", "audio", "tablet", "wearable", "console", "camera", "accessory",
                "electronics" };
        comboCategory = new JComboBox<>(cats);
        comboCategory.addActionListener(e -> loadProducts(productGrid, (String) comboCategory.getSelectedItem()));
        filterPanel.add(comboCategory);

        p.add(filterPanel, BorderLayout.NORTH);

        productGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        productGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        productGrid.setBackground(BG_COLOR);

        loadProducts(productGrid, "All");

        JScrollPane scroll = new JScrollPane(productGrid);
        scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void loadProducts(JPanel grid, String category) {
        grid.removeAll();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM products";
            if (category != null && !category.equals("All")) {
                query += " WHERE ptype = ?";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            if (category != null && !category.equals("All")) {
                ps.setString(1, category);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                grid.add(createProductCard(
                        rs.getString("pid"),
                        rs.getString("pname"),
                        rs.getDouble("pprice"),
                        rs.getString("image_name")));
            }
        } catch (Exception e) {
            System.err.println("DB Error: " + e.getMessage());
        }
        grid.revalidate();
        grid.repaint();
    }

    private JPanel createProductCard(String id, String name, double price, String img) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        
        javax.swing.border.Border defaultBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        javax.swing.border.Border hoverBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(19, 19, 19, 19)); // Adjust padding so size is completely static

        card.setBorder(defaultBorder);

        JLabel lblImg = new JLabel();
        String[] paths = { "images/" + img, "./images/" + img, "src/images/" + img };
        ImageIcon icon = null;
        for (String p : paths) {
            if (new java.io.File(p).exists()) {
                icon = new ImageIcon(p);
                break;
            }
        }

        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            lblImg.setIcon(new ImageIcon(scaled));
        } else {
            lblImg.setText("IMAGE");
            lblImg.setOpaque(true);
            lblImg.setBackground(SECONDARY_COLOR);
            lblImg.setPreferredSize(new Dimension(140, 140));
            lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        }
        lblImg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setForeground(Color.BLACK);

        JLabel lblPrice = new JLabel("₹ " + price);
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblPrice.setForeground(new Color(80, 80, 80));
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAdd = new JButton("Add to Cart");
        btnAdd.setBackground(Color.WHITE);
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.setVisible(false); // Hidden by default, explicit interaction
        btnAdd.addActionListener(e -> addToCart(id));

        card.add(lblImg);
        card.add(Box.createVerticalStrut(15));
        card.add(lblName);
        card.add(lblPrice);
        card.add(Box.createVerticalStrut(15));
        card.add(btnAdd);
        
        // Hover effects for the Product Card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(hoverBorder);
                btnAdd.setVisible(true);
                btnAdd.setBackground(new Color(230, 230, 230));
                btnAdd.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(defaultBorder);
                btnAdd.setVisible(false);
                btnAdd.setBackground(Color.WHITE);
                btnAdd.setForeground(Color.BLACK);
            }
        });

        return card;
    }

    private void addToCart(String prodId) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please Login First!");
            showLoginView();
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO cart (username, prodid, quantity) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE quantity = quantity + 1");
            ps.setString(1, currentUser);
            ps.setString(2, prodId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Added to cart!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel cartContent;

    private JPanel createCartPanel() {
        JPanel p = new JPanel(new BorderLayout());
        cartContent = new JPanel(new BorderLayout());
        p.add(cartContent, BorderLayout.CENTER);
        return p;
    }

    private void refreshCart() {
        cartContent.removeAll();
        if (currentUser == null)
            return;

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setFont(new Font("Consolas", Font.PLAIN, 14));
        list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        double total = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.pname, c.quantity, p.pprice FROM cart c JOIN products p ON c.prodid = p.pid WHERE c.username = ?");
            ps.setString(1, currentUser);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double subtotal = rs.getInt(2) * rs.getDouble(3);
                String row = String.format("%-30s x%-3d  ₹%-12.2f", rs.getString(1), rs.getInt(2), subtotal);
                model.addElement(row);
                total += subtotal;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel lblTotal = new JLabel("Total Payable: ₹" + total + "  ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        cartContent.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(lblTotal, BorderLayout.NORTH);

        final double finalTotal = total;
        JButton btnPay = new JButton("Proceed to Pay");
        btnPay.setBackground(Color.WHITE);
        btnPay.setForeground(Color.BLACK);
        btnPay.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnPay.setPreferredSize(new Dimension(1000, 60));
        btnPay.setFocusPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnPay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPay.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPay.setBackground(Color.WHITE);
            }
        });
        btnPay.addActionListener(e -> handleCheckout(finalTotal));

        bottomPanel.add(btnPay, BorderLayout.SOUTH);

        cartContent.add(bottomPanel, BorderLayout.SOUTH);
        cartContent.revalidate();
        cartContent.repaint();
    }

    private void handleCheckout(double total) {
        if (total <= 0) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this,
                "Total Amount: ₹" + total + "\nProceed with Payment?") == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                String orderId = "ORD" + System.currentTimeMillis();
                String transId = "TXN" + System.currentTimeMillis();

                // 1. Move from cart to orders
                PreparedStatement psOrder = conn.prepareStatement(
                        "INSERT INTO orders (orderid, prodid, quantity, amount) " +
                                "SELECT ?, prodid, quantity, (quantity * (SELECT pprice FROM products WHERE pid = prodid)) FROM cart WHERE username = ?");
                psOrder.setString(1, orderId);
                psOrder.setString(2, currentUser);
                psOrder.executeUpdate();

                // 2. Add to transactions
                PreparedStatement psTrans = conn.prepareStatement("INSERT INTO transactions VALUES (?, ?, NOW(), ?)");
                psTrans.setString(1, transId);
                psTrans.setString(2, currentUser);
                psTrans.setDouble(3, total);
                psTrans.executeUpdate();

                // 3. Clear cart
                PreparedStatement psClear = conn.prepareStatement("DELETE FROM cart WHERE username = ?");
                psClear.setString(1, currentUser);
                psClear.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Order Successful! Order ID: " + orderId);
                refreshCart();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Payment Failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private JTabbedPane adminTabs;
    private JPanel adminProdPanel, adminUserPanel, adminOrderPanel;

    private JPanel createAdminPanel() {
        JPanel p = new JPanel(new BorderLayout());
        adminTabs = new JTabbedPane();
        adminTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        adminProdPanel = new JPanel(new BorderLayout());
        adminUserPanel = new JPanel(new BorderLayout());
        adminOrderPanel = new JPanel(new BorderLayout());

        adminTabs.addTab("Manage Products", adminProdPanel);
        adminTabs.addTab("Manage Customers", adminUserPanel);
        adminTabs.addTab("Manage Orders", adminOrderPanel);

        // Refresh data when tabs are changed
        adminTabs.addChangeListener(e -> refreshAdmin());

        p.add(adminTabs, BorderLayout.CENTER);
        return p;
    }

    private void refreshAdmin() {
        int index = adminTabs.getSelectedIndex();
        if (index == 0)
            refreshAdminProducts();
        else if (index == 1)
            refreshAdminUsers();
        else if (index == 2)
            refreshAdminOrders();
    }

    private void refreshAdminProducts() {
        adminProdPanel.removeAll();
        String[] cols = { "ID", "Product Name", "Category", "Price", "Stock" };
        DefaultTableModel tmodel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(tmodel);
        table.setRowHeight(30);

        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM products");
            while (rs.next()) {
                tmodel.addRow(new Object[] {
                        rs.getString("pid"), rs.getString("pname"),
                        rs.getString("ptype"), rs.getDouble("pprice"), rs.getInt("pquantity")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton btnAddProd = new JButton("Add New Product");
        btnAddProd.setBackground(Color.WHITE);
        btnAddProd.setForeground(Color.BLACK);
        btnAddProd.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        btnAddProd.addActionListener(e -> showAddProductDialog());

        JButton btnDel = new JButton("Delete Selected Product");
        btnDel.setBackground(Color.WHITE);
        btnDel.setForeground(Color.BLACK);
        btnDel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String pid = (String) table.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Delete product " + pid + "?") == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE pid=?");
                        ps.setString(1, pid);
                        ps.executeUpdate();
                        refreshAdminProducts();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JPanel prodActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        prodActions.add(btnAddProd);
        prodActions.add(btnDel);

        adminProdPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        adminProdPanel.add(prodActions, BorderLayout.SOUTH);
        adminProdPanel.revalidate();
        adminProdPanel.repaint();
    }

    private void showAddProductDialog() {
        JTextField pid = new JTextField();
        JTextField pname = new JTextField();
        String[] cats = { "mobile", "laptop", "audio", "tablet", "wearable", "console", "camera", "accessory",
                "electronics" };
        JComboBox<String> ptype = new JComboBox<>(cats);
        JTextField pprice = new JTextField();
        JTextField pqty = new JTextField();
        JTextField pimg = new JTextField("default.png");

        Object[] msg = {
                "Product ID:", pid,
                "Product Name:", pname,
                "Product Category:", ptype,
                "Price (₹):", pprice,
                "Quantity:", pqty,
                "Image Filename:", pimg
        };

        int res = JOptionPane.showConfirmDialog(this, msg, "Add New Product", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO products VALUES (?, ?, ?, 'Info', ?, ?, ?)");
                ps.setString(1, pid.getText());
                ps.setString(2, pname.getText());
                ps.setString(3, (String) ptype.getSelectedItem());
                ps.setDouble(4, Double.parseDouble(pprice.getText()));
                ps.setInt(5, Integer.parseInt(pqty.getText()));
                ps.setString(6, pimg.getText());
                ps.executeUpdate();
                refreshAdminProducts();
                JOptionPane.showMessageDialog(this, "Product Added!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void refreshAdminUsers() {
        adminUserPanel.removeAll();
        String[] cols = { "Email/Username", "Full Name", "Mobile", "Address" };
        DefaultTableModel tmodel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(tmodel);
        table.setRowHeight(30);

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn
                    .prepareStatement("SELECT email, name, mobile, address FROM users WHERE email != ?");
            ps.setString(1, currentUser);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tmodel.addRow(new Object[] { rs.getString(1), rs.getString(2), rs.getLong(3), rs.getString(4) });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton btnDel = new JButton("Remove Customer Account");
        btnDel.setBackground(Color.WHITE);
        btnDel.setForeground(Color.BLACK);
        btnDel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String email = (String) table.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this,
                        "Permanently remove customer " + email + "?") == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE email=?");
                        ps.setString(1, email);
                        ps.executeUpdate();
                        refreshAdminUsers();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        adminUserPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        adminUserPanel.add(btnDel, BorderLayout.SOUTH);
        adminUserPanel.revalidate();
        adminUserPanel.repaint();
    }

    private void refreshAdminOrders() {
        adminOrderPanel.removeAll();
        String[] cols = { "Order ID", "Product", "Qty", "Amount", "Status" };
        DefaultTableModel tmodel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(tmodel);
        table.setRowHeight(30);

        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT o.orderid, p.pname, o.quantity, o.amount, o.shipped FROM orders o JOIN products p ON o.prodid = p.pid");
            while (rs.next()) {
                int shipped = rs.getInt(5);
                String status = (shipped == 0) ? "Processing" : (shipped == 1 ? "Shipped" : "Delivered");
                tmodel.addRow(new Object[] { rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDouble(4), status });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnShip = new JButton("Mark as Shipped");
        btnShip.setForeground(Color.BLACK);
        JButton btnDeliver = new JButton("Mark as Delivered");
        btnDeliver.setForeground(Color.BLACK);
        btnShip.addActionListener(e -> updateOrderStatus(table, 1));
        btnDeliver.addActionListener(e -> updateOrderStatus(table, 2));

        actions.add(btnShip);
        actions.add(btnDeliver);
        adminOrderPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        adminOrderPanel.add(actions, BorderLayout.SOUTH);
        adminOrderPanel.revalidate();
        adminOrderPanel.repaint();
    }

    private void updateOrderStatus(JTable table, int status) {
        int row = table.getSelectedRow();
        if (row != -1) {
            String oid = (String) table.getValueAt(row, 0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE orders SET shipped=? WHERE orderid=?");
                ps.setInt(1, status);
                ps.setString(2, oid);
                ps.executeUpdate();
                refreshAdminOrders();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private JPanel createLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        box.setBackground(Color.WHITE);

        JLabel header = new JLabel("Member Login");
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(PRIMARY_COLOR);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField username = new JTextField(20);
        username.setMaximumSize(new Dimension(300, 40));
        username.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            
        JPasswordField pass = new JPasswordField(20);
        pass.setMaximumSize(new Dimension(300, 40));
        pass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JButton btn = new JButton("LOGIN");
        btn.setMaximumSize(new Dimension(300, 45));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        JButton btnReg = new JButton("Don't have an account? Register");
        btnReg.setBorderPainted(false);
        btnReg.setContentAreaFilled(false);
        btnReg.setForeground(Color.BLACK);
        btnReg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReg.addActionListener(e -> cardLayout.show(mainContainer, "register"));

        

        box.add(header);
        box.add(Box.createVerticalStrut(30));
        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(Color.BLACK);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(lblUser);
        box.add(username);
        box.add(Box.createVerticalStrut(15));
        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(Color.BLACK);
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(lblPass);
        box.add(pass);
        box.add(Box.createVerticalStrut(25));
        box.add(btn);
        box.add(Box.createVerticalStrut(15));
        box.add(btnReg);

        p.add(box);
        return p;
    }

    private JPanel createRegisterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        box.setBackground(Color.WHITE);

        JLabel header = new JLabel("Create Account");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField name = new JTextField(20);
        name.setMaximumSize(new Dimension(300, 40));
        name.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        JTextField username = new JTextField(20);
        username.setMaximumSize(new Dimension(300, 40));
        username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        JPasswordField pass = new JPasswordField(20);
        pass.setMaximumSize(new Dimension(300, 40));
        pass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        JButton btn = new JButton("CREATE ACCOUNT");
        btn.setMaximumSize(new Dimension(300, 45));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        JButton btnBack = new JButton("Already have an account? Login");
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setForeground(Color.BLACK);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(e -> showLoginView());

        btn.addActionListener(e -> {
            String userInput = username.getText().trim();
            String passInput = new String(pass.getPassword());

            // ✅ VALIDATION
           if (!validateLoginInput(userInput, passInput)) {
               return;
            }

           try (Connection conn = DatabaseConnection.getConnection()) {

           PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE email=? AND password=?");

            ps.setString(1, userInput);
            ps.setString(2, passInput);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
              currentUser = userInput;
              isAdmin = currentUser.toLowerCase().contains("admin");
              updateNavbar();
              JOptionPane.showMessageDialog(this, "Welcome " + rs.getString("name"));
              showProductView();
            } else {
             JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }

       }       catch (Exception ex) {
              JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
          }
       });

        box.add(header);
        box.add(Box.createVerticalStrut(25));
        box.add(new JLabel("Full Name"));
        box.add(name);
        box.add(Box.createVerticalStrut(10));
        box.add(new JLabel("Username"));
        box.add(username);
        box.add(Box.createVerticalStrut(10));
        box.add(new JLabel("Password"));
        box.add(pass);
        box.add(Box.createVerticalStrut(25));
        box.add(btn);
        box.add(Box.createVerticalStrut(15));
        box.add(btnBack);

        p.add(box);
        return p;
    }
    private boolean validateRegisterInput(String name, String username, String password) {

    // Empty check
    if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required!");
        return false;
    }

    // Name validation (only letters + space)
    if (!name.matches("^[A-Za-z ]+$")) {
        JOptionPane.showMessageDialog(this, "Name should contain only letters!");
        return false;
    }

    // ✅ USERNAME VALIDATION (NO EMAIL NOW)
    if (!username.matches("^[A-Za-z0-9]{4,15}$")) {
        JOptionPane.showMessageDialog(this,
            "Username must be 4-15 characters and contain only letters & numbers!");
        return false;
    }

    // Password length
    if (password.length() < 6) {
        JOptionPane.showMessageDialog(this, "Password must be at least 6 characters!");
        return false;
    }

    // Strong password
    if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).+$")) {
        JOptionPane.showMessageDialog(this,
            "Password must contain at least 1 letter and 1 number!");
        return false;
    }

    return true;
}
    private boolean validateLoginInput(String username, String password) {

    // Empty check
    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
        return false;
    }

    // ✅ USERNAME VALIDATION (NO EMAIL NOW)
    if (!username.matches("^[A-Za-z0-9]{4,15}$")) {
        JOptionPane.showMessageDialog(this,
            "Username must be 4-15 characters and contain only letters & numbers!");
        return false;
    }

    // Password length check
    if (password.length() < 6) {
        JOptionPane.showMessageDialog(this,
            "Password must be at least 6 characters!");
        return false;
    }

    return true;
}

    private void showProductView() {
        cardLayout.show(mainContainer, "products");
    }

    private void showLoginView() {
        cardLayout.show(mainContainer, "login");
    }
}
