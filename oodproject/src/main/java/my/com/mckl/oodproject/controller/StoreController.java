package my.com.mckl.oodproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import my.com.mckl.oodproject.model.Product;
import my.com.mckl.oodproject.repository.ProductRepository;

@Controller
public class StoreController {


    // Redirects "localhost:8080/" directly to the Login page
    @GetMapping("/")
    public String home() {
        // Changes the destinationto the login page
        return "redirect:/admin/login";
    }

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/store")
public String showStore(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
    List<Product> products;
    
    // Requires you to add findByNameContainingIgnoreCase in Repository
    // If you don't want to touch Repository, just keep your original findAll()
    if (keyword != null && !keyword.isEmpty()) {
        // You would need to add this method to ProductRepository first
        // products = productRepository.findByNameContainingIgnoreCase(keyword);
        products = productRepository.findAll(); // Placeholder until repo is updated
    } else {
        products = productRepository.findAll();
    }
    
    model.addAttribute("products", products);
    return "store";
}
}