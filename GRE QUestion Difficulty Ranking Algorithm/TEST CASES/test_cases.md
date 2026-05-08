# Project Test Cases

This document outlines the test cases for the Difficulty Ranking Algorithm and core system features.

## 1. Difficulty Ranking Logic Test Cases

The algorithm uses the following parameters for calculation:
- **CR**: Correct Rate (%)
- **AT**: Average Time (seconds)
- **SR**: Skip Rate (%)
- **ET**: Expected Time (seconds)

| Test Case ID | Description | Input (CR, AT, SR, ET) | Expected Output | Actual Output |
| :--- | :--- | :--- | :--- | :--- |
| TC-01 | High skip rate question | CR: 80, AT: 30, **SR: 35**, ET: 60 | NEEDS_ATTENTION | NEEDS_ATTENTION |
| TC-02 | Easy question (High CR, Low AT) | CR: 90, AT: 40, SR: 5, ET: 60 | EASY | EASY |
| TC-03 | Difficult question (Low CR) | **CR: 20**, AT: 120, SR: 10, ET: 60 | DIFFICULT | DIFFICULT |
| TC-04 | Moderate question (High AT) | CR: 50, AT: 100, SR: 10, ET: 60 | MODERATE | MODERATE |
| TC-05 | Borderline Difficult | CR: 65, **AT: 80**, SR: 10, ET: 60 | DIFFICULT | DIFFICULT |

## 2. Functional Test Cases

| Feature | Test Action | Expected Result |
| :--- | :--- | :--- |
| **Login** | Enter valid credentials | User redirected to correct dashboard (Teacher/Student) |
| **Login** | Enter invalid credentials | Display "Invalid username or password" error |
| **Test Creation** | Create test with 5 questions | Test successfully saved in database and visible to students |
| **Submission** | Student submits test | Marks calculated instantly and ranking updated in background |
| **Analytics** | Teacher views report | Distribution chart displays correct counts for each level |

## 3. Boundary & Error Handling

| Scenario | Input | Expected Behavior |
| :--- | :--- | :--- |
| **Empty Answer** | Submit without typing | Answer marked as "Incomplete/Skipped" |
| **Time Out** | Duration reaches zero | Auto-submission of the test |
| **Database Down** | Launch app | Display informative error "Could not connect to database" |
