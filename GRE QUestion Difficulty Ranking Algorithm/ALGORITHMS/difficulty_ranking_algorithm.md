# Difficulty Ranking Algorithm

The **GRE Question Difficulty Ranking Algorithm** is a data-driven approach to categorizing exam questions based on actual student performance metrics.

## Core Logic

The algorithm evaluates each question using four primary parameters:
1.  **Correct Rate (CR)**: Percentage of students who answered the question correctly.
2.  **Average Time (AT)**: The mean time spent by students on the question.
3.  **Skip Rate (SR)**: Percentage of students who chose to skip the question.
4.  **Expected Time (ET)**: The pre-defined estimated time required to solve the question.

## Difficulty Levels

| Level | Condition | Explanation |
| :--- | :--- | :--- |
| **NEEDS ATTENTION** | `Skip Rate > 30%` | High skip rate indicates the question might be confusing, broken, or extremely intimidating. |
| **EASY** | `CR >= 70%` AND `AT <= ET` | Most students get it right within the expected time frame. |
| **DIFFICULT** | `CR < 40%` OR (`CR < 70%` AND `AT > ET`) | Low correctness or high effort (time) required. |
| **MODERATE** | `40% <= CR < 70%` OR Default | Balanced performance metrics. |

## Implementation Trace

The logic is implemented in the `AnalyticsDAO.java` file within the `calculateDifficulty` method:

```java
public String calculateDifficulty(double correctRate, double avgTime, double skipRate, int expectedTime) {
    if (skipRate > 30) return "NEEDS_ATTENTION";
    if (correctRate >= 70 && avgTime <= expectedTime) return "EASY";
    if (correctRate < 40) return "DIFFICULT";
    if (correctRate >= 40 && correctRate < 70 && avgTime > expectedTime) return "MODERATE";
    if (correctRate < 70 && avgTime > expectedTime) return "DIFFICULT";
    return "MODERATE";
}
```

## Benefits
- **Dynamic Updates**: As more students take the test, the difficulty level evolves.
- **Teacher Insights**: Helps educators refine their question banks.
- **Fair Assessment**: Ensures that students are graded against realistic benchmarks.
