-- Drop foreign keys and tables to recreate them with new structure
DROP TABLE IF EXISTS leave_requests;
DROP TABLE IF EXISTS leave_quota;
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

-- Create leave_requests table with leave_type
CREATE TABLE leave_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(50) DEFAULT 'Pending',
    leave_type VARCHAR(50) DEFAULT 'Casual' NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Create leave_quota table for tracking leave balance
CREATE TABLE leave_quota (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    leave_type VARCHAR(50) NOT NULL,
    total_days INT NOT NULL,
    used_days INT DEFAULT 0,
    remaining_days INT NOT NULL,
    year INT NOT NULL,
    UNIQUE KEY unique_quota (employee_id, leave_type, year),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Insert dummy admin and employee data for testing
INSERT INTO employees (name, email, department, password, role) VALUES 
('Admin User', 'admin@company.com', 'Management', 'admin123', 'Admin'),
('Alice Smith', 'alice@company.com', 'IT', 'password123', 'Employee'),
('Bob Johnson', 'bob@company.com', 'HR', 'password123', 'Employee');

-- Insert leave quota data for 2026 (current year)
INSERT INTO leave_quota (employee_id, leave_type, total_days, used_days, remaining_days, year) VALUES
(2, 'Casual', 12, 0, 12, 2026),
(2, 'Sick', 7, 0, 7, 2026),
(2, 'Personal', 3, 0, 3, 2026),
(3, 'Casual', 12, 0, 12, 2026),
(3, 'Sick', 7, 0, 7, 2026),
(3, 'Personal', 3, 0, 3, 2026);


