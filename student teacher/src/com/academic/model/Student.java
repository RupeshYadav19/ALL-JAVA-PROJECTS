package com.academic.model;

public class Student {
    private int id;
    private int userId;
    private String fullName;
    private String parentName;
    private String email;
    private String phone;
    private String dateOfBirth;
    private String address;
    private double sgpa;
    private int credits;
    private double attendancePercent;
    private boolean conductViolation;
    private String conductType;
    private String stream;
    private int year;
    private int semester;
    private double cgpaFirstYear;
    private double sgpaThirdSem;
    private String semStatus; // "Ongoing" or "Completed"

    public Student() {
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getParentName() {
        return parentName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public double getSgpa() {
        return sgpa;
    }

    public int getCredits() {
        return credits;
    }

    public double getAttendancePercent() {
        return attendancePercent;
    }

    public boolean isConductViolation() {
        return conductViolation;
    }

    public String getConductType() {
        return conductType;
    }

    public String getStream() {
        return stream;
    }

    public int getYear() {
        return year;
    }

    public int getSemester() {
        return semester;
    }

    public double getCgpaFirstYear() {
        return cgpaFirstYear;
    }

    public double getSgpaThirdSem() {
        return sgpaThirdSem;
    }

    public String getSemStatus() {
        return semStatus;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSgpa(double sgpa) {
        this.sgpa = sgpa;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setAttendancePercent(double attendancePercent) {
        this.attendancePercent = attendancePercent;
    }

    public void setConductViolation(boolean conductViolation) {
        this.conductViolation = conductViolation;
    }

    public void setConductType(String conductType) {
        this.conductType = conductType;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public void setCgpaFirstYear(double cgpaFirstYear) {
        this.cgpaFirstYear = cgpaFirstYear;
    }

    public void setSgpaThirdSem(double sgpaThirdSem) {
        this.sgpaThirdSem = sgpaThirdSem;
    }

    public void setSemStatus(String semStatus) {
        this.semStatus = semStatus;
    }
}
