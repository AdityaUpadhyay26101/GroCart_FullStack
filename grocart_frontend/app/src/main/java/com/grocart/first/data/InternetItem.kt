package com.grocart.first.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
// ✅ SERIALIZABLE CLASSES FOR ITEMS AND ORDERS
@Serializable
data class InternetItem(
    @SerialName("stringResourceId") val itemName: String = "",
    @SerialName("itemCategoryId") val itemCategory: String = "",
    @SerialName("itemQuantity") val itemQuantity: String = "",
    @SerialName("item_price") val itemPrice: Int = 0,
    @SerialName("imageResourceId") val imageUrl: String = ""

)