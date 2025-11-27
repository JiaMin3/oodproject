package my.com.mckl.oodproject.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    
    @GetMapping("/admin/login")
    public String showLoginPage() {
        return "admin/login";
    }
    
    @PostMapping("/admin/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpSession session, 
                        RedirectAttributes redirectAttributes) {
        
        System.out.println("--- LOGIN ATTEMPT: " + username + " ---"); // DEBUG PRINT

        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("adminUser", username);
            System.out.println("--- LOGIN SUCCESS! Session ID: " + session.getId() + " ---"); // DEBUG PRINT
            
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back!");
            return "redirect:/admin";
        } else {
            System.out.println("--- LOGIN FAILED (Wrong Password) ---"); // DEBUG PRINT
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials");
            return "redirect:/admin/login";
        }
    }
    
    @GetMapping("/admin/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Logged out successfully");
        return "redirect:/admin/login";
    }
}