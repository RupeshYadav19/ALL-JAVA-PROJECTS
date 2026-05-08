package com.pharmacypro.models;

import java.math.BigDecimal;

public class SalesBillItem {
    private int id;
    private int billId;
    private int productId;
    private int batchId;
    private int quantity;
    private int looseQty;
    private BigDecimal rate;
    private BigDecimal mrp;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal amount;
    private BigDecimal marginPercent;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getLooseQty() { return looseQty; }
    public void setLooseQty(int looseQty) { this.looseQty = looseQty; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public BigDecimal getMrp() { return mrp; }
    public void setMrp(BigDecimal mrp) { this.mrp = mrp; }

    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getMarginPercent() { return marginPercent; }
    public void setMarginPercent(BigDecimal marginPercent) { this.marginPercent = marginPercent; }
}
