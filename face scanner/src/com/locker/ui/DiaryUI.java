package com.locker.ui;

import com.locker.db.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DiaryUI extends JFrame {
    private int userId;
    private JPanel listPanel;
    private JTextArea contentArea;
    private JTextField titleField;

    public DiaryUI(int userId) {
        this.userId = userId;
        setTitle("My Secure Diary");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Sidebar for list of entries
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBackground(new Color(240, 242, 245));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JLabel listHeader = new JLabel("Past Entries", JLabel.CENTER);
        listHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        listHeader.setPreferredSize(new Dimension(300, 50));
        sidebar.add(listHeader, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        sidebar.add(scrollPane, BorderLayout.CENTER);

        // Main area for writing new entry
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(Color.WHITE);
        mainArea.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleField.setBorder(BorderFactory.createTitledBorder("Title"));
        headerPanel.add(titleField, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save Entry");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(new Color(34, 197, 94));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(150, 50));
        headerPanel.add(saveBtn, BorderLayout.EAST);

        mainArea.add(headerPanel, BorderLayout.NORTH);

        contentArea = new JTextArea();
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createTitledBorder("Content"));
        JScrollPane contentScroll = new JScrollPane(contentArea);
        mainArea.add(contentScroll, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainArea, BorderLayout.CENTER);

        saveBtn.addActionListener(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            if (!title.isEmpty() && !content.isEmpty()) {
                if (DatabaseManager.addDiaryEntry(userId, title, content)) {
                    JOptionPane.showMessageDialog(this, "Entry Saved!");
                    refreshList();
                    titleField.setText("");
                    contentArea.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill both title and content!");
            }
        });

        refreshList();
        setVisible(true);
    }

    private void refreshList() {
        listPanel.removeAll();
        List<String[]> entries = DatabaseManager.getDiaryEntries(userId);
        for (String[] entry : entries) {
            JPanel entryItem = new JPanel(new BorderLayout());
            entryItem.setMaximumSize(new Dimension(300, 80));
            entryItem.setBackground(Color.WHITE);
            entryItem.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));

            JLabel titleLbl = new JLabel(entry[0]);
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel dateLbl = new JLabel(entry[2]);
            dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            dateLbl.setForeground(Color.GRAY);

            entryItem.add(titleLbl, BorderLayout.NORTH);
            entryItem.add(dateLbl, BorderLayout.SOUTH);

            entryItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            entryItem.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    titleField.setText(entry[0]);
                    contentArea.setText(entry[1]);
                }
            });

            listPanel.add(entryItem);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}
