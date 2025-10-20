package dao;

import util.DBConnection;
import java.sql.*;

/**
 * Account Data Access Object
 * Handles all account-related database operations
 */
public class AccountDAO {
    
    /**
     * Get account balance
     * @param accountId Account ID
     * @return Current balance
     */
    public double getBalance(int accountId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT balance FROM accounts WHERE account_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting balance: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }
    
    /**
     * Update account balance
     * @param accountId Account ID
     * @param newBalance New balance amount
     * @return true if update successful
     */
    public boolean updateBalance(int accountId, double newBalance) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Check if account exists
     * @param accountId Account ID to check
     * @return true if exists, false otherwise
     */
    public boolean accountExists(int accountId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM accounts WHERE account_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking account: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * Get account ID by user ID
     * @param userId User ID
     * @return Account ID
     */
    public int getAccountIdByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT account_id FROM accounts WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("account_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting account ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    
    /**
     * Get account by user ID
     * @param userId User ID
     * @return ResultSet with account details or null
     */
    public ResultSet getAccountByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT account_id, balance FROM accounts WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            return pstmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Error getting account: " + e.getMessage());
            e.printStackTrace();
            // Clean up resources on error
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Create new account for user
     * @param userId User ID
     * @param initialBalance Initial balance
     * @return Account ID of newly created account, or -1 if failed
     */
    public int createAccount(int userId, double initialBalance) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO accounts (user_id, balance) VALUES (?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, initialBalance);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * Deposit funds into an account
     * @param accountId Account ID
     * @param amount Amount to deposit
     * @return true if deposit successful
     */
    public boolean deposit(int accountId, double amount) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Lock the row and get current balance
            String lockSql = "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE";
            pstmt = conn.prepareStatement(lockSql);
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                double newBalance = currentBalance + amount;
                
                // Update the balance
                String updateSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setDouble(1, newBalance);
                pstmt.setInt(2, accountId);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction
                    return true;
                }
            }
            
            conn.rollback(); // Rollback if something went wrong
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error during deposit: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
