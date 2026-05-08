package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password hashing and strength utilities.
 */
public class PasswordUtils {

    /** SHA-256 hash — matches MySQL SHA2(val,256) */
    public static String hash(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plainText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /** Returns 0-100 strength score */
    public static int strength(String password) {
        if (password == null || password.isEmpty()) return 0;
        int score = 0;
        if (password.length() >= 6)  score += 20;
        if (password.length() >= 10) score += 20;
        if (password.matches(".*[A-Z].*")) score += 20;
        if (password.matches(".*[0-9].*")) score += 20;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score += 20;
        return score;
    }

    public static String strengthLabel(int score) {
        if (score <= 20) return "Very Weak";
        if (score <= 40) return "Weak";
        if (score <= 60) return "Medium";
        if (score <= 80) return "Strong";
        return "Very Strong";
    }

    public static java.awt.Color strengthColor(int score) {
        if (score <= 20) return UIUtils.DANGER;
        if (score <= 40) return new java.awt.Color(0xFF, 0x69, 0x00);
        if (score <= 60) return UIUtils.WARNING;
        if (score <= 80) return new java.awt.Color(0x27, 0xAE, 0x60);
        return new java.awt.Color(0x1A, 0x8A, 0x4A);
    }
}
