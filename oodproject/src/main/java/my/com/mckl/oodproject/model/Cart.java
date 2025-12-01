package my.com.mckl.oodproject.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;

    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;

    // Relationship: One Cart has Many Items
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> items = new ArrayList<>();

    // Constructors
    public Cart() {}
    public Cart(String sessionId) {
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public Integer getCartId() { return cartId; }
    public void setCartId(Integer cartId) { this.cartId = cartId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}