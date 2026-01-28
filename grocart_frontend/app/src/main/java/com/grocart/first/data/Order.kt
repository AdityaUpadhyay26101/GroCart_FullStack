// Updated Order.kt
package com.grocart.first.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties // Prevents crashes if extra fields exist in Firebase
data class Order(
    val items: List<InternetItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)