package com.greexam.db;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Singleton Database Connection Manager.
 * Reads configuration from db.properties in the classpath.
 */
public class DBConnection {

    private static DBConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;

    private DBConnection() {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                throw new RuntimeException("db.properties not found in classpath!");
            }
            props.load(is);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");
            Class.forName(driver);

            // 1. First, connect to server without database to ensure it exists
            String serverUrl = url.substring(0, url.indexOf("/", 13) + 1); // Extract "jdbc:mysql://localhost:3306/"
            try (Connection serverConn = DriverManager.getConnection(serverUrl, username, password);
                 Statement stmt = serverConn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS gre_exam_system");
            } catch (SQLException e) {
                System.err.println("Note: Could not ensure database exists via server root. Attempting direct connection...");
            }

            // 2. Now connect to the actual database
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Database connected successfully.");
        } catch (Exception e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Convenience: get a fresh connection from the pool/driver.
     */
    public static Connection conn() {
        return getInstance().getConnection();
    }

    /**
     * Initialize the database schema if tables don't exist.
     */
    public void initializeDatabase() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            String catalog = conn.getCatalog();
            try (ResultSet rs = meta.getTables(catalog, null, "Users", null)) {
                if (!rs.next()) {
                    System.out.println("Creating database tables for catalog: " + catalog);
                    try (Statement stmt = conn.createStatement()) {
                        executeSqlScript(stmt);
                    }
                    System.out.println("✓ Database tables created successfully.");
                } else {
                    System.out.println("✓ Database tables already exist in " + catalog + ".");
                    // Ensure default teacher exists even if tables were already there
                    try (Statement stmt = conn.createStatement()) {
                        insertDefaultTeacher(stmt);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertDefaultTeacher(Statement stmt) throws SQLException {
        String sql = "INSERT IGNORE INTO Users (name, username, password_hash, role, email, secret_question, secret_answer) " +
                     "VALUES ('Admin Teacher', 'teacher', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', " +
                     "'teacher', 'teacher@greexam.com', 'What is your pet name?', 'buddy')";
        stmt.execute(sql);
    }

    private void executeSqlScript(Statement stmt) throws SQLException {
        String[] tables = {
            // Users
            """
            CREATE TABLE IF NOT EXISTS Users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                username VARCHAR(50) NOT NULL UNIQUE,
                password_hash VARCHAR(64) NOT NULL,
                role ENUM('teacher','student') NOT NULL,
                email VARCHAR(100) NOT NULL,
                secret_question VARCHAR(255),
                secret_answer VARCHAR(255),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB
            """,
            // Questions
            """
            CREATE TABLE IF NOT EXISTS Questions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                teacher_id INT NOT NULL,
                question_text TEXT NOT NULL,
                question_type ENUM('MCQ','FILL_BLANK','ONE_WORD','SHORT_ANSWER','LONG_ANSWER','MATCH') NOT NULL,
                marks INT NOT NULL DEFAULT 1,
                topic VARCHAR(100),
                expected_time_seconds INT DEFAULT 60,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (teacher_id) REFERENCES Users(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // QuestionOptions
            """
            CREATE TABLE IF NOT EXISTS QuestionOptions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                question_id INT NOT NULL,
                option_text VARCHAR(500) NOT NULL,
                is_correct BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // FillBlankAnswers
            """
            CREATE TABLE IF NOT EXISTS FillBlankAnswers (
                id INT AUTO_INCREMENT PRIMARY KEY,
                question_id INT NOT NULL,
                blank_position INT NOT NULL,
                correct_answer VARCHAR(255) NOT NULL,
                case_sensitive BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // MatchPairs
            """
            CREATE TABLE IF NOT EXISTS MatchPairs (
                id INT AUTO_INCREMENT PRIMARY KEY,
                question_id INT NOT NULL,
                left_item VARCHAR(255) NOT NULL,
                right_item VARCHAR(255) NOT NULL,
                FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // Tests
            """
            CREATE TABLE IF NOT EXISTS Tests (
                id INT AUTO_INCREMENT PRIMARY KEY,
                teacher_id INT NOT NULL,
                title VARCHAR(200) NOT NULL,
                duration_minutes INT NOT NULL,
                passing_marks INT NOT NULL DEFAULT 0,
                start_time DATETIME NOT NULL,
                end_time DATETIME NOT NULL,
                is_published BOOLEAN DEFAULT FALSE,
                show_one_at_time BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (teacher_id) REFERENCES Users(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // TestQuestions
            """
            CREATE TABLE IF NOT EXISTS TestQuestions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                test_id INT NOT NULL,
                question_id INT NOT NULL,
                order_number INT NOT NULL,
                marks INT NOT NULL,
                FOREIGN KEY (test_id) REFERENCES Tests(id) ON DELETE CASCADE,
                FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // TestStudents
            """
            CREATE TABLE IF NOT EXISTS TestStudents (
                id INT AUTO_INCREMENT PRIMARY KEY,
                test_id INT NOT NULL,
                student_id INT NOT NULL,
                assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (test_id) REFERENCES Tests(id) ON DELETE CASCADE,
                FOREIGN KEY (student_id) REFERENCES Users(id) ON DELETE CASCADE,
                UNIQUE KEY unique_test_student (test_id, student_id)
            ) ENGINE=InnoDB
            """,
            // Submissions
            """
            CREATE TABLE IF NOT EXISTS Submissions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                test_id INT NOT NULL,
                student_id INT NOT NULL,
                start_time DATETIME,
                end_time DATETIME,
                total_marks_obtained DOUBLE DEFAULT 0,
                is_completed BOOLEAN DEFAULT FALSE,
                auto_submitted BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (test_id) REFERENCES Tests(id) ON DELETE CASCADE,
                FOREIGN KEY (student_id) REFERENCES Users(id) ON DELETE CASCADE,
                UNIQUE KEY unique_submission (test_id, student_id)
            ) ENGINE=InnoDB
            """,
            // StudentAnswers
            """
            CREATE TABLE IF NOT EXISTS StudentAnswers (
                id INT AUTO_INCREMENT PRIMARY KEY,
                submission_id INT NOT NULL,
                question_id INT NOT NULL,
                answer_text TEXT,
                is_correct BOOLEAN DEFAULT NULL,
                marks_obtained DOUBLE DEFAULT 0,
                time_spent_seconds INT DEFAULT 0,
                is_skipped BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (submission_id) REFERENCES Submissions(id) ON DELETE CASCADE,
                FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // DifficultyRankings
            """
            CREATE TABLE IF NOT EXISTS DifficultyRankings (
                id INT AUTO_INCREMENT PRIMARY KEY,
                question_id INT NOT NULL,
                test_id INT NOT NULL,
                difficulty_level ENUM('EASY','MODERATE','DIFFICULT','NEEDS_ATTENTION') DEFAULT 'MODERATE',
                avg_time_seconds DOUBLE DEFAULT 0,
                correct_rate DOUBLE DEFAULT 0,
                skip_rate DOUBLE DEFAULT 0,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE,
                FOREIGN KEY (test_id) REFERENCES Tests(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // TeacherFeedback
            """
            CREATE TABLE IF NOT EXISTS TeacherFeedback (
                id INT AUTO_INCREMENT PRIMARY KEY,
                submission_id INT NOT NULL,
                question_id INT NOT NULL,
                teacher_comment TEXT,
                manual_marks DOUBLE DEFAULT 0,
                FOREIGN KEY (submission_id) REFERENCES Submissions(id) ON DELETE CASCADE,
                FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // Notifications
            """
            CREATE TABLE IF NOT EXISTS Notifications (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                message TEXT NOT NULL,
                is_read BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """,
            // Default Teacher (teacher / teacher123)
            """
            INSERT IGNORE INTO Users (name, username, password_hash, role, email, secret_question, secret_answer)
            VALUES ('Admin Teacher', 'teacher', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
                    'teacher', 'teacher@greexam.com', 'What is your pet name?', 'buddy')
            """
        };

        for (String sql : tables) {
            stmt.execute(sql);
        }
    }

    /**
     * Check if any users exist in the Users table.
     */
    public boolean hasUsers() {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT COUNT(*) FROM Users");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
