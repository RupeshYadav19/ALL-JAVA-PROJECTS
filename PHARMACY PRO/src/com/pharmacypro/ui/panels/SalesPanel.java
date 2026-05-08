package com.pharmacypro.ui.panels;

import com.pharmacypro.models.*;
import com.pharmacypro.ui.components.*;
import com.pharmacypro.ui.dialogs.*;
import com.pharmacypro.utils.AppColors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

public class SalesPanel extends JPanel {
    private ProductSearchPopup productPopup;
    private PatientSearchPopup patientPopup;
    private DoctorSearchPopup doctorPopup;
    private JTable billTable;
    private DefaultTableModel billModel;
    private BillSummaryPanel summary;
    
    private int selectedPatientId = -1;
    private int selectedDoctorId = -1;
    private java.util.List<Product> cartProducts = new java.util.ArrayList<>();
    private java.util.List<ProductBatch> cartBatches = new java.util.ArrayList<>();

    public SalesPanel(JFrame parentFrame) {
        setLayout(new BorderLayout());
        
        productPopup = new ProductSearchPopup(parentFrame);
        patientPopup = new PatientSearchPopup(parentFrame);
        doctorPopup = new DoctorSearchPopup(parentFrame);

        productPopup.setSelectionListener((product, batch) -> {
            addBillRow(product, batch);
            updateBillTotals();
        });
        
        patientPopup.setSelectionListener(patient -> {
            // Find patient field and set text. Using reference below.
        });

        // Top Area
        JPanel topArea = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ExpiryAlertsWidget alerts = new ExpiryAlertsWidget(5);
        alerts.setPreferredSize(new Dimension(150, 50));
        topArea.add(alerts);
        add(topArea, BorderLayout.NORTH);

        // Center Area using JSplitPane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.75);

        // Billing Table (Left)
        JPanel leftPanel = new JPanel(new BorderLayout());
        PlaceholderTextField searchBar = new PlaceholderTextField("Search products you want to sell (Alt+S)");
        searchBar.setBackground(AppColors.YELLOW_HIGHLIGHT);
        searchBar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && productPopup.isVisible()) {
                    productPopup.focusTable();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    productPopup.setVisible(false);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    return;
                } else if (searchBar.getText().length() > 1) {
                    productPopup.search(searchBar.getText().trim());
                    Point p = searchBar.getLocationOnScreen();
                    productPopup.setLocation(p.x, p.y + searchBar.getHeight());
                    productPopup.setVisible(true);
                    searchBar.requestFocus();
                } else {
                    productPopup.setVisible(false);
                }
            }
        });
        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchWrapper.add(searchBar);
        leftPanel.add(searchWrapper, BorderLayout.NORTH);

        String[] cols = {"Sr", "Product", "Batch", "Expiry", "Qty", "Loose", "Rate", "MRP", "Disc%", "Amount", "Action"};
        billModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c >= 4 && c <= 8; }
        };
        billTable = new JTable(billModel);
        billTable.setRowHeight(30);
        leftPanel.add(new JScrollPane(billTable), BorderLayout.CENTER);

        // Bottom Bar in Left
        JPanel bottomBar = new JPanel(new GridBagLayout());
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1: Patient and Doctor
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        PlaceholderTextField ptField = new PlaceholderTextField("Patient (Alt+P)");
        ptField.setBackground(AppColors.WHITE);
        ptField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && patientPopup.isVisible()) {
                    patientPopup.focusTable();
                    e.consume();
                } else if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_P) {
                    Window win = SwingUtilities.getWindowAncestor(SalesPanel.this);
                    if (win instanceof Frame) {
                        new PatientDialog((Frame) win).setVisible(true);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    patientPopup.setVisible(false);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
                
                if (ptField.getText().length() > 1) {
                    patientPopup.search(ptField.getText().trim());
                    Point p = ptField.getLocationOnScreen();
                    patientPopup.setLocation(p.x, p.y + ptField.getHeight());
                    patientPopup.setVisible(true);
                    ptField.requestFocus();
                } else {
                    patientPopup.setVisible(false);
                }
            }
        });
        patientPopup.setSelectionListener(patient -> {
            ptField.setText(patient.getName());
            selectedPatientId = patient.getId();
            patientPopup.setVisible(false);
        });
        bottomBar.add(ptField, gbc);

        gbc.gridx = 1; gbc.weightx = 0.5;
        PlaceholderTextField drField = new PlaceholderTextField("Doctor (Alt+T)");
        drField.setBackground(AppColors.WHITE);
        drField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && doctorPopup.isVisible()) {
                    doctorPopup.focusTable();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    doctorPopup.setVisible(false);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ESCAPE) return;
                
                if (drField.getText().length() > 1) {
                    doctorPopup.search(drField.getText().trim());
                    Point p = drField.getLocationOnScreen();
                    doctorPopup.setLocation(p.x, p.y + drField.getHeight());
                    doctorPopup.setVisible(true);
                    drField.requestFocus();
                } else {
                    doctorPopup.setVisible(false);
                }
            }
        });
        doctorPopup.setListener(doctor -> {
            drField.setText(doctor.getName());
            selectedDoctorId = doctor.getId();
            doctorPopup.setVisible(false);
        });
        bottomBar.add(drField, gbc);

        // Row 2: Remarks and Mode
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        bottomBar.add(new PlaceholderTextField("Remarks (Alt+R)"), gbc);

        JPanel pmtPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        pmtPanel.add(new JComboBox<>(new String[]{"CASH", "CREDIT", "UPI", "CARD"}));
        
        PlaceholderTextField txtPaid = new PlaceholderTextField("Paid Amt");
        PlaceholderTextField txtBalance = new PlaceholderTextField("Balance");
        txtBalance.setEditable(false);
        txtBalance.setBackground(AppColors.TABLE_ALT_ROW);
        
        pmtPanel.add(txtPaid);
        pmtPanel.add(txtBalance);
        
        gbc.gridx = 1; gbc.weightx = 0.5;
        bottomBar.add(pmtPanel, gbc);

        // Row 3: Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.add(new RoundedButton("Print & Save Ctrl P", AppColors.PRIMARY_PURPLE, Color.WHITE, 12));
        
        RoundedButton btnSave = new RoundedButton("Save Bill Ctrl S", AppColors.SUCCESS_GREEN, Color.WHITE, 12);
        btnSave.addActionListener(e -> {
            if (billModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Cart is empty!");
                return;
            }
            try {
                SalesBill bill = new SalesBill();
                bill.setInvoiceNo(new com.pharmacypro.dao.SalesDAO().generateInvoiceNumber());
                bill.setPatientId(selectedPatientId);
                bill.setDoctorId(selectedDoctorId);
                bill.setBillDate(java.time.LocalDate.now());
                JComboBox<String> comboPmt = (JComboBox<String>) pmtPanel.getComponent(0);
                bill.setPaymentMode(comboPmt.getSelectedItem().toString());
                PlaceholderTextField txtRemarks = (PlaceholderTextField) bottomBar.getComponent(2); // Remarks is the 3rd component added (index 2)
                bill.setRemarks(txtRemarks.getText());
                
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (int i = 0; i < billModel.getRowCount(); i++) {
                     totalAmount = totalAmount.add(new BigDecimal(billModel.getValueAt(i, 9).toString()));
                }
                bill.setBillAmount(totalAmount);
                bill.setTotalAmount(totalAmount);
                bill.setDiscount(BigDecimal.ZERO);
                bill.setExtraDiscount(BigDecimal.ZERO);
                bill.setRoundOff(BigDecimal.ZERO);
                
                PlaceholderTextField txtPaidField = (PlaceholderTextField) pmtPanel.getComponent(1);
                String paidText = txtPaidField.getText().trim();
                if (paidText.isEmpty() || paidText.equals("Paid Amt")) {
                    bill.setAmountPaid(BigDecimal.ZERO);
                } else {
                    try {
                        bill.setAmountPaid(new BigDecimal(paidText));
                    } catch(Exception parseEx) { bill.setAmountPaid(BigDecimal.ZERO); }
                }
                
                bill.setBalance(totalAmount.subtract(bill.getAmountPaid()));
                bill.setCreatedBy("admin");

                java.util.List<SalesBillItem> items = new java.util.ArrayList<>();
                for (int i = 0; i < billModel.getRowCount(); i++) {
                     SalesBillItem item = new SalesBillItem();
                     item.setProductId(cartProducts.get(i).getId());
                     item.setBatchId(cartBatches.get(i).getId());
                     item.setQuantity(Integer.parseInt(billModel.getValueAt(i, 4).toString()));
                     item.setLooseQty(0);
                     item.setRate(new BigDecimal(billModel.getValueAt(i, 6).toString()));
                     item.setMrp(new BigDecimal(billModel.getValueAt(i, 7).toString()));
                     item.setDiscountPercent(new BigDecimal(billModel.getValueAt(i, 8).toString()));
                     item.setAmount(new BigDecimal(billModel.getValueAt(i, 9).toString()));
                     items.add(item);
                }
                
                int result = new com.pharmacypro.dao.SalesDAO().saveBill(bill, items);
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Bill Saved Successfully! ID: " + result);
                    billModel.setRowCount(0);
                    cartProducts.clear();
                    cartBatches.clear();
                    updateBillTotals();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving bill: " + ex.getMessage());
            }
        });
        btnRow.add(btnSave);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        bottomBar.add(btnRow, gbc);

        // Row 4: Shortcuts
        JPanel shortcutRow = new JPanel(new BorderLayout());
        shortcutRow.setBackground(Color.BLACK);
        JLabel lblShortcut = new JLabel("  Ctrl L Shortcuts | Ctrl B Add Batch | Esc Close Search");
        lblShortcut.setForeground(Color.WHITE);
        lblShortcut.setFont(new Font("Segoe UI", Font.BOLD, 12));
        shortcutRow.add(lblShortcut, BorderLayout.WEST);
        
        gbc.gridy = 3; gbc.insets = new Insets(10, -10, -10, -10);
        bottomBar.add(shortcutRow, gbc);

        leftPanel.add(bottomBar, BorderLayout.SOUTH);
        split.setLeftComponent(leftPanel);

        // Bill Summary Panel (Right)
        summary = new BillSummaryPanel();
        split.setRightComponent(summary);

        add(split, BorderLayout.CENTER);

        // Table Change Listener for Totals
        billModel.addTableModelListener(e -> updateBillTotals());
        
        billTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = billTable.getSelectedColumn();
                int row = billTable.getSelectedRow();
                if (col == 10 && row != -1) { // Action Delete Column
                    billModel.removeRow(row);
                    cartProducts.remove(row);
                    cartBatches.remove(row);
                    for(int i=0; i<billModel.getRowCount(); i++) {
                        billModel.setValueAt(i+1, i, 0); // Update Sr No
                    }
                    updateBillTotals();
                }
            }
        });
    }

    private void addBillRow(Product product, ProductBatch batch) {
        cartProducts.add(product);
        cartBatches.add(batch);
        int sr = billModel.getRowCount() + 1;
        BigDecimal rate = batch.getMrp(); // Default to MRP as rate
        billModel.addRow(new Object[]{
            sr, product.getName(), batch.getBatchNo(), batch.getExpiryDate(), 
            1, 0, rate, batch.getMrp(), 0, rate, "Delete"
        });
    }

    private boolean isUpdatingTotals = false;

    private void updateBillTotals() {
        if (isUpdatingTotals) return;
        isUpdatingTotals = true;
        try {
            int items = billModel.getRowCount();
            int totalQty = 0;
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;

            for (int i = 0; i < items; i++) {
                try {
                    int qty = Integer.parseInt(billModel.getValueAt(i, 4).toString());
                    BigDecimal rate = new BigDecimal(billModel.getValueAt(i, 6).toString());
                    BigDecimal discPct = new BigDecimal(billModel.getValueAt(i, 8).toString());
                    
                    BigDecimal rowAmount = rate.multiply(new BigDecimal(qty));
                    BigDecimal rowDisc = rowAmount.multiply(discPct).divide(new BigDecimal(100));
                    BigDecimal finalAmount = rowAmount.subtract(rowDisc);

                    billModel.setValueAt(finalAmount, i, 9);
                    
                    totalQty += qty;
                    subtotal = subtotal.add(rowAmount);
                    totalDiscount = totalDiscount.add(rowDisc);
                } catch (Exception ex) {}
            }

            BigDecimal total = subtotal.subtract(totalDiscount);
            summary.updateTotals(items, totalQty, subtotal, totalDiscount, total);
        } finally {
            isUpdatingTotals = false;
        }
    }
}
