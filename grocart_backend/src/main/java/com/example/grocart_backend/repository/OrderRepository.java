package com.example.grocart_backend.repository;


import com.example.grocart_backend.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // User ki order history dekhne ke liye
    List<OrderEntity> findByUserId(Long userId);
}
