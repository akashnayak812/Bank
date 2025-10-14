<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Account - Online Banking</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    
    <style>
        body {
            background: linear-gradient(135deg, #0052cc 0%, #00bfa5 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .add-account-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
            overflow: hidden;
            max-width: 500px;
            width: 100%;
        }
        
        .card-header-custom {
            background: linear-gradient(135deg, #0052cc, #00bfa5);
            color: white;
            padding: 30px;
            text-align: center;
        }
        
        .card-header-custom h2 {
            margin: 0;
            font-weight: 600;
        }
        
        .card-header-custom p {
            margin: 10px 0 0 0;
            opacity: 0.9;
        }
        
        .card-body-custom {
            padding: 40px;
        }
        
        .form-label {
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
        }
        
        .form-control {
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            padding: 12px 15px;
            transition: all 0.3s;
        }
        
        .form-control:focus {
            border-color: #0052cc;
            box-shadow: 0 0 0 0.2rem rgba(0, 82, 204, 0.25);
        }
        
        .btn-create {
            background: linear-gradient(135deg, #0052cc, #00bfa5);
            border: none;
            border-radius: 10px;
            padding: 14px;
            font-weight: 600;
            font-size: 16px;
            color: white;
            width: 100%;
            transition: transform 0.3s;
        }
        
        .btn-create:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 82, 204, 0.3);
        }
        
        .alert-custom {
            border-radius: 10px;
            border: none;
        }
        
        .info-box {
            background: #f4f7fc;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 20px;
        }
        
        .info-box i {
            color: #0052cc;
            font-size: 24px;
            margin-right: 10px;
        }
        
        .logout-link {
            color: #0052cc;
            text-decoration: none;
            display: inline-block;
            margin-top: 15px;
        }
        
        .logout-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="add-account-card">
            <div class="card-header-custom">
                <i class="bi bi-bank" style="font-size: 48px;"></i>
                <h2>Create Your Bank Account</h2>
                <p>Welcome, <%= session.getAttribute("name") %>!</p>
            </div>
            
            <div class="card-body-custom">
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger alert-custom" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>
                
                <div class="info-box">
                    <i class="bi bi-info-circle-fill"></i>
                    <strong>Set up your account</strong>
                    <p class="mb-0 mt-2">Please enter an initial balance to create your bank account. You can add more funds later.</p>
                </div>
                
                <form action="AddAccountServlet" method="post">
                    <div class="mb-4">
                        <label for="balance" class="form-label">
                            <i class="bi bi-currency-rupee"></i> Initial Balance
                        </label>
                        <input type="number" 
                               class="form-control" 
                               id="balance" 
                               name="balance" 
                               placeholder="Enter initial balance (₹)" 
                               min="0" 
                               step="0.01"
                               required>
                        <small class="text-muted">Minimum: ₹0.00</small>
                    </div>
                    
                    <button type="submit" class="btn btn-create">
                        <i class="bi bi-check-circle me-2"></i>
                        Create Account
                    </button>
                </form>
                
                <div class="text-center mt-4">
                    <a href="LogoutServlet" class="logout-link">
                        <i class="bi bi-box-arrow-right"></i> Logout
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
