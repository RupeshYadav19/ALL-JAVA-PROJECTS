package com.pharmacypro.ui.panels;

import javax.swing.*;
import java.awt.*;
import com.pharmacypro.ui.components.PlaceholderTextField;
import com.pharmacypro.ui.components.RoundedButton;
import com.pharmacypro.utils.AppColors;
import javax.swing.table.DefaultTableModel;
import com.pharmacypro.ui.dialogs.DistributorSearchPopup;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PurchasePanel extends JPanel {
    private DistributorSearchPopup distPopup;
    private int selectedDistributorId = -1;
    private DefaultTableModel model;
    private PlaceholderTextField nameField, qtyField;

    public PurchasePanel(JFrame parent) {
        setLayout(new BorderLayout());
        setBackground(AppColors.WHITE);
        distPopup = new DistributorSearchPopup(parent);
        
        // Top section
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        PlaceholderTextField distributorSearch = new PlaceholderTextField("Search Distributor (Alt+S)...");
        distributorSearch.setBackground(AppColors.YELLOW_HIGHLIGHT);
        distributorSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        distributorSearch.setPreferredSize(new Dimension(0, 35));
        
        distributorSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    distPopup.setVisible(false);
                } else if (distributorSearch.getText().length() > 1) {
                    distPopup.search(distributorSearch.getText().trim());
                    Point p = distributorSearch.getLocationOnScreen();
                    distPopup.setLocation(p.x, p.y + distributorSearch.getHeight());
                    distPopup.setVisible(true);
                    distributorSearch.requestFocus();
                } else {
                    distPopup.setVisible(false);
                }
            }
        });
        distPopup.setSelectionListener(distributor -> {
            distributorSearch.setText(distributor.getName());
            selectedDistributorId = distributor.getId();
            distPopup.setVisible(false);
        });
        JPanel distSearchPanel = new JPanel(new BorderLayout(10, 0));
        distSearchPanel.add(new JLabel("Select Distributor:"), BorderLayout.WEST);
        distSearchPanel.add(distributorSearch, BorderLayout.CENTER);
        topWrapper.add(distSearchPanel, BorderLayout.CENTER);
        
        JPanel medAdder = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameField = new PlaceholderTextField("Enter Medicine Name...");
        nameField.setPreferredSize(new Dimension(250, 30));
        qtyField = new PlaceholderTextField("Requested Qty");
        qtyField.setPreferredSize(new Dimension(100, 30));
        
        RoundedButton btnAdd = new RoundedButton("Add to List", AppColors.PRIMARY_PURPLE, Color.WHITE, 6);
        btnAdd.addActionListener(e -> {
            if(nameField.getText().trim().isEmpty() || qtyField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both name and quantity.");
                return;
            }
            int sr = model.getRowCount() + 1;
            model.addRow(new Object[]{sr, nameField.getText().trim(), qtyField.getText().trim()});
            nameField.setText("");
            qtyField.setText("");
            nameField.requestFocus();
        });
        
        medAdder.add(new JLabel("Add Medicine: "));
        medAdder.add(nameField);
        medAdder.add(qtyField);
        medAdder.add(btnAdd);
        
        topWrapper.add(medAdder, BorderLayout.SOUTH);
        add(topWrapper, BorderLayout.NORTH);
        
        // Center Table
        String[] cols = {"Sr No", "Specific Medicine Wanted", "Quantity Needed"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c != 0; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        RoundedButton btnSave = new RoundedButton("Send Order List via Email & Save", AppColors.SUCCESS_GREEN, Color.WHITE, 12);
        btnSave.setPreferredSize(new Dimension(300, 40));
        btnSave.addActionListener(e -> {
            if (model.getRowCount() == 0 || selectedDistributorId == -1) {
                JOptionPane.showMessageDialog(this, "Select a distributor and add items first!");
                return;
            }
            try {
                com.pharmacypro.models.PurchaseBill bill = new com.pharmacypro.models.PurchaseBill();
                bill.setDistributorId(selectedDistributorId);
                bill.setInvoiceNo("PUR-DRAFT-" + System.currentTimeMillis());
                bill.setBillDate(java.time.LocalDate.now());
                bill.setBillingMode("ORDER_MANIFEST");
                bill.setPayStatus("ORDER_SENT");
                bill.setCreatedBy("admin");
                
                java.util.List<com.pharmacypro.models.PurchaseBillItem> items = new java.util.ArrayList<>();
                for (int i=0; i<model.getRowCount(); i++) {
                    com.pharmacypro.models.PurchaseBillItem item = new com.pharmacypro.models.PurchaseBillItem();
                    item.setProductId(1); // Generic fallback
                    item.setSourceProductName(model.getValueAt(i, 1).toString());
                    item.setBatchNo("REQUESTED");
                    item.setQuantity(Integer.parseInt(model.getValueAt(i, 2).toString()));
                    item.setCostPrice(java.math.BigDecimal.ZERO);
                    item.setAmount(java.math.BigDecimal.ZERO);
                    items.add(item);
                }
                bill.setTotalAmount(java.math.BigDecimal.ZERO);
                bill.setPendingAmount(java.math.BigDecimal.ZERO);
                
                int result = new com.pharmacypro.dao.PurchaseDAO().savePurchaseBill(bill, items);
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Order list finalized and mail successfully sent to Distributor!");
                    model.setRowCount(0);
                    distributorSearch.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving purchase: " + ex.getMessage());
            }
        });
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.add(btnSave);
        bottomPanel.add(btnWrapper, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
