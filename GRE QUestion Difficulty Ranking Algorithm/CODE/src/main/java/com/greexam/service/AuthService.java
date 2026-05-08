package com.greexam.service;

import com.greexam.dao.UserDAO;
import com.greexam.model.User;
import com.greexam.util.PasswordUtil;

/**
 * Authentication service handling login, registration, and session management.
 */
public class AuthService {

    private static AuthService instance;
    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Attempt login with username and password.
     * @return authenticated User or null if failed
     */
    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            this.currentUser = user;
            return user;
        }
        return null;
    }

    /**
     * Register a new user.
     * @return true if registration successful
     */
    public boolean register(String name, String username, String password, String role,
                            String email, String secretQuestion, String secretAnswer) {
        if (userDAO.usernameExists(username)) {
            return false;
        }

        User user = new User(name, username, PasswordUtil.hashPassword(password), role, email);
        user.setSecretQuestion(secretQuestion);
        user.setSecretAnswer(PasswordUtil.hashPassword(secretAnswer.toLowerCase().trim()));
        return userDAO.insert(user);
    }

    /**
     * Reset password via secret question verification.
     * @return true if reset successful
     */
    public boolean resetPassword(String username, String secretAnswer, String newPassword) {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            String hashedAnswer = PasswordUtil.hashPassword(secretAnswer.toLowerCase().trim());
            if (hashedAnswer.equals(user.getSecretAnswer())) {
                return userDAO.updatePassword(user.getId(), PasswordUtil.hashPassword(newPassword));
            }
        }
        return false;
    }

    /**
     * Get the secret question for a username.
     */
    public String getSecretQuestion(String username) {
        User user = userDAO.findByUsername(username);
        return user != null ? user.getSecretQuestion() : null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isTeacher() {
        return currentUser != null && currentUser.isTeacher();
    }

    public boolean isStudent() {
        return currentUser != null && currentUser.isStudent();
    }
}
