package com.pharmacypro.ui.dialogs;

import com.pharmacypro.dao.PatientDAO;
import com.pharmacypro.models.Patient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientSearchPopup extends JWindow {
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblLastVisit, lblOutstanding;
    private List<Patient> currentPatients;

    public interface PatientSelectionListener {
        void onPatientSelected(Patient patient);
    }
    
    private PatientSelectionListener listener;

    public void setSelectionListener(PatientSelectionListener listener) {
        this.listener = listener;
    }

    public PatientSearchPopup(Window owner) {
        super(owner);
        setLayout(new BorderLayout());
        ((JPanel)getContentPane()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        // Main Container
        JPanel main = new JPanel(new GridLayout(1, 2));
        
        // Left Column (Table)
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel(" Enter Select | Alt P Add New Patient "), BorderLayout.NORTH);
        
        String[] cols = {"Name", "Mobile", "Identifier"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    int row = table.getSelectedRow();
                    if (row != -1 && listener != null && currentPatients != null) {
                        listener.onPatientSelected(currentPatients.get(row));
                        setVisible(false);
                    }
                    e.consume();
                } else if (e.isAltDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_P) {
                    Window win = SwingUtilities.getWindowAncestor(PatientSearchPopup.this);
                    if (win instanceof Frame) {
                        new PatientDialog((Frame) win).setVisible(true);
                        setVisible(false);
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });
        
        left.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(left);

        // Right Column (Details)
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblLastVisit = new JLabel("Last Visit: -");
        lblOutstanding = new JLabel("Outstanding: ₹0.00");
        
        right.add(lblLastVisit);
        right.add(Box.createVerticalStrut(10));
        right.add(lblOutstanding);
        right.add(Box.createVerticalGlue());
        right.add(new JLabel("Profile completeness: 80%"));
        
        main.add(right);
        add(main, BorderLayout.CENTER);
        
        setSize(700, 300);
    }

    public void search(String query) {
        try {
            model.setRowCount(0);
            currentPatients = new PatientDAO().searchPatients(query);
            for (Patient p : currentPatients) {
                model.addRow(new Object[]{p.getName(), p.getMobile(), p.getIdentifier()});
            }
            if (!currentPatients.isEmpty()) {
                table.setRowSelectionInterval(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void focusTable() {
        if (table.getRowCount() > 0) {
            table.requestFocus();
            if (table.getSelectedRow() == -1) {
                table.setRowSelectionInterval(0, 0);
            }
        }
    }
}
