-- ============================================================
-- GRE Online Exam Management System — Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS gre_exam_system;
USE gre_exam_system;

-- -----------------------------------------------------------
-- 1. Users
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS Users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(64)  NOT NULL,
    role            ENUM('teacher','student') NOT NULL,
    email           VARCHAR(100) NOT NULL,
    secret_question VARCHAR(255),
    secret_answer   VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 2. Questions
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS Questions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id      INT NOT NULL,
    question_text   TEXT NOT NULL,
    question_type   ENUM('MCQ','FILL_BLANK','ONE_WORD','SHORT_ANSWER','LONG_ANSWER','MATCH') NOT NULL,
    marks           INT NOT NULL DEFAULT 1,
    topic           VARCHAR(100),
    expected_time_seconds INT DEFAULT 60,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES Users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 3. QuestionOptions (for MCQ)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS QuestionOptions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    question_id     INT NOT NULL,
    option_text     VARCHAR(500) NOT NULL,
    is_correct      BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 4. FillBlankAnswers
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS FillBlankAnswers (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    question_id     INT NOT NULL,
    blank_position  INT NOT NULL,
    correct_answer  VARCHAR(255) NOT NULL,
    case_sensitive  BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 5. MatchPairs
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS MatchPairs (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    question_id     INT NOT NULL,
    left_item       VARCHAR(255) NOT NULL,
    right_item      VARCHAR(255) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 6. Tests
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS Tests (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id       INT NOT NULL,
    title            VARCHAR(200) NOT NULL,
    duration_minutes INT NOT NULL,
    passing_marks    INT NOT NULL DEFAULT 0,
    start_time       DATETIME NOT NULL,
    end_time         DATETIME NOT NULL,
    is_published     BOOLEAN DEFAULT FALSE,
    show_one_at_time BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES Users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 7. TestQuestions
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS TestQuestions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    test_id         INT NOT NULL,
    question_id     INT NOT NULL,
    order_number    INT NOT NULL,
    marks           INT NOT NULL,
    FOREIGN KEY (test_id)     REFERENCES Tests(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 8. TestStudents
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS TestStudents (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    test_id         INT NOT NULL,
    student_id      INT NOT NULL,
    assigned_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (test_id)    REFERENCES Tests(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES Users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_test_student (test_id, student_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 9. Submissions
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS Submissions (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    test_id              INT NOT NULL,
    student_id           INT NOT NULL,
    start_time           DATETIME,
    end_time             DATETIME,
    total_marks_obtained DOUBLE DEFAULT 0,
    is_completed         BOOLEAN DEFAULT FALSE,
    auto_submitted       BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (test_id)    REFERENCES Tests(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES Users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_submission (test_id, student_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 10. StudentAnswers
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS StudentAnswers (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    submission_id       INT NOT NULL,
    question_id         INT NOT NULL,
    answer_text         TEXT,
    is_correct          BOOLEAN DEFAULT NULL,
    marks_obtained      DOUBLE DEFAULT 0,
    time_spent_seconds  INT DEFAULT 0,
    is_skipped          BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (submission_id) REFERENCES Submissions(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id)   REFERENCES Questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 11. DifficultyRankings
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS DifficultyRankings (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    question_id      INT NOT NULL,
    test_id          INT NOT NULL,
    difficulty_level ENUM('EASY','MODERATE','DIFFICULT','NEEDS_ATTENTION') DEFAULT 'MODERATE',
    avg_time_seconds DOUBLE DEFAULT 0,
    correct_rate     DOUBLE DEFAULT 0,
    skip_rate        DOUBLE DEFAULT 0,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE,
    FOREIGN KEY (test_id)     REFERENCES Tests(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 12. TeacherFeedback
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS TeacherFeedback (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    submission_id    INT NOT NULL,
    question_id      INT NOT NULL,
    teacher_comment  TEXT,
    manual_marks     DOUBLE DEFAULT 0,
    FOREIGN KEY (submission_id) REFERENCES Submissions(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id)   REFERENCES Questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- 13. Notifications
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS Notifications (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    message     TEXT NOT NULL,
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Insert a default teacher account (password: teacher123)
-- SHA-256 of 'teacher123' = ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f  (actual hash)
-- -----------------------------------------------------------
INSERT INTO Users (name, username, password_hash, role, email, secret_question, secret_answer)
VALUES ('Admin Teacher', 'teacher', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
        'teacher', 'teacher@greexam.com', 'What is your pet name?', 'buddy')
ON DUPLICATE KEY UPDATE id=id;
