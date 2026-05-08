-- ============================================================
-- WeddingGenie Database Schema
-- Database: wedding_planner_db
-- MySQL 8.0
-- ============================================================

CREATE DATABASE IF NOT EXISTS wedding_planner_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE wedding_planner_db;

-- 1. USERS
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(200) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    city VARCHAR(100),
    role ENUM('admin','vendor','user') DEFAULT 'user',
    profile_pic_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- 2. VENDORS
CREATE TABLE IF NOT EXISTS vendors (
    vendor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    business_name VARCHAR(200) NOT NULL,
    category ENUM('Photographer','Makeup Artist','Caterer','Decorator','Mehndi Artist','DJ','Choreographer','Venue','Bridal Wear','Groom Wear','Invitation','Jewellery','Wedding Planner','Trousseau Packer','Transport','Wedding Cake') NOT NULL,
    city VARCHAR(100),
    locality VARCHAR(200),
    description TEXT,
    starting_price DECIMAL(12,2) DEFAULT 0,
    rating DECIMAL(2,1) DEFAULT 0.0,
    review_count INT DEFAULT 0,
    portfolio_path VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    award_winner BOOLEAN DEFAULT FALSE,
    specialties VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. EVENTS
CREATE TABLE IF NOT EXISTS events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(200) NOT NULL,
    event_type ENUM('Haldi','Mehendi','Sangeet','Wedding','Reception','Engagement','Tilak','Cocktail') NOT NULL,
    description TEXT,
    venue VARCHAR(300),
    city VARCHAR(100),
    date DATE,
    time VARCHAR(20),
    capacity INT DEFAULT 100,
    price_per_head DECIMAL(10,2) DEFAULT 0,
    total_price DECIMAL(12,2) DEFAULT 0,
    status ENUM('active','inactive','completed') DEFAULT 'active',
    image_path VARCHAR(500),
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 4. SERVICES
CREATE TABLE IF NOT EXISTS services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    vendor_id INT NOT NULL,
    service_name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    price DECIMAL(12,2) DEFAULT 0,
    price_type ENUM('fixed','per_head','per_day') DEFAULT 'fixed',
    is_available BOOLEAN DEFAULT TRUE,
    images_path VARCHAR(500),
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

-- 5. BOOKINGS
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    event_date DATE,
    guest_count INT DEFAULT 1,
    ceremony_types VARCHAR(500),
    total_price DECIMAL(12,2) DEFAULT 0,
    advance_paid DECIMAL(12,2) DEFAULT 0,
    special_requests TEXT,
    status ENUM('pending','approved','rejected','cancelled','completed') DEFAULT 'pending',
    payment_status ENUM('unpaid','advance_paid','fully_paid') DEFAULT 'unpaid',
    rejection_reason TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE SET NULL
);

-- 6. BOOKING_SERVICES (junction)
CREATE TABLE IF NOT EXISTS booking_services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    service_id INT NOT NULL,
    vendor_id INT NOT NULL,
    quantity INT DEFAULT 1,
    price DECIMAL(12,2) DEFAULT 0,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE
);

-- 7. REVIEWS
CREATE TABLE IF NOT EXISTS reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    vendor_id INT NOT NULL,
    booking_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    photos_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_approved BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE SET NULL
);

-- 8. GUESTS
CREATE TABLE IF NOT EXISTS guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    user_id INT NOT NULL,
    guest_name VARCHAR(150) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(200),
    relation VARCHAR(100),
    side ENUM('bride','groom') DEFAULT 'bride',
    rsvp_status ENUM('pending','attending','not_attending') DEFAULT 'pending',
    meal_preference ENUM('veg','non-veg') DEFAULT 'veg',
    table_no INT,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 9. CHECKLIST
CREATE TABLE IF NOT EXISTS checklist (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category VARCHAR(100),
    task_name VARCHAR(300) NOT NULL,
    due_date DATE,
    is_done BOOLEAN DEFAULT FALSE,
    priority ENUM('high','medium','low') DEFAULT 'medium',
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 10. BUDGET
CREATE TABLE IF NOT EXISTS budget (
    budget_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category VARCHAR(100),
    item_name VARCHAR(200) NOT NULL,
    estimated_amount DECIMAL(12,2) DEFAULT 0,
    actual_amount DECIMAL(12,2) DEFAULT 0,
    paid_amount DECIMAL(12,2) DEFAULT 0,
    vendor_id INT,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id) ON DELETE SET NULL
);

-- 11. GALLERY
CREATE TABLE IF NOT EXISTS gallery (
    gallery_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200),
    type ENUM('mehndi','lehenga','decor','ceremony','venue','makeup','invitation') NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    tags VARCHAR(500),
    uploaded_by INT,
    is_featured BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 12. INVITATIONS
