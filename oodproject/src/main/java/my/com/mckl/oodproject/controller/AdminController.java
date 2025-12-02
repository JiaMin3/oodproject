package my.com.mckl.oodproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import my.com.mckl.oodproject.model.Order;
import my.com.mckl.oodproject.model.Product;
import my.com.mckl.oodproject.repository.OrderRepository;
import my.com.mckl.oodproject.repository.ProductRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    // --- 1. DASHBOARD ---
    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        Object user = session.getAttribute("adminUser");
        System.out.println("--- DASHBOARD ACCESS CHECK. User: " + user + " ---"); 


        model.addAttribute("totalProducts", productRepository.count());
        
        // --- DB SAFETY FIX: Try to get count, but default to 0 if DB is broken ---
        long orderCount = 0;
        try {
            orderCount = orderRepository.count();
        } catch (Exception e) {
            System.out.println("Warning: Could not fetch order count. " + e.getMessage());
        }
        model.addAttribute("totalOrders", orderCount);
        
        model.addAttribute("pendingOrders", 0); 
        
        return "admin/dashboard";
    }

    // --- 2. PRODUCT LIST ---
    @GetMapping("/products")
    public String listProducts(Model model, HttpSession session) {
        // if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";

        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "admin/products/list"; 
    }

    // --- 3. ADD PRODUCT FORM ---
    @GetMapping("/products/add")
    public String showAddProductForm(Model model, HttpSession session) {
        // if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";

        model.addAttribute("product", new Product());
        return "admin/product-form"; 
    }

    // --- 4. SAVE PRODUCT ---
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") Product product, HttpSession session) {
        // if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";

        productRepository.save(product);
        return "redirect:/admin/products";
    }

    // --- 5. DELETE PRODUCT ---
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, HttpSession session) {
        // if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";

        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }

    // --- 6. ORDER LIST ---
    @GetMapping("/orders")
    public String listOrders(Model model, HttpSession session) {
        // if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";

        // Wrapped in try-catch to prevent crashing
        try {
            List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderId"));
            model.addAttribute("orders", orders);
        } catch (Exception e) {
            System.out.println("--- ERROR FETCHING ORDERS: " + e.getMessage() + " ---");
            model.addAttribute("orders", List.of()); // Empty list if error
        }
        
        return "admin/order-list";
    }

    // --- 7. EDIT PRODUCT FORM ---
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Integer id, Model model) {
        // Find the product by ID
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        
        model.addAttribute("product", product);
        
        return "admin/product-form"; 
    }

    // --- 8. MANUAL ORDER FORM ---
    @GetMapping("/orders/create")
    public String showCreateOrderForm(Model model, HttpSession session) {
        // You can reuse the "store" page for this, or make a simple admin form.
        // For now, let's just redirect them to the store as a simple "Manual Order" method
        return "redirect:/store"; 
    }

    // --- 9. VIEW ORDER DETAILS ---
    @GetMapping("/orders/view/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, Model model) {
        // 1. Pull the entire row from SQL
        Order order = orderRepository.findById(id).orElse(null);
        
        // 2. Put it in the model so HTML can see it
        model.addAttribute("order", order);
        
        // 3. Go to the details page
        return "admin/order-info"; 
    }

    // --- 10. SHOW EDIT FORM ---
    @GetMapping("/orders/edit/{id}")
    public String showEditOrderForm(@PathVariable("id") Integer id, Model model) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        
        model.addAttribute("order", order);
        return "admin/order-edit"; 
    }

    // --- 11. SAVE UPDATED ORDER ---
    @PostMapping("/orders/update")
    public String updateOrder(@ModelAttribute("order") Order order) {
        // We need to fetch the existing order to preserve the items list and date
        // (The form usually only sends back editable fields like Status/Address)
        Order existingOrder = orderRepository.findById(order.getOrderId()).orElse(null);
        
        if (existingOrder != null) {
            existingOrder.setStatus(order.getStatus());
            existingOrder.setCustomerName(order.getCustomerName());
            existingOrder.setCustomerEmail(order.getCustomerEmail());
            existingOrder.setCustomerPhone(order.getCustomerPhone());
            existingOrder.setCustomerAddress(order.getCustomerAddress());
            
            // Save the changes
            orderRepository.save(existingOrder);
        }
        return "redirect:/admin/orders";
    }

    // --- 12. DELETE ORDER ---
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id) {
        orderRepository.deleteById(id);
        return "redirect:/admin/orders";
    }
}