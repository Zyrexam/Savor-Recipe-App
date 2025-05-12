package com.example.savor_recipe_app.data

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val preferences: Map<String, Any> = mapOf()
)

data class FavoriteRecipe(
    val id: String = "",
    val name: String = ""
)

class UserRepository {
    private val database: FirebaseDatabase = Firebase.database
    private val usersRef = database.getReference("users")

    suspend fun createUserProfile(user: FirebaseUser, profile: UserProfile) {
        try {
            val userData = profile.copy(
                userId = user.uid,
                email = user.email ?: profile.email
            )
            usersRef.child(user.uid).setValue(userData).await()
        } catch (e: Exception) {
            throw Exception("Failed to create user profile: ${e.message}")
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val snapshot = usersRef.child(userId).get().await()
            snapshot.getValue(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>) {
        try {
            usersRef.child(userId).updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Failed to update user profile: ${e.message}")
        }
    }


    suspend fun getFavoriteRecipes(userId: String): List<FavoriteRecipe> {
        return try {
            val snapshot = usersRef.child(userId).child("favorites").get().await()
            snapshot.children.mapNotNull { it.getValue(FavoriteRecipe::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addFavoriteRecipe(userId: String, recipeId: String, recipeName: String) {
        try {
            val favoriteData = mapOf(
                "id" to recipeId,
                "name" to recipeName
            )
            usersRef.child(userId).child("favorites").child(recipeId).setValue(favoriteData).await()
        } catch (e: Exception) {
            throw Exception("Failed to add favorite recipe: ${e.message}")
        }
    }

    suspend fun removeFavoriteRecipe(userId: String, recipeId: String) {
        try {
            usersRef.child(userId).child("favorites").child(recipeId).removeValue().await()
        } catch (e: Exception) {
            throw Exception("Failed to remove favorite recipe: ${e.message}")
        }
    }
} 