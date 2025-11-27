package my.com.mckl.oodproject.controller;

import my.com.mckl.oodproject.model.Product;
import my.com.mckl.oodproject.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StoreController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/store")
    public String showStore(Model model) {
        // 1. Fetch all products from DB
        List<Product> products = productRepository.findAll();
        
        // 2. Add to model so HTML can use it
        model.addAttribute("products", products);
        
        // 3. Return the "store.html" template
        return "store";
    }
}