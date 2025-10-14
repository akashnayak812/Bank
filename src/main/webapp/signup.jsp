<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up - Online Banking</title>
    <link rel="stylesheet" href="css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="logo-section">
            <h1>🏦 Online Banking</h1>
            <p class="tagline">Join us today!</p>
        </div>
        
        <h2>Create Your Account</h2>
        
        <!-- Display error message -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                ✗ <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <form action="SignupServlet" method="post" class="form">
            <div class="form-group">
                <label for="name">👤 Full Name</label>
                <input type="text" id="name" name="name" placeholder="Enter your full name" required>
            </div>
            
            <div class="form-group">
                <label for="email">📧 Email Address</label>
                <input type="email" id="email" name="email" placeholder="Enter your email" required>
            </div>
            
            <div class="form-group">
                <label for="password">🔒 Password</label>
                <input type="password" id="password" name="password" placeholder="Create a password (min 6 characters)" required minlength="6">
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">🔒 Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Re-enter your password" required minlength="6">
            </div>
            
            <div class="form-group">
                <label for="initialBalance">💰 Initial Deposit Amount</label>
                <input type="number" id="initialBalance" name="initialBalance" placeholder="Enter initial deposit (e.g., 10000)" required min="0" step="0.01">
                <small style="color: #666; font-size: 0.9em;">Minimum balance required to open account</small>
            </div>
            
            <button type="submit" class="btn btn-primary">Sign Up</button>
        </form>
        
        <div class="footer-text">
            <p>Already have an account? <a href="login.jsp">Login here</a></p>
        </div>
    </div>
    
    <script>
        // Client-side password validation
        document.querySelector('form').addEventListener('submit', function(e) {
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match!');
            }
        });
    </script>
</body>
</html>
