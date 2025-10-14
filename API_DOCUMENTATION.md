# 📚 API Documentation - Online Banking Platform

## 🌐 Servlet Endpoints

All servlets are accessible via HTTP POST/GET requests.

---

## 🔐 Authentication Endpoints

### 1. User Registration
**Endpoint:** `/SignupServlet`  
**Method:** `POST`  
**Description:** Register a new user account

**Request Parameters:**
```
name: String (required) - Full name
email: String (required) - Email address (must be unique)
password: String (required) - Password (min 6 characters)
confirmPassword: String (required) - Password confirmation
```

**Response:**
- Success: Redirect to `login.jsp` with success message
- Failure: Redirect to `signup.jsp` with error message

**Validation Rules:**
- All fields are required
- Email must be unique
- Password must be at least 6 characters
- Passwords must match

**Example:**
```java
POST /online-banking/SignupServlet
Content-Type: application/x-www-form-urlencoded

name=John+Doe&email=john@example.com&password=password123&confirmPassword=password123
```

---

### 2. User Login
**Endpoint:** `/LoginServlet`  
**Method:** `POST`  
**Description:** Authenticate user and create session

**Request Parameters:**
```
email: String (required)
password: String (required)
```

**Response:**
- Success: Redirect to `/DashboardServlet`
- Failure: Redirect to `login.jsp` with error message

**Session Attributes (on success):**
```java
userId: Integer
name: String
email: String
accountId: Integer
accountNumber: Integer
balance: Double
```

**Example:**
```java
POST /online-banking/LoginServlet
Content-Type: application/x-www-form-urlencoded

email=john@example.com&password=password123
```

---

### 3. User Logout
**Endpoint:** `/LogoutServlet`  
**Method:** `GET`  
**Description:** Invalidate session and logout user

**Response:**
- Redirect to `login.jsp?logout=true`

**Example:**
```
GET /online-banking/LogoutServlet
```

---

## 📊 Dashboard Endpoints

### 4. Dashboard
**Endpoint:** `/DashboardServlet`  
**Method:** `GET`  
**Description:** Display user dashboard with account info

**Authorization:** Required (Session must exist)

**Response:**
- Success: Forward to `dashboard.jsp`
- Unauthorized: Redirect to `login.jsp`

**Updates:**
- Fetches latest balance from database
- Updates session balance

**Example:**
```
GET /online-banking/DashboardServlet
```

---

## 💸 Transaction Endpoints

### 5. Money Transfer
**Endpoint:** `/TransferServlet`  
**Method:** `POST`  
**Description:** Transfer money between accounts (ACID transaction)

**Authorization:** Required

**Request Parameters:**
```
receiverAccountId: Integer (required) - Receiver's account number
amount: Double (required) - Amount to transfer (must be > 0)
```

**Validation:**
- Amount must be greater than 0
- Receiver account must exist
- Cannot transfer to own account
- Sender must have sufficient balance

**Response:**
- Success: Forward to `transfer.jsp` with success message
- Failure: Forward to `transfer.jsp` with error message

**Transaction Steps:**
1. Check sender balance
2. Validate receiver account
3. Debit from sender (atomic)
4. Credit to receiver (atomic)
5. Record transaction
6. Commit or Rollback

**Example:**
```java
POST /online-banking/TransferServlet
Content-Type: application/x-www-form-urlencoded

receiverAccountId=2&amount=500.00
```

---

### 6. Transaction History
**Endpoint:** `/TransactionHistoryServlet`  
**Method:** `GET`  
**Description:** Display user's transaction history

**Authorization:** Required

**Response:**
- Success: Forward to `transactions.jsp` with transaction list
- Unauthorized: Redirect to `login.jsp`

**Data Returned:**
```java
List<Map<String, Object>> transactions
  - txnId: Integer
  - senderAccount: Integer
  - receiverAccount: Integer
  - amount: Double
  - date: Timestamp
  - status: String
  - type: String (SENT/RECEIVED)
```

**Limit:** Last 50 transactions

**Example:**
```
GET /online-banking/TransactionHistoryServlet
```

---

## 🗄️ DAO Methods

### UserDAO Methods

#### registerUser
```java
public boolean registerUser(String name, String email, String password)
```
**Description:** Register new user with hashed password  
**Returns:** `true` if successful, `false` otherwise

---

#### loginUser
```java
public ResultSet loginUser(String email, String password)
```
**Description:** Authenticate user credentials  
**Returns:** `ResultSet` with user data if valid, `null` otherwise

---

