# 🏦 Online Banking Platform

A full-stack **Online Banking Platform** built with **Java, JSP, Servlets, JDBC, and MySQL** with a beautiful, modern, and responsive UI/UX design.

## ✨ Features

### 🔐 Secure Authentication
- User registration with email and password
- Passwords stored as SHA-256 hashed values
- Secure session management
- Session timeout after 30 minutes of inactivity

### 📊 Dashboard
- Displays user's name, account number, and current balance
- Quick action buttons for easy navigation
- Real-time balance updates
- Beautiful gradient design

### 💸 Money Transfer
- Transfer funds between accounts instantly
- JDBC transactions ensure atomicity (commit or rollback)
- Prevents overdrafts, invalid accounts, and negative transfers
- Input validation on both frontend and backend
- Transaction confirmation messages

### 📜 Transaction History
- View complete transaction history
- Displays: Date, From, To, Amount, Status, Type (Sent/Received)
- Color-coded transactions (green for received, red for sent)
- Hover effects and clean table design
- Shows last 50 transactions

### 🔒 Security
- **PreparedStatements** to prevent SQL Injection
- **Password hashing** using SHA-256
- **Session management** with HttpOnly cookies
- **Input validation** on both client and server side
- **Authorization checks** on all protected pages

## 🗄️ Database Schema

```sql
-- Users Table
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL
);

-- Accounts Table
CREATE TABLE accounts (
  account_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  balance DECIMAL(12,2) DEFAULT 0.00,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Transactions Table
CREATE TABLE transactions (
  txn_id INT AUTO_INCREMENT PRIMARY KEY,
  sender_account INT NOT NULL,
  receiver_account INT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(20) DEFAULT 'SUCCESS',
  FOREIGN KEY (sender_account) REFERENCES accounts(account_id),
  FOREIGN KEY (receiver_account) REFERENCES accounts(account_id)
);
```

## 📁 Project Structure

```
/Bank/
  ├── src/
  │   ├── dao/
  │   │   ├── UserDAO.java              # User database operations
  │   │   ├── AccountDAO.java           # Account management
  │   │   └── TransactionDAO.java       # Transaction handling (ACID)
  │   ├── servlets/
  │   │   ├── LoginServlet.java         # User authentication
  │   │   ├── SignupServlet.java        # User registration
  │   │   ├── TransferServlet.java      # Money transfer
  │   │   ├── DashboardServlet.java     # Dashboard display
  │   │   ├── TransactionHistoryServlet.java  # Transaction history
  │   │   └── LogoutServlet.java        # User logout
  │   └── util/
  │       └── DBConnection.java         # Database connection utility
  ├── webapp/
  │   ├── login.jsp                     # Login page
  │   ├── signup.jsp                    # Registration page
  │   ├── dashboard.jsp                 # User dashboard
  │   ├── transfer.jsp                  # Money transfer page
  │   ├── transactions.jsp              # Transaction history page
  │   └── css/
  │       └── style.css                 # Modern responsive styles
  ├── WEB-INF/
  │   └── web.xml                       # Web application configuration
  ├── database/
  │   └── setup.sql                     # Database setup script
  └── pom.xml                           # Maven dependencies
```

## 🚀 Setup Instructions

### Prerequisites
- **Java JDK 11+** installed
- **Apache Tomcat 9+** or any Java EE application server
- **MySQL 8.0+** installed and running
- **Maven** (for dependency management)

### Step 1: Database Setup

1. Start MySQL server
2. Run the database setup script:

```bash
mysql -u root -p < database/setup.sql
```

3. Update database credentials in `src/util/DBConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/online_banking";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password"; // Update this
```

### Step 2: Build the Project

Using Maven:

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile Java files
- Create a WAR file in the `target/` directory

### Step 3: Deploy to Tomcat

**Option 1: Manual Deployment**
1. Copy the generated `online-banking.war` from `target/` directory
2. Paste it in Tomcat's `webapps/` directory
3. Start Tomcat server
4. Access the application at: `http://localhost:8080/online-banking/`

