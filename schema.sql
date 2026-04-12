-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS leave_management;
USE leave_management;

-- Drop foreign keys and tables to recreate them with new structure
DROP TABLE IF EXISTS leave_requests;
DROP TABLE IF EXISTS employees;

-- Create employees table with password and role
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    department VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'Employee'
);

-- Create leave_requests table
CREATE TABLE leave_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(50) DEFAULT 'Pending',
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Insert dummy admin and employee data for testing
INSERT INTO employees (name, email, department, password, role) VALUES 
('Admin User', 'admin@company.com', 'Management', 'admin123', 'Admin'),
('Alice Smith', 'alice@company.com', 'IT', 'password123', 'Employee'),
('Bob Johnson', 'bob@company.com', 'HR', 'password123', 'Employee');
