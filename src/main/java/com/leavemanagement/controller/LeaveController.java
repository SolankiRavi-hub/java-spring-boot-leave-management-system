package com.leavemanagement.controller;

import java.beans.PropertyEditorSupport;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leavemanagement.model.Employee;
import com.leavemanagement.model.LeaveRequest;
import com.leavemanagement.service.LeaveService;


@Controller
public class LeaveController {

    @Autowired
    private LeaveService leaveService;


    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUser") != null;
    }


    private boolean isAdmin(HttpSession session) {
        Employee emp = (Employee) session.getAttribute("loggedInUser");
        return emp != null && "Admin".equals(emp.getRole());
    }


    @GetMapping("/")
    public String home(HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (isAdmin(session)) {
            return "redirect:/viewRequests";
        }
        return "redirect:/viewHistory";
    }


    @GetMapping("/applyLeave")
    public String showApplyForm(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        
        Employee emp = (Employee) session.getAttribute("loggedInUser");
        
        // Get remaining balance for all leave types
        int casualRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Casual");
        int sickRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Sick");
        int personalRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Personal");
        
        model.addAttribute("leaveRequest", new LeaveRequest());
        model.addAttribute("casualRemaining", casualRemaining);
        model.addAttribute("sickRemaining", sickRemaining);
        model.addAttribute("personalRemaining", personalRemaining);
        
        return "applyLeave";
    }


    @PostMapping("/applyLeave")
    public String submitLeave(@ModelAttribute LeaveRequest leaveRequest,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Invalid input. Please check dates and try again.");
            return "applyLeave";
        }

        if (leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
            model.addAttribute("error", "Start date and end date are required.");
            return "applyLeave";
        }

        if (leaveRequest.getEndDate().before(leaveRequest.getStartDate())) {
            model.addAttribute("error", "End date cannot be before start date.");
            return "applyLeave";
        }

        Employee emp = (Employee) session.getAttribute("loggedInUser");
        
        // Validate leave type is selected
        if (leaveRequest.getLeaveType() == null || leaveRequest.getLeaveType().isEmpty()) {
            model.addAttribute("error", "Please select a leave type.");
            return "applyLeave";
        }
        
        // Calculate days requested
        int daysRequested = leaveService.calculateLeaveDays(
            leaveRequest.getStartDate(), 
            leaveRequest.getEndDate()
        );
        
        // Check if employee has enough leave balance
        if (!leaveService.hasEnoughLeaveBalance(emp.getId(), leaveRequest.getLeaveType(), daysRequested)) {
            // Get remaining balance for all types to show
            int casualRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Casual");
            int sickRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Sick");
            int personalRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Personal");
            
            int currentTypeRemaining = leaveRequest.getLeaveType().equals("Casual") ? casualRemaining :
                                       leaveRequest.getLeaveType().equals("Sick") ? sickRemaining :
                                       personalRemaining;

            String errorMsg = String.format(
                "❌ Insufficient %s leave balance! You requested %d days but have only %d days remaining.\n\n" +
                "Your remaining balance:\n" +
                "• Casual Leave: %d days\n" +
                "• Sick Leave: %d days\n" +
                "• Personal Leave: %d days",
                leaveRequest.getLeaveType(), 
                daysRequested,
                currentTypeRemaining,
                casualRemaining,
                sickRemaining,
                personalRemaining
            );
            
            model.addAttribute("error", errorMsg);
            model.addAttribute("casualRemaining", casualRemaining);
            model.addAttribute("sickRemaining", sickRemaining);
            model.addAttribute("personalRemaining", personalRemaining);
            model.addAttribute("leaveRequest", leaveRequest);
            return "applyLeave";
        }

        leaveRequest.setEmployeeId(emp.getId());
        leaveRequest.setStatus("Pending");

        leaveService.applyLeave(leaveRequest);
        model.addAttribute("success", "Leave request submitted successfully for " + daysRequested + " days!");
        return "redirect:/viewHistory";
    }


    @GetMapping("/viewHistory")
    public String viewHistory(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        Employee emp = (Employee) session.getAttribute("loggedInUser");
        List<LeaveRequest> history = leaveService.getLeaveHistory(emp.getId());
        model.addAttribute("history", history);
        return "leaveHistory";
    }


    @PostMapping("/cancelLeave")
    public String cancelLeave(@RequestParam("id") int id, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        // Delete the leave request
        leaveService.cancelLeave(id);
        return "redirect:/viewHistory";
    }


    @GetMapping("/editLeave")
    public String showEditLeaveForm(@RequestParam("id") int id, HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        Employee emp = (Employee) session.getAttribute("loggedInUser");
        LeaveRequest leave = leaveService.getLeaveByIdAndEmployee(id, emp.getId());
        if (leave == null || !"Pending".equals(leave.getStatus())) {
            return "redirect:/viewHistory";
        }
        
        // Get remaining balance for all leave types
        int casualRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Casual");
        int sickRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Sick");
        int personalRemaining = leaveService.getRemainingLeaveBalance(emp.getId(), "Personal");

        model.addAttribute("leaveRequest", leave);
        model.addAttribute("casualRemaining", casualRemaining);
        model.addAttribute("sickRemaining", sickRemaining);
        model.addAttribute("personalRemaining", personalRemaining);
        return "editLeave";
    }


    @PostMapping("/editLeave")
    public String editLeave(@ModelAttribute LeaveRequest leaveRequest,
                            BindingResult bindingResult,
                            HttpSession session,
                            Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Invalid input. Please check dates and try again.");
            return "editLeave";
        }

        if (leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
            model.addAttribute("error", "Start date and end date are required.");
            return "editLeave";
        }

        if (leaveRequest.getEndDate().before(leaveRequest.getStartDate())) {
            model.addAttribute("error", "End date cannot be before start date.");
            return "editLeave";
        }

        Employee emp = (Employee) session.getAttribute("loggedInUser");
        leaveRequest.setEmployeeId(emp.getId());

        boolean updated = leaveService.updatePendingLeave(leaveRequest);
        if (!updated) {
            model.addAttribute("error", "Unable to edit this leave. It may already be processed.");
            return "editLeave";
        }

        return "redirect:/viewHistory";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    setValue(Date.valueOf(text));
                }
            }
        });
    }
}
