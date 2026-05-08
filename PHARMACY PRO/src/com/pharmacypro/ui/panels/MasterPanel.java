package com.pharmacypro.ui.panels;

import com.pharmacypro.dao.*;
import com.pharmacypro.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.pharmacypro.ui.dialogs.PatientDialog;
import com.pharmacypro.ui.dialogs.DoctorDialog;
import com.pharmacypro.ui.dialogs.DistributorDialog;
import com.pharmacypro.ui.dialogs.ProductDialog;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MasterPanel extends JPanel {
    private JTable pTable, dTable, distTable, prodTable;
    private DefaultTableModel pModel, dModel, distModel, prodModel;
    private JTextField pSearch, dSearch, distSearch, prodSearch;

    public MasterPanel() {
        setLayout(new BorderLayout());
        
        JTabbedPane tabs = new JTabbedPane();
        
        // --- Patients Tab ---
        JPanel patientsPanel = new JPanel(new BorderLayout());
        JPanel pTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSearch = new JTextField(20);
        JButton btnAddP = new JButton("Add Patient");
        JButton btnRefreshP = new JButton("Refresh");
        
        btnAddP.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            if(win instanceof Frame) {
                new PatientDialog((Frame)win).setVisible(true);
                loadPatientData(); // Refresh after dialog
            }
        });
        btnRefreshP.addActionListener(e -> loadPatientData());
        
        JButton btnDelP = new JButton("Delete Selected");
        btnDelP.addActionListener(e -> {
            int row = pTable.getSelectedRow();
            if(row >= 0) {
                int id = (int) pModel.getValueAt(row, 0);
                try {
                    new PatientDAO().deletePatient(id);
                    loadPatientData();
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });

        pSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { loadPatientData(); }
        });
        
        pTop.add(new JLabel("Search:")); pTop.add(pSearch); pTop.add(btnAddP); pTop.add(btnRefreshP); pTop.add(btnDelP);
        patientsPanel.add(pTop, BorderLayout.NORTH);
        
        String[] pCols = {"ID", "Name", "Mobile", "Email", "Address", "Identifier", "Outstanding"};
        pModel = new DefaultTableModel(pCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        pTable = new JTable(pModel);
        patientsPanel.add(new JScrollPane(pTable), BorderLayout.CENTER);
        tabs.addTab("Patients", patientsPanel);
        
        // --- Doctors Tab ---
        JPanel doctorsPanel = new JPanel(new BorderLayout());
        JPanel dTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dSearch = new JTextField(20);
        JButton btnAddD = new JButton("Add Doctor");
        JButton btnRefreshD = new JButton("Refresh");
        btnAddD.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            if(win instanceof Frame) {
                new DoctorDialog((Frame)win).setVisible(true);
                loadDoctorData();
            }
        });
        btnRefreshD.addActionListener(e -> loadDoctorData());
        
        JButton btnDelD = new JButton("Delete Selected");
        btnDelD.addActionListener(e -> {
            int row = dTable.getSelectedRow();
            if(row >= 0) {
                int id = (int) dModel.getValueAt(row, 0);
                try {
                    new DoctorDAO().deleteDoctor(id);
                    loadDoctorData();
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });

        dSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { loadDoctorData(); }
        });
        dTop.add(new JLabel("Search:")); dTop.add(dSearch); dTop.add(btnAddD); dTop.add(btnRefreshD); dTop.add(btnDelD);
        doctorsPanel.add(dTop, BorderLayout.NORTH);
        
        String[] dCols = {"ID", "Name", "Mobile", "Specialization", "Address"};
        dModel = new DefaultTableModel(dCols, 0);
        dTable = new JTable(dModel);
        doctorsPanel.add(new JScrollPane(dTable), BorderLayout.CENTER);
        tabs.addTab("Doctors", doctorsPanel);
        
        // --- Distributors Tab ---
        JPanel distPanel = new JPanel(new BorderLayout());
        JPanel distTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        distSearch = new JTextField(20);
        JButton btnAddDist = new JButton("Add Distributor");
        JButton btnRefreshDist = new JButton("Refresh");
        btnAddDist.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            if(win instanceof Frame) {
                new DistributorDialog((Frame)win).setVisible(true);
                loadDistributorData();
            }
        });
        btnRefreshDist.addActionListener(e -> loadDistributorData());
        
        JButton btnDelDist = new JButton("Delete Selected");
        btnDelDist.addActionListener(e -> {
            int row = distTable.getSelectedRow();
            if(row >= 0) {
                int id = (int) distModel.getValueAt(row, 0);
                try {
                    new DistributorDAO().deleteDistributor(id);
                    loadDistributorData();
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });

        distSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { loadDistributorData(); }
        });
        distTop.add(new JLabel("Search:")); distTop.add(distSearch); distTop.add(btnAddDist); distTop.add(btnRefreshDist); distTop.add(btnDelDist);
        distPanel.add(distTop, BorderLayout.NORTH);
        
        String[] distCols = {"ID", "Name", "Mobile", "GST No", "Pending Amt", "Email"};
        distModel = new DefaultTableModel(distCols, 0);
        distTable = new JTable(distModel);
        distPanel.add(new JScrollPane(distTable), BorderLayout.CENTER);
        tabs.addTab("Distributors", distPanel);
        
        // --- Added Products Tab ---
        JPanel prodPanel = new JPanel(new BorderLayout());
        JPanel prodTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prodSearch = new JTextField(20);
        JButton btnAddProd = new JButton("Add Product");
        JButton btnRefreshProd = new JButton("Refresh");
        btnAddProd.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            if(win instanceof Frame) {
                new ProductDialog((Frame)win).setVisible(true);
                loadProductData();
            }
        });
        btnRefreshProd.addActionListener(e -> loadProductData());
        
        JButton btnDelProd = new JButton("Delete Selected");
        btnDelProd.addActionListener(e -> {
            int row = prodTable.getSelectedRow();
            if(row >= 0) {
                int id = (int) prodModel.getValueAt(row, 0);
                try {
                    new ProductDAO().deleteProduct(id);
                    loadProductData();
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });

        prodSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { loadProductData(); }
        });
        prodTop.add(new JLabel("Search:")); prodTop.add(prodSearch); prodTop.add(btnAddProd); prodTop.add(btnRefreshProd); prodTop.add(btnDelProd);
        prodPanel.add(prodTop, BorderLayout.NORTH);
        
        String[] prodCols = {"ID", "Name", "Manufacturer", "MRP", "Pack Size", "HSN"};
        prodModel = new DefaultTableModel(prodCols, 0);
        prodTable = new JTable(prodModel);
        prodPanel.add(new JScrollPane(prodTable), BorderLayout.CENTER);
        tabs.addTab("Added Products", prodPanel);
        
        add(tabs, BorderLayout.CENTER);
        
        // Initial Data Load
        loadAllData();
    }

    private void loadAllData() {
        loadPatientData();
        loadDoctorData();
        loadDistributorData();
        loadProductData();
    }

    private void loadPatientData() {
        try {
            pModel.setRowCount(0);
            List<Patient> patients = new PatientDAO().searchPatients(pSearch.getText().trim());
            for (Patient p : patients) {
                pModel.addRow(new Object[]{p.getId(), p.getName(), p.getMobile(), p.getEmail(), p.getAddress(), p.getIdentifier(), p.getOutstanding()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadDoctorData() {
        try {
            dModel.setRowCount(0);
            List<Doctor> doctors = new DoctorDAO().searchDoctors(dSearch.getText().trim());
            for (Doctor d : doctors) {
                dModel.addRow(new Object[]{d.getId(), d.getName(), d.getMobile(), d.getSpecialization(), d.getAddress()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadDistributorData() {
        try {
            distModel.setRowCount(0);
            List<Distributor> distributors = new DistributorDAO().searchDistributors(distSearch.getText().trim());
            for (Distributor d : distributors) {
                distModel.addRow(new Object[]{d.getId(), d.getName(), d.getMobile(), d.getGstNo(), d.getPendingAmount(), d.getEmail()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadProductData() {
        try {
            prodModel.setRowCount(0);
            List<Product> products = new ProductDAO().searchProducts(prodSearch.getText().trim());
            for (Product p : products) {
                prodModel.addRow(new Object[]{p.getId(), p.getName(), p.getManufacturer(), p.getDefaultMrp(), p.getPackSize(), p.getHsnCode()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
