package com.leavemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leavemanagement.dao.EmployeeDAO;
import com.leavemanagement.model.Employee;

/**
 * EmployeeService provides business logic for employee-related operations.
 * Acts as an intermediary between controllers and the DAO layer.
 */
@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeDAO employeeDAO;

    /**
     * Register a new employee.
     */
    public void registerEmployee(Employee employee) {
        employeeDAO.registerEmployee(employee);
    }

    /**
     * Authenticate an employee with email and password.
     * Returns Employee object if successful, null otherwise.
     */
    @Transactional(readOnly = true)
    public Employee authenticate(String email, String password) {
        return employeeDAO.authenticate(email, password);
    }

    /**
     * Retrieve employee details by ID.
     */
    @Transactional(readOnly = true)
    public Employee getEmployeeById(int id) {
        return employeeDAO.getEmployeeById(id);
    }
}
