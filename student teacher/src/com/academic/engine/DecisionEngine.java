package com.academic.engine;

import com.academic.model.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * DecisionEngine evaluates a Student against all 10 defined academic test
 * cases.
 * Returns a List of String arrays where each entry is:
 * { testCaseId, description, preconditions, expectedResult, actualResult,
 * status }
 */
public class DecisionEngine {

    public List<String[]> evaluate(Student s) {
        List<String[]> results = new ArrayList<>();

        // --- Pre-processing: ACAD_008 (Null/Zero SGPA) ---
        if (s.getSgpa() == 0.0) {
            // ACAD_008 logic handled below
        }

        // --- Pre-processing: ACAD_009 (SGPA out of bounds) ---
        double sgpa = s.getSgpa();
        if (sgpa > 10.0) {
            sgpa = 10.0;
        }

        double attendance = s.getAttendancePercent();
        int credits = s.getCredits();
        boolean hasViol = s.isConductViolation();
        String condType = s.getConductType() != null ? s.getConductType() : "";

        // ============================================================
        // ACAD_001 — Dean's List
        // ============================================================
        {
            String id = "ACAD_001";
            String desc = "Dean's List Eligibility";
            String pre = "SGPA >= 9.5, Credits >= 15, No Conduct Violation";
            String exp = "DEANS_LIST";
            String act;
            if (sgpa >= 9.5 && credits >= 15 && !hasViol) {
                act = "DEANS_LIST";
            } else {
                act = "NOT_ELIGIBLE";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_002 — Good Standing
        // ============================================================
        {
            String id = "ACAD_002";
            String desc = "Good Standing Eligibility";
            String pre = "SGPA >= 7.5, Credits >= 12, No Conduct Violation";
            String exp = "GOOD_STANDING";
            String act;
            if (sgpa >= 7.5 && credits >= 12 && !hasViol) {
                act = "GOOD_STANDING";
            } else {
                act = "NOT_ELIGIBLE";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_003 — Academic Probation (basic)
        // ============================================================
        {
            String id = "ACAD_003";
            String desc = "Academic Probation";
            String pre = "SGPA < 5.0, Credits >= 12, No Conduct Violation";
            String exp = "ACADEMIC_PROBATION";
            String act;
            if (sgpa < 5.0 && credits >= 12 && !hasViol) {
                act = "ACADEMIC_PROBATION";
            } else {
                act = "NOT_TRIGGERED";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_004 — Attendance Warning
        // ============================================================
        {
            String id = "ACAD_004";
            String desc = "Attendance Warning";
            String pre = "Attendance < 75%, SGPA >= 7.5";
            String exp = "ATTENDANCE_WARNING";
            String act;
            if (attendance < 75 && sgpa >= 7.5) {
                act = "ATTENDANCE_WARNING";
            } else {
                act = "NOT_TRIGGERED";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_005 — Disciplinary Review (low SGPA + violation)
        // ============================================================
        {
            String id = "ACAD_005";
            String desc = "Disciplinary Review (Low SGPA)";
            String pre = "Conduct Violation = true, SGPA < 6.0";
            String exp = "DISCIPLINARY_REVIEW";
            String act;
            if (hasViol && sgpa < 6.0) {
                act = "DISCIPLINARY_REVIEW";
            } else {
                act = "NOT_TRIGGERED";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_006 — Priority: Urgent Violation overrides Dean's List
        // ============================================================
        {
            String id = "ACAD_006";
            String desc = "Priority: Urgent Violation vs Dean's List";
            String pre = "ConductType = Urgent, SGPA >= 10.0";
            String exp = "DISCIPLINARY_REVIEW";
            String act;
            if ("Urgent".equalsIgnoreCase(condType) && sgpa >= 10.0) {
                act = "DISCIPLINARY_REVIEW";
            } else {
                act = "NOT_TRIGGERED";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_007 — Priority: Probation takes priority over Attendance Warning
        // ============================================================
        {
            String id = "ACAD_007";
            String desc = "Priority: Probation over Attendance Warning";
            String pre = "SGPA < 5.0, Attendance < 65%";
            String exp = "ACADEMIC_PROBATION";
            String act;
            if (sgpa < 5.0 && attendance < 65) {
                act = "ACADEMIC_PROBATION";
            } else {
                act = "NOT_TRIGGERED";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_008 — Null/Zero GPA Handling
        // ============================================================
        {
            String id = "ACAD_008";
            String desc = "Null/Zero SGPA Handling";
            String pre = "Student name exists, SGPA is 0 or not provided";
            String exp = "System assigned 0.0";
            String act;
            if (s.getSgpa() == 0.0 && s.getFullName() != null && !s.getFullName().isEmpty()) {
                act = "System assigned 0.0";
            } else {
                act = "SGPA provided (" + String.format("%.2f", s.getSgpa()) + ")";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_009 — Out-of-bounds SGPA
        // ============================================================
        {
            String id = "ACAD_009";
            String desc = "SGPA Out-of-Bounds Capping";
            String pre = "SGPA > 10.0";
            String exp = "SGPA capped at 10.0";
            String act;
            if (s.getSgpa() > 10.0) {
                act = "SGPA capped at 10.0";
            } else {
                act = "SGPA within range (" + String.format("%.2f", s.getSgpa()) + ")";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // ACAD_010 — Multiple Violations (Priority 1: Conduct)
        // ============================================================
        {
            String id = "ACAD_010";
            String desc = "Multiple Violations — Conduct Priority";
            String pre = "Violation = true, SGPA < 5.0, Attendance < 40%";
            String exp = "DISCIPLINARY_REVIEW";
            String act;
            if (hasViol && sgpa < 5.0 && attendance < 40) {
                act = "DISCIPLINARY_REVIEW";
            } else {
                act = "NOT_TRIGGERED";
            }
            results.add(row(id, desc, pre, exp, act));
        }

        // ============================================================
        // IMPROVEMENT_LOGIC
        // ============================================================
        if (s.getSemester() == 3) {
            String id = "IMPROVE_001";
            String desc = "Academic Improvement Analysis";
            String pre = "Semester 3 student";
            String exp = "Analyzed";
            String act;
            double prevAvg = s.getCgpaFirstYear();
            double curr = s.getSgpaThirdSem();
            if (curr > prevAvg) {
                act = "Improved from " + prevAvg + " to " + curr;
            } else if (curr < prevAvg) {
                act = "Declined from " + prevAvg + " to " + curr + ". Advice: focus on core subjects.";
            } else {
                act = "Maintained performance at " + curr;
            }
            results.add(row(id, desc, pre, exp, act));
        }

        return results;
    }

    /**
     * Builds a result row and determines Pass/Fail status.
     */
    private String[] row(String id, String desc, String pre, String expected, String actual) {
        String status;
        if (expected.equals(actual)) {
            status = "Pass";
        } else if (actual.contains("WARNING") || actual.contains("System assigned") || actual.contains("capped")) {
            status = "Pass"; // boundary-handling tests pass when triggered correctly
        } else {
            status = "Fail";
        }
        return new String[] { id, desc, pre, expected, actual, status };
    }
}
