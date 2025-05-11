package com.example.savor_recipe_app.data

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val preferences: Map<String, Any> = mapOf()
)

class UserRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    private val usersCollection = db.collection("users")

    suspend fun createUserProfile(user: FirebaseUser, profile: UserProfile) {
        try {
            val userData = profile.copy(
                userId = user.uid,
                email = user.email ?: profile.email
            )
            usersCollection.document(user.uid).set(userData).await()
        } catch (e: Exception) {
            throw Exception("Failed to create user profile: ${e.message}")
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>) {
        try {
            usersCollection.document(userId).update(updates).await()
        } catch (e: Exception) {
            throw Exception("Failed to update user profile: ${e.message}")
        }
    }

    suspend fun addFavoriteRecipe(userId: String, recipeId: String) {
        try {
            usersCollection.document(userId)
                .collection("favorites")
                .document(recipeId)
                .set(mapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "recipeId" to recipeId
                ))
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to add favorite recipe: ${e.message}")
        }
    }

    suspend fun removeFavoriteRecipe(userId: String, recipeId: String) {
        try {
            usersCollection.document(userId)
                .collection("favorites")
                .document(recipeId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to remove favorite recipe: ${e.message}")
        }
    }

    suspend fun getFavoriteRecipes(userId: String): List<String> {
        return try {
            val snapshot = usersCollection.document(userId)
                .collection("favorites")
                .get()
                .await()
            snapshot.documents.mapNotNull { it.getString("recipeId") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun isRecipeFavorite(userId: String, recipeId: String): Boolean {
        return try {
            val document = usersCollection.document(userId)
                .collection("favorites")
                .document(recipeId)
                .get()
                .await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }
} 