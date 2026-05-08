package models;

public class Vendor {
    private int vendorId;
    private int userId;
    private String businessName;
    private String category;
    private String city;
    private String locality;
    private String description;
    private double startingPrice;
    private double rating;
    private int reviewCount;
    private String portfolioPath;
    private boolean isVerified;
    private boolean isFeatured;
    private boolean awardWinner;
    private String specialties;
    // From join with users
    private String ownerName;
    private String email;
    private String phone;

    public Vendor() {}

    // Getters & Setters
    public int getVendorId()            { return vendorId; }
    public void setVendorId(int v)      { this.vendorId = v; }
    public int getUserId()              { return userId; }
    public void setUserId(int v)        { this.userId = v; }
    public String getBusinessName()     { return businessName; }
    public void setBusinessName(String v){ this.businessName = v; }
    public String getCategory()         { return category; }
    public void setCategory(String v)   { this.category = v; }
    public String getCity()             { return city; }
    public void setCity(String v)       { this.city = v; }
    public String getLocality()         { return locality; }
    public void setLocality(String v)   { this.locality = v; }
    public String getDescription()      { return description; }
    public void setDescription(String v){ this.description = v; }
    public double getStartingPrice()    { return startingPrice; }
    public void setStartingPrice(double v){ this.startingPrice = v; }
    public double getRating()           { return rating; }
    public void setRating(double v)     { this.rating = v; }
    public int getReviewCount()         { return reviewCount; }
    public void setReviewCount(int v)   { this.reviewCount = v; }
    public String getPortfolioPath()    { return portfolioPath; }
    public void setPortfolioPath(String v){ this.portfolioPath = v; }
    public boolean isVerified()         { return isVerified; }
    public void setVerified(boolean v)  { this.isVerified = v; }
    public boolean isFeatured()         { return isFeatured; }
    public void setFeatured(boolean v)  { this.isFeatured = v; }
    public boolean isAwardWinner()      { return awardWinner; }
    public void setAwardWinner(boolean v){ this.awardWinner = v; }
    public String getSpecialties()      { return specialties; }
    public void setSpecialties(String v){ this.specialties = v; }
    public String getOwnerName()        { return ownerName; }
    public void setOwnerName(String v)  { this.ownerName = v; }
    public String getEmail()            { return email; }
    public void setEmail(String v)      { this.email = v; }
    public String getPhone()            { return phone; }
    public void setPhone(String v)      { this.phone = v; }
    public String toString()            { return businessName + " (" + category + ", " + city + ")"; }
}
