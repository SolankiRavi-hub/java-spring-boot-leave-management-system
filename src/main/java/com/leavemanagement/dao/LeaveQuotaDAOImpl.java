package com.leavemanagement.dao;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.leavemanagement.model.LeaveQuota;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
@Repository
public class LeaveQuotaDAOImpl implements LeaveQuotaDAO {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void saveQuota(LeaveQuota quota) {
        entityManager.persist(quota);
    }
    @Override
    public LeaveQuota getQuotaByEmployeeAndType(int employeeId, String leaveType, int year) {
        List<LeaveQuota> results = entityManager.createQuery(
            "SELECT q FROM LeaveQuota q WHERE q.employeeId = :employeeId AND q.leaveType = :leaveType AND q.year = :year",
            LeaveQuota.class)
            .setParameter("employeeId", employeeId)
            .setParameter("leaveType", leaveType)
            .setParameter("year", year)
            .setMaxResults(1)
            .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    @Override
    public List<LeaveQuota> getQuotasByEmployee(int employeeId) {
        return entityManager.createQuery(
            "SELECT q FROM LeaveQuota q WHERE q.employeeId = :employeeId ORDER BY q.year DESC, q.leaveType ASC",
            LeaveQuota.class)
            .setParameter("employeeId", employeeId)
            .getResultList();
    }
    @Override
    public void updateQuota(LeaveQuota quota) {
        entityManager.merge(quota);
    }
    @Override
    public boolean hasEnoughBalance(int employeeId, String leaveType, int daysRequested, int year) {
        LeaveQuota quota = getQuotaByEmployeeAndType(employeeId, leaveType, year);
        if (quota == null) {
            return false;
        }
        return quota.getRemainingDays() >= daysRequested;
    }
    @Override
    public int getRemainingBalance(int employeeId, String leaveType, int year) {
        LeaveQuota quota = getQuotaByEmployeeAndType(employeeId, leaveType, year);
        return quota == null ? 0 : quota.getRemainingDays();
    }
}