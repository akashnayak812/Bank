<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, java.text.SimpleDateFormat" %>
<%@ page session="true" %>
<%
    // Check if user is logged in
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String name = (String) session.getAttribute("name");
    Integer accountNumber = (Integer) session.getAttribute("accountNumber");
    List<Map<String, Object>> transactions = (List<Map<String, Object>>) request.getAttribute("transactions");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction History - Online Banking</title>
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
    
    <div class="container-wide">
        <h2>📊 Transaction History</h2>
        
        <div class="account-info-bar">
            <p>Account Number: <strong><%= accountNumber %></strong></p>
        </div>
        
        <% if (transactions != null && !transactions.isEmpty()) { %>
            <div class="table-container">
                <table class="transaction-table">
                    <thead>
                        <tr>
                            <th>Transaction ID</th>
                            <th>Date & Time</th>
                            <th>Type</th>
                            <th>From Account</th>
                            <th>To Account</th>
                            <th>Amount</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, Object> txn : transactions) { 
                            String type = (String) txn.get("type");
                            String typeClass = "WITHDRAWAL".equals(type) ? "type-withdrawal" : (type.equals("SENT") ? "type-sent" : "type-received");
                            String amountClass = "WITHDRAWAL".equals(type) ? "amount-negative" : (type.equals("SENT") ? "amount-negative" : "amount-positive");
                            String amountSign = "WITHDRAWAL".equals(type) ? "- ₹" : (type.equals("SENT") ? "- ₹" : "+ ₹");
                        %>
                            <tr>
                                <td>#<%= txn.get("txnId") %></td>
                                <td><%= dateFormat.format(txn.get("date")) %></td>
                                <td><span class="badge <%= typeClass %>"><%= type %></span></td>
                                <td><%= txn.get("senderAccount") %></td>
                                <td><%= txn.get("receiverAccount") %></td>
                                <td class="<%= amountClass %>">
                                    <%= amountSign %><%= String.format("%.2f", txn.get("amount")) %>
                                </td>
                                <td><span class="badge badge-success"><%= txn.get("status") %></span></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            
            <div class="transaction-summary">
                <p>Showing <%= transactions.size() %> most recent transactions</p>
            </div>
        <% } else { %>
            <div class="empty-state">
                <div class="empty-icon">📭</div>
                <h3>No Transactions Yet</h3>
                <p>You haven't made any transactions yet. Start by transferring money!</p>
                <a href="TransferServlet" class="btn btn-primary">Make a Transfer</a>
            </div>
        <% } %>
        
        <div class="back-link">
            <a href="DashboardServlet">← Back to Dashboard</a>
        </div>
    </div>
</body>
</html>
