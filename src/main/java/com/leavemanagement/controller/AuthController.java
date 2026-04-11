package com.leavemanagement.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.leavemanagement.model.Employee;
import com.leavemanagement.service.EmployeeService;


@Controller
public class AuthController {

    @Autowired
    private EmployeeService employeeService;


    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session, Model model) {
        Employee employee;
        try {
            employee = employeeService.authenticate(email, password);
        } catch (DataAccessException ex) {
            model.addAttribute("error", "Database error while logging in. Please check DB connection.");
            return "login";
        }

        if (employee != null) {
            session.setAttribute("loggedInUser", employee);
            if ("Admin".equals(employee.getRole())) {
                return "redirect:/viewRequests";
            } else {
                return "redirect:/viewHistory";
            }
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }


    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> loginJson(@RequestBody Employee request, HttpSession session) {
        try {
            Employee employee = employeeService.authenticate(request.getEmail(), request.getPassword());

            if (employee == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
            }

            session.setAttribute("loggedInUser", employee);

            if ("Admin".equals(employee.getRole())) {
                return ResponseEntity.ok("Login successful. Role: Admin. Redirect to /viewRequests");
            }
            return ResponseEntity.ok("Login successful. Role: Employee. Redirect to /viewHistory");
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Database error while logging in. Please check DB connection.");
        }
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "register";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute Employee employee, Model model) {
        try {
            if (employee.getRole() == null || employee.getRole().isEmpty()) {
                employee.setRole("Employee");
            }

            employeeService.registerEmployee(employee);
            return "redirect:/login";
        } catch (DuplicateKeyException e) {
            model.addAttribute("error", "Registration failed. Email already exists.");
            return "register";
        } catch (DataAccessException e) {
            model.addAttribute("error", "Registration failed due to database issue. Check DB/schema.");
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
            return "register";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
        
        // Check if user is logged in
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        // Fetch fresh employee details from database
        Employee employee = employeeService.getEmployeeById(loggedInUser.getId());
        model.addAttribute("employee", employee);
        return "profile";
    }
}
