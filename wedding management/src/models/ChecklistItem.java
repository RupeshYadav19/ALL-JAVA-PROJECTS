package models;

import java.sql.Date;

public class ChecklistItem {
    private int itemId;
    private int userId;
    private String category;
    private String taskName;
    private Date dueDate;
    private boolean isDone;
    private String priority;
    private String notes;

    public ChecklistItem() {}

    public ChecklistItem(int userId, String category, String taskName, String priority) {
        this.userId = userId; this.category = category;
        this.taskName = taskName; this.priority = priority;
    }

    public int getItemId()               { return itemId; }
    public void setItemId(int v)         { this.itemId = v; }
    public int getUserId()               { return userId; }
    public void setUserId(int v)         { this.userId = v; }
    public String getCategory()          { return category; }
    public void setCategory(String v)    { this.category = v; }
    public String getTaskName()          { return taskName; }
    public void setTaskName(String v)    { this.taskName = v; }
    public Date getDueDate()             { return dueDate; }
    public void setDueDate(Date v)       { this.dueDate = v; }
    public boolean isDone()              { return isDone; }
    public void setDone(boolean v)       { this.isDone = v; }
    public String getPriority()          { return priority; }
    public void setPriority(String v)    { this.priority = v; }
    public String getNotes()             { return notes; }
    public void setNotes(String v)       { this.notes = v; }
}
