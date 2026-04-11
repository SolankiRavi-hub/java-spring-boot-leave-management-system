package com.leavemanagement.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.leavemanagement.model.Employee;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registerEmployee(Employee employee) {
        if (employee.getRole() == null || employee.getRole().trim().isEmpty()) {
            employee.setRole("Employee");
        }
        entityManager.persist(employee);
    }

    @Override
    public Employee authenticate(String email, String password) {
        List<Employee> results = entityManager.createQuery(
                "SELECT e FROM Employee e WHERE e.email = :email AND e.password = :password", Employee.class)
            .setParameter("email", email)
            .setParameter("password", password)
            .setMaxResults(1)
            .getResultList();

        if (results.isEmpty()) {
            return null; // authentication failed
        }
        return results.get(0);
    }

    @Override
    public Employee getEmployeeById(int id) {
        return entityManager.find(Employee.class, id);
    }
}
