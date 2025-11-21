package my.com.mckl.oodproject.controller;

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
                       RedirectAttributes redirectAttributes) {
        
        if ("admin".equals(username) && "admin123".equals(password)) {
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back!");
            return "redirect:/admin";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials");
            return "redirect:/admin/login";
        }
    }
    
    @GetMapping("/admin/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "Logged out successfully");
        return "redirect:/admin/login";
    }
}