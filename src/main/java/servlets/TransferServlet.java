package servlets;

import dao.TransactionDAO;
import dao.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Transfer Servlet
 * Handles money transfer between accounts
 * Implements secure transaction processing with validation
 */
@WebServlet("/TransferServlet")
public class TransferServlet extends HttpServlet {
    
    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    
    @Override
    public void init() throws ServletException {
        transactionDAO = new TransactionDAO();
        accountDAO = new AccountDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Get sender's account ID from session
        int senderAccountId = (Integer) session.getAttribute("accountId");
        
        try {
            // Get transfer details from form
            String receiverAccountIdStr = request.getParameter("receiverAccountId");
            String amountStr = request.getParameter("amount");
            
            // Validation: Check if fields are empty
            if (receiverAccountIdStr == null || receiverAccountIdStr.trim().isEmpty() ||
                amountStr == null || amountStr.trim().isEmpty()) {
                
                request.setAttribute("error", "All fields are required!");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                return;
            }
            
            int receiverAccountId = Integer.parseInt(receiverAccountIdStr);
            double amount = Double.parseDouble(amountStr);
            
            // Validation: Check for negative or zero amount
            if (amount <= 0) {
                request.setAttribute("error", "Amount must be greater than zero!");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                return;
            }
            
            // Validation: Check if sender is trying to transfer to own account
            if (senderAccountId == receiverAccountId) {
                request.setAttribute("error", "Cannot transfer to your own account!");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                return;
            }
            
            // Validation: Check if receiver account exists
            if (!accountDAO.accountExists(receiverAccountId)) {
                request.setAttribute("error", "Receiver account does not exist!");
                transactionDAO.recordFailedTransaction(senderAccountId, receiverAccountId, amount, "Invalid receiver account");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                return;
            }
            
            // Validation: Check if sender has sufficient balance
            double senderBalance = accountDAO.getBalance(senderAccountId);
            if (senderBalance < amount) {
                request.setAttribute("error", "Insufficient balance! Available: ₹" + senderBalance);
                transactionDAO.recordFailedTransaction(senderAccountId, receiverAccountId, amount, "Insufficient balance");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                return;
            }
            
            // Perform the transfer (ATOMIC TRANSACTION)
            boolean transferSuccess = transactionDAO.transferMoney(senderAccountId, receiverAccountId, amount);
            
            if (transferSuccess) {
                // Update session balance
                double newBalance = accountDAO.getBalance(senderAccountId);
                session.setAttribute("balance", newBalance);
                
                // Success message
                request.setAttribute("success", "Transfer successful! Amount: ₹" + amount + " transferred to Account #" + receiverAccountId);
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
                
            } else {
                // Transfer failed
                request.setAttribute("error", "Transfer failed! Please try again.");
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            // Invalid number format
            request.setAttribute("error", "Invalid account number or amount!");
            request.getRequestDispatcher("transfer.jsp").forward(request, response);
            
        } catch (Exception e) {
            // General error
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during transfer. Please try again.");
            request.getRequestDispatcher("transfer.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Forward to transfer page
        request.getRequestDispatcher("transfer.jsp").forward(request, response);
    }
}
