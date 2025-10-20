package dao;

import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Transaction Data Access Object
 * Handles all transaction-related database operations
 * Implements ACID properties for money transfers
 */
public class TransactionDAO {
    
    /**
     * Transfer money between accounts (ATOMIC TRANSACTION)
     * Uses database transactions to ensure data integrity
     * 
     * @param senderAccountId Sender's account ID
     * @param receiverAccountId Receiver's account ID
     * @param amount Amount to transfer
     * @return true if transfer successful, false otherwise
     */
    public boolean transferMoney(int senderAccountId, int receiverAccountId, double amount) {
        Connection conn = null;
        PreparedStatement pstmtCheckBalance = null;
        PreparedStatement pstmtDebit = null;
        PreparedStatement pstmtCredit = null;
        PreparedStatement pstmtTransaction = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // START TRANSACTION
            conn.setAutoCommit(false);
            
            // Step 1: Check if sender has sufficient balance
            String checkBalanceSql = "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE";
            pstmtCheckBalance = conn.prepareStatement(checkBalanceSql);
            pstmtCheckBalance.setInt(1, senderAccountId);
            rs = pstmtCheckBalance.executeQuery();
            
            if (!rs.next()) {
                System.err.println("Sender account not found!");
                conn.rollback();
                return false;
            }
            
            double senderBalance = rs.getDouble("balance");
            
            // Validate sufficient balance
            if (senderBalance < amount) {
                System.err.println("Insufficient balance! Available: " + senderBalance);
                conn.rollback();
                return false;
            }
            
            // Step 2: Debit from sender's account
            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            pstmtDebit = conn.prepareStatement(debitSql);
            pstmtDebit.setDouble(1, amount);
            pstmtDebit.setInt(2, senderAccountId);
            int debitRows = pstmtDebit.executeUpdate();
            
            if (debitRows == 0) {
                System.err.println("Failed to debit sender account!");
                conn.rollback();
                return false;
            }
            
            // Step 3: Credit to receiver's account
            String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            pstmtCredit = conn.prepareStatement(creditSql);
            pstmtCredit.setDouble(1, amount);
            pstmtCredit.setInt(2, receiverAccountId);
            int creditRows = pstmtCredit.executeUpdate();
            
            if (creditRows == 0) {
                System.err.println("Failed to credit receiver account!");
                conn.rollback();
                return false;
            }
            
            // Step 4: Record transaction
            String transactionSql = "INSERT INTO transactions (sender_account, receiver_account, amount, status, transaction_type) " +
                                   "VALUES (?, ?, ?, 'SUCCESS', 'TRANSFER')";
            pstmtTransaction = conn.prepareStatement(transactionSql);
            pstmtTransaction.setInt(1, senderAccountId);
            pstmtTransaction.setInt(2, receiverAccountId);
            pstmtTransaction.setDouble(3, amount);
            pstmtTransaction.executeUpdate();
            
            // COMMIT TRANSACTION - All operations successful
            conn.commit();
            System.out.println("Transaction successful! Amount: " + amount);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Transaction failed: " + e.getMessage());
            e.printStackTrace();
            
            // ROLLBACK on error - Undo all changes
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to error.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
            
        } finally {
            // Close all resources
            try {
                if (rs != null) rs.close();
                if (pstmtTransaction != null) pstmtTransaction.close();
                if (pstmtCredit != null) pstmtCredit.close();
                if (pstmtDebit != null) pstmtDebit.close();
                if (pstmtCheckBalance != null) pstmtCheckBalance.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Withdraw money from an account (ATOMIC TRANSACTION)
     * Uses database transactions to ensure data integrity
     * 
     * @param accountId Account ID to withdraw from
     * @param amount Amount to withdraw
     * @return true if withdrawal successful, false otherwise
     */
    public boolean withdrawMoney(int accountId, double amount) {
        Connection conn = null;
        PreparedStatement pstmtCheckBalance = null;
        PreparedStatement pstmtDebit = null;
        PreparedStatement pstmtTransaction = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // START TRANSACTION
            conn.setAutoCommit(false);
            
            // Step 1: Check if account has sufficient balance
            String checkBalanceSql = "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE";
            pstmtCheckBalance = conn.prepareStatement(checkBalanceSql);
            pstmtCheckBalance.setInt(1, accountId);
            rs = pstmtCheckBalance.executeQuery();
            
            if (!rs.next()) {
                System.err.println("Account not found!");
                conn.rollback();
                return false;
            }
            
            double accountBalance = rs.getDouble("balance");
            
            // Validate sufficient balance
            if (accountBalance < amount) {
                System.err.println("Insufficient balance! Available: " + accountBalance);
                conn.rollback();
                return false;
            }
            
            // Step 2: Debit from account
            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            pstmtDebit = conn.prepareStatement(debitSql);
            pstmtDebit.setDouble(1, amount);
            pstmtDebit.setInt(2, accountId);
            int debitRows = pstmtDebit.executeUpdate();
            
            if (debitRows == 0) {
                System.err.println("Failed to debit account!");
                conn.rollback();
                return false;
            }
            
            // Step 3: Record transaction (sender and receiver are the same for withdrawal)
            String transactionSql = "INSERT INTO transactions (sender_account, receiver_account, amount, status, transaction_type) " +
                                   "VALUES (?, ?, ?, 'SUCCESS', 'WITHDRAWAL')";
            pstmtTransaction = conn.prepareStatement(transactionSql);
            pstmtTransaction.setInt(1, accountId);
            pstmtTransaction.setInt(2, accountId);
            pstmtTransaction.setDouble(3, amount);
            pstmtTransaction.executeUpdate();
            
            // COMMIT TRANSACTION - All operations successful
            conn.commit();
            System.out.println("Withdrawal successful! Amount: " + amount);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Withdrawal failed: " + e.getMessage());
            e.printStackTrace();
            
            // ROLLBACK on error - Undo all changes
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to error.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
            
        } finally {
            // Close all resources
            try {
                if (rs != null) rs.close();
                if (pstmtTransaction != null) pstmtTransaction.close();
                if (pstmtDebit != null) pstmtDebit.close();
                if (pstmtCheckBalance != null) pstmtCheckBalance.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get transaction history for a user's account
     * @param accountId Account ID
     * @return List of transactions
     */
    public List<Map<String, Object>> getTransactionHistory(int accountId) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Query to get all transactions where user is sender or receiver
            String sql = "SELECT txn_id, sender_account, receiver_account, amount, txn_date, status, transaction_type " +
                        "FROM transactions " +
                        "WHERE sender_account = ? OR receiver_account = ? " +
                        "ORDER BY txn_date DESC " +
                        "LIMIT 50";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            pstmt.setInt(2, accountId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("txnId", rs.getInt("txn_id"));
                transaction.put("senderAccount", rs.getInt("sender_account"));
                transaction.put("receiverAccount", rs.getInt("receiver_account"));
                transaction.put("amount", rs.getDouble("amount"));
                transaction.put("date", rs.getTimestamp("txn_date"));
                transaction.put("status", rs.getString("status"));
                
                String transactionType = rs.getString("transaction_type");
                transaction.put("transactionType", transactionType);
                
                // Determine transaction type (Sent, Received, or Withdrawal)
                if ("WITHDRAWAL".equals(transactionType)) {
                    transaction.put("type", "WITHDRAWAL");
                } else if (rs.getInt("sender_account") == accountId) {
                    transaction.put("type", "SENT");
                } else {
                    transaction.put("type", "RECEIVED");
                }
                
                transactions.add(transaction);
            }
            
            System.out.println("Retrieved " + transactions.size() + " transactions for account: " + accountId);
            
        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
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
        
        return transactions;
    }
    
    /**
     * Record a failed transaction
     * @param senderAccountId Sender account ID
     * @param receiverAccountId Receiver account ID
     * @param amount Amount attempted
     * @param reason Failure reason
     */
    public void recordFailedTransaction(int senderAccountId, int receiverAccountId, double amount, String reason) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO transactions (sender_account, receiver_account, amount, status, transaction_type) " +
                        "VALUES (?, ?, ?, ?, 'TRANSFER')";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, senderAccountId);
            pstmt.setInt(2, receiverAccountId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, "FAILED: " + reason);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error recording failed transaction: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
