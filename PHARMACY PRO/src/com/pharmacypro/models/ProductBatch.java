package com.pharmacypro.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductBatch {
    private int id;
    private int productId;
    private String batchNo;
    private LocalDate expiryDate;
    private int quantity;
    private int looseQty;
    private BigDecimal costPrice;
    private BigDecimal mrp;
    private BigDecimal marginPercent;
    private boolean isAuto;
    private int distributorId;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getLooseQty() { return looseQty; }
    public void setLooseQty(int looseQty) { this.looseQty = looseQty; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getMrp() { return mrp; }
    public void setMrp(BigDecimal mrp) { this.mrp = mrp; }

    public BigDecimal getMarginPercent() { return marginPercent; }
    public void setMarginPercent(BigDecimal marginPercent) { this.marginPercent = marginPercent; }

    public boolean isAuto() { return isAuto; }
    public void setAuto(boolean isAuto) { this.isAuto = isAuto; }

    public int getDistributorId() { return distributorId; }
    public void setDistributorId(int distributorId) { this.distributorId = distributorId; }
}
