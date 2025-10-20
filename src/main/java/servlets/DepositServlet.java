package servlets;

import dao.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/DepositServlet")
public class DepositServlet extends HttpServlet {
    
    private AccountDAO accountDAO;
    
    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Forward to the deposit page
        request.getRequestDispatcher("deposit.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            int accountId = (Integer) session.getAttribute("accountId");
            String amountStr = request.getParameter("amount");
            
            if (amountStr == null || amountStr.trim().isEmpty()) {
                request.setAttribute("error", "Deposit amount is required.");
                request.getRequestDispatcher("deposit.jsp").forward(request, response);
                return;
            }
            
            BigDecimal amount = new BigDecimal(amountStr);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("error", "Deposit amount must be positive.");
                request.getRequestDispatcher("deposit.jsp").forward(request, response);
                return;
            }
            
            boolean success = accountDAO.deposit(accountId, amount.doubleValue());
            
            if (success) {
                // Update balance in session and redirect to dashboard
                double newBalance = accountDAO.getBalance(accountId);
                session.setAttribute("balance", newBalance);
                response.sendRedirect("DashboardServlet");
            } else {
                request.setAttribute("error", "Deposit failed. Please try again.");
                request.getRequestDispatcher("deposit.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid amount format.");
            request.getRequestDispatcher("deposit.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An unexpected error occurred.");
            request.getRequestDispatcher("deposit.jsp").forward(request, response);
        }
    }
}
