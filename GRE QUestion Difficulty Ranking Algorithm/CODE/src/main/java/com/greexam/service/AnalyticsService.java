package com.greexam.service;

import com.greexam.dao.AnalyticsDAO;

import java.util.List;
import java.util.Map;

/**
 * Service for computing and retrieving analytics data.
 * Uses Strategy pattern for difficulty ranking algorithms.
 */
public class AnalyticsService {

    private final AnalyticsDAO analyticsDAO = new AnalyticsDAO();

    public Map<String, Object> getTestOverview(int testId) {
        return analyticsDAO.getTestOverview(testId);
    }

    public List<Map<String, Object>> getPerQuestionAnalytics(int testId) {
        return analyticsDAO.getPerQuestionAnalytics(testId);
    }

    public void refreshDifficultyRankings(int testId) {
        analyticsDAO.updateDifficultyRankings(testId);
    }

    public List<Map<String, Object>> getTopicWeaknessReport(int testId) {
        return analyticsDAO.getTopicWeaknessReport(testId);
    }

    public List<Map<String, Object>> getLeaderboard(int testId) {
        return analyticsDAO.getLeaderboard(testId);
    }

    public List<Map<String, Object>> getStudentScoreTrend(int studentId) {
        return analyticsDAO.getStudentScoreTrend(studentId);
    }

    /**
     * Get difficulty distribution counts for a test.
     */
    public Map<String, Integer> getDifficultyDistribution(int testId) {
        List<Map<String, Object>> analytics = getPerQuestionAnalytics(testId);
        Map<String, Integer> distribution = new java.util.LinkedHashMap<>();
        distribution.put("EASY", 0);
        distribution.put("MODERATE", 0);
        distribution.put("DIFFICULT", 0);
        distribution.put("NEEDS_ATTENTION", 0);

        for (Map<String, Object> row : analytics) {
            String level = (String) row.get("difficultyLevel");
            distribution.merge(level, 1, Integer::sum);
        }
        return distribution;
    }
}