#### emailExists
```java
public boolean emailExists(String email)
```
**Description:** Check if email already registered  
**Returns:** `true` if exists, `false` otherwise

---

### AccountDAO Methods

#### getBalance
```java
public double getBalance(int accountId)
```
**Description:** Get current account balance  
**Returns:** Current balance as `double`

---

#### accountExists
```java
public boolean accountExists(int accountId)
```
**Description:** Check if account exists  
**Returns:** `true` if exists, `false` otherwise

---

### TransactionDAO Methods

#### transferMoney
```java
public boolean transferMoney(int senderAccountId, int receiverAccountId, double amount)
```
**Description:** Transfer money with ACID guarantees  
**Returns:** `true` if successful, `false` otherwise

**Transaction Flow:**
1. `BEGIN TRANSACTION`
2. Lock sender account (`FOR UPDATE`)
3. Check balance
4. Debit sender
5. Credit receiver
6. Record transaction
7. `COMMIT` or `ROLLBACK`

---

#### getTransactionHistory
```java
public List<Map<String, Object>> getTransactionHistory(int accountId)
```
**Description:** Get transaction history for account  
**Returns:** List of transaction maps

---

## 🔒 Security Features

### SQL Injection Prevention
All queries use `PreparedStatement`:

```java
String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, email);
pstmt.setString(2, hashedPassword);
```

---

### Password Hashing
Passwords are hashed using SHA-256:

```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hash = digest.digest(password.getBytes());
// Convert to hex string
```

---

### Session Management
- Session timeout: 30 minutes
- HttpOnly cookies enabled
- Session validation on protected pages

```java
HttpSession session = request.getSession(false);
if (session == null || session.getAttribute("userId") == null) {
    response.sendRedirect("login.jsp");
    return;
}
```

---

## 📊 Database Schema

### Users Table
```sql
users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
)
```

### Accounts Table
```sql
accounts (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    balance DECIMAL(12,2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
)
```

### Transactions Table
```sql
transactions (
    txn_id INT PRIMARY KEY AUTO_INCREMENT,
    sender_account INT NOT NULL,
    receiver_account INT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'SUCCESS',
    FOREIGN KEY (sender_account) REFERENCES accounts(account_id),
    FOREIGN KEY (receiver_account) REFERENCES accounts(account_id)
)
```

---

## 🎯 Error Codes

| Code | Description | Action |
|------|-------------|--------|
| 400 | Bad Request | Check input parameters |
| 401 | Unauthorized | Login required |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Invalid endpoint |
| 500 | Server Error | Check logs |

---

## 📝 Example Workflows

### Complete User Journey

#### 1. Registration
```
POST /SignupServlet
→ Insert user into database
→ Create account with initial balance
→ Redirect to login
```

#### 2. Login
```
POST /LoginServlet
→ Verify credentials
→ Create session
→ Redirect to dashboard
```

#### 3. View Dashboard
```
GET /DashboardServlet
→ Fetch latest balance
→ Update session
→ Display dashboard
```

#### 4. Transfer Money
```
POST /TransferServlet
→ Validate inputs
→ Begin transaction
→ Debit sender
→ Credit receiver
→ Commit transaction
→ Update balance
```

#### 5. View History
```
GET /TransactionHistoryServlet
→ Fetch transactions
→ Display in table
```

#### 6. Logout
```
GET /LogoutServlet
→ Invalidate session
→ Redirect to login
```

---

## 🔧 Configuration

### Database Connection
**File:** `src/util/DBConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/online_banking";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

### Web Configuration
**File:** `WEB-INF/web.xml`

```xml
<session-config>
    <session-timeout>30</session-timeout>
</session-config>
```

---

## 📊 Performance Considerations

1. **Connection Pooling:** Consider implementing for production
2. **Caching:** Session attributes cached in memory
3. **Indexing:** Database indexes on frequently queried columns
4. **Transaction Isolation:** Uses default isolation level
5. **Prepared Statements:** Compiled and cached by database

---

## 🧪 Testing Examples

### Test User Registration
```bash
curl -X POST http://localhost:8080/online-banking/SignupServlet \
  -d "name=Test User&email=test@example.com&password=password123&confirmPassword=password123"
```

### Test Login
```bash
curl -X POST http://localhost:8080/online-banking/LoginServlet \
  -d "email=john@example.com&password=password123" \
  -c cookies.txt
```

### Test Transfer
```bash
curl -X POST http://localhost:8080/online-banking/TransferServlet \
  -d "receiverAccountId=2&amount=100.00" \
  -b cookies.txt
```

---

**Documentation Version:** 1.0.0  
**Last Updated:** 2025
