import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CurrencyConverter extends JFrame {
    private ExchangeRate exchangeRate;
    private ConversionHistory conversionHistory;

    // UI Components
    private JTextField amountField;
    private JComboBox<String> fromCombo;
    private JComboBox<String> toCombo;
    private JLabel resultLabel;
    private JTextArea historyArea;
    private JButton convertBtn, swapBtn, resetBtn;

    public CurrencyConverter() {
        exchangeRate = new ExchangeRate();
        conversionHistory = new ConversionHistory();

        setTitle("Advanced Currency Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        addListeners();
    }

    private void initComponents() {
        // Main Panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(236, 240, 241)); // Light gray/blue

        // Header
        JLabel headerLabel = new JLabel("Premium Currency Converter", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(new Color(44, 62, 80));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
        Font activeFont = new Font("Segoe UI", Font.PLAIN, 18);

        JLabel amountLabel = new JLabel("Enter Amount:");
        amountLabel.setFont(labelFont);
        amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.BOLD, 22));
        amountField.setForeground(new Color(52, 73, 94));
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JLabel fromLabel = new JLabel("From Currency:");
        fromLabel.setFont(labelFont);
        fromCombo = new JComboBox<>(exchangeRate.getSupportedCurrencies());
        fromCombo.setFont(activeFont);
        fromCombo.setSelectedItem("USD - US Dollar");

        JLabel toLabel = new JLabel("To Currency:");
        toLabel.setFont(labelFont);
        toCombo = new JComboBox<>(exchangeRate.getSupportedCurrencies());
        toCombo.setFont(activeFont);
        toCombo.setSelectedItem("INR - Indian Rupee");

        swapBtn = new JButton("Swap ⇌");
        swapBtn.setBackground(new Color(41, 128, 185)); // Slightly darker blue
        swapBtn.setForeground(Color.WHITE);
        swapBtn.setFocusPainted(false);
        swapBtn.setOpaque(true);
        swapBtn.setBorderPainted(false);
        swapBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Layout Input Panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        inputPanel.add(amountLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        inputPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(fromLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(fromCombo, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(swapBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(toLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        inputPanel.add(toCombo, gbc);

        // Action Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);

        convertBtn = new JButton("CONVERT");
        convertBtn.setPreferredSize(new Dimension(180, 55));
        convertBtn.setBackground(new Color(39, 174, 96)); // Darker Green
        convertBtn.setForeground(Color.WHITE);
        convertBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        convertBtn.setOpaque(true);
        convertBtn.setBorderPainted(false);

        resetBtn = new JButton("RESET ALL");
        resetBtn.setPreferredSize(new Dimension(180, 55));
        resetBtn.setBackground(new Color(192, 57, 43)); // Darker Red
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        resetBtn.setOpaque(true);
        resetBtn.setBorderPainted(false);

        buttonPanel.add(convertBtn);
        buttonPanel.add(resetBtn);

        // Result Label
        resultLabel = new JLabel("Converted Amount: ---", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        resultLabel.setForeground(new Color(39, 174, 96));
        resultLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Container for Input and Result
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(inputPanel, BorderLayout.NORTH);
        centerWrapper.add(buttonPanel, BorderLayout.CENTER);
        centerWrapper.add(resultLabel, BorderLayout.SOUTH);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // History Panel
        JPanel historyPanel = new JPanel(new BorderLayout(5, 10));
        historyPanel.setOpaque(false);
        JLabel historyLabel = new JLabel("Conversion History (Recent 10)");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        historyLabel.setForeground(new Color(44, 62, 80));

        historyArea = new JTextArea(8, 20);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        historyArea.setBackground(Color.WHITE);
        historyArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));

        historyPanel.add(historyLabel, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(historyPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addListeners() {
        convertBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });

        swapBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String from = (String) fromCombo.getSelectedItem();
                String to = (String) toCombo.getSelectedItem();
                fromCombo.setSelectedItem(to);
                toCombo.setSelectedItem(from);
                resultLabel.setText("Converted Amount: ---");
            }
        });

        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                amountField.setText("");
                fromCombo.setSelectedItem("USD - US Dollar");
                toCombo.setSelectedItem("INR - Indian Rupee");
                resultLabel.setText("Converted Amount: ---");
                conversionHistory.clear();
                updateHistoryDisplay();
            }
        });
    }

    private void performConversion() {
        try {
            String amountStr = amountField.getText().trim();
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount.", "Input Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();

            double result = exchangeRate.convert(amount, from, to);
            resultLabel.setText(String.format("Converted Amount: %.2f %s", result, to));

            conversionHistory.addEntry(amount, from, to, result);
            updateHistoryDisplay();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format. Please enter a number.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during conversion: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHistoryDisplay() {
        List<String> history = conversionHistory.getHistory();
        StringBuilder sb = new StringBuilder();
        for (String entry : history) {
            sb.append(entry).append("\n");
        }
        historyArea.setText(sb.toString());
    }
}
