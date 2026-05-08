package com.pharmacypro.ui.components;

import com.pharmacypro.utils.AppFonts;
import com.pharmacypro.utils.CurrencyFormatter;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class BillSummaryPanel extends JPanel {
    private JLabel lblOutstanding;
    private JLabel lblItemsQty;
    private JLabel lblMarginPct;
    private JLabel lblMarginAmt;
    private JLabel lblBillAmount;
    private JLabel lblDiscount;
    private JLabel lblExtraDiscount;
    private JLabel lblRoundOff;
    private JLabel lblTotal;

    public BillSummaryPanel() {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Bill Summary");
        header.setFont(AppFonts.HEADING);
        add(header);
        add(Box.createRigidArea(new Dimension(0, 10)));

        lblOutstanding = addRow("Outstanding:", "₹0.00");
        lblItemsQty = addRow("Items/qty:", "0/0");
        lblMarginPct = addRow("Margin (%):", "0");
        lblMarginAmt = addRow("Margin:", "₹0.00");
        lblBillAmount = addRow("Bill Amount:", "₹0.00");
        lblDiscount = addRow("Discount:", "₹0.00");
        lblExtraDiscount = addRow("Extra Discount:", "₹0.00");
        lblRoundOff = addRow("Round-off:", "₹0.00");
        
        add(Box.createRigidArea(new Dimension(0, 10)));
        JSeparator sep = new JSeparator();
        add(sep);
        add(Box.createRigidArea(new Dimension(0, 10)));
        
        lblTotal = addRow("Total:", "₹0.00");
        lblTotal.setFont(AppFonts.AMOUNT);
    }

    private JLabel addRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setFont(AppFonts.BODY);
        JLabel v = new JLabel(value);
        v.setFont(AppFonts.BODY);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        add(row);
        add(Box.createRigidArea(new Dimension(0, 5)));
        return v;
    }

    public void updateTotals(int items, int qty, BigDecimal subtotal, BigDecimal discount, BigDecimal total) {
        lblItemsQty.setText(items + "/" + qty);
        lblBillAmount.setText(CurrencyFormatter.format(subtotal));
        lblDiscount.setText(CurrencyFormatter.format(discount));
        lblTotal.setText(CurrencyFormatter.format(total));
    }
}
