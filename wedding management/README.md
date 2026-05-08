# 🌸 WeddingGenie — Indian Online Wedding Planner Management System

WeddingGenie is a comprehensive desktop application built in Java Swing for managing all aspects of an Indian wedding. It follows the **MVC (Model-View-Controller)** architecture and uses **MySQL** for robust data management.

## 🚀 Getting Started

### Prerequisites
1. **Java SE 17** or higher.
2. **MySQL 8.0** Server.
3. **MySQL JDBC Connector** (`mysql-connector-j-8.x.jar`) placed in the `lib` folder.

### Database Setup
1. Open MySQL Workbench or any MySQL client.
2. Run the SQL script located at `database/schema.sql` to create the database and tables.
3. Update `db.DBConnection` with your MySQL `username` and `password`.

### How to Run
Double-click `run.bat` (Windows) or use the following commands:
```bash
javac -d bin -cp "lib/*;." src/**/*.java
java -cp "bin;lib/*;." main.WeddingGenie
```

## 🛠 Features

### 1. Admin Dashboard
- **Analytics & Home**: Overview of bookings, revenue, and pending approvals.
- **Management**: Full CRUD for Events, Vendors, Bookings, Users, and Gallery.
- **Real Weddings**: Manage customer success stories.
- **Reporting**: Export booking summaries and revenue reports to CSV/TXT.

### 2. Vendor Dashboard
- **Business Profile**: Manage business details, pricing, and specialties.
- **Service Management**: CRUD for specific services (e.g., Candid Photography vs. Video).
- **Booking Requests**: Accept or decline bookings with reasons.
- **Portfolio & Reviews**: Upload portfolio images and view customer ratings.

### 3. User (Couple) Dashboard
- **Vendor Search**: Filter by category, city, budget (slider), and rating.
- **Event Booking**: Direct booking of pre-defined wedding events.
- **Wedding Tools**: 
  - **Checklist**: Categorized tasks with progress tracking.
  - **Budget Tracker**: Track estimated vs. actual expenses.
  - **Guest Manager**: RSVP tracking and Bride/Groom side management.
- **Creative Tools**:
  - **E-Invite Creator**: Create digital invites with live preview and 3 themes.
  - **Hashtag Generator**: Generate creative wedding hashtags.
  - **Wedding Songs**: Curated list of 19+ Indian wedding classics.
- **Ideas Gallery**: Browse style inspiration for Mehndi, Attire, Decor, etc.

## 📁 Project Structure
- `src/main`: Entry point.
- `src/auth`: Login, Registration, and Splash Screen.
- `src/admin`: Admin dashboard and panels.
- `src/vendor`: Vendor dashboard and panels.
- `src/user`: Couple dashboard and creative tools.
- `src/dao`: Data Access Objects (11 classes).
- `src/models`: Data models (10 classes).
- `src/utils`: Component factories, UI constants, and validation helpers.
- `src/db`: Database connection manager.

## 🎨 UI Design
- **Color Palette**: Rose Gold, Cream, and Deep Brown for a premium wedding look.
- **Custom Components**: Star Rating, Circular Progress, Hoverable Cards, Animated Splash Screen.

---
Built with ❤️ by WeddingGenie Team.
