CREATE DATABASE IF NOT EXISTS banking_app;
USE banking_app;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    mobile VARCHAR(15) UNIQUE,
    upi_id VARCHAR(50) UNIQUE,
    balance DECIMAL(15, 2) DEFAULT 1000.00,
    max_send DECIMAL(15, 2) DEFAULT 5000.00,
    max_receive DECIMAL(15, 2) DEFAULT 10000.00,
    is_frozen TINYINT(1) DEFAULT 0 -- 0: Active, 1: Frozen
);

-- Seed data with updated columns
INSERT INTO users (username, password, full_name, mobile, upi_id, balance, max_send, max_receive, is_frozen) 
VALUES 
('admin', '12345', 'Administrator', '1234567890', 'admin@upi', 100000.00, 1000000.00, 1000000.00, 0),
('rupesh', 'pass123', 'Rupesh', '9876543210', 'rupesh@okaxis', 20000.00, 5000.00, 10000.00, 0),
('testuser', 'test123', 'Test User', '9988776655', 'test@upi', 1000.00, 5000.00, 10000.00, 0)
ON DUPLICATE KEY UPDATE 
    max_send = VALUES(max_send), 
    max_receive = VALUES(max_receive),
    is_frozen = VALUES(is_frozen);

CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    payment_type VARCHAR(50), -- Increased from 20 to 50
    recipient VARCHAR(100),
    amount DECIMAL(15, 2),
    status VARCHAR(20), -- Success, Failed
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
