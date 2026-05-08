package com.greexam.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility class for date-time formatting and conversion.
 */
public class DateTimeUtil {

    public static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");
    public static final DateTimeFormatter DB_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMAT) : "N/A";
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMAT) : "N/A";
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMAT) : "N/A";
    }

    public static String formatForDB(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DB_FORMAT) : null;
    }

    public static LocalDateTime fromTimestamp(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    public static Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    public static LocalDateTime fromDate(Date date) {
        if (date == null) return null;
        return new Timestamp(date.getTime()).toLocalDateTime();
    }

    /**
     * Format seconds into mm:ss or hh:mm:ss display.
     */
    public static String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Format a countdown in a human-readable way.
     */
    public static String formatCountdown(long totalSeconds) {
        if (totalSeconds <= 0) return "00:00";
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long secs = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%dh %02dm %02ds", hours, minutes, secs);
        }
        return String.format("%02d:%02d", minutes, secs);
    }
}
