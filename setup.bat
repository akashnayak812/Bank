@echo off
REM =========================================
REM Online Banking Platform - Quick Start Script (Windows)
REM =========================================

echo.
echo ========================================
echo Online Banking Platform - Quick Start
echo ========================================
echo.

REM Check Java
echo Checking Java installation...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Java found
) else (
    echo [ERROR] Java not found. Please install Java 11+
    pause
    exit /b 1
)

REM Check Maven
echo Checking Maven installation...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Maven found
) else (
    echo [WARNING] Maven not found. You'll need to build manually.
)

REM Check MySQL
echo Checking MySQL installation...
mysql --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] MySQL found
) else (
    echo [ERROR] MySQL not found. Please install MySQL 8.0+
    pause
    exit /b 1
)

echo.
echo ========================================
echo Setup Steps:
echo ========================================

REM Step 1: Database Setup
echo.
echo Step 1: Database Setup
echo ----------------------
set /p MYSQL_PASSWORD="Enter MySQL root password: "

echo Creating database...
mysql -u root -p%MYSQL_PASSWORD% < database\setup.sql

if %errorlevel% equ 0 (
    echo [OK] Database created successfully
) else (
    echo [ERROR] Database creation failed
    pause
    exit /b 1
)

REM Step 2: Build Project
echo.
echo Step 2: Build Project
echo --------------------

mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo Building with Maven...
    mvn clean package
    
    if %errorlevel% equ 0 (
        echo [OK] Build successful
        echo WAR file created: target\online-banking.war
    ) else (
        echo [ERROR] Build failed
        pause
        exit /b 1
    )
) else (
    echo [WARNING] Maven not found. Please build manually.
)

REM Step 3: Deployment Instructions
echo.
echo ========================================
echo Deployment Instructions:
echo ========================================
echo.
echo 1. Copy WAR file to Tomcat webapps directory:
echo    copy target\online-banking.war "C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\"
echo.
echo 2. Start Tomcat:
echo    "C:\Program Files\Apache Software Foundation\Tomcat 9.0\bin\startup.bat"
echo.
echo 3. Access the application:
echo    http://localhost:8080/online-banking/
echo.
echo Default Login:
echo    Email: john@example.com
echo    Password: password123
echo.
echo [OK] Setup complete!
echo.

pause
