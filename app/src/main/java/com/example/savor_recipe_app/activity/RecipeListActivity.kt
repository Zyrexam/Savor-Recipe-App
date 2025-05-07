package com.example.savor_recipe_app.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.savor_recipe_app.BuildConfig
import com.example.savor_recipe_app.model.Recipe
import com.example.savor_recipe_app.ui.theme.SavorRecipeAppTheme
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class RecipeListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ingredients = intent.getStringExtra("ingredients") ?: ""
        val searchTerm = intent.getStringExtra("search") ?: ""
        val dietaryFilters = intent.getStringExtra("dietary") ?: ""
        val category = intent.getStringExtra("category") ?: ""

        setContent {
            SavorRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecipeListScreen(
                        ingredients = ingredients,
                        searchTerm = searchTerm,
                        dietaryFilters = dietaryFilters,
                        category = category,
                        onBackPressed = { finish() },
                        onRecipeSelected = { recipeId ->
                            val intent = Intent(this, RecipeListActivity::class.java).apply {
                                putExtra("recipeId", recipeId)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

interface SpoonacularApiService {
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String = "",
        @Query("includeIngredients") includeIngredients: String = "",
        @Query("type") type: String = "",
        @Query("diet") diet: String = "",
        @Query("number") number: Int = 20,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true,
        @Query("fillIngredients") fillIngredients: Boolean = true
    ): RecipeSearchResponse
}

data class RecipeSearchResponse(
    val results: List<Recipe>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

@Composable
fun RecipeListScreen(
    ingredients: String,
    searchTerm: String,
    dietaryFilters: String,
    category: String,
    onBackPressed: () -> Unit,
    onRecipeSelected: (Int) -> Unit
) {
    val apiKey = BuildConfig.SPOONACULAR_API_KEY
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var animationStarted by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "floatingElements")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiService = remember { retrofit.create(SpoonacularApiService::class.java) }

    LaunchedEffect(ingredients, searchTerm, dietaryFilters, category) {
        isLoading = true
        error = null
        try {
            val query = when {
                searchTerm.isNotEmpty() -> searchTerm
                category.isNotEmpty() -> category
                else -> ""
            }
            val type = when {
                category.isNotEmpty() && !listOf("vegetarian", "vegan", "gluten-free", "dairy-free").contains(category) -> category
                else -> ""
            }
            val diet = when {
                dietaryFilters.isNotEmpty() -> dietaryFilters.split(",").firstOrNull() ?: ""
                category == "vegetarian" -> "vegetarian"
                category == "vegan" -> "vegan"
                else -> ""
            }
            val response = apiService.searchRecipes(
                apiKey = apiKey,
                query = query,
                includeIngredients = ingredients,
                type = type,
                diet = diet
            )
            recipes = response.results
            delay(500)
            animationStarted = true
        } catch (e: Exception) {
            error = "Failed to load recipes: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF8FAFF),
                    Color(0xFFEEF2FA),
                    Color(0xFFE6EAF6)
                )
            )
        )
    ) {
        // Decorative circles with animation
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset((-60).dp, (-40).dp + floatingOffset.dp)
                .alpha(0.08f)
                .background(Color(0xFF4D6BC6), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomEnd)
                .offset(60.dp, 60.dp - floatingOffset.dp)
                .alpha(0.10f)
                .background(Color(0xFF2B3A67), shape = CircleShape)
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top app bar with improved styling
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Back button with improved styling
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x10000000))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2B3A67)
                    )
                }

                // Title based on search parameters
                val title = when {
                    category.isNotEmpty() -> "$category Recipes"
                    ingredients.isNotEmpty() -> "Recipes with Your Ingredients"
                    searchTerm.isNotEmpty() -> "Results for \"$searchTerm\""
                    else -> "Recipe Results"
                }

                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF2B3A67),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search within results...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF4D6BC6)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF6E7691)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4D6BC6),
                    unfocusedBorderColor = Color(0xFFE1E5EE),
                    focusedLabelColor = Color(0xFF4D6BC6),
                    unfocusedLabelColor = Color(0xFF6E7691),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Filter chips for active filters
            val activeFilters = buildList {
                if (ingredients.isNotEmpty()) {
                    ingredients.split(",").forEach { add("Ingredient: $it") }
                }
                if (dietaryFilters.isNotEmpty()) {
                    dietaryFilters.split(",").forEach { add("Diet: $it") }
                }
            }

            if (activeFilters.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    items(activeFilters) { filter ->
                        FilterChip(
                            selected = true,
                            onClick = { /* No action needed */ },
                            label = { Text(filter) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4D6BC6).copy(alpha = 0.15f),
                                selectedLabelColor = Color(0xFF4D6BC6),
                                selectedLeadingIconColor = Color(0xFF4D6BC6)
                            )
                        )
                    }
                }
            }

            // Main content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    isLoading -> {
                        // Loading state
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF4D6BC6),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Finding delicious recipes...",
                                fontSize = 16.sp,
                                color = Color(0xFF6E7691)
                            )
                        }
                    }
                    error != null -> {
                        // Error state
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFE57373),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = error ?: "An unknown error occurred",
                                fontSize = 16.sp,
                                color = Color(0xFF6E7691),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onBackPressed,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4D6BC6)
                                )
                            ) {
                                Text("Go Back")
                            }
                        }
                    }
                    recipes.isEmpty() -> {
                        // Empty state
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = Color(0xFF6E7691),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No recipes found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2B3A67)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try different ingredients or categories",
                                fontSize = 16.sp,
                                color = Color(0xFF6E7691),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onBackPressed,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4D6BC6)
                                )
                            ) {
                                Text("Go Back")
                            }
                        }
                    }
                    else -> {
                        // Filter recipes based on search query
                        val filteredRecipes = if (searchQuery.isEmpty()) {
                            recipes
                        } else {
                            recipes.filter {
                                it.title.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        // Results list
                        this@Column.AnimatedVisibility(
                            visible = animationStarted,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(filteredRecipes) { recipe ->
                                    RecipeCard(
                                        recipe = recipe,
                                        onClick = { onRecipeSelected(recipe.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Recipe image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(recipe.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay for better text visibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x80000000)
                                ),
                                startY = 0f,
                                endY = 500f
                            )
                        )
                )

                // Recipe time and servings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xCCFFFFFF))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFF4D6BC6),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.readyInMinutes} min",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2B3A67)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Servings
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xCCFFFFFF))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF4D6BC6),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.servings} servings",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2B3A67)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Health score
                    if (recipe.healthScore > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xCCFFFFFF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFE57373),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recipe.healthScore}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2B3A67)
                            )
                        }
                    }
                }
            }

            // Recipe details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = recipe.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B3A67),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Diet tags
                recipe.diets?.takeIf { it.isNotEmpty() }?.let { diets ->
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp)
                    ) {
                        diets.take(3).forEach { diet ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color(0xFF81C784).copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = diet,
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        if (diets.size > 3) {
                            Text(
                                text = "+${diets.size - 3} more",
                                fontSize = 12.sp,
                                color = Color(0xFF6E7691),
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Summary (truncated)
                Text(
                    text = recipe.summary.replace(Regex("<[^>]*>"), ""),
                    fontSize = 14.sp,
                    color = Color(0xFF6E7691),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // View recipe button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = onClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF4D6BC6)
                        )
                    ) {
                        Text(
                            text = "View Recipe",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}