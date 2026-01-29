package com.example.grocart_backend.controller;

import com.example.grocart_backend.model.CartItem;
import com.example.grocart_backend.model.OrderEntity;
import com.example.grocart_backend.repository.CartRepository;
import com.example.grocart_backend.repository.OrderRepository;
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

    @PostMapping("/place/{userId}")
    public ResponseEntity<String> placeOrder(@PathVariable Long userId, @RequestBody Integer total) {
        OrderEntity newOrder = new OrderEntity();
        newOrder.setUserId(userId);
        newOrder.setTotalAmount(total);

        orderRepository.save(newOrder);

        // Cart clear karne ka logic
        List<CartItem> userItems = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(userItems);

        return ResponseEntity.ok("Order placed and cart cleared!");
    }
}
