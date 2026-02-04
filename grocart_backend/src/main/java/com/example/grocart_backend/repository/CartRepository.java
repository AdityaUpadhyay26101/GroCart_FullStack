package com.example.grocart_backend.repository;

import com.example.grocart_backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // ✅ Important for null safety

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {


    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndItemName(Long userId, String itemName);

    void deleteByUserId(Long userId);
}