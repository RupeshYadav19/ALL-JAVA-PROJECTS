package com.pharmacypro.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class ProductInfoDialog extends JDialog {
    public ProductInfoDialog(Frame owner) {
        super(owner, "Product Info", true);
        setSize(900, 500);
        setLayout(new BorderLayout());
        
        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("DOLO 650MG STRIP OF 15 TABLETS [50::10]");
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel man = new JLabel("Manufacturer Name: MICRO LABS");
        man.setForeground(Color.GRAY);
        JLabel comp = new JLabel("Composition: PARACETAMOL/ACETAMINOPHEN(650.0 MG)");
        comp.setForeground(Color.GRAY);
        
        header.add(name);
        header.add(man);
        header.add(comp);
        add(header, BorderLayout.NORTH);
        
        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Transactions Alt 1", new JScrollPane(new JTable(new String[][]{}, new String[]{"Distributor", "Eff.CP \u20B9", "MRP \u20B9", "Margin %", "Invoice"})));
        
        JPanel gen = new JPanel(new GridLayout(6, 2));
        gen.add(new JLabel("Product Name:")); gen.add(new JTextField());
        gen.add(new JLabel("Manufacturer:")); gen.add(new JTextField());
        gen.add(new JLabel("Composition:")); gen.add(new JTextField());
        gen.add(new JLabel("HSN Code:")); gen.add(new JTextField());
        tabs.addTab("General Details Alt 2", gen);
        
        tabs.addTab("Substitutes Alt 3", new JScrollPane(new JTable(new String[][]{}, new String[]{"Product Name", "Margin %"})));
        tabs.addTab("Margin Alt 4", new JPanel());
        
        add(tabs, BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(new JButton("Close"));
        add(footer, BorderLayout.SOUTH);
    }
}
