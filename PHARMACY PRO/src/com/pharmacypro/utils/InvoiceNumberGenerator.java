package com.pharmacypro.utils;

public class InvoiceNumberGenerator {
    // Already implemented inside DAOs in earlier phases if following patterns, 
    // but here is a simple util version for flexibility.
    public static String generateNextSalesInvoice(int nextId) {
        return "SL-" + nextId + "F";
    }
}
