package com.pharmacypro.ui.dialogs;

import com.pharmacypro.dao.DistributorDAO;
import com.pharmacypro.models.Distributor;
import com.pharmacypro.utils.AppColors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DistributorSearchPopup extends JWindow {
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblPendingAmt, lblPendingInvoice;
    private List<Distributor> currentDistributors;

    public interface DistributorSelectionListener {
        void onDistributorSelected(Distributor distributor);
    }
    
    private DistributorSelectionListener listener;

    public void setSelectionListener(DistributorSelectionListener listener) {
        this.listener = listener;
    }

    public DistributorSearchPopup(Window owner) {
        super(owner);
        setLayout(new BorderLayout());
        ((JPanel)getContentPane()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        // Main split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(500);

        // Left Panel (Table)
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("  Select Distributor (Enter) | Alt+D Add New "), BorderLayout.NORTH);
        
        String[] cols = {"Name", "Mobile", "GST No"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);

        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    int row = table.getSelectedRow();
                    if (row != -1 && listener != null && currentDistributors != null) {
                        listener.onDistributorSelected(currentDistributors.get(row));
                        setVisible(false);
                    }
                    e.consume();
                } else if (e.isAltDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_D) {
                    Window win = SwingUtilities.getWindowAncestor(DistributorSearchPopup.this);
                    if (win instanceof Frame) {
                        new DistributorDialog((Frame) win).setVisible(true);
                        setVisible(false);
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1 && listener != null && currentDistributors != null) {
                        listener.onDistributorSelected(currentDistributors.get(row));
                        setVisible(false);
                    }
                }
            }
        });

        left.add(new JScrollPane(table), BorderLayout.CENTER);
        split.setLeftComponent(left);

        // Right Panel (Details Summary)
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        right.setBackground(AppColors.TEAL_BG);
        
        lblPendingAmt = new JLabel("Pending Amount: ₹0.00");
        lblPendingInvoice = new JLabel("Pending Invoices: 0");
        
        right.add(new JLabel("Distributor Details:"));
        right.add(Box.createVerticalStrut(10));
        right.add(lblPendingAmt);
        right.add(lblPendingInvoice);
        right.add(Box.createVerticalGlue());
        
        split.setRightComponent(right);
        add(split, BorderLayout.CENTER);
        
        setSize(800, 350);
    }

    public void search(String query) {
        try {
            model.setRowCount(0);
            currentDistributors = new DistributorDAO().searchDistributors(query);
            for (Distributor d : currentDistributors) {
                model.addRow(new Object[]{d.getName(), d.getMobile(), d.getGstNo()});
            }
            if (!currentDistributors.isEmpty()) {
                table.setRowSelectionInterval(0, 0);
                lblPendingAmt.setText("Pending Amount: ₹" + currentDistributors.get(0).getPendingAmount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
