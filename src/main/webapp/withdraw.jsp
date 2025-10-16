<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%
    // Check if user is logged in
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String name = (String) session.getAttribute("name");
    Integer accountNumber = (Integer) session.getAttribute("accountNumber");
    Double balance = (Double) session.getAttribute("balance");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Withdraw Money - Online Banking</title>
    <link rel="stylesheet" href="css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <!-- Navigation Bar -->
    <div class="navbar">
        <div class="nav-brand">🏦 Online Banking</div>
        <div class="nav-user">
            Welcome, <strong><%= name %></strong> 👋
        </div>
        <div class="nav-links">
            <a href="DashboardServlet">Dashboard</a>
            <a href="TransferServlet">Transfer Money</a>
            <a href="WithdrawServlet">Withdraw</a>
            <a href="TransactionHistoryServlet">Transactions</a>
            <a href="LogoutServlet" class="logout-btn">Logout</a>
        </div>
    </div>
    
    <div class="container">
        <h2>💰 Withdraw Money</h2>
        
        <!-- Current Balance Display -->
        <div class="balance-display">
            <p>Your Available Balance: <strong>₹<%= String.format("%.2f", balance) %></strong></p>
        </div>
        
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
        
        <form action="WithdrawServlet" method="post" class="form withdraw-form">
            <div class="form-group">
                <label for="accountNumber">Account Number</label>
                <input type="text" id="accountNumber" value="<%= accountNumber %>" readonly class="readonly-input">
            </div>
            
            <div class="form-group">
                <label for="amount">Withdrawal Amount (₹)</label>
                <input type="number" id="amount" name="amount" 
                       placeholder="Enter amount to withdraw" 
                       required min="0.01" step="0.01" max="<%= balance %>">
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Withdraw</button>
                <a href="DashboardServlet" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
        
        <!-- Withdrawal Guidelines -->
        <div class="info-box">
            <h4>📌 Withdrawal Guidelines</h4>
            <ul>
                <li>Ensure you have sufficient balance before withdrawing</li>
                <li>Minimum withdrawal amount: ₹0.01</li>
                <li>Withdrawals are processed instantly</li>
                <li>Your balance will be updated immediately</li>
            </ul>
        </div>
    </div>
    
    <script>
        // Validate amount before submission
        document.querySelector('.withdraw-form').addEventListener('submit', function(e) {
            var amount = parseFloat(document.getElementById('amount').value);
            var balance = <%= balance %>;
            
            if (amount <= 0) {
                e.preventDefault();
                alert('Amount must be greater than zero!');
            } else if (amount > balance) {
                e.preventDefault();
                alert('Insufficient balance! Available: ₹' + balance.toFixed(2));
            }
        });
    </script>
</body>
</html>
