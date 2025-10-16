package servlets;

import dao.UserDAO;
import dao.AccountDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Signup Servlet
 * Handles user registration
 */
@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String initialBalanceStr = request.getParameter("initialBalance");
        
        // Validation
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            initialBalanceStr == null || initialBalanceStr.trim().isEmpty()) {
            
            request.setAttribute("error", "All fields are required!");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }
        
        // Validate initial balance
        double initialBalance;
        try {
            initialBalance = Double.parseDouble(initialBalanceStr);
            if (initialBalance < 0) {
                request.setAttribute("error", "Initial balance cannot be negative!");
                request.getRequestDispatcher("signup.jsp").forward(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid initial balance amount!");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match!");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }
        
        // Check password strength (minimum 6 characters)
        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters long!");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            request.setAttribute("error", "Email already registered! Please login.");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
            return;
        }
        
        // Register user
        int userId = userDAO.registerUser(name, email, password);
        
        if (userId > 0) {
            // Create account with initial balance
            AccountDAO accountDAO = new AccountDAO();
            int accountId = accountDAO.createAccount(userId, initialBalance);
            
            if (accountId > 0) {
                // Registration and account creation successful
                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("name", name);
                session.setAttribute("email", email);
                session.setAttribute("accountId", accountId);
                session.setAttribute("balance", initialBalance);
                session.setMaxInactiveInterval(30 * 60);
                
                System.out.println("User registered and account created: " + email + " with balance: ₹" + initialBalance);
                
                // Redirect to dashboard
                response.sendRedirect("DashboardServlet");
            } else {
                // Account creation failed - should rollback user creation but for now show error
                request.setAttribute("error", "Account creation failed! Please try again.");
                request.getRequestDispatcher("signup.jsp").forward(request, response);
            }
        } else {
            // Registration failed
            request.setAttribute("error", "Registration failed! Please try again.");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect GET requests to signup page
        response.sendRedirect("signup.jsp");
    }
}
