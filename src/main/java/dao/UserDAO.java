package dao;

import util.DBConnection;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User Data Access Object
 * Handles all user-related database operations
 */
public class UserDAO {
    
    /**
     * Hash password using SHA-256
     * @param password Plain text password
     * @return Hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            
            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Register a new user
     * @param name User's full name
     * @param email User's email
     * @param password User's password
     * @return user ID if registration successful, -1 otherwise
     */
    public int registerUser(String name, String email, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Hash the password for security
            String hashedPassword = hashPassword(password);
            
            // Insert user into users table
            String sql = "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated user_id
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    System.out.println("User registered successfully: " + email);
                    return userId;
                }
            }
            
            return -1;
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return -1;
            
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Authenticate user login
     * @param email User's email
     * @param password User's password
     * @return User data as ResultSet if valid, null otherwise
     */
    public ResultSet loginUser(String email, String password) {
        try {
            Connection conn = DBConnection.getConnection();
            
            // Hash the input password
            String hashedPassword = hashPassword(password);
            
            // Query to check credentials - use LEFT JOIN to allow users without accounts
            String sql = "SELECT u.user_id, u.name, u.email, a.account_id, a.balance " +
                        "FROM users u " +
                        "LEFT JOIN accounts a ON u.user_id = a.user_id " +
                        "WHERE u.email = ? AND u.password_hash = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, hashedPassword);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Login successful for: " + email);
                return rs;
            } else {
                System.out.println("Invalid credentials for: " + email);
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
