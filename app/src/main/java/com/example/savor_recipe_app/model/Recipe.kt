package com.example.savor_recipe_app.model

data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTime: Int, // in minutes
    val cookTime: Int, // in minutes
    val servings: Int,
    val category: String,
    val imageUrl: String,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isDairyFree: Boolean = false,
    val rating: Float = 0f,
    val difficulty: String = "Medium" // Easy, Medium, Hard
) 