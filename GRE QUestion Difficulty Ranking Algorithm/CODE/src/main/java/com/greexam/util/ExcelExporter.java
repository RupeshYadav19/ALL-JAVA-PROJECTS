package com.greexam.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    public static boolean exportAnalytics(String filePath, String testTitle, Map<String, Object> overview, List<Map<String, Object>> questionAnalytics) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Analytics - " + (testTitle != null ? testTitle.replaceAll("[^a-zA-Z0-9 ]", "") : "Test"));

            // Overview Section
            int rowNum = 0;
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("Test Analytics: " + (testTitle != null ? testTitle : "Test"));
            
            sheet.createRow(rowNum++); // Empty row
            
            String[] overviewKeys = {"totalStudents", "totalSubmissions", "avgMarks", "highestScore", "lowestScore"};
            String[] overviewLabels = {"Total Students", "Total Submissions", "Average Marks", "Highest Score", "Lowest Score"};
            
            for (int i = 0; i < overviewKeys.length; i++) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(overviewLabels[i]);
                Object val = overview.get(overviewKeys[i]);
                if (val instanceof Number) {
                    row.createCell(1).setCellValue(((Number) val).doubleValue());
                } else {
                    row.createCell(1).setCellValue(val != null ? String.valueOf(val) : "0");
                }
            }

            sheet.createRow(rowNum++); // Empty row

            // Analytics Table
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Q No.", "Type", "Topic", "Marks", "Avg Time (s)", "Correct Rate (%)", "Skip Rate (%)", "Difficulty"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int qNum = 1;
            for (Map<String, Object> qData : questionAnalytics) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(qNum++);
                row.createCell(1).setCellValue(String.valueOf(qData.getOrDefault("questionType", "")));
                row.createCell(2).setCellValue(String.valueOf(qData.getOrDefault("topic", "")));
                
                Object marks = qData.get("marks");
                row.createCell(3).setCellValue(marks instanceof Number ? ((Number) marks).doubleValue() : 0.0);
                
                Object avgTime = qData.get("avgTime");
                row.createCell(4).setCellValue(avgTime instanceof Number ? ((Number) avgTime).doubleValue() : 0.0);
                
                Object correctRate = qData.get("correctRate");
                row.createCell(5).setCellValue(correctRate instanceof Number ? ((Number) correctRate).doubleValue() : 0.0);
                
                Object skipRate = qData.get("skipRate");
                row.createCell(6).setCellValue(skipRate instanceof Number ? ((Number) skipRate).doubleValue() : 0.0);
                
                row.createCell(7).setCellValue(String.valueOf(qData.getOrDefault("difficultyLevel", "")));
            }

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
