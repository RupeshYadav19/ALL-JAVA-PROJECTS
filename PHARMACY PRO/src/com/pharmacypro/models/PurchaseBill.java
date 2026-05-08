package com.pharmacypro.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseBill {
    private int id;
    private String invoiceNo;
    private int distributorId;
    private LocalDate billDate;
    private String billingMode;
    private BigDecimal extraDiscount;
    private BigDecimal cdPercent;
    private BigDecimal creditNoteAmount;
    private BigDecimal mrpValue;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private boolean tcsApplied;
    private String payStatus;
    private BigDecimal pendingAmount;
    private int dueDays;
    private String remarks;
    private String purchaseOrderId;
    private String createdBy;
    private LocalDateTime createdAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

    public int getDistributorId() { return distributorId; }
    public void setDistributorId(int distributorId) { this.distributorId = distributorId; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public String getBillingMode() { return billingMode; }
    public void setBillingMode(String billingMode) { this.billingMode = billingMode; }

    public BigDecimal getExtraDiscount() { return extraDiscount; }
    public void setExtraDiscount(BigDecimal extraDiscount) { this.extraDiscount = extraDiscount; }

    public BigDecimal getCdPercent() { return cdPercent; }
    public void setCdPercent(BigDecimal cdPercent) { this.cdPercent = cdPercent; }

    public BigDecimal getCreditNoteAmount() { return creditNoteAmount; }
    public void setCreditNoteAmount(BigDecimal creditNoteAmount) { this.creditNoteAmount = creditNoteAmount; }

    public BigDecimal getMrpValue() { return mrpValue; }
    public void setMrpValue(BigDecimal mrpValue) { this.mrpValue = mrpValue; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public boolean isTcsApplied() { return tcsApplied; }
    public void setTcsApplied(boolean tcsApplied) { this.tcsApplied = tcsApplied; }

    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }

    public BigDecimal getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(BigDecimal pendingAmount) { this.pendingAmount = pendingAmount; }

    public int getDueDays() { return dueDays; }
    public void setDueDays(int dueDays) { this.dueDays = dueDays; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(String purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
