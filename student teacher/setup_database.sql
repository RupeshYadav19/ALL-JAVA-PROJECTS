-- ============================================================
-- Student Academic Eligibility Management System
-- Database Setup Script
-- Run this in MySQL before launching the application
-- ============================================================

CREATE DATABASE IF NOT EXISTS academic_system;
USE academic_system;

-- Users table (login credentials)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students table (personal + academic data)
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    full_name VARCHAR(100) NOT NULL,
    parent_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(15),
    date_of_birth DATE,
    address TEXT,
    sgpa DOUBLE,
    credits INT,
    attendance_percent DOUBLE,
    conduct_violation BOOLEAN DEFAULT FALSE,
    conduct_type VARCHAR(50),
    stream VARCHAR(50),
    year INT,
    semester INT,
    cgpa_first_year DOUBLE,
    sgpa_third_sem DOUBLE,
    sem_status VARCHAR(20) DEFAULT 'Completed',
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Eligibility results table (audit trail)
CREATE TABLE IF NOT EXISTS eligibility_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    test_case_id VARCHAR(20),
    result_status VARCHAR(50),
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id)
);

SELECT 'Database setup complete!' AS Status;
