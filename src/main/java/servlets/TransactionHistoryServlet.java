package servlets;

import dao.TransactionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Transaction History Servlet
 * Displays user's transaction history
 */
@WebServlet("/TransactionHistoryServlet")
public class TransactionHistoryServlet extends HttpServlet {
    
    private TransactionDAO transactionDAO;
    
    @Override
    public void init() throws ServletException {
        transactionDAO = new TransactionDAO();
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
        
        // Get account ID from session
        int accountId = (Integer) session.getAttribute("accountId");
        
        // Fetch transaction history
        List<Map<String, Object>> transactions = transactionDAO.getTransactionHistory(accountId);
        
        // Set transactions as request attribute
        request.setAttribute("transactions", transactions);
        
        // Forward to transactions JSP
        request.getRequestDispatcher("transactions.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
