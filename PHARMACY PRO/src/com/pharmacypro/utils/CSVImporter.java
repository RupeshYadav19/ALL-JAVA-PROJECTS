package com.pharmacypro.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import com.pharmacypro.models.PurchaseBillItem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CSVImporter {
    public static List<PurchaseBillItem> parsePurchaseCSV(String filePath) {
        List<PurchaseBillItem> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean first = true;
            // Simplified parsing
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] cols = line.split(",");
                if (cols.length >= 8) {
                    PurchaseBillItem item = new PurchaseBillItem();
                    item.setSourceProductName(cols[0]);
                    item.setBatchNo(cols[1]);
                    item.setExpiryDate(LocalDate.parse(cols[2], DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    item.setQuantity(Integer.parseInt(cols[3]));
                    item.setCostPrice(new BigDecimal(cols[4]));
                    item.setMrp(new BigDecimal(cols[5]));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
