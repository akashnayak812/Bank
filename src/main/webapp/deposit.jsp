<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Deposit - Online Banking</title>
    <link rel="stylesheet" href="css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h2>Make a Deposit</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                ✗ <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <form action="DepositServlet" method="post" class="form">
            <div class="form-group">
                <label for="amount">💰 Amount</label>
                <input type="number" id="amount" name="amount" step="0.01" min="0.01" placeholder="Enter amount to deposit" required>
            </div>
            
            <button type="submit" class="btn">Confirm Deposit</button>
        </form>
        
        <div class="links">
            <a href="DashboardServlet">Back to Dashboard</a>
        </div>
    </div>
</body>
</html>
