package my.com.mckl.oodproject.controller;

import java.math.BigDecimal;

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

    // This method handles the "Add to Cart" button click
    @PostMapping("/add")
    public String addToCart(@RequestParam Integer productId, HttpSession session) {
        
        // 1. Get the User's Session ID (This acts as their temporary ID)
        String sessionId = session.getId();
        
        // 2. Find their Cart, or create a new one if it doesn't exist
        Cart cart = cartRepository.findBySessionId(sessionId).orElse(new Cart(sessionId));
        
        // If it's a new cart (no ID yet), save it first
        if (cart.getCartId() == null) {
            cartRepository.save(cart);
        }

        // 3. Find the Product they want to buy
        Product product = productRepository.findById(productId).orElse(null);

        if (product != null) {
            // 4. Check if this product is ALREADY in their cart
            CartItem existingItem = null;
            
            // Loop through current items to find a match
            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getProductId().equals(productId)) {
                    existingItem = item;
                    break;
                }
            }

            if (existingItem != null) {
                // If exists, just increase quantity (+1)
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                cartItemRepository.save(existingItem);
            } else {
                // If not, create a new item
                CartItem newItem = new CartItem(cart, product, 1);
                cartItemRepository.save(newItem);
            }
        }
        
        // 5. Send them back to the store page
        return "redirect:/store";
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