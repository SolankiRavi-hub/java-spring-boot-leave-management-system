package com.leavemanagement.dao;

import com.leavemanagement.model.Employee;

public interface EmployeeDAO {
    void registerEmployee(Employee employee);
    Employee authenticate(String email, String password);
    Employee getEmployeeById(int id);
}
