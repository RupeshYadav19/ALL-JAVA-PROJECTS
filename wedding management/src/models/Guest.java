package models;

public class Guest {
    private int guestId;
    private int bookingId;
    private int userId;
    private String guestName;
    private String phone;
    private String email;
    private String relation;
    private String side;
    private String rsvpStatus;
    private String mealPreference;
    private int tableNo;

    public Guest() {}

    public int getGuestId()              { return guestId; }
    public void setGuestId(int v)        { this.guestId = v; }
    public int getBookingId()            { return bookingId; }
    public void setBookingId(int v)      { this.bookingId = v; }
    public int getUserId()               { return userId; }
    public void setUserId(int v)         { this.userId = v; }
    public String getGuestName()         { return guestName; }
    public void setGuestName(String v)   { this.guestName = v; }
    public String getPhone()             { return phone; }
    public void setPhone(String v)       { this.phone = v; }
    public String getEmail()             { return email; }
    public void setEmail(String v)       { this.email = v; }
    public String getRelation()          { return relation; }
    public void setRelation(String v)    { this.relation = v; }
    public String getSide()              { return side; }
    public void setSide(String v)        { this.side = v; }
    public String getRsvpStatus()        { return rsvpStatus; }
    public void setRsvpStatus(String v)  { this.rsvpStatus = v; }
    public String getStatus()            { return rsvpStatus; }
    public void setStatus(String v)      { this.rsvpStatus = v; }
    public String getMealPreference()    { return mealPreference; }
    public void setMealPreference(String v){ this.mealPreference = v; }
    public int getTableNo()              { return tableNo; }
    public void setTableNo(int v)        { this.tableNo = v; }
}
