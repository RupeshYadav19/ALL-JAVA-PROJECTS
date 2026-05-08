package models;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private int userId;
    private int vendorId;
    private int bookingId;
    private int rating;
    private String reviewText;
    private String photosPath;
    private Timestamp createdAt;
    private boolean isApproved;
    private String reviewerName; // joined

    public Review() {}

    public int getReviewId()             { return reviewId; }
    public void setReviewId(int v)       { this.reviewId = v; }
    public int getUserId()               { return userId; }
    public void setUserId(int v)         { this.userId = v; }
    public int getVendorId()             { return vendorId; }
    public void setVendorId(int v)       { this.vendorId = v; }
    public int getBookingId()            { return bookingId; }
    public void setBookingId(int v)      { this.bookingId = v; }
    public int getRating()               { return rating; }
    public void setRating(int v)         { this.rating = v; }
    public String getReviewText()        { return reviewText; }
    public void setReviewText(String v)  { this.reviewText = v; }
    public String getPhotosPath()        { return photosPath; }
    public void setPhotosPath(String v)  { this.photosPath = v; }
    public Timestamp getCreatedAt()      { return createdAt; }
    public void setCreatedAt(Timestamp v){ this.createdAt = v; }
    public boolean isApproved()          { return isApproved; }
    public void setApproved(boolean v)   { this.isApproved = v; }
    public String getReviewerName()      { return reviewerName; }
    public void setReviewerName(String v){ this.reviewerName = v; }
}
