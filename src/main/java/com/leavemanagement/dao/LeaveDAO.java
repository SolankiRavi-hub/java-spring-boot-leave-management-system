package com.leavemanagement.dao;

import java.util.List;
import com.leavemanagement.model.LeaveRequest;

public interface LeaveDAO {
    void applyLeave(LeaveRequest leave);
    List<LeaveRequest> getLeaveHistory(int empId);
    List<LeaveRequest> getAllLeaveRequests();
    LeaveRequest getLeaveById(int id);
    void updateLeaveStatus(int id, String status);
    void cancelLeave(int id);
    LeaveRequest getLeaveByIdAndEmployee(int id, int empId);
    boolean updatePendingLeave(LeaveRequest leave);
}
