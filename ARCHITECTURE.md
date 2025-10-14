# 🏗️ Architecture Diagram - Online Banking Platform

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT BROWSER                        │
│                     (HTML/CSS/JavaScript)                    │
└────────────────┬────────────────────────────────────────────┘
                 │ HTTP Request/Response
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                     APACHE TOMCAT 9.0                        │
│                   (Servlet Container)                        │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │              PRESENTATION LAYER (JSP)              │    │
│  │  ┌──────────┬──────────┬──────────┬──────────┐    │    │
│  │  │login.jsp │signup.jsp│dashboard │transfer  │    │    │
│  │  │          │          │.jsp      │.jsp      │    │    │
│  │  └──────────┴──────────┴──────────┴──────────┘    │    │
│  │  ┌──────────────────┬──────────────────────┐      │    │
│  │  │transactions.jsp  │ index.jsp            │      │    │
│  │  └──────────────────┴──────────────────────┘      │    │
│  └────────────────┬───────────────────────────────────┘    │
│                   │                                         │
│  ┌────────────────▼───────────────────────────────────┐    │
│  │           CONTROLLER LAYER (Servlets)              │    │
│  │  ┌──────────────┬──────────────┬─────────────┐    │    │
│  │  │LoginServlet  │SignupServlet │Dashboard    │    │    │
│  │  │              │              │Servlet      │    │    │
│  │  └──────────────┴──────────────┴─────────────┘    │    │
│  │  ┌──────────────┬──────────────┬─────────────┐    │    │
│  │  │Transfer      │Transaction   │Logout       │    │    │
│  │  │Servlet       │HistoryServlet│Servlet      │    │    │
│  │  └──────────────┴──────────────┴─────────────┘    │    │
│  └────────────────┬───────────────────────────────────┘    │
│                   │                                         │
│  ┌────────────────▼───────────────────────────────────┐    │
│  │          MODEL LAYER (DAO Classes)                 │    │
│  │  ┌──────────────┬──────────────┬─────────────┐    │    │
│  │  │UserDAO       │AccountDAO    │Transaction  │    │    │
│  │  │              │              │DAO          │    │    │
│  │  │- register    │- getBalance  │- transfer   │    │    │
│  │  │- login       │- update      │- history    │    │    │
│  │  │- hashPass    │- exists      │- record     │    │    │
│  │  └──────────────┴──────────────┴─────────────┘    │    │
│  └────────────────┬───────────────────────────────────┘    │
│                   │                                         │
│  ┌────────────────▼───────────────────────────────────┐    │
│  │              UTILITY LAYER                         │    │
│  │  ┌────────────────────────────────────────┐        │    │
│  │  │       DBConnection.java                │        │    │
│  │  │  - getConnection()                     │        │    │
│  │  │  - closeConnection()                   │        │    │
│  │  └────────────────────────────────────────┘        │    │
│  └────────────────┬───────────────────────────────────┘    │
└───────────────────┼─────────────────────────────────────────┘
                    │ JDBC Connection
                    ▼
┌─────────────────────────────────────────────────────────────┐
│                      MySQL DATABASE                          │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │    users     │  │   accounts   │  │ transactions │     │
│  │              │  │              │  │              │     │
│  │ user_id (PK) │  │ account_id   │  │ txn_id (PK)  │     │
│  │ name         │  │ user_id (FK) │  │ sender (FK)  │     │
│  │ email (UQ)   │  │ balance      │  │ receiver(FK) │     │
│  │ password_hash│  │              │  │ amount       │     │
│  │              │  │              │  │ txn_date     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

## Request Flow Diagram

```
┌──────────┐
│  User    │
└────┬─────┘
     │ 1. Enter credentials
     ▼
┌─────────────────┐
│  login.jsp      │
└────┬────────────┘
     │ 2. POST /LoginServlet
     ▼
┌─────────────────┐
│ LoginServlet    │───► 3. Validate input
└────┬────────────┘
     │ 4. Call loginUser()
     ▼
┌─────────────────┐
│   UserDAO       │───► 5. Hash password
└────┬────────────┘
     │ 6. Execute SQL query
     ▼
┌─────────────────┐
│  MySQL DB       │───► 7. Verify credentials
└────┬────────────┘
     │ 8. Return user data
     ▼
┌─────────────────┐
│ LoginServlet    │───► 9. Create session
└────┬────────────┘     10. Store user info
     │ 11. Redirect
     ▼
┌─────────────────┐
│ dashboard.jsp   │───► 12. Display account
└─────────────────┘
```

