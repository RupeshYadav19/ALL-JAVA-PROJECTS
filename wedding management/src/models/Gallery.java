package models;

public class Gallery {
    private int galleryId;
    private String title;
    private String type;
    private String imagePath;
    private String tags;
    private int uploadedBy;
    private boolean isFeatured;

    public Gallery() {}

    public int getGalleryId()            { return galleryId; }
    public void setGalleryId(int v)      { this.galleryId = v; }
    public String getTitle()             { return title; }
    public void setTitle(String v)       { this.title = v; }
    public String getType()              { return type; }
    public void setType(String v)        { this.type = v; }
    public String getImagePath()         { return imagePath; }
    public void setImagePath(String v)   { this.imagePath = v; }
    public String getTags()              { return tags; }
    public void setTags(String v)        { this.tags = v; }
    public int getUploadedBy()           { return uploadedBy; }
    public void setUploadedBy(int v)     { this.uploadedBy = v; }
    public boolean isFeatured()          { return isFeatured; }
    public void setFeatured(boolean v)   { this.isFeatured = v; }
}
