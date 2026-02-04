package com.grocart.first.data

import kotlinx.serialization.Serializable
// ✅ SERIALIZABLE CLASSES FOR order data
@Serializable
data class Order(
    val id: Int? = null, // Optional for new orders
    val items: List<InternetItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)