package com.example.grocart_backend.controller;

import com.example.grocart_backend.model.CartItem;
import com.example.grocart_backend.model.OrderEntity;
import com.example.grocart_backend.repository.CartRepository;
import com.example.grocart_backend.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;

    @Transactional
    @PostMapping("/place/{userId}")
    public ResponseEntity<String> placeOrder(@PathVariable Long userId, @RequestBody Integer total) {
        List<CartItem> userItems = cartRepository.findByUserId(userId);

        if (userItems.isEmpty()) return ResponseEntity.badRequest().body("Cart Khali Hai!");

        StringBuilder details = new StringBuilder();
        for (CartItem item : userItems) {

            details.append(item.getItemName()).append(" x").append(item.getQuantity()).append(", ");
        }

        OrderEntity newOrder = new OrderEntity();
        newOrder.setUserId(userId);
        newOrder.setTotalAmount(total);
        newOrder.setItemDetails(details.toString());

        System.out.println("DEBUG: Saving Order Details -> " + details.toString());

        orderRepository.save(newOrder);
        cartRepository.deleteAll(userItems);

        return ResponseEntity.ok("Order Saved!");
    }
}
