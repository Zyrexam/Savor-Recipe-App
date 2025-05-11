package com.example.savor_recipe_app.network

import com.example.savor_recipe_app.model.RecipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit interface for Spoonacular API endpoints
interface SpoonacularApi {

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        // Search keywords (e.g., "pasta", "chicken curry")
        @Query("query") query: String? = null,
        @Query("includeIngredients") ingredients: String? = null,
        @Query("diet") diet: String? = null,
        @Query("number") number: Int = 20,
        @Query("apiKey") apiKey: String
    ): RecipeSearchResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeById(
        @retrofit2.http.Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): com.example.savor_recipe_app.model.Recipe
} 