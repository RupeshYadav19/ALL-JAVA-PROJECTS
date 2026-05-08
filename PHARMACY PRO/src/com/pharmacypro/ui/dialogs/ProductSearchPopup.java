package com.pharmacypro.ui.dialogs;

import com.pharmacypro.dao.ProductDAO;
import com.pharmacypro.models.Product;
import com.pharmacypro.models.ProductBatch;
import com.pharmacypro.utils.AppColors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ProductSearchPopup extends JWindow {
    private JTable prodTable, batchTable;
    private DefaultTableModel prodModel, batchModel;
    private ProductDAO productDAO = new ProductDAO();
    private List<Product> currentProducts;
    private List<ProductBatch> currentBatches;
    private ProductSelectionListener selectionListener;

    public interface ProductSelectionListener {
        void onProductSelected(Product product, ProductBatch batch);
    }

    public ProductSearchPopup(Window owner) {
        super(owner);
        setLayout(new BorderLayout());
        ((JPanel)getContentPane()).setBorder(BorderFactory.createLineBorder(AppColors.BORDER_GRAY));
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppColors.YELLOW_HIGHLIGHT);
        header.add(new JLabel("Search Product... (Esc to Close)"));
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(450);

        // Left Panel (Products)
        String[] prodCols = {"Product", "Manufacturer", "MRP ₹", "Pack"};
        prodModel = new DefaultTableModel(prodCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        prodTable = new JTable(prodModel);
        prodTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && prodTable.getSelectedRow() != -1 && currentProducts != null) {
                int index = prodTable.getSelectedRow();
                if (index < currentProducts.size()) {
                    loadBatches(currentProducts.get(index).getId());
                }
            }
        });

        prodTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    batchTable.requestFocus();
                    if (batchTable.getRowCount() > 0) {
                        batchTable.setRowSelectionInterval(0, 0);
                    }
                } else if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    showSubstitutes();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });

        split.setLeftComponent(new JScrollPane(prodTable));

        // Right Panel (Batches)
        JPanel right = new JPanel(new BorderLayout());
        String[] batchCols = {"Batch", "Expiry", "Stock", "MRP"};
        batchModel = new DefaultTableModel(batchCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        batchTable = new JTable(batchModel);
        
        batchTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectBatch();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    prodTable.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });

        right.add(new JScrollPane(batchTable), BorderLayout.CENTER);
        
        JPanel substitutes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        substitutes.add(new JLabel("Substitutes Alt+S | Select Batch Enter"));
        right.add(substitutes, BorderLayout.SOUTH);

        split.setRightComponent(right);
        add(split, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(new JLabel("Enter to Add | Ctrl B Add Batch"));
        add(footer, BorderLayout.SOUTH);
        
        setSize(800, 400);
    }

    public void setSelectionListener(ProductSelectionListener listener) {
        this.selectionListener = listener;
    }

    public void search(String query) {
        try {
            prodModel.setRowCount(0);
            batchModel.setRowCount(0);
            currentProducts = productDAO.searchProducts(query);
            
            for (Product p : currentProducts) {
                prodModel.addRow(new Object[]{p.getName(), p.getManufacturer(), p.getDefaultMrp(), p.getPackSize()});
            }
            
            if (!currentProducts.isEmpty()) {
                prodTable.setRowSelectionInterval(0, 0);
                loadBatches(currentProducts.get(0).getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSubstitutes() {
        int row = prodTable.getSelectedRow();
        if (row != -1) {
            try {
                Product p = currentProducts.get(row);
                List<Product> subs = productDAO.getSubstitutes(p.getId());
                if (subs.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No substitutes found for " + p.getName());
                    return;
                }
                currentProducts = subs;
                prodModel.setRowCount(0);
                for (Product s : subs) {
                    prodModel.addRow(new Object[]{s.getName(), s.getManufacturer(), s.getDefaultMrp(), s.getPackSize()});
                }
                prodTable.setRowSelectionInterval(0, 0);
                loadBatches(currentProducts.get(0).getId());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void selectBatch() {
        int pRow = prodTable.getSelectedRow();
        int bRow = batchTable.getSelectedRow();
        if (pRow != -1 && bRow != -1 && selectionListener != null) {
            selectionListener.onProductSelected(currentProducts.get(pRow), currentBatches.get(bRow));
            setVisible(false);
        }
    }

    private void loadBatches(int productId) {
        try {
            batchModel.setRowCount(0);
            currentBatches = productDAO.getBatchesForProduct(productId);
            for (ProductBatch b : currentBatches) {
                batchModel.addRow(new Object[]{b.getBatchNo(), b.getExpiryDate(), b.getQuantity(), b.getMrp()});
            }
            if (!currentBatches.isEmpty()) {
                batchTable.setRowSelectionInterval(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void focusTable() {
        if (prodTable.getRowCount() > 0) {
            prodTable.requestFocus();
            if (prodTable.getSelectedRow() == -1) {
                prodTable.setRowSelectionInterval(0, 0);
                loadBatches(currentProducts.get(0).getId());
            }
        }
    }
}
