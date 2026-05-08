-- MySQL Workbench Script for Hotel Management System
-- Database: hotelMS

CREATE DATABASE IF NOT EXISTS hotelMS;
USE hotelMS;

-- Table for Login (Regular Users/Dashboard)
CREATE TABLE IF NOT EXISTS login (
    username VARCHAR(25),
    password VARCHAR(25)
);

-- Table for Login2 (Admin/Specific Login)
CREATE TABLE IF NOT EXISTS login2 (
    user_name VARCHAR(25),
    password VARCHAR(25)
);

-- Table for Rooms
CREATE TABLE IF NOT EXISTS room (
    roomnumber VARCHAR(10) PRIMARY KEY,
    availability VARCHAR(20),
    cleaning_status VARCHAR(20),
    price VARCHAR(10),
    bed_type VARCHAR(20)
);

-- Table for Customers
CREATE TABLE IF NOT EXISTS customer (
    id VARCHAR(20),
    number VARCHAR(30),
    name VARCHAR(30),
    gender VARCHAR(15),
    country VARCHAR(20),
    room VARCHAR(10),
    checkintime VARCHAR(80),
    deposit VARCHAR(20)
);

-- Table for Employees
CREATE TABLE IF NOT EXISTS employee (
    name VARCHAR(25),
    age VARCHAR(10),
    gender VARCHAR(15),
    job VARCHAR(30),
    salary VARCHAR(15),
    phone VARCHAR(15),
    gmail VARCHAR(40),
    aadhar VARCHAR(20)
);

-- Table for Drivers
CREATE TABLE IF NOT EXISTS driver (
    name VARCHAR(25),
    age VARCHAR(10),
    gender VARCHAR(15),
    company VARCHAR(20),
    carname VARCHAR(20),
    available VARCHAR(20),
    loacation VARCHAR(40)
);

-- Table for Departments
CREATE TABLE IF NOT EXISTS department (
    department VARCHAR(30),
    budget VARCHAR(20)
);

-- Insert Sample Data
INSERT INTO login VALUES ('admin', '12345');
INSERT INTO login2 VALUES ('admin', '12345');

INSERT INTO room VALUES ('101', 'Available', 'Cleaned', '1500', 'Single Bed');
INSERT INTO room VALUES ('102', 'Available', 'Cleaned', '2500', 'Double Bed');

INSERT INTO department VALUES ('House Keeping', '50000');
INSERT INTO department VALUES ('Kitchen', '100000');
INSERT INTO department VALUES ('Reception', '30000');
