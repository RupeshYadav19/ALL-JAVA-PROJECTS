package com.pharmacypro.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Distributor {
    private int id;
    private String name;
    private String address;
    private String identifier;
    private String mobile;
    private String email;
    private String gstNo;
    private String drugLicense;
    private BigDecimal pendingAmount;
    private int creditCycleDays;
    private BigDecimal lastPaymentAmount;
    private LocalDate lastPaymentDate;
    private BigDecimal lastInvoiceAmount;
    private BigDecimal totalCnAmount;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGstNo() { return gstNo; }
    public void setGstNo(String gstNo) { this.gstNo = gstNo; }

    public String getDrugLicense() { return drugLicense; }
    public void setDrugLicense(String drugLicense) { this.drugLicense = drugLicense; }

    public BigDecimal getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(BigDecimal pendingAmount) { this.pendingAmount = pendingAmount; }

    public int getCreditCycleDays() { return creditCycleDays; }
    public void setCreditCycleDays(int creditCycleDays) { this.creditCycleDays = creditCycleDays; }

    public BigDecimal getLastPaymentAmount() { return lastPaymentAmount; }
    public void setLastPaymentAmount(BigDecimal lastPaymentAmount) { this.lastPaymentAmount = lastPaymentAmount; }

    public LocalDate getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(LocalDate lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }

    public BigDecimal getLastInvoiceAmount() { return lastInvoiceAmount; }
    public void setLastInvoiceAmount(BigDecimal lastInvoiceAmount) { this.lastInvoiceAmount = lastInvoiceAmount; }

    public BigDecimal getTotalCnAmount() { return totalCnAmount; }
    public void setTotalCnAmount(BigDecimal totalCnAmount) { this.totalCnAmount = totalCnAmount; }
}
