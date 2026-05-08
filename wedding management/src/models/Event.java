package models;

import java.sql.Date;

public class Event {
    private int eventId;
    private String eventName;
    private String eventType;
    private String description;
    private String venue;
    private String city;
    private Date date;
    private String time;
    private int capacity;
    private double pricePerHead;
    private double totalPrice;
    private String status;
    private String imagePath;
    private int createdBy;

    public Event() {}

    public int getEventId()             { return eventId; }
    public void setEventId(int v)       { this.eventId = v; }
    public String getEventName()        { return eventName; }
    public void setEventName(String v)  { this.eventName = v; }
    public String getEventType()        { return eventType; }
    public void setEventType(String v)  { this.eventType = v; }
    public String getDescription()      { return description; }
    public void setDescription(String v){ this.description = v; }
    public String getVenue()            { return venue; }
    public void setVenue(String v)      { this.venue = v; }
    public String getCity()             { return city; }
    public void setCity(String v)       { this.city = v; }
    public Date getDate()               { return date; }
    public void setDate(Date v)         { this.date = v; }
    public String getTime()             { return time; }
    public void setTime(String v)       { this.time = v; }
    public int getCapacity()            { return capacity; }
    public void setCapacity(int v)      { this.capacity = v; }
    public double getPricePerHead()     { return pricePerHead; }
    public void setPricePerHead(double v){ this.pricePerHead = v; }
    public double getTotalPrice()       { return totalPrice; }
    public void setTotalPrice(double v) { this.totalPrice = v; }
    public String getStatus()           { return status; }
    public void setStatus(String v)     { this.status = v; }
    public String getImagePath()        { return imagePath; }
    public void setImagePath(String v)  { this.imagePath = v; }
    public int getCreatedBy()           { return createdBy; }
    public void setCreatedBy(int v)     { this.createdBy = v; }
    public String toString()            { return eventName + " — " + eventType + " @ " + venue; }
}
