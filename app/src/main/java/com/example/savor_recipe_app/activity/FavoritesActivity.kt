package com.example.savor_recipe_app.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.savor_recipe_app.auth.AuthViewModel
import com.example.savor_recipe_app.data.UserRepository
import com.example.savor_recipe_app.ui.theme.SavorRecipeAppTheme
import com.example.savor_recipe_app.util.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.savor_recipe_app.activity.RecipeDetailApiService
import com.example.savor_recipe_app.data.FavoriteRecipe
import com.example.savor_recipe_app.activity.RecipeDetail

class FavoritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavorRecipeAppTheme {
                FavoritesScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val userRepository = remember { UserRepository() }
    var favoriteRecipes by remember { mutableStateOf<List<FavoriteRecipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var recipeDetails by remember { mutableStateOf<List<RecipeDetail>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Retrofit for Spoonacular API
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiService = remember { retrofit.create(RecipeDetailApiService::class.java) }
    val apiKey = ApiConfig.SPOONACULAR_API_KEY

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            try {
                favoriteRecipes = userRepository.getFavoriteRecipes(user.uid)
                // Fetch details for each favorite recipe
                val details = mutableListOf<RecipeDetail>()
                for (fav in favoriteRecipes) {
                    try {
                        val detail = apiService.getRecipeInformation(fav.id.toInt(), apiKey)
                        details.add(detail)
                    } catch (_: Exception) {}
                }
                recipeDetails = details
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Recipes") }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (recipeDetails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "No favorite recipes yet",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recipeDetails) { recipe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Open RecipeDetailActivity
                                val intent = Intent(context, RecipeDetailActivity::class.java)
                                intent.putExtra("recipeId", recipe.id)
                                context.startActivity(intent)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(recipe.image)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = recipe.title,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = recipe.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "ID: ${recipe.id}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = {
                                // Remove from favorites
                                currentUser?.let { user ->
                                    coroutineScope.launch {
                                        userRepository.removeFavoriteRecipe(user.uid, recipe.id.toString())
                                        // Update UI
                                        favoriteRecipes = userRepository.getFavoriteRecipes(user.uid)
                                        recipeDetails = recipeDetails.filter { it.id != recipe.id }
                                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove from favorites",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 