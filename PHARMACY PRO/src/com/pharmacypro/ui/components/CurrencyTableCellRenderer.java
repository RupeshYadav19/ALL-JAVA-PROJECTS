package com.pharmacypro.ui.components;

import com.pharmacypro.utils.CurrencyFormatter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;
import java.math.BigDecimal;
import java.awt.Color;

public class CurrencyTableCellRenderer extends DefaultTableCellRenderer {
    public CurrencyTableCellRenderer() {
        setHorizontalAlignment(RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof BigDecimal) {
            BigDecimal amount = (BigDecimal) value;
            setText(CurrencyFormatter.format(amount));

            if (!isSelected) {
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    setForeground(Color.RED);
                } else if (amount.compareTo(BigDecimal.ZERO) == 0) {
                    setForeground(Color.GRAY);
                } else {
                    setForeground(Color.BLACK);
                }
            }
        }
        return c;
    }
}
