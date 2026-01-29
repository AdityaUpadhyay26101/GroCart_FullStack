package com.example.grocart_backend.repository;

import com.example.grocart_backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // ✅ Important for null safety

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    // 🛡️ Data Isolation: Sirf us user ka cart laao
    List<CartItem> findByUserId(Long userId);

    // ✅ Same item update karne ke liye: User ID aur Item Name dono se search karein
    Optional<CartItem> findByUserIdAndItemName(Long userId, String itemName);

    // Payment ke baad cart khali karne ke liye
    void deleteByUserId(Long userId);
}