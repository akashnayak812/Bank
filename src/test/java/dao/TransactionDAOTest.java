package dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TransactionDAO
 * Tests the transaction logic for withdrawals and transfers
 */
public class TransactionDAOTest {
    
    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    
    @BeforeEach
    public void setUp() {
        transactionDAO = new TransactionDAO();
        accountDAO = new AccountDAO();
    }
    
    /**
     * Test withdrawal functionality
     * This test verifies that:
     * 1. Withdrawal succeeds when balance is sufficient
     * 2. Balance is correctly updated after withdrawal
     */
    @Test
    public void testWithdrawMoney_Success() {
        // Note: This test assumes account with ID 1 exists with sufficient balance
        // In a real scenario, we would set up test data in a @BeforeEach method
        
        int testAccountId = 1;
        double withdrawalAmount = 100.0;
        
        // Get initial balance
        double initialBalance = accountDAO.getBalance(testAccountId);
        
        // Perform withdrawal
        boolean result = transactionDAO.withdrawMoney(testAccountId, withdrawalAmount);
        
        // Verify withdrawal was successful
        assertTrue(result, "Withdrawal should succeed");
        
        // Get updated balance
        double updatedBalance = accountDAO.getBalance(testAccountId);
        
        // Verify balance was correctly updated
        assertEquals(initialBalance - withdrawalAmount, updatedBalance, 0.01, 
                     "Balance should be reduced by withdrawal amount");
    }
    
    /**
     * Test withdrawal with insufficient balance
     * This test verifies that withdrawal fails when balance is insufficient
     */
    @Test
    public void testWithdrawMoney_InsufficientBalance() {
        int testAccountId = 1;
        double withdrawalAmount = 999999.0; // Very large amount
        
        // Get initial balance
        double initialBalance = accountDAO.getBalance(testAccountId);
        
        // Attempt withdrawal
        boolean result = transactionDAO.withdrawMoney(testAccountId, withdrawalAmount);
        
        // Verify withdrawal failed
        assertFalse(result, "Withdrawal should fail with insufficient balance");
        
        // Get updated balance
        double updatedBalance = accountDAO.getBalance(testAccountId);
        
        // Verify balance unchanged
        assertEquals(initialBalance, updatedBalance, 0.01, 
                     "Balance should remain unchanged when withdrawal fails");
    }
    
    /**
     * Test transfer functionality
     * This test verifies that:
     * 1. Transfer succeeds when sender has sufficient balance
     * 2. Sender's balance is reduced by transfer amount
     * 3. Receiver's balance is increased by transfer amount
     */
    @Test
    public void testTransferMoney_Success() {
        // Note: This test assumes accounts with IDs 1 and 2 exist
        int senderAccountId = 1;
        int receiverAccountId = 2;
        double transferAmount = 50.0;
        
        // Get initial balances
        double senderInitialBalance = accountDAO.getBalance(senderAccountId);
        double receiverInitialBalance = accountDAO.getBalance(receiverAccountId);
        
        // Perform transfer
        boolean result = transactionDAO.transferMoney(senderAccountId, receiverAccountId, transferAmount);
        
        // Verify transfer was successful
        assertTrue(result, "Transfer should succeed");
        
        // Get updated balances
        double senderUpdatedBalance = accountDAO.getBalance(senderAccountId);
        double receiverUpdatedBalance = accountDAO.getBalance(receiverAccountId);
        
        // Verify sender's balance was reduced
        assertEquals(senderInitialBalance - transferAmount, senderUpdatedBalance, 0.01, 
                     "Sender's balance should be reduced by transfer amount");
        
        // Verify receiver's balance was increased
        assertEquals(receiverInitialBalance + transferAmount, receiverUpdatedBalance, 0.01, 
                     "Receiver's balance should be increased by transfer amount");
    }
    
    /**
     * Test transfer with insufficient balance
     * This test verifies that transfer fails when sender has insufficient balance
     */
    @Test
    public void testTransferMoney_InsufficientBalance() {
        int senderAccountId = 1;
        int receiverAccountId = 2;
        double transferAmount = 999999.0; // Very large amount
        
        // Get initial balances
        double senderInitialBalance = accountDAO.getBalance(senderAccountId);
        double receiverInitialBalance = accountDAO.getBalance(receiverAccountId);
        
        // Attempt transfer
        boolean result = transactionDAO.transferMoney(senderAccountId, receiverAccountId, transferAmount);
        
        // Verify transfer failed
        assertFalse(result, "Transfer should fail with insufficient balance");
        
        // Get updated balances
        double senderUpdatedBalance = accountDAO.getBalance(senderAccountId);
        double receiverUpdatedBalance = accountDAO.getBalance(receiverAccountId);
        
        // Verify both balances unchanged
        assertEquals(senderInitialBalance, senderUpdatedBalance, 0.01, 
                     "Sender's balance should remain unchanged when transfer fails");
        assertEquals(receiverInitialBalance, receiverUpdatedBalance, 0.01, 
                     "Receiver's balance should remain unchanged when transfer fails");
    }
    
    /**
     * Test account exists check
     */
    @Test
    public void testAccountExists() {
        // Test with existing account
        boolean exists = accountDAO.accountExists(1);
        assertTrue(exists, "Account 1 should exist in the test database");
        
        // Test with non-existing account
        boolean notExists = accountDAO.accountExists(99999);
        assertFalse(notExists, "Account 99999 should not exist");
    }
}
