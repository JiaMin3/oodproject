package my.com.mckl.oodproject.controller;

import jakarta.servlet.http.HttpSession;
import my.com.mckl.oodproject.model.*;
import my.com.mckl.oodproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    // Show the Checkout Form
    @GetMapping
    public String showCheckout(Model model, HttpSession session) {
        // Check if cart is empty
        String sessionId = session.getId();
        Optional<Cart> cartOpt = cartRepository.findBySessionId(sessionId);
        
        if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
            return "redirect:/store"; // Kick them out if cart is empty
        }

        // Calculate total again to show on checkout page
        Cart cart = cartOpt.get();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            total = total.add(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        model.addAttribute("order", new Order());
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cart.getItems());
        
        return "checkout";
    }

    // Process the Order
    @PostMapping("/process")
    public String processOrder(@ModelAttribute("order") Order order, HttpSession session) {
        String sessionId = session.getId();
        Cart cart = cartRepository.findBySessionId(sessionId).orElse(null);

        if (cart != null) {
            // 1. Set Order Details
            order.setStatus("PENDING");
            
            // 2. Calculate Total and Convert CartItems to OrderItems
            BigDecimal finalTotal = BigDecimal.ZERO;
            
            for (CartItem cartItem : cart.getItems()) {
                BigDecimal subtotal = cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
                finalTotal = finalTotal.add(subtotal);
                
                // Create OrderItem
                OrderItem orderItem = new OrderItem(order, cartItem.getProduct(), cartItem.getQuantity(), subtotal);
                order.getItems().add(orderItem);
            }
            
            order.setTotalAmount(finalTotal);
            
            // 3. Save Order (Cascade will save OrderItems)
            orderRepository.save(order);
            
            // 4. Clear the Cart (Delete the cart entirely)
            cartRepository.delete(cart);
        }

        return "redirect:/store?success=true";
    }
}