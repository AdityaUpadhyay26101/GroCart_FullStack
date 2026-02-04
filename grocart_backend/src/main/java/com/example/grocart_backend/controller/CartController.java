package com.example.grocart_backend.controller;

import com.example.grocart_backend.model.CartItem;
import com.example.grocart_backend.repository.CartRepository;
import com.example.grocart_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // ✅ Added for delete operations
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long userId) {
        // Data Isolation: Fetching items only for this specific user
        List<CartItem> items = cartRepository.findByUserId(userId);
        return ResponseEntity.ok(items);
    }

    // Cart for Users and Seprating data rom one another to secure
    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addToCart(@PathVariable Long userId, @RequestBody CartItem newItem) {
        return userRepository.findById(userId).map(user -> {


            if (newItem.getItemName() == null || newItem.getItemName().isEmpty()) {
                return ResponseEntity.badRequest().body("Item name missing in request");
            }

            // Check if item already exists for this user
            Optional<CartItem> existingItem = cartRepository.findByUserIdAndItemName(userId, newItem.getItemName());

            if (existingItem.isPresent()) {

                CartItem itemToUpdate = existingItem.get();
                int currentQty = itemToUpdate.getQuantity() != null ? itemToUpdate.getQuantity() : 0;

                int newQty = newItem.getQuantity() != null ? newItem.getQuantity() : 1;

                itemToUpdate.setQuantity(currentQty + newQty);
                cartRepository.save(itemToUpdate);
                return ResponseEntity.ok("Quantity updated for " + newItem.getItemName());
            } else {

                newItem.setUser(user);

                // Set defaults for missing fields from Android
                if (newItem.getQuantity() == null) newItem.setQuantity(1);
                if (newItem.getItemPrice() == null) newItem.setItemPrice(0);

                cartRepository.save(newItem);
                return ResponseEntity.ok("New item added to MySQL: " + newItem.getItemName());
            }
        }).orElse(ResponseEntity.badRequest().body("User not found"));
    }

    @Transactional
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartRepository.deleteByUserId(userId);
        return ResponseEntity.ok("Cart cleared for user ID: " + userId);
    }
}