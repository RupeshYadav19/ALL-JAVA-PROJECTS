package com.pharmacypro.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreditNote {
    private int id;
    private int distributorId;
    private String cnNo;
    private BigDecimal amount;
    private LocalDate cnDate;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDistributorId() { return distributorId; }
    public void setDistributorId(int distributorId) { this.distributorId = distributorId; }

    public String getCnNo() { return cnNo; }
    public void setCnNo(String cnNo) { this.cnNo = cnNo; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getCnDate() { return cnDate; }
    public void setCnDate(LocalDate cnDate) { this.cnDate = cnDate; }
}
