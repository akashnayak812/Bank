package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Logout Servlet
 * Handles user logout and session cleanup
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get current session
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Get user info before invalidating
            String email = (String) session.getAttribute("email");
            
            // Invalidate session - removes all attributes
            session.invalidate();
            
            System.out.println("User logged out: " + email);
        }
        
        // Redirect to login page with logout message
        response.sendRedirect("login.jsp?logout=true");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
