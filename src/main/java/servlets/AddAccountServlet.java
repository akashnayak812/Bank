package servlets;

import dao.AccountDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Add Account Servlet
 * Handles account creation for new users
 */
@WebServlet("/AddAccountServlet")
public class AddAccountServlet extends HttpServlet {
    
    private AccountDAO accountDAO;
    
    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            int userId = (int) session.getAttribute("userId");
            double initialBalance = Double.parseDouble(request.getParameter("balance"));
            
            // Validate initial balance
            if (initialBalance < 0) {
                request.setAttribute("error", "Initial balance cannot be negative!");
                request.getRequestDispatcher("addAccount.jsp").forward(request, response);
                return;
            }
            
            // Create new account
            int accountId = accountDAO.createAccount(userId, initialBalance);
            
            if (accountId > 0) {
                // Update session with account details
                session.setAttribute("accountId", accountId);
                session.setAttribute("balance", initialBalance);
                
                response.sendRedirect("DashboardServlet");
            } else {
                request.setAttribute("error", "Failed to create account. Please try again.");
                request.getRequestDispatcher("addAccount.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Please enter a valid amount!");
            request.getRequestDispatcher("addAccount.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("addAccount.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
}
