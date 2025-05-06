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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savor_recipe_app.R
import com.example.savor_recipe_app.ui.theme.SavorRecipeAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SavorRecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onSearchByIngredients = {
                            val intent = Intent(this, SearchByIngredientsActivity::class.java)
                            startActivity(intent)
                        },
                        onSearchByCategory = {
                            val intent = Intent(this, SearchByCategoryActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun MainScreen(onSearchByIngredients: () -> Unit, onSearchByCategory: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var animationStarted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Animation states
    val welcomeTextAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(1000),
        label = "welcomeTextAlpha"
    )

    // Floating elements animation
    val infiniteTransition = rememberInfiniteTransition(label = "floatingElements")
    val floatingOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatingOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    LaunchedEffect(key1 = true) {
        animationStarted = true
        delay(500)
    }

    // Main container
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image with blur effect for depth
        Image(
            painter = painterResource(id = R.drawable.image1),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(2.dp),
            contentScale = ContentScale.Crop
        )

        // Overlay gradient with more sophisticated colors
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xDD000000), // More opaque at top
                            Color(0xBB000000),
                            Color(0x99000000)  // More transparent at bottom
                        )
                    )
                )
        )

        // Decorative floating elements
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset((-20).dp, 100.dp + floatingOffset1.dp)
                .alpha(0.15f)
                .background(Color(0xFF4D6BC6), shape = CircleShape)
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterEnd)
                .offset((-20).dp, floatingOffset2.dp)
                .alpha(0.12f)
                .background(Color(0xFFE57373), shape = CircleShape)
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome header with enhanced animation
            AnimatedVisibility(
                visible = animationStarted,
                enter = fadeIn(animationSpec = tween(1500)) +
                        slideInVertically(
                            initialOffsetY = { -50 },
                            animationSpec = tween(1200, easing = EaseOutQuart)
                        ),
                modifier = Modifier.padding(top = 80.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App logo/icon
                    Image(
                        painter = painterResource(id = R.drawable.kitchen),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .padding(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // App name with enhanced styling
                    Text(
                        text = "SAVOR",
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 8.sp,
                        modifier = Modifier
                            .graphicsLayer {
                                shadowElevation = 12f
                                shape = RoundedCornerShape(8.dp)
                            }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tagline with gradient text
                    Text(
                        text = "Discover Delicious Recipes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.alpha(welcomeTextAlpha)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle with more information
                    Text(
                        text = "Find recipes by ingredients or categories",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .alpha(welcomeTextAlpha)
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Search options with enhanced animation and styling
            AnimatedVisibility(
                visible = animationStarted,
                enter = fadeIn(animationSpec = tween(1500)) +
                        slideInVertically(
                            initialOffsetY = { 100 },
                            animationSpec = tween(1300, easing = EaseOutBack)
                        )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Search by ingredients button
                    SearchOptionButton(
                        icon = R.drawable.kitchen,
                        title = "Search by Ingredients",
                        description = "Find recipes using ingredients you have",
                        onClick = {
                            isLoading = true
                            scope.launch {
                                delay(500)
                                onSearchByIngredients()
                            }
                        },
                        isLoading = isLoading,
                        backgroundColor = Color(0xCCFFFFFF),
                        textColor = Color(0xFF2B3A67),
                        iconTint = Color(0xFF4D6BC6)
                    )

                    // Search by category button
                    SearchOptionButton(
                        icon = R.drawable.category,
                        title = "Search by Category",
                        description = "Browse recipes by cuisine or meal type",
                        onClick = {
                            isLoading = true
                            scope.launch {
                                delay(500)
                                onSearchByCategory()
                            }
                        },
                        isLoading = isLoading,
                        backgroundColor = Color(0xCCFFFFFF),
                        textColor = Color(0xFF2B3A67),
                        iconTint = Color(0xFFE57373)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer text
            AnimatedVisibility(
                visible = animationStarted,
                enter = fadeIn(animationSpec = tween(2000))
            ) {
                Text(
                    text = "Savor every bite",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
fun SearchOptionButton(
    icon: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    backgroundColor: Color = Color.White,
    textColor: Color = Color(0xFF2B3A67),
    iconTint: Color = Color(0xFF4D6BC6)
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(enabled = !isLoading) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with enhanced styling
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                iconTint,
                                iconTint.copy(alpha = 0.7f)
                            )
                        )
                    )
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Loading indicator or arrow
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = textColor
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_forward),
                        contentDescription = "Navigate",
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}