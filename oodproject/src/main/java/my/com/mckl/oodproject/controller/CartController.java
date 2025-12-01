package my.com.mckl.oodproject.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession; 
import my.com.mckl.oodproject.model.Cart;
import my.com.mckl.oodproject.model.CartItem;
import my.com.mckl.oodproject.model.Product;
import my.com.mckl.oodproject.repository.CartItemRepository;
import my.com.mckl.oodproject.repository.CartRepository;
import my.com.mckl.oodproject.repository.ProductRepository; 

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

   // Save CartItem directly to prevent overwriting list
    @PostMapping("/add")
    public String addToCart(@RequestParam Integer productId, HttpSession session) {
        
        // 1. Log the Session ID (Check your console!)
        String sessionId = session.getId();
        System.out.println("DEBUG: Session ID = " + sessionId);
        
        // 2. Get Product
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return "redirect:/store";

        // 3. Get Cart (Ensure it is saved)
        Cart cart = cartRepository.findBySessionId(sessionId).orElse(null);
        if (cart == null) {
            cart = new Cart(sessionId);
            cartRepository.save(cart); // Save to generate cartId
            System.out.println("DEBUG: Created New Cart ID = " + cart.getCartId());
        } else {
            System.out.println("DEBUG: Found Existing Cart ID = " + cart.getCartId());
        }

        // 4. FAIL-SAFE CHECK: Use IDs to find item
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), product.getProductId());

        if (existingItemOpt.isPresent()) {
            // SCENARIO A: It exists -> Update Quantity
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
            System.out.println("DEBUG: Increased quantity for Product ID " + productId);
        } else {
            // SCENARIO B: It's new -> Insert Row
            CartItem newItem = new CartItem(cart, product, 1);
            cartItemRepository.save(newItem);
            System.out.println("DEBUG: Inserted new row for Product ID " + productId);
        }
        
        return "redirect:/store?added=true";
    }
    
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        String sessionId = session.getId();
        Cart cart = cartRepository.findBySessionId(sessionId).orElse(new Cart(sessionId));
        
        // Calculate Total Price
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            BigDecimal lineTotal = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(lineTotal);
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        
        return "cart"; // Looks for templates/cart.html
    }

    /**
     * Handles the removal of a specific item from the cart.
     * Triggered by the "Trash" icon in the cart view.
     *
     * @param cartItemId 
     * @return
     */
    @GetMapping("/remove")
    public String removeFromCart(@RequestParam("id") Integer cartItemId) {
        // 1. Delete the item from the database using the Repository
        // Note: This permanently removes the row from the 'cart_items' table
        cartItemRepository.deleteById(cartItemId);

        // 2. Redirect back to the main cart page to refresh the view
        return "redirect:/cart";
    }
}