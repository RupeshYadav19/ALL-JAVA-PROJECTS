package com.journal.ui;

import com.journal.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class JournalFrame extends JFrame {
    private JTextArea notebookArea;
    private JSlider ratingSlider;
    private JLabel ratingValueLabel;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public JournalFrame() {
        setTitle("My Mental Health Journal - Notebook");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Journal Entry Tab
        JPanel entryPanel = createEntryPanel();
        tabbedPane.addTab("Write Daily Journal", entryPanel);

        // History Tab
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("Journal History", historyPanel);

        add(tabbedPane);
        loadHistory();
    }

    private JPanel createEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(253, 251, 241)); // Paper-like color

        // Notebook Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(245, 245, 220));
        JLabel dateLabel = new JLabel("Date: " + LocalDate.now());
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(dateLabel);

        JLabel pollLabel = new JLabel("How's your day? (1-10): ");
        pollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        header.add(pollLabel);

        ratingSlider = new JSlider(1, 10, 5);
        ratingSlider.setBackground(new Color(245, 245, 220));
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ratingSlider.setPreferredSize(new Dimension(200, 45));

        ratingValueLabel = new JLabel("Status: 5");
        ratingValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        ratingSlider.addChangeListener(e -> {
            ratingValueLabel.setText("Status: " + ratingSlider.getValue());
        });

        header.add(ratingSlider);
        header.add(ratingValueLabel);

        panel.add(header, BorderLayout.NORTH);

        // Notebook Area
        notebookArea = new JTextArea();
        notebookArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 18)); // Handwritten feel
        notebookArea.setBackground(new Color(255, 255, 255));
        notebookArea.setLineWrap(true);
        notebookArea.setWrapStyleWord(true);
        notebookArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JScrollPane scrollPane = new JScrollPane(notebookArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Footer with Submit Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new Color(245, 245, 220));
        JButton submitButton = new JButton("Save Journal");
        submitButton.setBackground(new Color(52, 152, 219));
        submitButton.setForeground(Color.BLACK);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.addActionListener(e -> saveEntry());
        footer.add(submitButton);

        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new String[] { "Date", "Rating", "Journal Content" }, 0);
        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh History");
        refreshBtn.addActionListener(e -> loadHistory());
        btnPanel.add(refreshBtn);

        JButton sendReportBtn = new JButton("Send Mental Health Report");
        sendReportBtn.setBackground(new Color(155, 89, 182));
        sendReportBtn.setForeground(Color.BLACK);
        sendReportBtn.addActionListener(e -> sendReport());
        btnPanel.add(sendReportBtn);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void saveEntry() {
        String content = notebookArea.getText();
        int rating = ratingSlider.getValue();

        if (content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please write something in your journal!");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO journal_entries (entry_date, content, rating) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setString(2, content);
            pstmt.setInt(3, rating);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Journal saved successfully!");
            notebookArea.setText("");
            loadHistory();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving entry: " + ex.getMessage());
        }
    }

    private void loadHistory() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT entry_date, rating, content FROM journal_entries ORDER BY entry_date DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getDate("entry_date"),
                        rs.getInt("rating"),
                        rs.getString("content")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Error loading history: " + ex.getMessage());
        }
    }

    private void sendReport() {
        String daysInput = JOptionPane.showInputDialog(this,
                "How many previous days of data do you want to include in the report?", "2");
        if (daysInput == null || daysInput.trim().isEmpty())
            return;

        int days;
        try {
            days = Integer.parseInt(daysInput.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of days.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT entry_date, rating FROM journal_entries WHERE entry_date >= ? ORDER BY entry_date ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(LocalDate.now().minusDays(days)));
            ResultSet rs = pstmt.executeQuery();

            StringBuilder report = new StringBuilder("Mental Health Progress Report (Last " + days + " Days)\n\n");
            int count = 0;
            double totalRating = 0;

            while (rs.next()) {
                report.append("Date: ").append(rs.getDate("entry_date"))
                        .append(" | Rating: ").append(rs.getInt("rating")).append("/10\n");
                totalRating += rs.getInt("rating");
                count++;
            }

            if (count < 2) {
                JOptionPane.showMessageDialog(this,
                        "You need at least 2 days of data within this range to generate a report.");
                return;
            }

            double average = totalRating / count;
            report.append("\nSummary:\n");
            report.append("Total Days Tracked: ").append(count).append("\n");
            report.append("Average Mood Rating: ").append(String.format("%.2f", average)).append("/10\n\n");

            // Mood-based encouragement
            report.append("--- Support & Encouragement ---\n");
            if (average < 6.0) {
                report.append(
                        "Daily Reminder: Everything will be fine. Tough days are temporary, but your strength is permanent. \n");
                report.append(
                        "Please take some time for yourself today and know that you are doing your best. Keep going! ❤\n");
            } else if (average >= 7.0) {
                report.append("Amazing Progress! You are doing great and keeping a wonderful positive momentum. \n");
                report.append(
                        "Keep maintaining these healthy habits and reflecting on your growth. Proud of you! 🌟\n");
            } else {
                report.append("Steadily balanced! You're navigating through your journey with consistency. \n");
                report.append("Keep reflecting and focusing on the small wins every day. You've got this! ✨\n");
            }

            String email = JOptionPane.showInputDialog(this, "Enter your email address to receive the report:");
            if (email != null && !email.trim().isEmpty()) {
                com.journal.service.EmailService.sendReport(email, "Your Mental Health Progress Report",
                        report.toString());
                JOptionPane.showMessageDialog(this, "Report sent successfully to " + email);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error sending email: " + ex.getMessage() + "\n(Check SMTP configuration at EmailService.java)");
        }
    }
}
