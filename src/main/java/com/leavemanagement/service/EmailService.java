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
            System.err.println("⚠️  Email not sent: Employee email is missing");
            return;
        }

        if (mailSender == null) {
            System.err.println("⚠️  MailSender not configured - Please check mail.properties or environment variables");
            System.err.println("   Available SMTP server configurations:");
            System.err.println("   1. Set environment variables: MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD");
            System.err.println("   2. Configure mail.properties in src/main/resources/");
            return;
        }

        String subject = "Your leave request has been " + newStatus;
        String text = buildMessageBody(employee, leave, newStatus);
        String toEmail = employee.getEmail();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        try {
            System.out.println("📧 Sending email notification...");
            System.out.println("   To: " + toEmail);
            System.out.println("   Subject: " + subject);
            System.out.println("   From: " + fromAddress);

            mailSender.send(message);

            System.out.println("✅ Email sent successfully to " + toEmail);
        } catch (Exception ex) {
            System.err.println("❌ Failed to send leave status email to " + toEmail);
            System.err.println("   Error: " + ex.getMessage());
            System.err.println("   Type: " + ex.getClass().getSimpleName());

            // Print root cause
            Throwable cause = ex.getCause();
            if (cause != null) {
                System.err.println("   Cause: " + cause.getMessage());
            }

            // Don't fail the business flow if email sending fails
            ex.printStackTrace();
        }
    }

    private String buildMessageBody(Employee employee, LeaveRequest leave, String newStatus) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(employee.getName() != null ? employee.getName() : "Employee").append(",\n\n");
        sb.append("Your leave request (ID: ").append(leave.getId()).append(") has been ").append(newStatus).append(".\n\n");
        sb.append("Details:\n");
        sb.append("Leave Type: ").append(leave.getLeaveType() != null ? leave.getLeaveType() : "Not specified").append("\n");
        sb.append("From: ").append(leave.getStartDate()).append("\n");
        sb.append("To:   ").append(leave.getEndDate()).append("\n");
        if (leave.getReason() != null && !leave.getReason().trim().isEmpty()) {
            sb.append("Reason: ").append(leave.getReason()).append("\n");
        }
        sb.append("\nRegards,\nLeave Management System");
        return sb.toString();
    }
}



