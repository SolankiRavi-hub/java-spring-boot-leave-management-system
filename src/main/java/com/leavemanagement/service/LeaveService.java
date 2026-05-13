package com.leavemanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leavemanagement.dao.LeaveDAO;
import com.leavemanagement.model.LeaveRequest;

/**
 * LeaveService provides business logic for leave request operations.
 * Acts as an intermediary between controllers and the DAO layer.
 */
@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveDAO leaveDAO;

    @Autowired
    private com.leavemanagement.dao.EmployeeDAO employeeDAO;

    @Autowired
    private EmailService emailService;

    /**
     * Submit a new leave request.
     * The status is automatically set to "Pending" in the DAO.
     */
    public void applyLeave(LeaveRequest leave) {
        leaveDAO.applyLeave(leave);
    }

    /**
     * Retrieve leave history for a specific employee.
     */
    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeaveHistory(int empId) {
        return leaveDAO.getLeaveHistory(empId);
    }

    /**
     * Retrieve all pending leave requests (for manager dashboard).
     */
    @Transactional(readOnly = true)
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveDAO.getAllLeaveRequests();
    }

    /**
     * Update the status of a leave request (Approved or Rejected).
     */
    public void updateLeaveStatus(int id, String status) {
        // Fetch leave and employee details first so we can send notification after update
        com.leavemanagement.model.LeaveRequest leave = leaveDAO.getLeaveById(id);
        if (leave == null) {
            return;
        }

        leaveDAO.updateLeaveStatus(id, status);

        // Send email notification to employee (best-effort)
        com.leavemanagement.model.Employee emp = employeeDAO.getEmployeeById(leave.getEmployeeId());
        try {
            emailService.sendLeaveStatusEmail(emp, leave, status);
        } catch (Exception ex) {
            // swallow to avoid breaking flow; logging would be better
            System.err.println("Failed to send leave status notification: " + ex.getMessage());
        }
    }

    /**
     * Cancel a leave request by deleting it from the database.
     */
    public void cancelLeave(int id) {
        leaveDAO.cancelLeave(id);
    }

    /**
     * Fetch a leave request by id only if it belongs to the given employee.
     */
    @Transactional(readOnly = true)
    public LeaveRequest getLeaveByIdAndEmployee(int id, int empId) {
        return leaveDAO.getLeaveByIdAndEmployee(id, empId);
    }

    /**
     * Update leave details only while request is still pending.
     */
    public boolean updatePendingLeave(LeaveRequest leave) {
        return leaveDAO.updatePendingLeave(leave);
    }
}
