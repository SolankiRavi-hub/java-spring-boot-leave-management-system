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
     * Also sends email notification to employee and deducts quota if approved.
     */
    public void updateLeaveStatus(int id, String status) {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("📋 Updating Leave Request #" + id + " to status: " + status);
        System.out.println("═══════════════════════════════════════════════════════════");

        // Fetch leave and employee details first so we can send notification after update
        com.leavemanagement.model.LeaveRequest leave = leaveDAO.getLeaveById(id);
        if (leave == null) {
            System.err.println("❌ Error: Leave request #" + id + " not found");
            return;
        }

        System.out.println("✓ Leave found - Employee ID: " + leave.getEmployeeId());
        System.out.println("✓ Leave Type: " + leave.getLeaveType());
        System.out.println("✓ Period: " + leave.getStartDate() + " to " + leave.getEndDate());

        // Get employee details
        com.leavemanagement.model.Employee emp = employeeDAO.getEmployeeById(leave.getEmployeeId());
        if (emp == null) {
            System.err.println("❌ Error: Employee #" + leave.getEmployeeId() + " not found");
            return;
        }

        System.out.println("✓ Employee: " + emp.getName() + " (" + emp.getEmail() + ")");

        // Deduct balance from quota when approving
        if ("Approved".equals(status)) {
            System.out.println("→ Processing approval - deducting leave quota...");
            deductLeaveBalance(leave);
        } else if ("Rejected".equals(status)) {
            System.out.println("→ Request rejected - no quota deduction");
        }

        // Update status in database
        System.out.println("→ Updating database status...");
        leaveDAO.updateLeaveStatus(id, status);
        System.out.println("✓ Status updated in database");

        // Send email notification to employee (best-effort)
        System.out.println("→ Sending email notification...");
        try {
            emailService.sendLeaveStatusEmail(emp, leave, status);
        } catch (Exception ex) {
            System.err.println("⚠️  Warning: Failed to send notification email");
            System.err.println("   Error: " + ex.getMessage());
        }

        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("✅ Leave request processing complete");
        System.out.println("═══════════════════════════════════════════════════════════\n");
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
