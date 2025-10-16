package servlets;

import dao.UserDAO;
import dao.AccountDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Login Servlet
 * Handles user authentication and session management
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get login credentials from form
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validation
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("error", "Email and password are required!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate user
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Email: " + email);
        System.out.println("Password length: " + password.length());
        
        ResultSet rs = userDAO.loginUser(email, password);
        
        try {
            if (rs != null) {
                System.out.println("Login successful - ResultSet is not null");
                // Login successful - Create session
                HttpSession session = request.getSession();
                
                // Store user information in session
                int userId = rs.getInt("user_id");
                session.setAttribute("userId", userId);
                session.setAttribute("name", rs.getString("name"));
                session.setAttribute("email", rs.getString("email"));
                
                // Set session timeout (30 minutes)
                session.setMaxInactiveInterval(30 * 60);
                
                System.out.println("Session created for user: " + email);
                
                // Check if account exists for this user
                AccountDAO accountDAO = new AccountDAO();
                ResultSet accRs = accountDAO.getAccountByUserId(userId);
                
                if (accRs != null && accRs.next()) {
                    // Account exists - set session and redirect to dashboard
                    session.setAttribute("accountId", accRs.getInt("account_id"));
                    session.setAttribute("accountNumber", accRs.getInt("account_id"));
                    session.setAttribute("balance", accRs.getDouble("balance"));
                    response.sendRedirect("DashboardServlet");
                } else {
                    // No account - redirect to add account page
                    response.sendRedirect("addAccount.jsp");
                }
                
            } else {
                // Login failed
                request.setAttribute("error", "Invalid email or password!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred. Please try again.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect GET requests to login page
        response.sendRedirect("login.jsp");
    }
}
