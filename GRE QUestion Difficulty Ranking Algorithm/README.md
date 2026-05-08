# GRE Question Difficulty Ranking Algorithm

## 1. Project Title
**GRE Question Difficulty Ranking Algorithm**

## 2. Project Description
An advanced GRE exam management system that utilizes student performance data (accuracy, completion time, and skip rates) to dynamically rank questions into four difficulty levels: Easy, Moderate, Difficult, and Needs Attention. The project provides a teacher-facing analytics dashboard and a student-facing exam interface.

## 3. Purpose of Project
The primary purpose is to automate the categorization of exam content based on objective, real-world data rather than subjective manual tagging. This enables:
- **Dynamic Content Optimization**: Continual refinement of question banks.
- **Accurate Benchmarking**: Better assessment of student ability relative to question complexity.
- **Data-Driven Education**: Helping teachers identify "pain point" topics using empirical evidence.

## 4. Steps to run the Code
### Prerequisites
- **Java JDK 17** or higher.
- **Maven 3.6+**.
- **MySQL 8.0+**.

### Setup Instructions
1.  **Database Configuration**:
    - Open your MySQL client and execute the script located at `CODE/sql/schema.sql`.
    - Ensure your database credentials in `CODE/src/main/java/com/greexam/db/DBConnection.java` match your local environment.
2.  **Build the Project**:
    - Navigate to the `CODE` directory via terminal.
    - Run: `mvn clean install`
3.  **Execute the Application**:
    - Run the main class: `mvn exec:java -Dexec.mainClass="com.greexam.main.Main"`
    - Use the default credentials:
        - **Username**: `teacher`
        - **Password**: `teacher123`

## 5. Required inputs & expected outputs
| Module | Input | Expected Output |
| :--- | :--- | :--- |
| **Login** | Username & Password | Access to Dashboard |
| **Exam** | Question Answers & Time | Calculated Marks & Analytics |
| **Ranking** | Correct Rate, Time Spent, Skips | Automatic Category Assignment |
| **Reports** | Test Selection | Visual Distribution Charts & Weakness Reports |

## 6. Individual contribution of the student
*(This section is a template to be filled by the team members)*

- **Student Name 1 (UID/Roll No)**: [Role and specific modules worked on, e.g., "Implemented the ranking algorithm logic in AnalyticsDAO and designed the Teacher Dashboard."]
- **Student Name 2 (UID/Roll No)**: [e.g., "Developed the Database schema and Student Exam Interface."]
- **Student Name 3 (UID/Roll No)**: [e.g., "Created the analytics service and visualization components."]

---