**Option 2: Using Maven Tomcat Plugin**

```bash
mvn tomcat7:run
```

Access at: `http://localhost:8080/banking/`

### Step 4: Access the Application

- **URL:** `http://localhost:8080/online-banking/login.jsp`
- **Sample Login:**
  - Email: `john@example.com`
  - Password: `password123`

## 🎨 UI/UX Design Features

- **Modern Gradient Design:** Beautiful blue-green gradient background
- **Responsive Layout:** Works perfectly on desktop, tablet, and mobile
- **Smooth Animations:** Fade-in effects and hover transitions
- **Clean Typography:** Uses Google Fonts (Poppins)
- **Color-Coded Transactions:** Visual distinction between sent/received
- **Card-Based Layout:** Clean, organized information display
- **Interactive Elements:** Hover effects on buttons and tables
- **Alert Messages:** Success and error messages with animations

## 🔧 Technical Implementation

### MVC Architecture
- **Model:** DAO classes (UserDAO, AccountDAO, TransactionDAO)
- **View:** JSP pages with embedded Java
- **Controller:** Servlet classes

### JDBC Transaction Management
```java
// Money transfer with ACID properties
conn.setAutoCommit(false);
try {
    // Debit from sender
    // Credit to receiver
    // Record transaction
    conn.commit();
} catch (Exception e) {
    conn.rollback();
}
```

### Security Best Practices
- **SQL Injection Prevention:** All queries use PreparedStatements
- **Password Hashing:** SHA-256 algorithm
- **Session Security:** HttpOnly cookies, session timeout
- **Input Validation:** Both client-side (JavaScript) and server-side (Java)

## 📝 Sample Test Data

The `setup.sql` script includes sample data:

| Email | Password | Balance |
|-------|----------|---------|
| john@example.com | password123 | ₹5,000.00 |
| jane@example.com | password123 | ₹7,500.00 |
| alice@example.com | password123 | ₹10,000.00 |
| bob@example.com | password123 | ₹3,000.00 |

## 🔍 Testing the Application

1. **Register a new user**
   - Go to signup page
   - Fill in details
   - Verify account creation with initial balance (₹1,000)

2. **Login**
   - Use registered credentials
   - Verify session creation and dashboard display

3. **Transfer Money**
   - Navigate to transfer page
   - Enter valid account number and amount
   - Verify balance update and transaction record

4. **View History**
   - Check transaction history
   - Verify all transactions are displayed correctly

## 🛠️ Technology Stack

- **Backend:** Java 11, Servlets, JSP
- **Database:** MySQL 8.0
- **Frontend:** HTML5, CSS3, JavaScript
- **Build Tool:** Maven
- **Server:** Apache Tomcat 9+
- **Architecture:** MVC Pattern
- **Security:** SHA-256 Hashing, PreparedStatements

## 📱 Responsive Breakpoints

- **Desktop:** > 768px
- **Tablet:** 768px - 480px
- **Mobile:** < 480px

## 🎯 Key Learning Points

1. **JDBC Transaction Management:** Implementing ACID properties
2. **Session Management:** Secure user authentication
3. **SQL Injection Prevention:** Using PreparedStatements
4. **Password Security:** Hashing with SHA-256
5. **MVC Pattern:** Separating concerns (Model, View, Controller)
6. **Responsive Design:** Mobile-first approach
7. **Form Validation:** Both client and server-side

## 🔄 Future Enhancements

- [ ] Email verification for registration
- [ ] Forgot password functionality
- [ ] Profile management
- [ ] Transaction filters and search
- [ ] PDF statement generation
- [ ] Two-factor authentication (2FA)
- [ ] Account statements export
- [ ] Loan management
- [ ] Credit/Debit card management
- [ ] Bill payments
- [ ] Beneficiary management

## 📄 License

This project is created for educational purposes.

## 👨‍💻 Author

Created with ❤️ for learning full-stack Java web development

---

**Note:** This is a demonstration project. For production use, additional security measures, error handling, and features should be implemented.
