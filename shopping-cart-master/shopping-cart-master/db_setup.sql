-- Database Setup for Standalone Shopping Cart
CREATE DATABASE IF NOT EXISTS shopping_cart;
USE shopping_cart;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(60) PRIMARY KEY,
    name VARCHAR(30),
    mobile BIGINT,
    address VARCHAR(250),
    pincode INT,
    password VARCHAR(20)
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    pid VARCHAR(45) PRIMARY KEY,
    pname VARCHAR(100),
    ptype VARCHAR(20),
    pinfo VARCHAR(350),
    pprice DECIMAL(12,2),
    pquantity INT,
    image_name VARCHAR(100) -- Storing image name instead of blob for simplicity in standalone
);

-- Cart table
CREATE TABLE IF NOT EXISTS cart (
    username VARCHAR(60),
    prodid VARCHAR(45),
    quantity INT,
    PRIMARY KEY (username, prodid),
    FOREIGN KEY (username) REFERENCES users(email),
    FOREIGN KEY (prodid) REFERENCES products(pid)
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    orderid VARCHAR(45),
    prodid VARCHAR(45),
    quantity INT,
    amount DECIMAL(10,2),
    shipped INT DEFAULT 0,
    PRIMARY KEY (orderid, prodid),
    FOREIGN KEY (prodid) REFERENCES products(pid)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transid VARCHAR(45) PRIMARY KEY,
    username VARCHAR(60),
    time DATETIME,
    amount DECIMAL(10,2),
    FOREIGN KEY (username) REFERENCES users(email)
);

-- Sample Data
INSERT IGNORE INTO users VALUES ('admin@gmail.com', 'Admin', 9876543210, 'Admin Home', 123456, 'admin');
INSERT IGNORE INTO users VALUES ('user@gmail.com', 'User', 1234567890, 'User Home', 654321, 'user');

INSERT IGNORE INTO products VALUES ('P001', 'Apple iPhone 13', 'mobile', 'Advanced dual-camera system', 125999.00, 100, 'iphone13.png');
INSERT IGNORE INTO products VALUES ('P002', 'MacBook Pro', 'laptop', 'M1 chip, 8GB RAM', 122900.00, 50, 'macbook.png');
INSERT IGNORE INTO products VALUES ('P003', 'Sony WH-1000XM4', 'audio', 'Noise canceling headphones', 24990.00, 75, 'sonyheadphones.png');
INSERT IGNORE INTO products VALUES ('P004', 'Samsung S21 Ultra', 'mobile', 'Phantom Black, 128GB', 105999.00, 60, 's21.png');
INSERT IGNORE INTO products VALUES ('P005', 'Dell XPS 13', 'laptop', '11th Gen i7, 16GB RAM', 115000.00, 40, 'dellxps.png');
INSERT IGNORE INTO products VALUES ('P006', 'iPad Air', 'tablet', '64GB, Space Gray', 54900.00, 80, 'ipadair.png');
INSERT IGNORE INTO products VALUES ('P007', 'Apple Watch Series 7', 'wearable', 'Starlight Aluminum Case', 41900.00, 120, 'applewatch.png');
INSERT IGNORE INTO products VALUES ('P008', 'Sony PlayStation 5', 'console', 'Digital Edition, 825GB', 39990.00, 30, 'ps5.png');
INSERT IGNORE INTO products VALUES ('P009', 'Nikon Z6 II', 'camera', 'Mirrorless Camera Body', 160000.00, 15, 'nikon.png');
INSERT IGNORE INTO products VALUES ('P010', 'Bose SoundLink', 'audio', 'Bluetooth Speaker', 18000.00, 100, 'bose.png');
INSERT IGNORE INTO products VALUES ('P011', 'Logitech G502', 'accessory', 'Gaming Mouse', 4500.00, 200, 'mouse.png');
INSERT IGNORE INTO products VALUES ('P012', 'Samsung 4K TV', 'electronics', '55 inch Crystal 4K', 46000.00, 25, 'samsungtv.png');
INSERT IGNORE INTO products VALUES ('P013', 'OnePlus 9 Pro', 'mobile', 'Morning Mist, 12GB RAM', 64999.00, 45, 'oneplus9.png');
INSERT IGNORE INTO products VALUES ('P014', 'HP Spectre x360', 'laptop', 'Intel Core i7, 16GB', 135000.00, 20, 'hpspectre.png');
INSERT IGNORE INTO products VALUES ('P015', 'JBL Flip 6', 'audio', 'Portable Bluetooth Speaker', 11999.00, 150, 'jblflip.png');
INSERT IGNORE INTO products VALUES ('P016', 'Nintendo Switch', 'console', 'OLED Model, Neon Blue/Red', 32000.00, 40, 'switch.png');
INSERT IGNORE INTO products VALUES ('P017', 'Canon EOS R6', 'camera', 'Mirrorless Full Frame', 215000.00, 10, 'canonr6.png');
INSERT IGNORE INTO products VALUES ('P018', 'Apple AirPods Pro', 'audio', 'MagSafe Charging Case', 24900.00, 200, 'airpods.png');
INSERT IGNORE INTO products VALUES ('P019', 'Razer BlackWidow', 'accessory', 'Mechanical Keyboard', 12000.00, 60, 'keyboard.png');
INSERT IGNORE INTO products VALUES ('P020', 'Samsung Galaxy Tab S7', 'tablet', 'Mystic Black, 128GB', 63000.00, 35, 'samtab.png');
INSERT IGNORE INTO products VALUES ('P021', 'Microsoft Surface Pro 8', 'laptop', 'Intel i5, 8GB RAM', 115000.00, 15, 'surface.png');
INSERT IGNORE INTO products VALUES ('P022', 'Google Pixel 6', 'mobile', 'Stormy Black, 128GB', 59999.00, 50, 'pixel6.png');
INSERT IGNORE INTO products VALUES ('P023', 'GoPro HERO 10', 'camera', 'Action Camera', 45000.00, 30, 'gopro.png');
INSERT IGNORE INTO products VALUES ('P024', 'Fitbit Charge 5', 'wearable', 'Health & Fitness Tracker', 14999.00, 90, 'fitbit.png');
INSERT IGNORE INTO products VALUES ('P025', 'Kindle Paperwhite', 'tablet', '8GB, 6.8 inch Display', 13999.00, 70, 'kindle.png');
INSERT IGNORE INTO products VALUES ('P026', 'Corsair Void RGB', 'audio', 'Gaming Headset', 8500.00, 45, 'corsair.png');
INSERT IGNORE INTO products VALUES ('P027', 'WD Black 1TB SSD', 'accessory', 'NVMe Internal SSD', 12000.00, 100, 'ssd.png');
