# 📋 Quick Reference Guide - Online Banking Platform

## 🚀 Quick Commands

### Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Run setup script
mysql -u root -p < database/setup.sql

# Verify tables
mysql -u root -p -e "USE online_banking; SHOW TABLES;"
```

### Build & Deploy
```bash
# Build with Maven
mvn clean package

# Copy to Tomcat
cp target/online-banking.war /path/to/tomcat/webapps/

# Start Tomcat
/path/to/tomcat/bin/startup.sh

# Stop Tomcat
/path/to/tomcat/bin/shutdown.sh
```

### Access URLs
```
Landing Page:   http://localhost:8080/online-banking/
Login:          http://localhost:8080/online-banking/login.jsp
Dashboard:      http://localhost:8080/online-banking/DashboardServlet
```

---

## 📁 File Locations

### Configuration Files
```
Database Config:    src/util/DBConnection.java
Web Config:         WEB-INF/web.xml
Maven Config:       pom.xml
Database Script:    database/setup.sql
```

### Source Files
```
DAO Classes:        src/dao/*.java
Servlets:           src/servlets/*.java
Utilities:          src/util/*.java
JSP Pages:          webapp/*.jsp
Stylesheets:        webapp/css/style.css
```

---

## 🔑 Default Credentials

```
Email:      john@example.com
Password:   password123
Balance:    ₹5,000.00

Email:      jane@example.com
Password:   password123
Balance:    ₹7,500.00
```

---

## 🗄️ Database Quick Reference

### Create User
```sql
INSERT INTO users (name, email, password_hash) 
VALUES ('John Doe', 'john@example.com', 'hashed_password');
```

### Create Account
```sql
INSERT INTO accounts (user_id, balance) 
VALUES (1, 1000.00);
```

### View Transactions
```sql
SELECT * FROM transactions 
WHERE sender_account = 1 OR receiver_account = 1 
ORDER BY txn_date DESC 
LIMIT 10;
```

### Check Balance
```sql
SELECT u.name, a.account_id, a.balance 
FROM users u 
JOIN accounts a ON u.user_id = a.user_id 
WHERE u.email = 'john@example.com';
```

### Reset Password (for testing)
```sql
-- Password: password123
-- SHA-256 Hash: ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
UPDATE users 
SET password_hash = 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f' 
WHERE email = 'john@example.com';
```

---

## 🛠️ Troubleshooting

### Database Connection Error
```
Error: "Database connection failed"
Fix:   1. Check MySQL is running: mysql -u root -p
       2. Verify database exists: SHOW DATABASES;
       3. Update DBConnection.java with correct password
       4. Restart Tomcat
```

### ClassNotFoundException
```
Error: "com.mysql.cj.jdbc.Driver not found"
Fix:   Copy MySQL connector to Tomcat lib:
       cp mysql-connector-java-8.0.33.jar /path/to/tomcat/lib/
```

### 404 Not Found
```
Error: "HTTP Status 404"
Fix:   1. Check WAR deployed: ls /path/to/tomcat/webapps/
       2. Wait for auto-deploy (few seconds)
       3. Check logs: tail -f /path/to/tomcat/logs/catalina.out
       4. Verify URL: http://localhost:8080/online-banking/
```

### Session Timeout
```
Error: Redirected to login after inactivity
Fix:   This is normal after 30 minutes. Just login again.
       To change timeout: Edit WEB-INF/web.xml
```

### Insufficient Balance
```
Error: "Insufficient balance"
Fix:   Add money to account:
       UPDATE accounts SET balance = 10000 WHERE account_id = 1;
```

---

## 📊 Common SQL Queries

### View All Users
```sql
SELECT user_id, name, email FROM users;
```

### View All Accounts with Balances
```sql
SELECT u.name, a.account_id, a.balance 
FROM users u 
JOIN accounts a ON u.user_id = a.user_id;
```

### Transaction Summary
```sql
SELECT 
    COUNT(*) as total_transactions,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount
FROM transactions 
WHERE status = 'SUCCESS';
```

### Top 10 Recent Transactions
```sql
SELECT 
    t.txn_id,
    t.sender_account,
    t.receiver_account,
    t.amount,
    t.txn_date,
    t.status
FROM transactions t
ORDER BY t.txn_date DESC
LIMIT 10;
```

### Account Activity
```sql
SELECT 
    (SELECT COUNT(*) FROM transactions WHERE sender_account = 1) as sent,
    (SELECT COUNT(*) FROM transactions WHERE receiver_account = 1) as received;
```

---

## 🔐 Password Hashing

### Generate SHA-256 Hash (Java)
```java
import java.security.MessageDigest;

public static String hashPassword(String password) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(password.getBytes());
    
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
    }
    return hexString.toString();
}
```

### Generate SHA-256 Hash (Command Line)
```bash
# macOS/Linux
echo -n "password123" | shasum -a 256

# Output: ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
```

---

## 📝 Session Management

### Create Session
```java
HttpSession session = request.getSession();
session.setAttribute("userId", userId);
session.setAttribute("name", name);
session.setAttribute("accountId", accountId);
session.setAttribute("balance", balance);
```

### Get Session Data
```java
HttpSession session = request.getSession(false);
Integer userId = (Integer) session.getAttribute("userId");
String name = (String) session.getAttribute("name");
```

### Invalidate Session
```java
HttpSession session = request.getSession(false);
if (session != null) {
    session.invalidate();
}
```

---

## 🎨 UI Customization

### Change Theme Colors
Edit `webapp/css/style.css`:
```css
/* Primary Color */
background: linear-gradient(135deg, #0052cc, #00bfa5);

/* Change to: */
background: linear-gradient(135deg, #6a11cb, #2575fc);
```

### Change Font
```css
font-family: 'Poppins', sans-serif;

/* Change to: */
font-family: 'Roboto', sans-serif;
```

### Adjust Container Width
```css
.container {
    max-width: 500px;  /* Change to your preferred width */
}
```

---

## 🔧 Configuration Updates

### Database Connection
File: `src/util/DBConnection.java`
```java
private static final String URL = "jdbc:mysql://localhost:3306/online_banking";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

### Session Timeout
File: `WEB-INF/web.xml`
```xml
<session-timeout>30</session-timeout>  <!-- minutes -->
```

### Server Port (Tomcat)
File: `tomcat/conf/server.xml`
```xml
<Connector port="8080" protocol="HTTP/1.1" />
```

---

## 📦 Maven Dependencies

### Add New Dependency
Edit `pom.xml`:
```xml
<dependency>
    <groupId>group.id</groupId>
    <artifactId>artifact-id</artifactId>
    <version>1.0.0</version>
</dependency>
```

Then rebuild:
```bash
mvn clean install
```

---

## 🧪 Testing Workflow

### 1. Test Registration
1. Go to: `http://localhost:8080/online-banking/signup.jsp`
2. Fill form with new user details
3. Click "Sign Up"
4. Verify redirect to login with success message

### 2. Test Login
1. Go to: `http://localhost:8080/online-banking/login.jsp`
2. Enter email and password
3. Click "Login"
4. Verify redirect to dashboard

### 3. Test Transfer
1. From dashboard, click "Transfer Money"
2. Enter receiver account number (e.g., 2)
3. Enter amount (e.g., 100.00)
4. Click "Transfer"
5. Verify success message and balance update

### 4. Test Transaction History
1. Click "Transactions" from navigation
2. Verify all transactions displayed
3. Check color coding (red=sent, green=received)

---

## 📊 Monitoring

### Check Tomcat Logs
```bash
# Real-time log monitoring
tail -f /path/to/tomcat/logs/catalina.out

# View last 100 lines
tail -n 100 /path/to/tomcat/logs/catalina.out

# Search for errors
grep -i "error" /path/to/tomcat/logs/catalina.out
```

### MySQL Query Log
```sql
-- Enable query log
SET GLOBAL general_log = 'ON';

-- View log location
SHOW VARIABLES LIKE 'general_log_file';
```

---

## 🔄 Backup & Restore

### Backup Database
```bash
# Full backup
mysqldump -u root -p online_banking > backup.sql

# Backup with timestamp
mysqldump -u root -p online_banking > backup_$(date +%Y%m%d).sql
```

### Restore Database
```bash
mysql -u root -p online_banking < backup.sql
```

---

## 📱 Mobile Testing

### Test Responsive Design
1. Open Chrome DevTools (F12)
2. Click device toolbar icon
3. Select device:
   - iPhone 12 Pro
   - iPad
   - Samsung Galaxy S20
4. Test all pages and features

---

## 🎯 Performance Tips

### Optimize Database
```sql
-- Add indexes for faster queries
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_user_id ON accounts(user_id);
CREATE INDEX idx_txn_date ON transactions(txn_date);
```

### Enable Connection Pooling
Use Apache DBCP or HikariCP for production

### Cache Static Resources
Configure Tomcat to cache CSS/JS files

---

## 📞 Quick Links

| Resource | URL |
|----------|-----|
| Tomcat Manager | http://localhost:8080/manager |
| phpMyAdmin | http://localhost/phpmyadmin |
| Project GitHub | (your-repo-url) |

---

## ✅ Pre-Deployment Checklist

- [ ] Database created and populated
- [ ] DBConnection.java updated with correct credentials
- [ ] Maven dependencies resolved
- [ ] Project builds without errors
- [ ] WAR file created successfully
- [ ] Tomcat is running
- [ ] WAR file deployed to webapps
- [ ] Application accessible via browser
- [ ] Login works with test account
- [ ] Money transfer works correctly
- [ ] Transaction history displays

---

## 🎓 Key Concepts Reference

### ACID Properties
- **Atomicity:** All or nothing (commit/rollback)
- **Consistency:** Data integrity maintained
- **Isolation:** Transactions don't interfere
- **Durability:** Changes persist after commit

### MVC Pattern
- **Model:** DAO classes (business logic)
- **View:** JSP pages (presentation)
- **Controller:** Servlets (request handling)

### Security Best Practices
- PreparedStatements (SQL injection prevention)
- Password hashing (SHA-256 or bcrypt)
- Session management (timeout, validation)
- Input validation (client + server)

---

**Quick Help:** For detailed information, see:
- README.md - Full documentation
- DEPLOYMENT.md - Deployment guide
- API_DOCUMENTATION.md - API reference
- ARCHITECTURE.md - System architecture

---

**Version:** 1.0.0  
**Last Updated:** October 2025
