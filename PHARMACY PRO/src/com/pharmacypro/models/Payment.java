package com.pharmacypro.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {
    private int id;
    private int partyId; // Represents either patient_id or distributor_id based on the table
    private int billId; // Represents either bill_id or purchase_id based on the table
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentType;
    private String transactionNo;
    private boolean isReceived; // true for payments_received, false for payments_made

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPartyId() { return partyId; }
    public void setPartyId(int partyId) { this.partyId = partyId; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }

    public boolean isReceived() { return isReceived; }
    public void setReceived(boolean received) { this.isReceived = received; }
}
