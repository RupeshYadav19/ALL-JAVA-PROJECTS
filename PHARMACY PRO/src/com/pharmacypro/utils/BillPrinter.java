package com.pharmacypro.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import com.pharmacypro.models.SalesBill;
import com.pharmacypro.models.SalesBillItem;
import java.util.List;

public class BillPrinter implements Printable {
    private SalesBill bill;
    private List<SalesBillItem> items;

    public void printBill(SalesBill bill, List<SalesBillItem> items) {
        this.bill = bill;
        this.items = items;
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        
        int y = 50;
        g2d.setFont(AppFonts.HEADING);
        g2d.drawString("PHARMACY PRO", 100, y);
        y += 20;
        
        g2d.setFont(AppFonts.BODY);
        g2d.drawString("Invoice: " + (bill != null ? bill.getInvoiceNo() : ""), 50, y);
        
        // Output specific layout goes here... (stubbed for length)
        y += 20;
        if (items != null) {
            for (SalesBillItem item : items) {
                g2d.drawString("Item: PID " + item.getProductId() + " Qty: " + item.getQuantity(), 50, y);
                y += 20;
            }
        }
        return PAGE_EXISTS;
    }
}
