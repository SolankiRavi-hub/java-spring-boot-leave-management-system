package com.leavemanagement.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.leavemanagement.model.LeaveRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class LeaveDAOImpl implements LeaveDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void applyLeave(LeaveRequest leave) {
        leave.setStatus("Pending");
        entityManager.persist(leave);
    }

    @Override
    public List<LeaveRequest> getLeaveHistory(int empId) {
        return entityManager.createQuery(
                "SELECT l FROM LeaveRequest l WHERE l.employeeId = :employeeId ORDER BY l.id DESC", LeaveRequest.class)
            .setParameter("employeeId", empId)
            .getResultList();
    }

    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        return entityManager.createQuery("SELECT l FROM LeaveRequest l ORDER BY l.id ASC", LeaveRequest.class)
            .getResultList();
    }

    @Override
    public void updateLeaveStatus(int id, String status) {
        LeaveRequest leaveRequest = entityManager.find(LeaveRequest.class, id);
        if (leaveRequest != null) {
            leaveRequest.setStatus(status);
            entityManager.createQuery("UPDATE LeaveRequest l SET l.status = :status WHERE l.id = :id")
                                  .setParameter("status", status)
                                  .setParameter("id", id)
                                  .executeUpdate();
        }
    }

    @Override
    public LeaveRequest getLeaveById(int id) {
        return entityManager.find(LeaveRequest.class, id);
    }

    @Override
    public void cancelLeave(int id) {
        LeaveRequest leaveRequest = entityManager.find(LeaveRequest.class, id);
        if (leaveRequest != null) {
            entityManager.remove(leaveRequest);
        }
    }

    @Override
    public LeaveRequest getLeaveByIdAndEmployee(int id, int empId) {
        List<LeaveRequest> results = entityManager.createQuery(
                "SELECT l FROM LeaveRequest l WHERE l.id = :id AND l.employeeId = :employeeId", LeaveRequest.class)
            .setParameter("id", id)
            .setParameter("employeeId", empId)
            .setMaxResults(1)
            .getResultList();

        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }


    @Override
    public boolean updatePendingLeave(LeaveRequest leave) {
        List<LeaveRequest> results = entityManager.createQuery(
                "SELECT l FROM LeaveRequest l WHERE l.id = :id AND l.employeeId = :employeeId AND l.status = 'Pending'",
                LeaveRequest.class)
            .setParameter("id", leave.getId())
            .setParameter("employeeId", leave.getEmployeeId())
            .setMaxResults(1)
            .getResultList();

        if (results.isEmpty()) {
            return false;
        }

        LeaveRequest existing = results.get(0);
        existing.setStartDate(leave.getStartDate());
        existing.setEndDate(leave.getEndDate());
        existing.setReason(leave.getReason());
        entityManager.merge(existing);
        return true;
    }
}
