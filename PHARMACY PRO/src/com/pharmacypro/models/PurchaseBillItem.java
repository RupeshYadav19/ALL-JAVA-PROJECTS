package com.pharmacypro.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseBillItem {
    private int id;
    private int purchaseId;
    private int productId;
    private String sourceProductName;
    private String batchNo;
    private LocalDate expiryDate;
    private int quantity;
    private int freeQty;
    private BigDecimal costPrice;
    private BigDecimal mrp;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal netGst;
    private BigDecimal amount;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getSourceProductName() { return sourceProductName; }
    public void setSourceProductName(String sourceProductName) { this.sourceProductName = sourceProductName; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getFreeQty() { return freeQty; }
    public void setFreeQty(int freeQty) { this.freeQty = freeQty; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getMrp() { return mrp; }
    public void setMrp(BigDecimal mrp) { this.mrp = mrp; }

    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getCgst() { return cgst; }
    public void setCgst(BigDecimal cgst) { this.cgst = cgst; }

    public BigDecimal getSgst() { return sgst; }
    public void setSgst(BigDecimal sgst) { this.sgst = sgst; }

    public BigDecimal getNetGst() { return netGst; }
    public void setNetGst(BigDecimal netGst) { this.netGst = netGst; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
