package com.leavemanagement.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "leave_quota")
public class LeaveQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "employee_id", nullable = false)
    private int employeeId;

    @Column(name = "leave_type", nullable = false, length = 50)
    private String leaveType;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(name = "used_days", nullable = false)
    private int usedDays;

    @Column(name = "remaining_days", nullable = false)
    private int remainingDays;

    @Column(name = "year", nullable = false)
    private int year;

    public LeaveQuota() {
    }

    public LeaveQuota(int employeeId, String leaveType, int totalDays, int usedDays, int remainingDays, int year) {
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
        this.remainingDays = remainingDays;
        this.year = year;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

    public int getUsedDays() { return usedDays; }
    public void setUsedDays(int usedDays) { this.usedDays = usedDays; }

    public int getRemainingDays() { return remainingDays; }
    public void setRemainingDays(int remainingDays) { this.remainingDays = remainingDays; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}

