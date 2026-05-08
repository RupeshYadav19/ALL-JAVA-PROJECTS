package com.pharmacypro.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    public static String format(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return format.format(amount).replace("Rs.", "₹").replace("INR", "₹ ").trim();
    }
}
