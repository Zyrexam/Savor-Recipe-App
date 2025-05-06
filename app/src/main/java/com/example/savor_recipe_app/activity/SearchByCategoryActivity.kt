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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savor_recipe_app.R
import com.example.savor_recipe_app.ui.theme.SavorRecipeAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

data class RecipeCategory(
    val name: String,
    val icon: Int,
    val color: Color,
    val description: String
)

class SearchByCategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SavorRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchByCategoryScreen(
                        onBackPressed = { finish() },
                        onSearch = { category ->
                            val intent = Intent(this, RecipeList::class.java).apply {
                                putExtra("category", category)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

data class CategoryItem(
    val id: String,
    val name: String,
    val icon: Int,
    val color: Color,
    val description: String = "" // Added description field
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByCategoryScreen(
    onBackPressed: () -> Unit,
    onSearch: (String) -> Unit
) {
    var searchTerm by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var animationStarted by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Floating animation
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

    // Recipe categories with improved colors
    val recipeCategories = kotlin.collections.listOf(
        RecipeCategory(
            "Breakfast",
            R.drawable.breakfast,
            Color(0xFFFFB74D),
            "Start your day with delicious breakfast recipes"
        ),
        RecipeCategory(
            "Lunch",
            R.drawable.lunch,
            Color(0xFF81C784),
            "Quick and satisfying lunch ideas"
        ),
        RecipeCategory(
            "Dinner",
            R.drawable.dinner,
            Color(0xFFE57373),
            "Family-friendly dinner recipes"
        ),
        RecipeCategory(
            "Desserts",
            R.drawable.dessert,
            Color(0xFFBA68C8),
            "Sweet treats and desserts"
        ),
        RecipeCategory(
            "Snacks",
            R.drawable.snacks,
            Color(0xFF4DB6AC),
            "Quick and easy snack recipes"
        ),
        RecipeCategory(
            "Vegetarian",
            R.drawable.vegetarian,
            Color(0xFF64B5F6),
            "Meat-free recipes"
        ),
        RecipeCategory(
            "Seafood",
            R.drawable.seafood,
            Color(0xFF7986CB),
            "Fresh seafood recipes"
        ),
        RecipeCategory(
            "Beverages",
            R.drawable.beverages,
            Color(0xFF4DD0E1),
            "Refreshing drinks and cocktails"
        )
    )

    LaunchedEffect(key1 = true) {
        animationStarted = true
        delay(300)
    }

    // Decorative background with improved gradient
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
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                // Top app bar
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
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
                        Text(
                            text = "Search by Category",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF2B3A67),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Description text
                item {
                    AnimatedVisibility(
                        visible = animationStarted,
                        enter = fadeIn(animationSpec = tween(800)) + slideInVertically(initialOffsetY = { -30 }, animationSpec = tween(800))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.category),
                                contentDescription = null,
                                tint = Color(0xFF4D6BC6),
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(bottom = 8.dp)
                            )

                            Text(
                                text = "Browse recipes by category and find your next favorite dish!",
                                fontSize = 15.sp,
                                color = Color(0xFF6E7691),
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                // Search term input
                item {
                    AnimatedVisibility(
                        visible = animationStarted,
                        enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(1000, easing = EaseOutBack))
                    ) {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .padding(horizontal = 8.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    spotColor = Color(0x40000000),
                                    ambientColor = Color(0x40000000),
                                    shape = RoundedCornerShape(28.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = searchTerm,
                                    onValueChange = { searchTerm = it },
                                    label = { Text("Recipe keywords (optional)", fontWeight = FontWeight.Bold, color = Color(0xFF4D6BC6)) },
                                    placeholder = { Text("e.g., pasta, quick dinner, etc.", fontWeight = FontWeight.SemiBold) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = Color(0xFF4D6BC6)
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchTerm.isNotEmpty()) {
                                            IconButton(onClick = { searchTerm = "" }) {
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
                                        focusedContainerColor = Color(0xFFF8FAFF),
                                        unfocusedContainerColor = Color(0xFFF8FAFF)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Categories grid
                item {
                    AnimatedVisibility(
                        visible = animationStarted,
                        enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(1000, easing = EaseOutBack))
                    ) {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .padding(horizontal = 8.dp, vertical = 16.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    spotColor = Color(0x40000000),
                                    ambientColor = Color(0x40000000),
                                    shape = RoundedCornerShape(28.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = Color(0xFF4D6BC6),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Recipe Categories",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2B3A67)
                                    )
                                }

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp) // Fixed height for the grid
                                ) {
                                    items(recipeCategories) { category ->
                                        CategoryCard(
                                            category = category,
                                            isSelected = selectedCategory == category.name,
                                            onClick = {
                                                selectedCategory = if (selectedCategory == category.name) null else category.name
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Search button
            Button(
                onClick = {
                    val context = LocalContext.current
                    val intent = Intent(context, RecipeList::class.java).apply {
                        putExtra("category", selectedCategory)
                        putExtra("search", searchTerm)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Find Recipes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: RecipeCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) category.color.copy(alpha = 0.2f) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, category.color) else null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(
                elevation = if (isSelected) 8.dp else 4.dp,
                spotColor = if (isSelected) category.color.copy(alpha = 0.3f) else Color(0x20000000),
                ambientColor = if (isSelected) category.color.copy(alpha = 0.2f) else Color(0x10000000),
                shape = RoundedCornerShape(16.dp)
            )
            .graphicsLayer {
                translationY = if (isSelected) -4f else 0f
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Category icon with colored background and improved styling
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) category.color else category.color.copy(alpha = 0.1f)
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = category.icon),
                    contentDescription = category.name,
                    tint = if (isSelected) Color.White else category.color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category name with improved styling
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) category.color else Color(0xFF2B3A67),
                textAlign = TextAlign.Center
            )

            // Add a subtle description if selected
            if (isSelected && category.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    fontSize = 12.sp,
                    color = Color(0xFF6E7691),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}