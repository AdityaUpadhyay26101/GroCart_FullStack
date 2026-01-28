package com.example.grocart_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data; // If using Lombok, otherwise generate getters/setters

@Entity
@Data
public class InternetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Matches your Android SerialName AND ViewModel logic
    @JsonProperty("stringResourceId")
    private String itemName;

    @JsonProperty("itemCategoryId")
    private String itemCategory;

    @JsonProperty("itemQuantity")
    private String itemQuantity;

    // CRITICAL: Change name to itemPrice to avoid any confusion with integer mapping
    @JsonProperty("item_price")
    private Integer itemPrice; // Using Integer instead of int is safer for JSON parsing

    @JsonProperty("imageResourceId")
    private String imageUrl;
}