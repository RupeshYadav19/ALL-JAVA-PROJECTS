package com.academic.service;

import com.academic.model.Student;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GeminiService {
    private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";
    private String apiKey;

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getFeedback(Student student, List<String[]> results) {
        if (apiKey == null || apiKey.isEmpty())
            return "API Key not provided.";

        try {
            URL url = new URL(API_ENDPOINT + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            StringBuilder p = new StringBuilder();
            p.append(
                    "Analyze this student academic record and eligibility results. Provide 3-4 bullet points of constructive feedback and improvement tips.\n\n");
            p.append("Student: ").append(student.getFullName()).append("\n");
            p.append("Year: ").append(student.getYear()).append(", Sem: ").append(student.getSemester()).append("\n");
            p.append("SGPA: ").append(student.getSgpa()).append("\n");
            p.append("Attendance: ").append(student.getAttendancePercent()).append("%\n\n");
            p.append("Eligibility Checks:\n");
            for (String[] res : results) {
                p.append("- ").append(res[1]).append(": ").append(res[4]).append(" (").append(res[5]).append(")\n");
            }

            // Proper JSON escaping for basic control characters
            String escapedPrompt = p.toString()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String jsonRequest = "{\"contents\": [{\"parts\":[{\"text\": \"" + escapedPrompt + "\"}]}]}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                // Very basic JSON parsing - extract text between "text": " and "
                String resStr = response.toString();
                int start = resStr.indexOf("\"text\": \"");
                if (start != -1) {
                    start += 9;
                    int end = resStr.indexOf("\"", start);
                    if (end != -1) {
                        return resStr.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
                    }
                }
                return "AI analysis completed. Please check your performance trends.";
            } else {
                // Read error stream for more details
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                StringBuilder errorMsg = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorMsg.append(line.trim());
                }
                System.err.println("Gemini API Error: " + errorMsg.toString());
                return "AI Feedback unavailable (HTTP " + code + "). Check console for details.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to connect to AI service: " + e.getMessage();
        }
    }
}
