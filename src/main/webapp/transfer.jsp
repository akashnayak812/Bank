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
    <title>Transfer Money - Online Banking</title>
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
            <a href="TransactionHistoryServlet">Transactions</a>
            <a href="LogoutServlet" class="logout-btn">Logout</a>
        </div>
    </div>
    
    <div class="container">
        <h2>💸 Transfer Money</h2>
        
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
        
        <form action="TransferServlet" method="post" class="form transfer-form">
            <div class="form-group">
                <label for="senderAccount">From Account</label>
                <input type="text" id="senderAccount" value="<%= accountNumber %>" readonly class="readonly-input">
            </div>
            
            <div class="form-group">
                <label for="receiverAccountId">To Account Number</label>
                <input type="number" id="receiverAccountId" name="receiverAccountId" 
                       placeholder="Enter receiver's account number" required min="1">
            </div>
            
            <div class="form-group">
                <label for="amount">Amount (₹)</label>
                <input type="number" id="amount" name="amount" 
                       placeholder="Enter amount to transfer" 
                       required min="0.01" step="0.01" max="<%= balance %>">
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Transfer</button>
                <a href="DashboardServlet" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
        
        <!-- Transfer Guidelines -->
        <div class="info-box">
            <h4>📌 Transfer Guidelines</h4>
            <ul>
                <li>Ensure you have sufficient balance before transferring</li>
                <li>Double-check the receiver's account number</li>
                <li>Minimum transfer amount: ₹0.01</li>
                <li>Transfers are processed instantly</li>
                <li>You cannot transfer to your own account</li>
            </ul>
        </div>
    </div>
    
    <script>
        // Validate amount before submission
        document.querySelector('.transfer-form').addEventListener('submit', function(e) {
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
