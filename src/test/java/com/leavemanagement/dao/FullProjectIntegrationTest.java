package com.leavemanagement.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.leavemanagement.config.TestConfig;
import com.leavemanagement.model.Employee;
import com.leavemanagement.model.LeaveRequest;
import com.leavemanagement.service.EmployeeService;
import com.leavemanagement.service.LeaveService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfig.class })
public class FullProjectIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveService leaveService;

    @Test
    public void testAuthenticationAndRegistration() {
        // Test Admin defined in schema.sql
        Employee admin = employeeService.authenticate("admin@company.com", "admin123");
        assertNotNull(admin, "Admin should be found in initial schema data");
        assertEquals("Admin", admin.getRole());

        // Register new employee
        Employee newEmp = new Employee(0, "Test User", "test@company.com", "Testing", "pass", "Employee");
        employeeService.registerEmployee(newEmp);

        // Authenticate new employee
        Employee loggedInUser = employeeService.authenticate("test@company.com", "pass");
        assertNotNull(loggedInUser, "Newly registered user should be authenticated");
        assertEquals("Test User", loggedInUser.getName());
    }

    @Test
    public void testLeaveApplicationFlow() {
        // Authenticate existing employee from schema
        Employee alice = employeeService.authenticate("alice@company.com", "password123");
        assertNotNull(alice);

        // Initial history should be empty
        List<LeaveRequest> historyBefore = leaveService.getLeaveHistory(alice.getId());
        assertEquals(0, historyBefore.size());

        // Apply for leave
        LeaveRequest req = new LeaveRequest();
        req.setEmployeeId(alice.getId());
        req.setStartDate(Date.valueOf("2024-05-01"));
        req.setEndDate(Date.valueOf("2024-05-05"));
        req.setReason("Vacation");
        leaveService.applyLeave(req);

        // Check history
        List<LeaveRequest> historyAfter = leaveService.getLeaveHistory(alice.getId());
        assertEquals(1, historyAfter.size());
        LeaveRequest savedReq = historyAfter.get(0);
        assertEquals("Pending", savedReq.getStatus());
        assertEquals("Vacation", savedReq.getReason());

        // Test Manager viewing requests
        List<LeaveRequest> pendingRequests = leaveService.getAllLeaveRequests();
        assertTrue(pendingRequests.size() >= 1, "There should be at least 1 pending request");
        
        // Test Manager updating status
        leaveService.updateLeaveStatus(savedReq.getId(), "Approved");
        
        // Verify update
        List<LeaveRequest> historyUpdated = leaveService.getLeaveHistory(alice.getId());
        assertEquals("Approved", historyUpdated.get(0).getStatus());
    }
}