CREATE TABLE IF NOT EXISTS invitations (
    invite_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    template_name VARCHAR(100),
    bride_name VARCHAR(150),
    groom_name VARCHAR(150),
    wedding_date VARCHAR(100),
    venue_text VARCHAR(500),
    invite_text TEXT,
    template_style ENUM('traditional','modern','floral','royal','minimal') DEFAULT 'traditional',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 13. NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    notif_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(200),
    message TEXT,
    type ENUM('booking','review','approval','reminder','system','enquiry') DEFAULT 'system',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 14. REAL_WEDDINGS
CREATE TABLE IF NOT EXISTS real_weddings (
    story_id INT AUTO_INCREMENT PRIMARY KEY,
    couple_names VARCHAR(300),
    city VARCHAR(100),
    venue VARCHAR(300),
    date DATE,
    story_text TEXT,
    photos_path VARCHAR(500),
    vendors_tagged VARCHAR(500),
    views INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE
);

-- 15. WEDDING_SONGS
CREATE TABLE IF NOT EXISTS wedding_songs (
    song_id INT AUTO_INCREMENT PRIMARY KEY,
    song_name VARCHAR(200) NOT NULL,
    singer VARCHAR(200),
    category ENUM('Baraat','Sangeet','Bride Entry','Ring Ceremony','Reception','Haldi','Mehndi') NOT NULL,
    language VARCHAR(50),
    mood VARCHAR(100)
);

-- ============================================================
-- SEED DATA
-- ============================================================

-- Admin account (password: Admin@123 -> SHA-256)
INSERT IGNORE INTO users (user_id, full_name, email, password_hash, phone, city, role, is_active)
VALUES (1, 'Admin User', 'admin@weddinggenie.com',
        SHA2('Admin@123', 256), '9999999999', 'Delhi', 'admin', 1);

-- Sample vendor users
INSERT IGNORE INTO users (user_id, full_name, email, password_hash, phone, city, role, is_active) VALUES
(2, 'Rahul Sharma', 'rahul@shutters.com', SHA2('Vendor@123', 256), '9876543210', 'Delhi', 'vendor', 1),
(3, 'Priya Mehendi', 'priya@mehendi.com', SHA2('Vendor@123', 256), '9876543211', 'Mumbai', 'vendor', 1),
(4, 'Feast Catering', 'feast@catering.com', SHA2('Vendor@123', 256), '9876543212', 'Bangalore', 'vendor', 1);

-- Sample couple user
INSERT IGNORE INTO users (user_id, full_name, email, password_hash, phone, city, role, is_active)
VALUES (5, 'Anjali & Rohan', 'anjali@couple.com', SHA2('User@123', 256), '9876543213', 'Delhi', 'user', 1);

-- Vendors
INSERT IGNORE INTO vendors (vendor_id, user_id, business_name, category, city, locality, description, starting_price, rating, review_count, is_verified, is_featured) VALUES
(1, 2, 'Shutter Stories', 'Photographer', 'Delhi', 'Connaught Place', 'Award-winning wedding photography capturing every precious moment with artistic vision.', 25000, 4.8, 127, 1, 1),
(2, 3, 'Mehendi Magic', 'Mehndi Artist', 'Mumbai', 'Andheri West', 'Traditional and modern bridal mehndi designs crafted with passion and precision.', 5000, 4.9, 89, 1, 1),
(3, 4, 'Feast & Feast Catering', 'Caterer', 'Bangalore', 'Koramangala', 'Authentic Indian cuisine for all wedding ceremonies with customized menus.', 800, 4.7, 203, 1, 0);

-- Sample events
INSERT IGNORE INTO events (event_id, event_name, event_type, description, venue, city, date, time, capacity, price_per_head, total_price, status, created_by) VALUES
(1, 'Royal Wedding Package', 'Wedding', 'A grand royal wedding experience at Delhi\'s most prestigious venue with full decoration and catering.', 'Taj Palace Hotel', 'Delhi', '2025-12-20', '18:00', 300, 5000, 1500000, 'active', 1),
(2, 'Sangeet Night Special', 'Sangeet', 'A vibrant Sangeet night filled with dance, music, and celebrations with live band performance.', 'ITC Maratha', 'Mumbai', '2025-11-15', '19:00', 150, 3000, 450000, 'active', 1),
(3, 'Haldi Morning Ceremony', 'Haldi', 'Traditional Haldi ceremony with floral decor and authentic rituals.', 'Leela Palace', 'Delhi', '2025-12-19', '10:00', 100, 1500, 150000, 'active', 1),
(4, 'Mehendi Evening', 'Mehendi', 'Beautiful Mehendi ceremony with live mehndi artists, music and decorations.', 'ITC Grand', 'Bangalore', '2026-01-10', '16:00', 200, 2000, 400000, 'active', 1),
(5, 'Reception Gala', 'Reception', 'Grand reception party with 5-star dining, DJ, and live entertainment.', 'Oberoi Grand', 'Kolkata', '2026-02-14', '20:00', 500, 4000, 2000000, 'active', 1);

-- Sample services
INSERT IGNORE INTO services (vendor_id, service_name, category, description, price, price_type, is_available) VALUES
(1, 'Bridal Portrait Session', 'Photography', 'Exclusive 4-hour bridal portrait photography session', 15000, 'fixed', 1),
(1, 'Full Wedding Coverage', 'Photography', 'Complete wedding day photography + videography (10 hours)', 50000, 'fixed', 1),
(2, 'Bridal Mehndi', 'Mehndi', 'Full hands and feet bridal mehndi with Arabic and Indian designs', 8000, 'fixed', 1),
(2, 'Guest Mehndi', 'Mehndi', 'Simple mehndi designs for wedding guests (per person)', 500, 'per_head', 1),
(3, 'Veg Buffet', 'Catering', 'Pure vegetarian 30-dish buffet with live counters', 1200, 'per_head', 1),
(3, 'Non-Veg Feast', 'Catering', 'Mixed vegetarian and non-vegetarian full course meal', 1800, 'per_head', 1);

-- Wedding songs
INSERT IGNORE INTO wedding_songs (song_name, singer, category, language, mood) VALUES
('Ainvayi Ainvayi', 'Salim-Sulaiman', 'Baraat', 'Hindi', 'Fun'),
('Desi Beat', 'Yo Yo Honey Singh', 'Baraat', 'Hindi', 'Energetic'),
('London Thumakda', 'Labh Janjua', 'Baraat', 'Hindi', 'Fun'),
('Gallan Goodiyaan', 'Shankar-Ehsaan-Loy', 'Sangeet', 'Hindi', 'Celebratory'),
('Nazm Nazm', 'Arko', 'Sangeet', 'Hindi', 'Romantic'),
('Mehendi Laga Ke Rakhna', 'Kavita Krishnamurthy', 'Mehndi', 'Hindi', 'Traditional'),
('Veere', 'Kanika Kapoor', 'Mehndi', 'Hindi', 'Fun'),
('Piya Re Piya Re', 'Shaan', 'Bride Entry', 'Hindi', 'Romantic'),
('Tujh Mein Rab Dikhta Hai', 'Roop Kumar Rathod', 'Bride Entry', 'Hindi', 'Emotional'),
('Teri Aankhon Mein', 'Darshan Raval', 'Ring Ceremony', 'Hindi', 'Romantic'),
('Viah', 'Harrdy Sandhu', 'Reception', 'Punjabi', 'Celebratory'),
('Nach Punjaban', 'Various', 'Reception', 'Punjabi', 'Energetic'),
('Go Go Govinda', 'Various', 'Haldi', 'Hindi', 'Fun'),
('Haldi', 'Vishal Mishra', 'Haldi', 'Hindi', 'Traditional'),
('Morni Banke', 'Asees Kaur', 'Sangeet', 'Hindi', 'Celebratory'),
('Teri Bhabhi', 'Neha Kakkar', 'Baraat', 'Hindi', 'Fun'),
('Sauda Khara Khara', 'Sukhbir', 'Baraat', 'Punjabi', 'Energetic'),
('Jhume Jo Pathaan', 'Arijit Singh', 'Reception', 'Hindi', 'Energetic'),
('Tere Vaaste', 'Varun Jain', 'Bride Entry', 'Hindi', 'Romantic'),
('Dholida', 'Various', 'Sangeet', 'Gujarati', 'Traditional');

-- Sample real wedding story
INSERT IGNORE INTO real_weddings (couple_names, city, venue, date, story_text, is_featured, views) VALUES
('Priya & Arjun', 'Udaipur', 'Lake Palace Hotel', '2025-10-15', 'A fairytale destination wedding by the lake. Priya wore a timeless red Banarasi lehenga while Arjun was dashing in a cream sherwani. The mandap was decorated with rose gold florals. Their sangeet had everyone dancing till midnight!', 1, 245),
('Sneha & Vikram', 'Delhi', 'The Imperial Hotel', '2025-09-20', 'A royal Delhi wedding with 800 guests from across India. The 3-day celebration started with a colorful Haldi morning, followed by an electrifying Sangeet night and a grand Wedding ceremony under the stars.', 1, 189);

-- Sample checklist templates (for admin-seeded reference; user items are created per user)

COMMIT;

SELECT 'WeddingGenie database setup complete!' AS Status;
