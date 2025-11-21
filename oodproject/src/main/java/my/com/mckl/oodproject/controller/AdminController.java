package my.com.mckl.oodproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @GetMapping
    public String adminDashboard(Model model) {
        // For now, just show the dashboard
        model.addAttribute("totalProducts", 0);
        model.addAttribute("totalOrders", 0);
        model.addAttribute("pendingOrders", 0);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/products")
    public String listProducts(Model model) {
        return "admin/products/list";
    }
    
    @GetMapping("/orders")
    public String listOrders(Model model) {
        return "admin/orders/list";
    }
}
