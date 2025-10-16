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
    <title>Dashboard - Online Banking</title>
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
    
    <div class="dashboard-container">
        <h2>Your Account Dashboard</h2>
        
        <!-- Account Summary Card -->
        <div class="card account-card">
            <div class="card-header">
                <h3>💳 Account Summary</h3>
            </div>
            <div class="card-body">
                <div class="info-row">
                    <span class="info-label">Account Number:</span>
                    <span class="info-value"><%= accountNumber %></span>
                </div>
                <div class="info-row">
                    <span class="info-label">Account Holder:</span>
                    <span class="info-value"><%= name %></span>
                </div>
                <div class="info-row balance-row">
                    <span class="info-label">Current Balance:</span>
                    <span class="info-value balance-amount">₹<%= String.format("%.2f", balance) %></span>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="quick-actions">
            <h3>Quick Actions</h3>
            <div class="action-buttons">
                <a href="TransferServlet" class="action-btn">
                    <div class="action-icon">💸</div>
                    <div class="action-text">Transfer Money</div>
                </a>
                <a href="WithdrawServlet" class="action-btn">
                    <div class="action-icon">💰</div>
                    <div class="action-text">Withdraw Money</div>
                </a>
                <a href="TransactionHistoryServlet" class="action-btn">
                    <div class="action-icon">📊</div>
                    <div class="action-text">View Transactions</div>
                </a>
                <a href="DashboardServlet" class="action-btn">
                    <div class="action-icon">🔄</div>
                    <div class="action-text">Refresh Balance</div>
                </a>
            </div>
        </div>
        
        <!-- Security Notice -->
        <div class="security-notice">
            <p>🔒 <strong>Security Tip:</strong> Never share your password or account details with anyone.</p>
        </div>
    </div>
</body>
</html>
