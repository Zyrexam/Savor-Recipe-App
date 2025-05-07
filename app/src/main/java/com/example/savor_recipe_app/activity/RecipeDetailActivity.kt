package com.example.savor_recipe_app.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.savor_recipe_app.ui.theme.SavorRecipeAppTheme
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.savor_recipe_app.BuildConfig

class RecipeDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get recipe ID from intent
        val recipeId = intent.getIntExtra("recipeId", -1)

        setContent {
            SavorRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecipeDetailScreen(
                        recipeId = recipeId,
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}

// API Service Interface for Recipe Details
interface RecipeDetailApiService {
    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = true
    ): RecipeDetail
}

// Data classes for Recipe Detail
data class RecipeDetail(
    val id: Int,
    val title: String,
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val summary: String,
    val instructions: String,
    val extendedIngredients: List<Ingredient>,
    val dishTypes: List<String>? = null,
    val diets: List<String>? = null,
    val healthScore: Int = 0,
    val spoonacularScore: Int = 0,
    val analyzedInstructions: List<AnalyzedInstruction>? = null
)

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val image: String
)

data class AnalyzedInstruction(
    val name: String,
    val steps: List<Step>
)

data class Step(
    val number: Int,
    val step: String,
    val ingredients: List<IngredientItem>? = null,
    val equipment: List<EquipmentItem>? = null
)

data class IngredientItem(
    val id: Int,
    val name: String,
    val image: String
)

data class EquipmentItem(
    val id: Int,
    val name: String,
    val image: String
)

@Composable
fun RecipeDetailScreen(recipeId: Int, onBackPressed: () -> Unit) {
    val apiKey = BuildConfig.SPOONACULAR_API_KEY

    var recipe by remember { mutableStateOf<RecipeDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var animationStarted by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    // Create Retrofit instance
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService = remember { retrofit.create(RecipeDetailApiService::class.java) }

    // Fetch recipe details
    LaunchedEffect(key1 = recipeId) {
        isLoading = true
        error = null

        try {
            if (recipeId != -1) {
                val recipeDetail = apiService.getRecipeInformation(
                    id = recipeId,
                    apiKey = apiKey
                )
                recipe = recipeDetail
                delay(500) // Add a small delay for smoother transition
                animationStarted = true
            } else {
                error = "Invalid recipe ID"
            }
        } catch (e: Exception) {
            error = "Failed to load recipe details: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Content based on loading state
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
                        text = "Loading recipe details...",
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
            recipe != null -> {
                // Recipe detail content
                RecipeDetailContent(
                    recipe = recipe!!,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onBackPressed = onBackPressed
                )
            }
        }
    }
}

@Composable
fun RecipeDetailContent(
    recipe: RecipeDetail,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Recipe image as background with blur
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(recipe.image)
                .crossfade(true)
                .build(),
            contentDescription = recipe.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .blur(8.dp)
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x80000000),
                            Color(0xCC000000)
                        )
                    )
                )
        )

        // Main content
        Column(modifier = Modifier.fillMaxSize()) {
            // Top app bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Back button
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x40FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Favorite button
                IconButton(
                    onClick = { /* Add to favorites */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x40FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
                }

                // Share button
                IconButton(
                    onClick = { /* Share recipe */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x40FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp))

            // Recipe content card
            Card(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                ) {
                    // Recipe title and info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = recipe.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2B3A67)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Recipe stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Time
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFF4D6BC6),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${recipe.readyInMinutes} min",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6E7691)
                                )
                            }

                            // Servings
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF4D6BC6),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${recipe.servings} servings",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6E7691)
                                )
                            }

                            // Health score
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFFE57373),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${recipe.healthScore}% healthy",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6E7691)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Diet tags
                        recipe.diets?.takeIf { it.isNotEmpty() }?.let { diets ->
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(diets) { diet ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(Color(0xFF81C784).copy(alpha = 0.2f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = diet,
                                            fontSize = 12.sp,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = Color(0xFF4D6BC6),
                        divider = {
                            Divider(
                                thickness = 2.dp,
                                color = Color(0xFFE1E5EE)
                            )
                        },
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 3.dp,
                                color = Color(0xFF4D6BC6)
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { onTabSelected(0) },
                            text = { Text("Ingredients") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { onTabSelected(1) },
                            text = { Text("Instructions") }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { onTabSelected(2) },
                            text = { Text("Summary") }
                        )
                    }

                    // Tab content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        when (selectedTab) {
                            0 -> IngredientsTab(recipe.extendedIngredients)
                            1 -> InstructionsTab(recipe.analyzedInstructions)
                            2 -> SummaryTab(recipe.summary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientsTab(ingredients: List<Ingredient>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(ingredients) { ingredient ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFF))
                    .padding(12.dp)
            ) {
                // Ingredient image
                AsyncImage(
                    model = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}",
                    contentDescription = ingredient.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Ingredient name and amount
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = ingredient.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2B3A67)
                    )
                    Text(
                        text = "${ingredient.amount} ${ingredient.unit}",
                        fontSize = 14.sp,
                        color = Color(0xFF6E7691)
                    )
                }
            }
        }
    }
}

@Composable
fun InstructionsTab(instructions: List<AnalyzedInstruction>?) {
    if (instructions.isNullOrEmpty()) {
        // No instructions available
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "No detailed instructions available for this recipe.",
                textAlign = TextAlign.Center,
                color = Color(0xFF6E7691)
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            instructions.forEach { instruction ->
                // Instruction name if available
                if (instruction.name.isNotEmpty()) {
                    item {
                        Text(
                            text = instruction.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2B3A67)
                        )
                    }
                }

                // Steps
                items(instruction.steps) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFF))
                            .padding(16.dp)
                    ) {
                        // Step number and text
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4D6BC6))
                            ) {
                                Text(
                                    text = "${step.number}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = step.step,
                                fontSize = 16.sp,
                                color = Color(0xFF2B3A67),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Ingredients used in this step
                        step.ingredients?.takeIf { it.isNotEmpty() }?.let { ingredients ->
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Ingredients for this step:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6E7691)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(ingredients) { ingredient ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AsyncImage(
                                            model = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}",
                                            contentDescription = ingredient.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = ingredient.name,
                                            fontSize = 12.sp,
                                            color = Color(0xFF6E7691),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(60.dp)
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
}

@Composable
fun SummaryTab(summary: String) {
    val cleanSummary = remember {
        summary.replace(Regex("<[^>]*>"), "")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = cleanSummary,
                fontSize = 16.sp,
                color = Color(0xFF2B3A67),
                lineHeight = 24.sp
            )
        }
    }
}