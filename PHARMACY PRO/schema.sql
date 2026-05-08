CREATE DATABASE IF NOT EXISTS pharmacy_pro;
USE pharmacy_pro;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20) DEFAULT 'admin',
  retailer_code VARCHAR(30)
);
INSERT INTO users VALUES (1,'admin','pharmapro','admin','RC001');

CREATE TABLE patients (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  mobile VARCHAR(15),
  email VARCHAR(100),
  address TEXT,
  identifier VARCHAR(50),
  date_of_birth DATE,
  gender VARCHAR(10),
  outstanding DECIMAL(10,2) DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE doctors (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  mobile VARCHAR(15),
  email VARCHAR(100),
  specialization VARCHAR(100),
  address TEXT
);

CREATE TABLE distributors (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  address TEXT,
  identifier VARCHAR(20),
  mobile VARCHAR(15),
  email VARCHAR(100),
  gst_no VARCHAR(20),
  drug_license VARCHAR(50),
  pending_amount DECIMAL(10,2) DEFAULT 0,
  credit_cycle_days INT DEFAULT 0,
  last_payment_amount DECIMAL(10,2) DEFAULT 0,
  last_payment_date DATE,
  last_invoice_amount DECIMAL(10,2) DEFAULT 0,
  total_cn_amount DECIMAL(10,2) DEFAULT 0
);

CREATE TABLE products (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  manufacturer VARCHAR(100),
  composition TEXT,
  hsn_code VARCHAR(20),
  default_mrp DECIMAL(10,2),
  gst_percent DECIMAL(5,2) DEFAULT 12,
  cgst DECIMAL(5,2) DEFAULT 6,
  sgst DECIMAL(5,2) DEFAULT 6,
  pack_size INT DEFAULT 1,
  is_schedule_h BOOLEAN DEFAULT FALSE
);

CREATE TABLE product_batches (
  id INT PRIMARY KEY AUTO_INCREMENT,
  product_id INT,
  batch_no VARCHAR(50),
  expiry_date DATE,
  quantity INT DEFAULT 0,
  loose_qty INT DEFAULT 0,
  cost_price DECIMAL(10,2),
  mrp DECIMAL(10,2),
  margin_percent DECIMAL(5,2),
  is_auto BOOLEAN DEFAULT FALSE,
  distributor_id INT,
  FOREIGN KEY (product_id) REFERENCES products(id),
  FOREIGN KEY (distributor_id) REFERENCES distributors(id)
);

CREATE TABLE sales_bills (
  id INT PRIMARY KEY AUTO_INCREMENT,
  invoice_no VARCHAR(50) UNIQUE,
  patient_id INT,
  doctor_id INT,
  bill_date DATE NOT NULL,
  payment_mode VARCHAR(20) DEFAULT 'CASH',
  discount DECIMAL(10,2) DEFAULT 0,
  extra_discount DECIMAL(10,2) DEFAULT 0,
  round_off DECIMAL(10,2) DEFAULT 0,
  bill_amount DECIMAL(10,2) DEFAULT 0,
  total_amount DECIMAL(10,2) DEFAULT 0,
  remarks TEXT,
  created_by VARCHAR(50),
  FOREIGN KEY (patient_id) REFERENCES patients(id),
  FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE sales_bill_items (
  id INT PRIMARY KEY AUTO_INCREMENT,
  bill_id INT,
  product_id INT,
  batch_id INT,
  quantity INT,
  loose_qty INT DEFAULT 0,
  rate DECIMAL(10,2),
  mrp DECIMAL(10,2),
  discount_percent DECIMAL(5,2) DEFAULT 0,
  discount_amount DECIMAL(10,2) DEFAULT 0,
  amount DECIMAL(10,2),
  margin_percent DECIMAL(5,2),
  FOREIGN KEY (bill_id) REFERENCES sales_bills(id),
  FOREIGN KEY (product_id) REFERENCES products(id),
  FOREIGN KEY (batch_id) REFERENCES product_batches(id)
);

CREATE TABLE purchase_bills (
  id INT PRIMARY KEY AUTO_INCREMENT,
  invoice_no VARCHAR(50),
  distributor_id INT,
  bill_date DATE NOT NULL,
  billing_mode VARCHAR(20) DEFAULT 'CREDIT',
  extra_discount DECIMAL(10,2) DEFAULT 0,
  cd_percent DECIMAL(5,2) DEFAULT 0,
  credit_note_amount DECIMAL(10,2) DEFAULT 0,
  mrp_value DECIMAL(10,2) DEFAULT 0,
  total_amount DECIMAL(10,2) DEFAULT 0,
  tax_amount DECIMAL(10,2) DEFAULT 0,
  tcs_applied BOOLEAN DEFAULT FALSE,
  pay_status VARCHAR(20) DEFAULT 'UNPAID',
  pending_amount DECIMAL(10,2) DEFAULT 0,
  due_days INT DEFAULT 0,
  remarks TEXT,
  purchase_order_id VARCHAR(50),
  created_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (distributor_id) REFERENCES distributors(id)
);

CREATE TABLE purchase_bill_items (
  id INT PRIMARY KEY AUTO_INCREMENT,
  purchase_id INT,
  product_id INT,
  source_product_name VARCHAR(200),
  batch_no VARCHAR(50),
  expiry_date DATE,
  quantity INT,
  free_qty INT DEFAULT 0,
  cost_price DECIMAL(10,2),
  mrp DECIMAL(10,2),
  discount_percent DECIMAL(5,2) DEFAULT 0,
  discount_amount DECIMAL(10,2) DEFAULT 0,
  cgst DECIMAL(5,2) DEFAULT 6,
  sgst DECIMAL(5,2) DEFAULT 6,
  net_gst DECIMAL(10,2),
  amount DECIMAL(10,2),
  FOREIGN KEY (purchase_id) REFERENCES purchase_bills(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE payments_made (
  id INT PRIMARY KEY AUTO_INCREMENT,
  distributor_id INT,
  purchase_id INT,
  amount DECIMAL(10,2),
  payment_date DATE,
  payment_type VARCHAR(20),
  transaction_no VARCHAR(100),
  FOREIGN KEY (distributor_id) REFERENCES distributors(id)
);

CREATE TABLE payments_received (
  id INT PRIMARY KEY AUTO_INCREMENT,
  patient_id INT,
  bill_id INT,
  amount DECIMAL(10,2),
  payment_date DATE,
  payment_type VARCHAR(20),
  transaction_no VARCHAR(100),
  FOREIGN KEY (patient_id) REFERENCES patients(id)
);

CREATE TABLE credit_notes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  distributor_id INT,
  cn_no VARCHAR(50),
  amount DECIMAL(10,2),
  cn_date DATE,
  FOREIGN KEY (distributor_id) REFERENCES distributors(id)
);

CREATE TABLE sales_returns (
  id INT PRIMARY KEY AUTO_INCREMENT,
  original_bill_id INT,
  invoice_no VARCHAR(50),
  return_date DATE,
  total_amount DECIMAL(10,2),
  reason TEXT,
  FOREIGN KEY (original_bill_id) REFERENCES sales_bills(id)
);

CREATE TABLE purchase_returns (
  id INT PRIMARY KEY AUTO_INCREMENT,
  original_purchase_id INT,
  invoice_no VARCHAR(50),
  return_date DATE,
  total_amount DECIMAL(10,2),
  FOREIGN KEY (original_purchase_id) REFERENCES purchase_bills(id)
);

CREATE TABLE mail_inbox (
  id INT PRIMARY KEY AUTO_INCREMENT,
  received_date TIMESTAMP,
  sender_name VARCHAR(100),
  sender_email VARCHAR(100),
  subject VARCHAR(200),
  attachment_filename VARCHAR(200),
  attachment_path TEXT,
  is_processed BOOLEAN DEFAULT FALSE
);

CREATE TABLE configuration (
  id INT PRIMARY KEY AUTO_INCREMENT,
  config_key VARCHAR(100) UNIQUE,
  config_value TEXT
);

INSERT INTO configuration VALUES
(1,'report_days','7'),
(2,'sms_enabled','true'),
(3,'sms_confirm','false'),
(4,'shift_enabled','false'),
(5,'max_tabs','15'),
(6,'min_barcode_size','4'),
(7,'sku_barcode','true'),
(8,'abdated_mrp','false'),
(9,'strict_medicine_search','false'),
(10,'password_save_sales','false'),
(11,'password_edit_sales','false'),
(12,'password_delete_sales','false'),
(13,'password_save_purchase','false');

-- --- DUMMY DATA ---

INSERT INTO patients (name, mobile, email, address, identifier) VALUES 
('Rahul Kumar', '9876543210', 'rahul@example.com', 'Delhi', 'P001'),
('Priya Singh', '8765432109', 'priya@example.com', 'Mumbai', 'P002'),
('Amit Sharma', '7654321098', 'amit@example.com', 'Bangalore', 'P003'),
('Sneha Gupta', '6543210987', 'sneha@example.com', 'Kolkata', 'P004'),
('Vikram Patel', '5432109876', 'vikram@example.com', 'Ahmedabad', 'P005'),
('Anjali Verma', '9123456780', 'anjali@example.com', 'Lucknow', 'P006'),
('Rohan Mehra', '9234567891', 'rohan@example.com', 'Patna', 'P007'),
('Kavita Reddy', '9345678912', 'kavita@example.com', 'Hyderabad', 'P008'),
('Deepak Jha', '9456789123', 'deepak@example.com', 'Ranchi', 'P009'),
('Suresh Raina', '9567891234', 'suresh@example.com', 'Chennai', 'P010'),
('Manish Pandey', '9678912345', 'manish@example.com', 'Nainital', 'P011'),
('Ishan Kishan', '9789123456', 'ishan@example.com', 'Patna', 'P012');

INSERT INTO products (name, manufacturer, composition, hsn_code, default_mrp, pack_size) VALUES
('Paracetamol 500mg', 'GlaxoSmithKline', 'Paracetamol', '3004', 50.00, 10),
('Amoxicillin 250mg', 'Sun Pharma', 'Amoxicillin', '3004', 120.00, 10),
('Cetirizine 10mg', 'Cipla', 'Cetirizine', '3004', 35.00, 10),
('Pantoprazole 40mg', 'Alkem', 'Pantoprazole', '3004', 80.00, 10),
('Azithromycin 500mg', 'Mankind', 'Azithromycin', '3004', 150.00, 5),
('Cough Syrup 100ml', 'Dabur', 'Ayurvedic', '3004', 95.00, 1),
('Vicks Vaporub', 'P&G', 'Menthol', '3004', 65.00, 1),
('Crocin Advance', 'GSK', 'Paracetamol', '3004', 42.00, 15),
('Telma 40', 'Glenmark', 'Telmisartan', '3004', 180.00, 15),
('Shelcal 500', 'Torrent', 'Calcium', '3004', 110.00, 15),
('Revital H', 'Sun Pharma', 'Multivitamin', '3004', 320.00, 30),
('Digene Tablet', 'Abbott', 'Antacid', '3004', 25.00, 10),
('Zifi 200', 'FDC', 'Cefixime', '3004', 140.00, 10);

INSERT INTO doctors (name, mobile, specialization, address) VALUES
('Dr. Ramesh Babu', '9988776655', 'General Physician', 'Clinic 1'),
('Dr. Sunita Rao', '8877665544', 'Pediatrician', 'Clinic 2'),
('Dr. VK Gupta', '7766554433', 'Orthopedic', 'Main Road'),
('Dr. S. K. Singh', '6655443322', 'Cardiologist', 'Sector 5');

INSERT INTO distributors (name, mobile, address, gst_no) VALUES
('Apollo Distributors', '9999999999', 'Chennai', '22AAAAA0000A1Z5'),
('MediCorp Suppliers', '8888888888', 'Pune', '27BBBBB0000B1Z5'),
('Generic Solutions', '7777777777', 'Delhi', '07CCCCC0000C1Z5'),
('Pharma Link', '6666666666', 'Bangalore', '29DDDDD0000D1Z5');

INSERT INTO product_batches (product_id, batch_no, expiry_date, quantity, loose_qty, cost_price, mrp, margin_percent, distributor_id) VALUES
(1, 'BAT001', '2025-12-31', 100, 0, 30.00, 50.00, 40.00, 1),
(2, 'BAT002', '2025-10-31', 50, 0, 80.00, 120.00, 33.33, 1),
(3, 'BAT003', '2026-01-31', 200, 0, 20.00, 35.00, 42.85, 2),
(4, 'BAT004', '2025-08-31', 150, 0, 50.00, 80.00, 37.50, 3),
(5, 'BAT005', '2026-05-31', 80, 0, 100.00, 150.00, 33.33, 4),
(6, 'CS001', '2025-06-30', 40, 0, 60.00, 95.00, 36.84, 1),
(7, 'VV202', '2027-02-28', 120, 0, 40.00, 65.00, 38.46, 2),
(8, 'CR99', '2025-11-30', 300, 0, 25.00, 42.00, 40.47, 3),
(9, 'TM400', '2026-09-30', 90, 0, 140.00, 180.00, 22.22, 4),
(10, 'SH55', '2025-04-30', 250, 0, 70.00, 110.00, 36.36, 1);

-
