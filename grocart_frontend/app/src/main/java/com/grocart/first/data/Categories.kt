package com.grocart.first.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Categories(
    @StringRes val stringResourceId : Int,
    @DrawableRes val imageResourceId : Int
    )