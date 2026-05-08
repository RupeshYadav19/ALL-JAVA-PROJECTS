package models;

public class Budget {
    private int budgetId;
    private int userId;
    private String category;
    private String itemName;
    private double estimatedAmount;
    private double actualAmount;
    private double paidAmount;
    private int vendorId;
    private String notes;
    private String vendorName; // joined

    public Budget() {}

    public int getBudgetId()             { return budgetId; }
    public void setBudgetId(int v)       { this.budgetId = v; }
    public int getUserId()               { return userId; }
    public void setUserId(int v)         { this.userId = v; }
    public String getCategory()          { return category; }
    public void setCategory(String v)    { this.category = v; }
    public String getItemName()          { return itemName; }
    public void setItemName(String v)    { this.itemName = v; }
    public double getEstimatedAmount()   { return estimatedAmount; }
    public void setEstimatedAmount(double v){ this.estimatedAmount = v; }
    public double getActualAmount()      { return actualAmount; }
    public void setActualAmount(double v){ this.actualAmount = v; }
    public double getPaidAmount()        { return paidAmount; }
    public void setPaidAmount(double v)  { this.paidAmount = v; }
    public int getVendorId()             { return vendorId; }
    public void setVendorId(int v)       { this.vendorId = v; }
    public String getNotes()             { return notes; }
    public void setNotes(String v)       { this.notes = v; }
    public String getVendorName()        { return vendorName; }
    public void setVendorName(String v)  { this.vendorName = v; }
    public double getBalance()           { return estimatedAmount - actualAmount; }
}
