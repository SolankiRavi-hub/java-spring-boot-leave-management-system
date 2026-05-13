package com.leavemanagement.dao;
import java.util.List;
import com.leavemanagement.model.LeaveQuota;
public interface LeaveQuotaDAO {
    void saveQuota(LeaveQuota quota);
    LeaveQuota getQuotaByEmployeeAndType(int employeeId, String leaveType, int year);
    List<LeaveQuota> getQuotasByEmployee(int employeeId);
    void updateQuota(LeaveQuota quota);
    boolean hasEnoughBalance(int employeeId, String leaveType, int daysRequested, int year);
    int getRemainingBalance(int employeeId, String leaveType, int year);
}