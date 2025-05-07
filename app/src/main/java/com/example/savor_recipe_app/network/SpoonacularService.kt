package com.example.savor_recipe_app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton object to provide a configured Retrofit instance for Spoonacular API
object SpoonacularService {
    private const val BASE_URL = "https://api.spoonacular.com/"

    // Lazily initialized API interface
    val api: SpoonacularApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpoonacularApi::class.java)
    }
} 