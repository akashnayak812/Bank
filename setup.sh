#!/bin/bash

# =========================================
# Online Banking Platform - Quick Start Script
# =========================================

echo "🏦 Online Banking Platform - Quick Start"
echo "========================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check Java
echo "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo -e "${GREEN}✓ Java found: $JAVA_VERSION${NC}"
else
    echo -e "${RED}✗ Java not found. Please install Java 11+${NC}"
    exit 1
fi

# Check Maven
echo "Checking Maven installation..."
if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version | grep "Apache Maven" | awk '{print $3}')
    echo -e "${GREEN}✓ Maven found: $MAVEN_VERSION${NC}"
else
    echo -e "${YELLOW}⚠ Maven not found. You'll need to build manually.${NC}"
fi

# Check MySQL
echo "Checking MySQL installation..."
if command -v mysql &> /dev/null; then
    echo -e "${GREEN}✓ MySQL found${NC}"
else
    echo -e "${RED}✗ MySQL not found. Please install MySQL 8.0+${NC}"
    exit 1
fi

echo ""
echo "========================================"
echo "Setup Steps:"
echo "========================================"

# Step 1: Database Setup
echo ""
echo "Step 1: Database Setup"
echo "----------------------"
read -p "Enter MySQL root password: " -s MYSQL_PASSWORD
echo ""

echo "Creating database..."
mysql -u root -p"$MYSQL_PASSWORD" < database/setup.sql 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database created successfully${NC}"
else
    echo -e "${RED}✗ Database creation failed${NC}"
    exit 1
fi

# Step 2: Configure Database Connection
echo ""
echo "Step 2: Configure Database Connection"
echo "-------------------------------------"
echo "Updating DBConnection.java..."

# Update password in DBConnection.java
sed -i.bak "s/private static final String PASSWORD = \".*\";/private static final String PASSWORD = \"$MYSQL_PASSWORD\";/" src/util/DBConnection.java

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database connection configured${NC}"
else
    echo -e "${YELLOW}⚠ Please manually update src/util/DBConnection.java${NC}"
fi

# Step 3: Build Project
echo ""
echo "Step 3: Build Project"
echo "--------------------"

if command -v mvn &> /dev/null; then
    echo "Building with Maven..."
    mvn clean package
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Build successful${NC}"
        echo -e "WAR file created: ${GREEN}target/online-banking.war${NC}"
    else
        echo -e "${RED}✗ Build failed${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}⚠ Maven not found. Please build manually.${NC}"
fi

# Step 4: Deployment Instructions
echo ""
echo "========================================"
echo "Deployment Instructions:"
echo "========================================"
echo ""
echo "1. Copy WAR file to Tomcat webapps directory:"
echo "   cp target/online-banking.war /path/to/tomcat/webapps/"
echo ""
echo "2. Start Tomcat:"
echo "   /path/to/tomcat/bin/startup.sh"
echo ""
echo "3. Access the application:"
echo "   http://localhost:8080/online-banking/"
echo ""
echo "Default Login:"
echo "   Email: john@example.com"
echo "   Password: password123"
echo ""
echo -e "${GREEN}Setup complete! 🎉${NC}"
echo ""
