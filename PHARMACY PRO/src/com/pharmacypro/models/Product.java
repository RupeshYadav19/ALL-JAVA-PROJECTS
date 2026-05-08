package com.pharmacypro.models;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private String manufacturer;
    private String composition;
    private String hsnCode;
    private BigDecimal defaultMrp;
    private BigDecimal gstPercent;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private int packSize;
    private boolean isScheduleH;
    private java.time.LocalDate expiryDate;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getComposition() { return composition; }
    public void setComposition(String composition) { this.composition = composition; }

    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }

    public BigDecimal getDefaultMrp() { return defaultMrp; }
    public void setDefaultMrp(BigDecimal defaultMrp) { this.defaultMrp = defaultMrp; }

    public BigDecimal getGstPercent() { return gstPercent; }
    public void setGstPercent(BigDecimal gstPercent) { this.gstPercent = gstPercent; }

    public BigDecimal getCgst() { return cgst; }
    public void setCgst(BigDecimal cgst) { this.cgst = cgst; }

    public BigDecimal getSgst() { return sgst; }
    public void setSgst(BigDecimal sgst) { this.sgst = sgst; }

    public int getPackSize() { return packSize; }
    public void setPackSize(int packSize) { this.packSize = packSize; }

    public boolean isScheduleH() { return isScheduleH; }
    public void setScheduleH(boolean scheduleH) { this.isScheduleH = scheduleH; }

    public java.time.LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(java.time.LocalDate expiryDate) { this.expiryDate = expiryDate; }
}
