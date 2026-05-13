package com.leavemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.leavemanagement.model.Employee;
import com.leavemanagement.model.LeaveRequest;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    // Default from address; can be overridden with MAIL_FROM env var
    private String fromAddress = System.getenv().getOrDefault("MAIL_FROM", "no-reply@company.com");

    public void sendLeaveStatusEmail(Employee employee, LeaveRequest leave, String newStatus) {
        if (employee == null || employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            return; // nothing to do
        }

        String subject = "Your leave request has been " + newStatus;
        String text = buildMessageBody(employee, leave, newStatus);

        if (mailSender == null) {
            // mail sender not configured (e.g., in tests); skip sending but don't fail
            System.err.println("MailSender not configured - skipping email to " + employee.getEmail());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(employee.getEmail());
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (Exception ex) {
            // Don't fail the business flow if email sending fails; log the error if logger is available
            System.err.println("Failed to send leave status email to " + employee.getEmail() + ": " + ex.getMessage());
        }
    }

    private String buildMessageBody(Employee employee, LeaveRequest leave, String newStatus) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(employee.getName() != null ? employee.getName() : "").append(",\n\n");
        sb.append("Your leave request (ID: ").append(leave.getId()).append(") has been ").append(newStatus).append(".\n\n");
        sb.append("Details:\n");
        sb.append("From: ").append(leave.getStartDate()).append("\n");
        sb.append("To:   ").append(leave.getEndDate()).append("\n");
        if (leave.getReason() != null) {
            sb.append("Reason: ").append(leave.getReason()).append("\n");
        }
        sb.append("\nRegards,\nLeave Management System");
        return sb.toString();
    }
}

