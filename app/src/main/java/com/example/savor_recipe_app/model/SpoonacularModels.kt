package com.example.savor_recipe_app.model

// This data class represents the entire response from Spoonacular's /recipes/complexSearch endpoint.
data class RecipeSearchResponse(
    val results: List<RecipeResult>,

    val offset: Int,
    val number: Int,
    val totalResults: Int
)

// This data class represents a single recipe in the search results.
data class RecipeResult(
    // Unique ID for the recipe
    val id: Int,
    val title: String,
    val image: String
) 