package com.pharmacypro.ui;

import com.pharmacypro.utils.AppColors;
import com.pharmacypro.utils.AppFonts;
import com.pharmacypro.ui.components.PlaceholderTextField;
import com.pharmacypro.utils.KeyboardShortcutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.pharmacypro.ui.panels.*;

public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private PlaceholderTextField globalSearch;

    public MainFrame() {
        setTitle("Pharmacy Pro");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        initTopBar();
        initGlobalShortcuts();

        // Add actual panels
        tabbedPane.addTab("Sales", new SalesPanel(this));
        tabbedPane.addTab("Purchase", new PurchasePanel(this));
        tabbedPane.addTab("Purchase Orders", new PurchaseOrdersPanel());
        tabbedPane.addTab("Sales Orders", new SalesOrdersPanel());
        tabbedPane.addTab("Master", new MasterPanel());
        tabbedPane.addTab("Payments", new PaymentsPanel());
    }

    private void initTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppColors.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER_GRAY),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Global Search Area
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        searchContainer.setBackground(AppColors.WHITE);
        
        globalSearch = new PlaceholderTextField("Search Module or Patient (Space) ...");
        globalSearch.setPreferredSize(new Dimension(500, 35));
        globalSearch.setBackground(new Color(255, 255, 220)); // Subtle yellow
        globalSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        globalSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                globalSearch.setBackground(AppColors.YELLOW_HIGHLIGHT);
            }
            @Override
            public void focusLost(FocusEvent e) {
                globalSearch.setBackground(new Color(255, 255, 220));
            }
        });
        
        searchContainer.add(globalSearch);
        topPanel.add(searchContainer, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initGlobalShortcuts() {
        JPanel root = (JPanel) getContentPane();
        KeyboardShortcutManager.register(root, "New Sales Bill", KeyEvent.VK_F1, e -> System.out.println("F1 Pressed"));
        KeyboardShortcutManager.register(root, "Module Menu", KeyEvent.VK_SPACE, e -> showModuleMenuPopup());
        // Can add all others as needed
    }

    private void showModuleMenuPopup() {
        // Module Menu Popup Implementation
        JDialog moduleMenu = new JDialog(this, "Module Menu", true);
        moduleMenu.setSize(getWidth(), 400);
        moduleMenu.setLocationRelativeTo(this);
        // Would be populated with the grid layout specified
    }
}
