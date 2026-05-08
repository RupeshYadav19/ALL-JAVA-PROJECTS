# System Diagrams

This document contains visual representations of the GRE Question Difficulty Ranking Algorithm's architecture and data structure.

## 1. System Architecture

The application follows a standard N-Tier architecture using Java Swing for the frontend and MySQL for the backend.

```mermaid
graph TD
    User((User/Student/Teacher)) --> UI[Java Swing Interface]
    UI --> Service[Service Layer - Business Logic]
    Service --> DAO[DAO Layer - Data Access]
    DAO --> DB[(MySQL Database)]
    
    subgraph Analytics
        Service --> AS[AnalyticsService]
        AS --> ADA[AnalyticsDAO]
        ADA --> Logic{Difficulty Ranking Logic}
    end
```

## 2. Entity Relationship Diagram (ERD)

The core entities involved in the difficulty ranking system.

```mermaid
erDiagram
    Users ||--o{ Questions : creates
    Users ||--o{ Submissions : performs
    Tests ||--o{ TestQuestions : contains
    Questions ||--o{ TestQuestions : used_in
    Submissions ||--o{ StudentAnswers : has
    Questions ||--o{ StudentAnswers : answered
    Questions ||--o{ DifficultyRankings : ranked_as
    Tests ||--o{ DifficultyRankings : calculated_for
```

## 3. Data Flow

1. Student takes a **Test**.
2. **StudentAnswers** are recorded (time, correctness, skip status).
3. **AnalyticsDAO** retrieves performance stats.
4. **Ranking Algorithm** calculates levels.
5. Results are persisted in **DifficultyRankings**.
