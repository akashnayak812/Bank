<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome - Online Banking</title>
    <link rel="stylesheet" href="css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        .hero-section {
            text-align: center;
            padding: 60px 20px;
        }
        .hero-section h1 {
            font-size: 3em;
            margin-bottom: 20px;
            color: white;
        }
        .hero-section p {
            font-size: 1.3em;
            color: white;
            margin-bottom: 40px;
        }
        .cta-buttons {
            display: flex;
            gap: 20px;
            justify-content: center;
            flex-wrap: wrap;
        }
        .cta-btn {
            padding: 15px 40px;
            font-size: 1.1em;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .cta-btn-primary {
            background: white;
            color: #0052cc;
        }
        .cta-btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 30px rgba(255, 255, 255, 0.3);
        }
        .cta-btn-secondary {
            background: transparent;
            color: white;
            border: 2px solid white;
        }
        .cta-btn-secondary:hover {
            background: white;
            color: #0052cc;
        }
        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 30px;
            margin-top: 60px;
            padding: 0 40px;
        }
        .feature-card {
            background: white;
            padding: 30px;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }
        .feature-icon {
            font-size: 3em;
            margin-bottom: 15px;
        }
        .feature-card h3 {
            color: #0052cc;
            margin-bottom: 10px;
        }
        .feature-card p {
            color: #666;
        }
    </style>
</head>
<body>
    <div class="hero-section">
        <h1>🏦 Welcome to Online Banking</h1>
        <p>Secure. Simple. Smart Banking at Your Fingertips</p>
        
        <div class="cta-buttons">
            <a href="login.jsp" class="cta-btn cta-btn-primary">Login</a>
            <a href="signup.jsp" class="cta-btn cta-btn-secondary">Sign Up</a>
        </div>
    </div>
    
    <div class="features">
        <div class="feature-card">
            <div class="feature-icon">🔒</div>
            <h3>Secure Transactions</h3>
            <p>Bank-grade encryption and security for all your transactions</p>
        </div>
        
        <div class="feature-card">
            <div class="feature-icon">💸</div>
            <h3>Instant Transfers</h3>
            <p>Transfer money between accounts instantly and securely</p>
        </div>
        
        <div class="feature-card">
            <div class="feature-icon">📊</div>
            <h3>Transaction History</h3>
            <p>Keep track of all your transactions in one place</p>
        </div>
        
        <div class="feature-card">
            <div class="feature-icon">📱</div>
            <h3>Responsive Design</h3>
            <p>Access your account from any device, anywhere, anytime</p>
        </div>
    </div>
</body>
</html>
