package servlets;

import dao.TransactionDAO;
import dao.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Withdraw Servlet
 * Handles money withdrawal from user account
 * Implements secure withdrawal processing with validation
 */
@WebServlet("/WithdrawServlet")
public class WithdrawServlet extends HttpServlet {
    
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
        
        // Get user's account ID from session
        int accountId = (Integer) session.getAttribute("accountId");
        
        try {
            // Get withdrawal amount from form
            String amountStr = request.getParameter("amount");
            
            // Validation: Check if field is empty
            if (amountStr == null || amountStr.trim().isEmpty()) {
                request.setAttribute("error", "Amount is required!");
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
                return;
            }
            
            double amount = Double.parseDouble(amountStr);
            
            // Validation: Check for negative or zero amount
            if (amount <= 0) {
                request.setAttribute("error", "Amount must be greater than zero!");
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
                return;
            }
            
            // Validation: Check if user has sufficient balance
            double accountBalance = accountDAO.getBalance(accountId);
            if (accountBalance < amount) {
                request.setAttribute("error", "Insufficient balance! Available: ₹" + accountBalance);
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
                return;
            }
            
            // Perform the withdrawal (ATOMIC TRANSACTION)
            boolean withdrawalSuccess = transactionDAO.withdrawMoney(accountId, amount);
            
            if (withdrawalSuccess) {
                // Update session balance
                double newBalance = accountDAO.getBalance(accountId);
                session.setAttribute("balance", newBalance);
                
                // Success message
                request.setAttribute("success", "Withdrawal successful! Amount: ₹" + amount + " has been withdrawn from your account.");
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
                
            } else {
                // Withdrawal failed
                request.setAttribute("error", "Withdrawal failed! Please try again.");
                request.getRequestDispatcher("withdraw.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            // Invalid number format
            request.setAttribute("error", "Invalid amount!");
            request.getRequestDispatcher("withdraw.jsp").forward(request, response);
            
        } catch (Exception e) {
            // General error
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during withdrawal. Please try again.");
            request.getRequestDispatcher("withdraw.jsp").forward(request, response);
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
        
        // Forward to withdraw page
        request.getRequestDispatcher("withdraw.jsp").forward(request, response);
    }
}
