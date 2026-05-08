# Project Report: GRE Question Difficulty Ranking Algorithm

## 1. Introduction
The **GRE Question Difficulty Ranking Algorithm** project is a sophisticated educational tool designed to bridge the gap between static test creation and dynamic student performance. By analyzing how students interact with questions—focusing on accuracy, speed, and hesitation—the system automatically classifies questions into meaningful difficulty tiers.

## 2. Problem Statement
Traditional GRE preparation systems often rely on manual difficulty tagging by educators, which can be subjective and may not reflect actual student experiences. Furthermore, a question might be "easy" for one cohort but "difficult" for another. There is a need for a system that adapts its ranking based on real-time empirical data.

## 3. Objectives
- To develop a robust automated ranking engine for GRE questions.
- To provide teachers with actionable insights into question quality.
- To help students understand their performance relative to the difficulty of their mistakes.
- To maintain a secure and scalable database of questions and student analytics.

## 4. System Overview
The system is built as a Java Desktop Application (Swing) with a MySQL backend. It features:
- **Teacher Dashboard**: For managing question banks and viewing global analytics.
- **Student Dashboard**: For taking tests and viewing personalized performance reports.
- **Analytics Engine**: The core component that processes submissions and updates difficulty levels.

## 5. Algorithm Explanation
The ranking logic uses a multi-factor approach:
- **Correct Rate**: High correctness implies ease.
- **Time Analysis**: Time spent exceeding the "Expected Time" indicates complexity.
- **Skip Rate**: Indicates "intimidating" or "poorly phrased" questions.

Detailed logic matches the following hierarchy:
- **NEEDS ATTENTION**: >30% Skip Rate.
- **EASY**: High accuracy + Fast completion.
- **DIFFICULT**: Low accuracy OR High completion time.
- **MODERATE**: Mid-range performance.

## 6. System Architecture & Diagrams
*Please refer to documentation in the `DIAGRAMS` folder for visual details.*

### 6.1 Entity Relationship
The database schema consists of 13 interconnected tables (Users, Questions, Tests, Submissions, etc.) that track every micro-interaction during an exam.

## 7. Implementation Highlights
- **Design Patterns**: Implementation of the Strategy pattern for ranking algorithms and Singleton for database connections.
- **Security**: SHA-256 password hashing and role-based access control.
- **UI/UX**: Modern look and feel powered by the FlatLaf library.

## 8. Conclusion
The GRE Question Difficulty Ranking Algorithm successfully demonstrates how data analytics can transform the educational assessment process. By automating difficulty ranking, it creates a more objective and evolving learning environment.
