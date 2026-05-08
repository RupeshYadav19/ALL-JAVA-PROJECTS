package com.greexam.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class PDFExporter {

    public static boolean exportAnalytics(String filePath, String testTitle, Map<String, Object> overview, List<Map<String, Object>> questionAnalytics) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Check if testTitle is null and provide a default if so to prevent NullPointerException
            if (testTitle == null) {
                testTitle = "Analytics Report";
            }

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Test Analytics: " + testTitle, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Blank line

            // Overview
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            document.add(new Paragraph("Overview", headFont));
            document.add(new Paragraph("Total Students Assigned: " + overview.getOrDefault("totalStudents", 0)));
            document.add(new Paragraph("Total Submissions: " + overview.getOrDefault("totalSubmissions", 0)));
            document.add(new Paragraph("Average Marks: " + overview.getOrDefault("avgMarks", 0.0)));
            document.add(new Paragraph("Highest Score: " + overview.getOrDefault("highestScore", 0.0)));
            document.add(new Paragraph("Lowest Score: " + overview.getOrDefault("lowestScore", 0.0)));
            document.add(new Paragraph(" "));

            // Analytics Table
            document.add(new Paragraph("Per Question Analytics", headFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 2, 2, 2, 2});

            addTableHeader(table, "Q No.");
            addTableHeader(table, "Type");
            addTableHeader(table, "Avg Time (s)");
            addTableHeader(table, "Correct Rate");
            addTableHeader(table, "Skip Rate");
            addTableHeader(table, "Difficulty");

            int qNum = 1;
            for (Map<String, Object> row : questionAnalytics) {
                table.addCell(String.valueOf(qNum++));
                table.addCell(String.valueOf(row.getOrDefault("questionType", "")));
                
                Object avgTimeVal = row.get("avgTime");
                table.addCell((avgTimeVal != null) ? String.format("%.1f", ((Number) avgTimeVal).doubleValue()) : "0.0");
                
                Object correctRateVal = row.get("correctRate");
                table.addCell((correctRateVal != null) ? String.format("%.1f%%", ((Number) correctRateVal).doubleValue()) : "0.0%");
                
                Object skipRateVal = row.get("skipRate");
                table.addCell((skipRateVal != null) ? String.format("%.1f%%", ((Number) skipRateVal).doubleValue()) : "0.0%");
                
                table.addCell(String.valueOf(row.getOrDefault("difficultyLevel", "")));
            }

            document.add(table);
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        table.addCell(header);
    }
}
