package com.pharmacypro.ui.panels;

import javax.swing.*;
import java.awt.*;
import com.pharmacypro.utils.AppColors;

public class ConfigPanel extends JPanel {
    public ConfigPanel() {
        setLayout(new BorderLayout());
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(200);
        
        // Left
        String[] listData = {"Account", "Sales", "Purchase", "Return", "Other Setting", "Email", "Bill Printing"};
        JList<String> list = new JList<>(listData);
        list.setBackground(Color.DARK_GRAY);
        list.setForeground(Color.WHITE);
        list.setSelectedIndex(4); // "Other Setting" selected
        split.setLeftComponent(new JScrollPane(list));
        
        // Right
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Report Setting
        right.add(new JLabel("Report Setting"));
        right.add(new JCheckBox("Only View Reports for Current Day"));
        JPanel pnlRep = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlRep.add(new JLabel("Set Number of Days for Report Data"));
        pnlRep.add(new JSpinner(new SpinnerNumberModel(7, 1, 365, 1)));
        right.add(pnlRep);
        
        right.add(new JSeparator());
        
        // SMS Setting
        right.add(new JLabel("SMS Settings"));
        JCheckBox chkSms = new JCheckBox("Enable SMS Notification"); chkSms.setSelected(true);
        right.add(chkSms);
        right.add(new JCheckBox("Confirm Before Sending SMS"));
        
        right.add(new JSeparator());
        
        // Required Passwords
        right.add(new JLabel("Required Passwords on:"));
        right.add(new JCheckBox("Saving Sales Bill"));
        right.add(new JCheckBox("Edit Sales"));
        right.add(new JCheckBox("Delete Sales"));
        right.add(new JCheckBox("Saving Purchase Bill"));
        
        right.add(new JSeparator());
        
        JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTabs.add(new JLabel("Set Maximum Tabs"));
        pnlTabs.add(new JSpinner(new SpinnerNumberModel(15, 1, 50, 1)));
        right.add(pnlTabs);
        
        // Save
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        btnSave.setBackground(AppColors.BLUE_BG);
        footer.add(btnSave);
        right.add(footer);
        
        split.setRightComponent(new JScrollPane(right));
        add(split, BorderLayout.CENTER);
    }
}