## Money Transfer Flow (ACID Transaction)

```
┌──────────────────────────────────────────────────────────┐
│                  TRANSFER PROCESS                         │
└──────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│ 1. User submits transfer form (transfer.jsp)           │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ 2. TransferServlet validates input                     │
│    - Check amount > 0                                  │
│    - Verify receiver account exists                    │
│    - Ensure not self-transfer                          │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ 3. TransactionDAO.transferMoney() starts               │
│    conn.setAutoCommit(false) ◄── BEGIN TRANSACTION     │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ 4. Lock sender account (FOR UPDATE)                    │
│    SELECT balance FROM accounts WHERE id = ? FOR UPDATE│
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ 5. Check sufficient balance                            │
│    If balance < amount → ROLLBACK                      │
└───────────────────────┬────────────────────────────────┘
                        │ Balance OK
                        ▼
┌────────────────────────────────────────────────────────┐
│ 6. Debit sender account                                │
│    UPDATE accounts SET balance = balance - amount      │
│    WHERE account_id = sender                           │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ 7. Credit receiver account                             │
│    UPDATE accounts SET balance = balance + amount      │
│    WHERE account_id = receiver                         │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ 8. Record transaction                                  │
│    INSERT INTO transactions (sender, receiver, amount) │
└───────────────────────┬────────────────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────────────────┐
│ 9. Commit transaction                                  │
│    conn.commit() ◄── COMMIT ALL CHANGES                │
└───────────────────────┬────────────────────────────────┘
                        │
         ┌──────────────┴──────────────┐
         │                             │
         ▼                             ▼
    ┌─────────┐                  ┌─────────┐
    │ SUCCESS │                  │  ERROR  │
    └────┬────┘                  └────┬────┘
         │                             │
         │                             ▼
         │                    conn.rollback()
         │                    ◄── UNDO ALL CHANGES
         │                             │
         └─────────────┬───────────────┘
                       │
                       ▼
              ┌─────────────────┐
              │ Return to user  │
              └─────────────────┘
```

## Security Layers

```
┌─────────────────────────────────────────────────────────┐
│                  SECURITY ARCHITECTURE                   │
└─────────────────────────────────────────────────────────┘

Layer 1: INPUT VALIDATION
┌─────────────────────────────────────────────────────────┐
│ • Client-side JavaScript validation                     │
│ • HTML5 input attributes (required, type, min, max)     │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
Layer 2: SERVER-SIDE VALIDATION
┌─────────────────────────────────────────────────────────┐
│ • Servlet parameter validation                          │
│ • Data type checking                                    │
│ • Business logic validation                             │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
Layer 3: SQL INJECTION PREVENTION
┌─────────────────────────────────────────────────────────┐
│ • PreparedStatement usage                               │
│ • Parameter binding (no string concatenation)           │
│ • SQL query parameterization                            │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
Layer 4: PASSWORD SECURITY
┌─────────────────────────────────────────────────────────┐
│ • SHA-256 hashing algorithm                             │
│ • One-way encryption (irreversible)                     │
│ • Salting recommended for production                    │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
Layer 5: SESSION MANAGEMENT
┌─────────────────────────────────────────────────────────┐
│ • HttpOnly cookies (prevent XSS)                        │
│ • Session timeout (30 minutes)                          │
│ • Session validation on protected pages                 │
│ • Secure flag for HTTPS (production)                    │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
Layer 6: DATABASE SECURITY
┌─────────────────────────────────────────────────────────┐
│ • Foreign key constraints                               │
│ • CHECK constraints (balance >= 0)                      │
│ • ACID transaction isolation                            │
│ • Row-level locking (FOR UPDATE)                        │
└─────────────────────────────────────────────────────────┘
```

## MVC Pattern Implementation

