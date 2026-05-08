package com.greexam.ui.teacher;

import com.greexam.dao.UserDAO;
import com.greexam.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class StudentsListPanel extends JPanel {

    private UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public StudentsListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
        loadStudents("");
    }

    private void initUI() {
        // Top Panel for Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search Students:"));
        txtSearch = new JTextField(25);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadStudents(txtSearch.getText().trim());
            }
        });
        topPanel.add(txtSearch);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadStudents(txtSearch.getText().trim()));
        topPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Username", "Email", "Registered On"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStudents(String keyword) {
        tableModel.setRowCount(0);
        List<User> students;
        if (keyword.isEmpty()) {
            students = userDAO.findAllStudents();
        } else {
            students = userDAO.searchStudents(keyword);
        }

        for (User u : students) {
            tableModel.addRow(new Object[]{
                    u.getId(),
                    u.getName(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getCreatedAt()
            });
        }
    }
}
