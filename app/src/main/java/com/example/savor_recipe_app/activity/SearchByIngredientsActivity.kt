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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext

class SearchByIngredientsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SavorRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchByIngredientsScreen(
                        onBackPressed = { finish() },
                        onSearch = { ingredients, searchTerm, dietaryFilters ->
                            val intent = Intent(this, RecipeList::class.java).apply {
                                putExtra("ingredients", ingredients)
                                putExtra("search", searchTerm)
                                putExtra("dietary", dietaryFilters)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

data class IngredientCategory(
    val name: String,
    val icon: Int,
    val color: Color,
    val ingredients: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByIngredientsScreen(
    onBackPressed: () -> Unit,
    onSearch: (String, String, String) -> Unit
) {
    var ingredients by remember { mutableStateOf("") }
    var searchTerm by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var animationStarted by remember { mutableStateOf(false) }
    var selectedIngredients by remember { mutableStateOf(listOf<String>()) }
    var selectedDietaryFilters by remember { mutableStateOf(listOf<String>()) }
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val ingredientsFocusRequester = remember { FocusRequester() }

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

    // Ingredient categories with improved colors
    val ingredientCategories = listOf(
        IngredientCategory(
            "Proteins",
            R.drawable.kitchen,
            Color(0xFFE57373),
            listOf("Chicken", "Beef", "Pork", "Tofu", "Eggs", "Fish", "Shrimp", "Turkey", "Beans", "Lentils")
        ),
        IngredientCategory(
            "Vegetables",
            R.drawable.vegetarian,
            Color(0xFF81C784),
            listOf("Tomatoes", "Onions", "Garlic", "Potatoes", "Carrots", "Broccoli", "Spinach", "Bell Peppers", "Zucchini", "Mushrooms")
        ),
        IngredientCategory(
            "Grains",
            R.drawable.restaurant,
            Color(0xFFFFB74D),
            listOf("Rice", "Pasta", "Bread", "Quinoa", "Oats", "Barley", "Couscous", "Flour", "Cornmeal", "Tortillas")
        ),
        IngredientCategory(
            "Dairy",
            R.drawable.breakfast,
            Color(0xFF64B5F6),
            listOf("Milk", "Cheese", "Butter", "Yogurt", "Cream", "Sour Cream", "Cream Cheese", "Cottage Cheese", "Parmesan", "Mozzarella")
        ),
        IngredientCategory(
            "Herbs & Spices",
            R.drawable.dinner,
            Color(0xFFBA68C8),
            listOf("Salt", "Pepper", "Basil", "Oregano", "Thyme", "Rosemary", "Cumin", "Paprika", "Cinnamon", "Chili Powder")
        ),
        IngredientCategory(
            "Fruits",
            R.drawable.category,
            Color(0xFF4DB6AC),
            listOf("Apples", "Bananas", "Lemons", "Limes", "Oranges", "Berries", "Pineapple", "Avocado", "Mango", "Grapes")
        )
    )

    val dietaryOptions = listOf(
        "Vegetarian", "Vegan", "Gluten-Free", "Dairy-Free",
        "Low-Carb", "Keto", "Paleo", "Nut-Free"
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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
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
                    Text(
                        text = "Search by Ingredients",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color(0xFF2B3A67),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Description text with improved animation
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = fadeIn(animationSpec = tween(800)) + slideInVertically(initialOffsetY = { -30 }, animationSpec = tween(800))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        // Small icon
                        Icon(
                            painter = painterResource(id = R.drawable.kitchen),
                            contentDescription = null,
                            tint = Color(0xFF4D6BC6),
                            modifier = Modifier
                                .size(32.dp)
                                .padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Select ingredients you have, and we'll find delicious recipes for you!",
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

                Spacer(modifier = Modifier.height(8.dp))

                // Main content area - expanded to fill available space
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
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Search term input with improved styling
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

                            Spacer(modifier = Modifier.height(16.dp))

                            // Selected ingredients display with improved styling
                            if (selectedIngredients.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4D6BC6),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Selected Ingredients",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2B3A67)
                                    )
                                }

                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(selectedIngredients) { ingredient ->
                                        IngredientChip(
                                            text = ingredient,
                                            onRemove = {
                                                selectedIngredients = selectedIngredients - ingredient
                                            }
                                        )
                                    }
                                }

                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color(0xFFE1E5EE)
                                )
                            }

                            // Category tabs with improved styling
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
                                    text = "Ingredient Categories",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2B3A67)
                                )
                            }

                            // Horizontal scrollable category tabs with improved styling
                            ScrollableTabRow(
                                selectedTabIndex = selectedCategoryIndex,
                                edgePadding = 0.dp,
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFF4D6BC6),
                                indicator = { tabPositions ->
                                    TabRowDefaults.Indicator(
                                        modifier = Modifier
                                            .tabIndicatorOffset(tabPositions[selectedCategoryIndex])
                                            .padding(horizontal = 16.dp),
                                        height = 3.dp,
                                        color = ingredientCategories[selectedCategoryIndex].color
                                    )
                                },
                                divider = {}
                            ) {
                                ingredientCategories.forEachIndexed { index, category ->
                                    Tab(
                                        selected = selectedCategoryIndex == index,
                                        onClick = { selectedCategoryIndex = index },
                                        text = {
                                            Text(
                                                text = category.name,
                                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = category.icon),
                                                contentDescription = null,
                                                tint = if (selectedCategoryIndex == index) category.color else Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        selectedContentColor = category.color,
                                        unselectedContentColor = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Ingredients grid for selected category with improved styling
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                contentPadding = PaddingValues(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                items(ingredientCategories[selectedCategoryIndex].ingredients) { ingredient ->
                                    IngredientCard(
                                        ingredient = ingredient,
                                        color = ingredientCategories[selectedCategoryIndex].color,
                                        isSelected = selectedIngredients.contains(ingredient),
                                        onClick = {
                                            selectedIngredients = if (selectedIngredients.contains(ingredient)) {
                                                selectedIngredients - ingredient
                                            } else {
                                                selectedIngredients + ingredient
                                            }
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Dietary preferences section with improved styling
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = Color(0xFF4D6BC6),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Dietary Preferences (Optional)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2B3A67)
                                )
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(dietaryOptions) { option ->
                                    FilterChip(
                                        selected = selectedDietaryFilters.contains(option),
                                        onClick = {
                                            selectedDietaryFilters = if (selectedDietaryFilters.contains(option)) {
                                                selectedDietaryFilters - option
                                            } else {
                                                selectedDietaryFilters + option
                                            }
                                        },
                                        label = { Text(option) },
                                        leadingIcon = if (selectedDietaryFilters.contains(option)) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF4D6BC6).copy(alpha = 0.15f),
                                            selectedLabelColor = Color(0xFF4D6BC6),
                                            selectedLeadingIconColor = Color(0xFF4D6BC6)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    val context = LocalContext.current
                    val intent = Intent(context, RecipeList::class.java).apply {
                        putExtra("ingredients", selectedIngredients.joinToString(","))
                        putExtra("dietary", selectedDietaryFilters.joinToString(","))
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
fun IngredientCard(
    ingredient: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, color) else null,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 6.dp else 2.dp,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(bottom = 4.dp)
                )
            }

            Text(
                text = ingredient,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) color else Color(0xFF2B3A67),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun IngredientChip(text: String, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFF4D6BC6),
        shadowElevation = 4.dp,
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(50.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// Helper extension to use Brush as a color in ButtonDefaults
fun Brush.toBrushColor(): Color = Color.Unspecified