```
┌─────────────────────────────────────────────────────────┐
│                      MVC PATTERN                         │
└─────────────────────────────────────────────────────────┘

VIEW (JSP Pages)
┌────────────────────────────────────┐
│ • login.jsp                        │
│ • signup.jsp                       │
│ • dashboard.jsp                    │
│ • transfer.jsp                     │
│ • transactions.jsp                 │
└────────────┬───────────────────────┘
             │ User actions
             ▼
CONTROLLER (Servlets)
┌────────────────────────────────────┐
│ • LoginServlet                     │
│ • SignupServlet                    │
│ • DashboardServlet                 │
│ • TransferServlet                  │
│ • TransactionHistoryServlet        │
│ • LogoutServlet                    │
└────────────┬───────────────────────┘
             │ Call business logic
             ▼
MODEL (DAO + Entities)
┌────────────────────────────────────┐
│ DAO Layer:                         │
│ • UserDAO                          │
│ • AccountDAO                       │
│ • TransactionDAO                   │
│                                    │
│ Utility:                           │
│ • DBConnection                     │
└────────────┬───────────────────────┘
             │ JDBC operations
             ▼
DATABASE (MySQL)
┌────────────────────────────────────┐
│ • users                            │
│ • accounts                         │
│ • transactions                     │
└────────────────────────────────────┘
```

## Session Lifecycle

```
┌──────────────────────────────────────────────────────────┐
│                   SESSION LIFECYCLE                       │
└──────────────────────────────────────────────────────────┘

1. User Login
   │
   ├─► LoginServlet validates credentials
   │
   ├─► HttpSession session = request.getSession()
   │
   ├─► session.setAttribute("userId", user.getId())
   │   session.setAttribute("name", user.getName())
   │   session.setAttribute("email", user.getEmail())
   │   session.setAttribute("accountId", account.getId())
   │   session.setAttribute("balance", account.getBalance())
   │
   └─► session.setMaxInactiveInterval(1800) // 30 min

2. Using Session
   │
   ├─► Every protected page checks:
   │   HttpSession session = request.getSession(false)
   │   if (session == null || session.getAttribute("userId") == null)
   │       → redirect to login.jsp
   │
   └─► Access session data:
       Integer userId = (Integer) session.getAttribute("userId")

3. Session Timeout
   │
   ├─► After 30 minutes of inactivity
   │
   └─► Server automatically invalidates session

4. User Logout
   │
   ├─► LogoutServlet called
   │
   ├─► session.invalidate()
   │
   └─► Redirect to login.jsp?logout=true
```

## Deployment Architecture

```
┌──────────────────────────────────────────────────────────┐
│              DEVELOPMENT ENVIRONMENT                      │
└──────────────────────────────────────────────────────────┘
                           │
                           │ Build (mvn clean package)
                           ▼
┌──────────────────────────────────────────────────────────┐
│                  online-banking.war                       │
└──────────────────────────────────────────────────────────┘
                           │
                           │ Deploy
                           ▼
┌──────────────────────────────────────────────────────────┐
│              APACHE TOMCAT SERVER                         │
│                                                           │
│  ┌─────────────────────────────────────────────────┐    │
│  │             webapps/                             │    │
│  │  └── online-banking/                             │    │
│  │       ├── WEB-INF/                               │    │
│  │       │   ├── classes/                           │    │
│  │       │   │   ├── dao/                           │    │
│  │       │   │   ├── servlets/                      │    │
│  │       │   │   └── util/                          │    │
│  │       │   ├── lib/                               │    │
│  │       │   └── web.xml                            │    │
│  │       ├── css/                                   │    │
│  │       └── *.jsp                                  │    │
│  └─────────────────────────────────────────────────┘    │
└───────────────────────┬──────────────────────────────────┘
                        │ JDBC
                        ▼
┌──────────────────────────────────────────────────────────┐
│                  MySQL DATABASE                           │
│                  (Port 3306)                              │
└──────────────────────────────────────────────────────────┘
```

---

**Legend:**
- `┌─┐` Box/Container
- `│` Vertical connection
- `▼` Flow direction
- `◄──` Backward reference
- `─►` Forward reference
- `└─┘` End of container

