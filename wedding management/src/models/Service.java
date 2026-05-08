package models;

public class Service {
    private int serviceId;
    private int vendorId;
    private String serviceName;
    private String category;
    private String description;
    private double price;
    private String priceType;
    private boolean isAvailable;
    private String imagesPath;
    private String vendorName; // joined

    public Service() {}

    public int getServiceId()            { return serviceId; }
    public void setServiceId(int v)      { this.serviceId = v; }
    public int getVendorId()             { return vendorId; }
    public void setVendorId(int v)       { this.vendorId = v; }
    public String getServiceName()       { return serviceName; }
    public void setServiceName(String v) { this.serviceName = v; }
    public String getCategory()          { return category; }
    public void setCategory(String v)    { this.category = v; }
    public String getDescription()       { return description; }
    public void setDescription(String v) { this.description = v; }
    public double getPrice()             { return price; }
    public void setPrice(double v)       { this.price = v; }
    public String getPriceType()         { return priceType; }
    public void setPriceType(String v)   { this.priceType = v; }
    public boolean isAvailable()         { return isAvailable; }
    public void setAvailable(boolean v)  { this.isAvailable = v; }
    public String getImagesPath()        { return imagesPath; }
    public void setImagesPath(String v)  { this.imagesPath = v; }
    public String getVendorName()        { return vendorName; }
    public void setVendorName(String v)  { this.vendorName = v; }
    public String toString()             { return serviceName + " (₹" + price + " / " + priceType + ")"; }
}
