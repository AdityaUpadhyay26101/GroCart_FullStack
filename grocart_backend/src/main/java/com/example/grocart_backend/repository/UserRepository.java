package com.example.grocart_backend.repository;



import com.example.grocart_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Login ke liye username se user dhundna
    Optional<User> findByUsername(String username);

    // Registration ke waqt check karne ke liye ki username unique hai ya nahi
    Boolean existsByUsername(String username);

    // Registration ke waqt email check karne ke liye
    Boolean existsByEmail(String email);
}