package com.restaurant.model;

public class TableSlot {
    private int id;
    private String label; // e.g., "T1", "T2"
    private int capacity;
    private boolean isReserved;

    public TableSlot(int id, String label, int capacity, boolean isReserved) {
        this.id = id;
        this.label = label;
        this.capacity = capacity;
        this.isReserved = isReserved;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }
}
