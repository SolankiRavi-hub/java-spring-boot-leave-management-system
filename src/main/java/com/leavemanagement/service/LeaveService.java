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

    @Autowired
    private com.leavemanagement.dao.LeaveQuotaDAO leaveQuotaDAO;

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

        // Deduct balance from quota when approving
        if ("Approved".equals(status)) {
            deductLeaveBalance(leave);
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

    /**
     * Calculate number of days between two dates (inclusive of both end dates).
     */
    public int calculateLeaveDays(java.sql.Date startDate, java.sql.Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        // +1 to include both start and end dates
        return (int) (diff / (1000 * 60 * 60 * 24)) + 1;
    }

    /**
     * Check if employee has enough leave balance for the requested days and type.
     */
    @Transactional(readOnly = true)
    public boolean hasEnoughLeaveBalance(int employeeId, String leaveType, int daysRequested) {
        int currentYear = java.time.Year.now().getValue();
        return leaveQuotaDAO.hasEnoughBalance(employeeId, leaveType, daysRequested, currentYear);
    }

    /**
     * Get remaining leave balance for employee and leave type.
     */
    @Transactional(readOnly = true)
    public int getRemainingLeaveBalance(int employeeId, String leaveType) {
        int currentYear = java.time.Year.now().getValue();
        return leaveQuotaDAO.getRemainingBalance(employeeId, leaveType, currentYear);
    }

    /**
     * Deduct leave days from quota when admin approves a request.
     */
    public void deductLeaveBalance(LeaveRequest leave) {
        int currentYear = java.time.Year.now().getValue();
        com.leavemanagement.model.LeaveQuota quota = leaveQuotaDAO.getQuotaByEmployeeAndType(
            leave.getEmployeeId(), leave.getLeaveType(), currentYear);

        if (quota != null && quota.getRemainingDays() >= 0) {
            int daysRequested = calculateLeaveDays(leave.getStartDate(), leave.getEndDate());
            quota.setUsedDays(quota.getUsedDays() + daysRequested);
            quota.setRemainingDays(quota.getRemainingDays() - daysRequested);
            leaveQuotaDAO.updateQuota(quota);
        }
    }

    /**
     * Restore leave balance if request is rejected.
     */
    public void restoreLeaveBalance(LeaveRequest leave) {
        // This is for future use if we want to handle rejections
        // Currently balance is only deducted on approval
    }
}
