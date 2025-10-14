package servlets;

import dao.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Dashboard Servlet
 * Displays user dashboard with updated account information
 */
@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    
    private AccountDAO accountDAO;
    
    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            // User not logged in - redirect to login page
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Check if account exists in session
        if (session.getAttribute("accountId") == null) {
            // No account - redirect to add account page
            response.sendRedirect("addAccount.jsp");
            return;
        }
        
        // Get account ID from session
        int accountId = (Integer) session.getAttribute("accountId");
        
        // Fetch updated balance from database
        double currentBalance = accountDAO.getBalance(accountId);
        
        // Update session with latest balance
        session.setAttribute("balance", currentBalance);
        
        // Forward to dashboard JSP
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
