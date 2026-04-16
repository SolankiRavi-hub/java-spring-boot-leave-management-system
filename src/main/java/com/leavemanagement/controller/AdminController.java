package com.leavemanagement.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leavemanagement.model.Employee;
import com.leavemanagement.model.LeaveRequest;
import com.leavemanagement.service.LeaveService;


@Controller
public class AdminController {

    @Autowired
    private LeaveService leaveService;

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUser") != null;
    }

    private boolean isAdmin(HttpSession session) {
        Employee emp = (Employee) session.getAttribute("loggedInUser");
        return emp != null && "Admin".equals(emp.getRole());
    }

    @GetMapping("/viewRequests")
    public String viewAllRequests(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!isAdmin(session)) {
            return "redirect:/viewHistory";
        }

        List<LeaveRequest> requests = leaveService.getAllLeaveRequests();
        model.addAttribute("requests", requests);
        return "adminDashboard";
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("id") int id,
                               @RequestParam("status") String status,
                               HttpSession session) {
        if (!isLoggedIn(session) || !isAdmin(session)) {
            return "redirect:/login";
        }

        if (!"Approved".equals(status) && !"Rejected".equals(status)) {
            return "redirect:/viewRequests";
        }

        leaveService.updateLeaveStatus(id, status);
        return "redirect:/viewRequests";
    }
}

