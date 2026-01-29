package com.example.grocart_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Matches Android SerialName "stringResourceId"
    @JsonProperty("stringResourceId")
    @Column(name = "item_name")
    private String itemName;

    // Matches Android SerialName "item_price"
    @JsonProperty("item_price")
    @Column(name = "item_price")
    private Integer itemPrice;

    // Matches Android SerialName "imageResourceId"
    @JsonProperty("imageResourceId")
    @Column(name = "image_url")
    private String imageUrl;

    private Integer quantity;
}