package com.grocart.first.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
// ✅ DATA CLASS FOR CATEGORIES
data class Categories(
    @StringRes val stringResourceId : Int,
    @DrawableRes val imageResourceId : Int
    )