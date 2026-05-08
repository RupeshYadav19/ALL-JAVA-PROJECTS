package models;

import java.sql.Date;
import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private int userId;
    private int eventId;
    private Timestamp bookingDate;
    private Date eventDate;
    private int guestCount;
    private String ceremonyTypes;
    private double totalPrice;
    private double advancePaid;
    private String specialRequests;
    private String status;
    private String paymentStatus;
    private String rejectionReason;
    // Joined fields
    private String userName;
    private String eventName;
    private String eventType;

    public Booking() {}

    public int getBookingId()               { return bookingId; }
    public void setBookingId(int v)         { this.bookingId = v; }
    public int getUserId()                  { return userId; }
    public void setUserId(int v)            { this.userId = v; }
    public int getEventId()                 { return eventId; }
    public void setEventId(int v)           { this.eventId = v; }
    public Timestamp getBookingDate()       { return bookingDate; }
    public void setBookingDate(Timestamp v) { this.bookingDate = v; }
    public Date getEventDate()              { return eventDate; }
    public void setEventDate(Date v)        { this.eventDate = v; }
    public int getGuestCount()              { return guestCount; }
    public void setGuestCount(int v)        { this.guestCount = v; }
    public String getCeremonyTypes()        { return ceremonyTypes; }
    public void setCeremonyTypes(String v)  { this.ceremonyTypes = v; }
    public double getTotalPrice()           { return totalPrice; }
    public void setTotalPrice(double v)     { this.totalPrice = v; }
    public double getAdvancePaid()          { return advancePaid; }
    public void setAdvancePaid(double v)    { this.advancePaid = v; }
    public String getSpecialRequests()      { return specialRequests; }
    public void setSpecialRequests(String v){ this.specialRequests = v; }
    public String getStatus()               { return status; }
    public void setStatus(String v)         { this.status = v; }
    public String getPaymentStatus()        { return paymentStatus; }
    public void setPaymentStatus(String v)  { this.paymentStatus = v; }
    public String getRejectionReason()      { return rejectionReason; }
    public void setRejectionReason(String v){ this.rejectionReason = v; }
    public String getUserName()             { return userName; }
    public void setUserName(String v)       { this.userName = v; }
    public String getEventName()            { return eventName; }
    public void setEventName(String v)      { this.eventName = v; }
    public String getEventType()            { return eventType; }
    public void setEventType(String v)      { this.eventType = v; }
}
