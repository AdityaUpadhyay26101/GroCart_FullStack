package com.grocart.first.data

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int? = null, // MySQL ki primary key ke liye
    val items: List<InternetItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)