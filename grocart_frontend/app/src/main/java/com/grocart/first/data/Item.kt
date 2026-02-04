package com.grocart.first.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
// ✅ DATA CLASS FOR ITEMS
data class Item(
    @StringRes val stringResourceId: Int,
    @StringRes val itemCategoryId: Int,
    val itemQuantityId: String,
    val itemPrice: Int,
    @DrawableRes val imageResourceId: Int
)
