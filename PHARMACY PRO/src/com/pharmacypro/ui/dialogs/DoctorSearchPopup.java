package com.pharmacypro.ui.dialogs;

import com.pharmacypro.dao.DoctorDAO;
import com.pharmacypro.models.Doctor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DoctorSearchPopup extends JWindow {
    private JTable table;
    private DefaultTableModel model;
    private DoctorDAO doctorDAO = new DoctorDAO();
    private List<Doctor> currentDoctors;
    private DoctorSelectionListener listener;

    public interface DoctorSelectionListener {
        void onDoctorSelected(Doctor doctor);
    }

    public DoctorSearchPopup(Window owner) {
        super(owner);
        setLayout(new BorderLayout());
        ((JPanel)getContentPane()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        String[] cols = {"Name", "Specialization", "Mobile"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectDoctor();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectDoctor();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        setSize(400, 200);
    }

    public void setListener(DoctorSelectionListener listener) {
        this.listener = listener;
    }

    public void search(String query) {
        try {
            model.setRowCount(0);
            currentDoctors = doctorDAO.searchDoctors(query);
            for (Doctor d : currentDoctors) {
                model.addRow(new Object[]{d.getName(), d.getSpecialization(), d.getMobile()});
            }
            if (!currentDoctors.isEmpty()) {
                table.setRowSelectionInterval(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectDoctor() {
        int row = table.getSelectedRow();
        if (row != -1 && listener != null) {
            listener.onDoctorSelected(currentDoctors.get(row));
            setVisible(false);
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
