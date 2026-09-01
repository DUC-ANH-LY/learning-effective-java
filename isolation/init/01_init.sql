-- ==========================================================
-- Initial Database Setup for Transaction Isolation Lab
-- ==========================================================

CREATE DATABASE IF NOT EXISTS isolation_lab;
USE isolation_lab;

-- Accounts table: useful for Dirty Read, Non-Repeatable Read, Lost Update experiments
DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB;

-- Inventory table: useful for Range queries, Phantom Read, and Gap Lock experiments
DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory (
    id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL
) ENGINE=InnoDB;

-- Reset and seed initial sample data
INSERT INTO accounts (id, name, balance, status) VALUES
(1, 'Alice', 1000.00, 'ACTIVE'),
(2, 'Bob', 500.00, 'ACTIVE'),
(3, 'Charlie', 200.00, 'ACTIVE');

INSERT INTO inventory (id, item_name, category, quantity, price) VALUES
(1, 'Laptop', 'Electronics', 10, 1200.00),
(2, 'Smartphone', 'Electronics', 25, 800.00),
(3, 'Desk Chair', 'Furniture', 15, 150.00),
(4, 'Coffee Maker', 'Appliances', 8, 90.00);

-- Helper procedure to reset state between experiments
DELIMITER //
CREATE PROCEDURE reset_lab_data()
BEGIN
    SET FOREIGN_KEY_CHECKS = 0;
    TRUNCATE TABLE accounts;
    TRUNCATE TABLE inventory;
    
    INSERT INTO accounts (id, name, balance, status) VALUES
    (1, 'Alice', 1000.00, 'ACTIVE'),
    (2, 'Bob', 500.00, 'ACTIVE'),
    (3, 'Charlie', 200.00, 'ACTIVE');

    INSERT INTO inventory (id, item_name, category, quantity, price) VALUES
    (1, 'Laptop', 'Electronics', 10, 1200.00),
    (2, 'Smartphone', 'Electronics', 25, 800.00),
    (3, 'Desk Chair', 'Furniture', 15, 150.00),
    (4, 'Coffee Maker', 'Appliances', 8, 90.00);
    SET FOREIGN_KEY_CHECKS = 1;
END //
DELIMITER ;
