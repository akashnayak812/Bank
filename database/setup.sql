-- ================================================
-- Online Banking Platform - Database Setup Script
-- ================================================

-- Create database
CREATE DATABASE IF NOT EXISTS online_banking;
USE online_banking;

-- Drop tables if they exist (for fresh start)
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS users;

-- ==================
-- 1. Users Table
-- ==================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================
-- 2. Accounts Table
-- ==================
CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    balance DECIMAL(12,2) DEFAULT 0.00 CHECK (balance >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================
-- 3. Transactions Table
-- ==================
CREATE TABLE transactions (
    txn_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_account INT NOT NULL,
    receiver_account INT NOT NULL,
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'SUCCESS',
    transaction_type VARCHAR(20) DEFAULT 'TRANSFER',
    description VARCHAR(255),
    FOREIGN KEY (sender_account) REFERENCES accounts(account_id),
    FOREIGN KEY (receiver_account) REFERENCES accounts(account_id),
    INDEX idx_sender (sender_account),
    INDEX idx_receiver (receiver_account),
    INDEX idx_txn_date (txn_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================
-- Sample Data (Optional)
-- ==================

-- Insert sample users (password is "password123" hashed with SHA-256)
-- SHA-256 hash of "password123": ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
INSERT INTO users (name, email, password_hash) VALUES
('John Doe', 'john@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
('Jane Smith', 'jane@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
('Alice Johnson', 'alice@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
('Bob Williams', 'bob@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f');

-- Insert sample accounts with initial balances
INSERT INTO accounts (user_id, balance) VALUES
(1, 5000.00),
(2, 7500.00),
(3, 10000.00),
(4, 3000.00);

-- Insert sample transactions
INSERT INTO transactions (sender_account, receiver_account, amount, status, transaction_type) VALUES
(1, 2, 500.00, 'SUCCESS', 'TRANSFER'),
(2, 3, 1000.00, 'SUCCESS', 'TRANSFER'),
(3, 1, 250.00, 'SUCCESS', 'TRANSFER'),
(4, 2, 750.00, 'SUCCESS', 'TRANSFER');

-- ==================
-- Verification Queries
-- ==================

-- View all users
SELECT * FROM users;

-- View all accounts with user information
SELECT 
    u.user_id,
    u.name,
    u.email,
    a.account_id,
    a.balance
FROM users u
JOIN accounts a ON u.user_id = a.user_id;

-- View all transactions
SELECT 
    t.txn_id,
    t.sender_account,
    t.receiver_account,
    t.amount,
    t.txn_date,
    t.status
FROM transactions t
ORDER BY t.txn_date DESC;

-- ==================
-- Database Info
-- ==================
SELECT 'Database setup completed successfully!' AS Status;
SHOW TABLES;
