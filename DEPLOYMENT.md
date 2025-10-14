# 📋 Deployment Guide - Online Banking Platform

## 🚀 Quick Start Deployment

This guide will help you deploy the Online Banking Platform on Apache Tomcat.

---

## 📦 Prerequisites Checklist

- [ ] **Java JDK 11 or higher** installed
- [ ] **Apache Tomcat 9 or higher** installed
- [ ] **MySQL 8.0 or higher** installed and running
- [ ] **Maven 3.6+** installed (optional, but recommended)
- [ ] **Git** installed (for cloning the project)

---

## 🔧 Step-by-Step Deployment

### Step 1: Verify Java Installation

```bash
java -version
```

Expected output: `java version "11.x.x"` or higher

If not installed, download from: https://www.oracle.com/java/technologies/downloads/

---

### Step 2: Install and Start MySQL

**macOS:**
```bash
brew install mysql
brew services start mysql
```

**Linux:**
```bash
sudo apt-get install mysql-server
sudo systemctl start mysql
```

**Windows:**
Download MySQL installer from: https://dev.mysql.com/downloads/installer/

---

### Step 3: Setup Database

1. **Login to MySQL:**
```bash
mysql -u root -p
```

2. **Run the setup script:**
```bash
mysql -u root -p < database/setup.sql
```

Or manually:
```sql
source /path/to/Bank/database/setup.sql
```

3. **Verify database creation:**
```sql
USE online_banking;
SHOW TABLES;
SELECT * FROM users;
```

---

### Step 4: Configure Database Connection

Edit `src/util/DBConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/online_banking";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_mysql_password"; // ⚠️ UPDATE THIS
```

---

### Step 5: Build the Project

**Using Maven:**

```bash
# Navigate to project directory
cd /Users/akashdegavath/Documents/Bank

# Clean and build
mvn clean package

# This creates: target/online-banking.war
```

**Manual Build (without Maven):**

```bash
# Compile Java files
javac -d bin -cp "lib/*" src/**/*.java

# Create WAR file
jar -cvf online-banking.war -C webapp/ .
jar -uvf online-banking.war -C bin/ .
```

---

### Step 6: Deploy to Tomcat

#### Option A: Automatic Deployment (Recommended)

1. Copy WAR file to Tomcat webapps:
```bash
cp target/online-banking.war /path/to/tomcat/webapps/
```

**macOS (Homebrew Tomcat):**
```bash
cp target/online-banking.war /usr/local/Cellar/tomcat/*/libexec/webapps/
```

**Linux:**
```bash
sudo cp target/online-banking.war /opt/tomcat/webapps/
```

**Windows:**
```bash
copy target\online-banking.war C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\
```

2. Start Tomcat:
```bash
# macOS
brew services start tomcat

# Linux
sudo systemctl start tomcat

# Or use startup script
/path/to/tomcat/bin/startup.sh
```

3. Tomcat will auto-deploy the WAR file

#### Option B: Tomcat Manager (GUI)

1. Access Tomcat Manager: `http://localhost:8080/manager`
2. Login with admin credentials
3. Upload `online-banking.war` file
4. Click "Deploy"

#### Option C: Maven Tomcat Plugin

```bash
mvn tomcat7:run
```

Access at: `http://localhost:8080/banking/`

---

### Step 7: Access the Application

🌐 **URL:** `http://localhost:8080/online-banking/`

**Default Login Credentials:**
- Email: `john@example.com`
- Password: `password123`

---

## 🔍 Verification Steps

### 1. Check Tomcat Logs
```bash
tail -f /path/to/tomcat/logs/catalina.out
```

Look for:
- `Database connected successfully!`
- `Deployment successful`

### 2. Test Database Connection

Access: `http://localhost:8080/online-banking/login.jsp`

If page loads → Database connection successful ✅

### 3. Test Login

1. Go to login page
2. Enter: `john@example.com` / `password123`
3. Should redirect to dashboard

---

## 🐛 Troubleshooting

### Problem: "Database connection failed"

**Solution:**
1. Verify MySQL is running: `mysql -u root -p`
2. Check database exists: `SHOW DATABASES;`
3. Verify credentials in `DBConnection.java`
4. Check MySQL port (default: 3306)

---

### Problem: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Solution:**
1. Ensure `mysql-connector-java-8.0.33.jar` is in project
2. Add to Tomcat lib folder:
```bash
cp ~/.m2/repository/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar /path/to/tomcat/lib/
```

---

### Problem: "404 - Page Not Found"

**Solution:**
1. Check WAR file deployed: `ls /path/to/tomcat/webapps/`
2. Verify folder exists: `/path/to/tomcat/webapps/online-banking/`
3. Restart Tomcat
4. Check correct URL: `http://localhost:8080/online-banking/login.jsp`

---

### Problem: Servlet errors

**Solution:**
1. Check servlet-api dependency in `pom.xml`
2. Verify Java version compatibility
3. Check `web.xml` configuration
4. Review Tomcat logs for stack traces

---

## 📊 Port Configuration

Default ports:
- **Tomcat:** 8080
- **MySQL:** 3306

To change Tomcat port, edit: `/path/to/tomcat/conf/server.xml`

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

---

## 🔐 Security Checklist for Production

- [ ] Change default MySQL passwords
- [ ] Enable HTTPS (SSL/TLS)
- [ ] Update `<secure>true</secure>` in `web.xml`
- [ ] Remove sample user accounts
- [ ] Implement rate limiting
- [ ] Add CAPTCHA to login
- [ ] Enable Tomcat security manager
- [ ] Restrict database user privileges
- [ ] Set up firewall rules
- [ ] Regular security audits

---

## 📝 Useful Commands

**Start Tomcat:**
```bash
/path/to/tomcat/bin/startup.sh
```

**Stop Tomcat:**
```bash
/path/to/tomcat/bin/shutdown.sh
```

**View Tomcat Logs:**
```bash
tail -f /path/to/tomcat/logs/catalina.out
```

**Check Tomcat Status:**
```bash
curl http://localhost:8080
```

**Rebuild and Redeploy:**
```bash
mvn clean package
cp target/online-banking.war /path/to/tomcat/webapps/
/path/to/tomcat/bin/shutdown.sh
/path/to/tomcat/bin/startup.sh
```

---

## 🌐 Production Deployment Recommendations

1. **Use Apache HTTP Server** as reverse proxy
2. **Enable SSL/TLS** with Let's Encrypt
3. **Set up connection pooling** (Apache DBCP or HikariCP)
4. **Implement caching** (Redis or Memcached)
5. **Use environment variables** for sensitive data
6. **Set up monitoring** (Prometheus, Grafana)
7. **Configure backups** for database
8. **Use load balancer** for high availability

---

## 📞 Support

For issues or questions:
- Check Tomcat logs: `/path/to/tomcat/logs/`
- Review MySQL error logs
- Verify all configuration files

---

**Happy Banking! 🏦**
