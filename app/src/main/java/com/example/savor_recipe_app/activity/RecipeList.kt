package com.example.savor_recipe_app.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.savor_recipe_app.model.Recipe
import com.example.savor_recipe_app.ui.theme.SavorRecipeAppTheme

class RecipeList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Get search parameters from intent
        val category = intent.getStringExtra("category") ?: ""
        val ingredients = intent.getStringExtra("ingredients")?.split(",") ?: emptyList()
        val searchTerm = intent.getStringExtra("search") ?: ""
        val dietaryFilters = intent.getStringExtra("dietary")?.split(",") ?: emptyList()

        setContent {
            SavorRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecipeListScreen(
                        category = category,
                        ingredients = ingredients,
                        searchTerm = searchTerm,
                        dietaryFilters = dietaryFilters,
                        onBackPressed = { finish() },
                        onRecipeClick = { recipe ->
                            // TODO: Navigate to recipe details
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    category: String,
    ingredients: List<String>,
    searchTerm: String,
    dietaryFilters: List<String>,
    onBackPressed: () -> Unit,
    onRecipeClick: (Recipe) -> Unit
) {
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // TODO: Replace with actual data fetching
    LaunchedEffect(category, ingredients, searchTerm, dietaryFilters) {
        // Simulate loading
        kotlinx.coroutines.delay(1000)
        recipes = getSampleRecipes().filter { recipe ->
            var matches = true
            
            // Filter by category
            if (category.isNotEmpty()) {
                matches = matches && recipe.category.equals(category, ignoreCase = true)
            }
            
            // Filter by ingredients
            if (ingredients.isNotEmpty()) {
                matches = matches && ingredients.any { ingredient ->
                    recipe.ingredients.any { it.contains(ingredient, ignoreCase = true) }
                }
            }
            
            // Filter by search term
            if (searchTerm.isNotEmpty()) {
                matches = matches && (
                    recipe.name.contains(searchTerm, ignoreCase = true) ||
                    recipe.description.contains(searchTerm, ignoreCase = true)
                )
            }
            
            // Filter by dietary restrictions
            if (dietaryFilters.isNotEmpty()) {
                matches = matches && when {
                    dietaryFilters.contains("Vegetarian") -> recipe.isVegetarian
                    dietaryFilters.contains("Vegan") -> recipe.isVegan
                    dietaryFilters.contains("Gluten-Free") -> recipe.isGlutenFree
                    dietaryFilters.contains("Dairy-Free") -> recipe.isDairyFree
                    else -> true
                }
            }
            
            matches
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            category.isNotEmpty() -> "Recipes in $category"
                            ingredients.isNotEmpty() -> "Recipes with ${ingredients.joinToString(", ")}"
                            else -> "Search Results"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (recipes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No recipes found",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(recipes) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Recipe image
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                contentScale = ContentScale.Crop
            )

            // Recipe details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = recipe.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = recipe.description,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Prep time
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.prepTime + recipe.cookTime} min",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    
                    // Difficulty
                    Text(
                        text = recipe.difficulty,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", recipe.rating),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// Sample data for testing
private fun getSampleRecipes(): List<Recipe> {
    return listOf(
        Recipe(
            id = "1",
            name = "Classic Margherita Pizza",
            description = "A simple yet delicious pizza with fresh tomatoes, mozzarella, and basil.",
            ingredients = listOf(
                "Pizza dough",
                "Fresh tomatoes",
                "Fresh mozzarella",
                "Fresh basil",
                "Olive oil",
                "Salt"
            ),
            instructions = listOf(
                "Preheat oven to 450°F",
                "Roll out the dough",
                "Add toppings",
                "Bake for 12-15 minutes"
            ),
            prepTime = 20,
            cookTime = 15,
            servings = 4,
            category = "Dinner",
            imageUrl = "https://example.com/margherita.jpg",
            isVegetarian = true,
            rating = 4.5f,
            difficulty = "Easy"
        ),
        Recipe(
            id = "2",
            name = "Chocolate Chip Cookies",
            description = "Classic homemade chocolate chip cookies that are soft and chewy.",
            ingredients = listOf(
                "Butter",
                "Sugar",
                "Eggs",
                "Vanilla extract",
                "Flour",
                "Chocolate chips"
            ),
            instructions = listOf(
                "Cream butter and sugar",
                "Add eggs and vanilla",
                "Mix in dry ingredients",
                "Bake at 350°F for 10-12 minutes"
            ),
            prepTime = 15,
            cookTime = 12,
            servings = 24,
            category = "Desserts",
            imageUrl = "https://example.com/cookies.jpg",
            rating = 4.8f,
            difficulty = "Easy"
        )
    )
} 