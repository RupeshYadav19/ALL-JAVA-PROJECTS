package utils;

import java.util.regex.Pattern;

/**
 * Input validation helpers.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[6-9]\\d{9}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isPositiveNumber(String s) {
        try { return Double.parseDouble(s) > 0; }
        catch (NumberFormatException e) { return false; }
    }

    public static boolean isValidDate(String date) {
        try {
            java.time.LocalDate.parse(date);
            return true;
        } catch (Exception e) { return false; }
    }

    public static String requireNonEmpty(String value, String fieldName) {
        if (!isNotEmpty(value)) throw new IllegalArgumentException(fieldName + " is required.");
        return value.trim();
    }
}
