package com.example.savor_recipe_app.model

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val summary: String,
    val diets: List<String> = emptyList(),
    val healthScore: Int = 0
) 