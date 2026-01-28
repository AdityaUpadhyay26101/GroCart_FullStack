package com.grocart.first.network

import com.grocart.first.data.InternetItem
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "http://10.0.2.2:8080"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        Json {
            ignoreUnknownKeys = true // This is the crucial line to add
            coerceInputValues = true // Optional: handles null values safely
        }.asConverterFactory("application/json".toMediaType())
    )
    .baseUrl(BASE_URL)
    .build()

interface FirstApiService {
    @GET("android/grocery_delivery_app/items.json")
    suspend fun getItems() : List<InternetItem>
}

object FirstApi {
    val retrofitService : FirstApiService by lazy {
        retrofit.create(FirstApiService::class.java)
    }
}



