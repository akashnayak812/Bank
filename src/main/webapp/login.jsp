<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Online Banking</title>
    <link rel="stylesheet" href="css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="logo-section">
            <h1>🏦 Online Banking</h1>
            <p class="tagline">Secure. Simple. Smart.</p>
        </div>
        
        <h2>Welcome Back!</h2>
        
        <!-- Display logout message -->
        <% if (request.getParameter("logout") != null) { %>
            <div class="alert alert-success">
                ✓ You have been logged out successfully.
            </div>
        <% } %>
        
        <!-- Display error message -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                ✗ <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <!-- Display success message -->
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                ✓ <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <form action="LoginServlet" method="post" class="form">
            <div class="form-group">
                <label for="email">📧 Email Address</label>
                <input type="email" id="email" name="email" placeholder="Enter your email" required>
            </div>
            
            <div class="form-group">
                <label for="password">🔒 Password</label>
                <input type="password" id="password" name="password" placeholder="Enter your password" required>
            </div>
            
            <button type="submit" class="btn btn-primary">Login</button>
        </form>
        
        <div class="footer-text">
            <p>Don't have an account? <a href="signup.jsp">Sign up here</a></p>
        </div>
    </div>
</body>
</html>
