package my.com.mckl.oodproject.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;
import my.com.mckl.oodproject.model.Cart;
import my.com.mckl.oodproject.model.CartItem;
import my.com.mckl.oodproject.repository.CartRepository;

@ControllerAdvice // This runs for ALL Controllers (Store, Cart, Checkout)
public class GlobalControllerAdvice {

    @Autowired
    private CartRepository cartRepository;

    // This adds "cartCount" to every HTML page automatically
    @ModelAttribute("cartCount")
    public int populateCartCount(HttpSession session) {
        String sessionId = session.getId();
        Optional<Cart> cartOpt = cartRepository.findBySessionId(sessionId);
        
        if (cartOpt.isPresent()) {
            // Sum up the quantities of all items
            return cartOpt.get().getItems().stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
        }
        
        return 0; // Empty cart
    }
}