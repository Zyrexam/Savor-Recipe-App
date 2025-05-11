package com.example.savor_recipe_app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavorRecipeAppTheme {
                SplashScreen {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    var showSkipButton by remember { mutableStateOf(false) }

    // Logo and main element animations
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(
            durationMillis = 800,
            easing = EaseOutBack
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )

    // Floating animation for ingredients
    val floatAnim = rememberInfiniteTransition(label = "float")
    val floatOffset1 by floatAnim.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )
    val floatOffset2 by floatAnim.animateFloat(
        initialValue = 15f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )
    val floatOffset3 by floatAnim.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float3"
    )

    // Rotation animations for ingredients
    val rotateAnim = rememberInfiniteTransition(label = "rotate")
    val rotation1 by rotateAnim.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation1"
    )
    val rotation2 by rotateAnim.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation2"
    )
    val rotation3 by rotateAnim.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation3"
    )

    // Shimmer animation for taglines
    val shimmerColors = listOf(
        Color(0xFFFFDAB9),  // Peach
        Color.White,
        Color(0xFFFFDAB9),  // Peach
        Color(0xFFFFA07A)   // Light Salmon
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "translate"
    )

    // Scale pulsing for ingredients
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulse1 by pulseAnim.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )
    val pulse2 by pulseAnim.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )
    val pulse3 by pulseAnim.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse3"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(500) // Show skip button after 500ms
        showSkipButton = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image with enhanced fitting
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 1.dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }

        // Overlay gradient with enhanced opacity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xDD000000),
                            Color(0x88000000),
                            Color(0xBB000000)
                        )
                    )
                )
        )

        // Main content
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(0.1f))

                // Floating ingredient icons
                Box(
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .scale(scale)
                        .alpha(alpha),
                    contentAlignment = Alignment.Center
                ) {
                    // Mushroom icon - high resolution (512x512)
                    Image(
                        painter = painterResource(id = R.drawable.mashroom),
                        contentDescription = "Mushroom",
                        modifier = Modifier
                            .size(72.dp)
                            .scale(pulse1)
                            .offset(x = (-80).dp, y = floatOffset1.dp)
                            .rotate(rotation1)
                            .shadow(8.dp)
                    )

                    // Parsley icon - high resolution (512x512)
                    Image(
                        painter = painterResource(id = R.drawable.parsley),
                        contentDescription = "Parsley",
                        modifier = Modifier
                            .size(64.dp)
                            .scale(pulse2)
                            .offset(x = 60.dp, y = floatOffset2.dp)
                            .rotate(rotation2)
                            .shadow(6.dp)
                    )

                    // Tomato icon - high resolution (512x512)
                    Image(
                        painter = painterResource(id = R.drawable.tomato),
                        contentDescription = "Tomato",
                        modifier = Modifier
                            .size(68.dp)
                            .scale(pulse3)
                            .offset(x = 0.dp, y = floatOffset3.dp)
                            .rotate(rotation3)
                            .shadow(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // App name with enhanced styling
                Text(
                    text = "SAVOR",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 8.sp,
                    modifier = Modifier
                        .alpha(alpha)
                        .shadow(6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tagline with shimmer effect
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .alpha(alpha)
                ) {
                    Text(
                        text = "TASTE THE EXPERIENCE",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = shimmerColors,
                                start = Offset(translateAnim - 1000, 0f),
                                end = Offset(translateAnim, 0f)
                            )
                        ),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button-like element with gradient
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .alpha(alpha * 0.9f)
                        .width(220.dp)
                        .height(48.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF5252),  // Red accent
                                    Color(0xFFFF8A65)   // Light coral
                                )
                            )
                        )
                ) {
                    Text(
                        text = "MAKE IT DELICIOUS",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.weight(0.2f))

                // Version text at the bottom
                Text(
                    text = "Savor Recipe App v1.0",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .alpha(alpha * 0.7f)
                )
            }

            // Skip button in bottom right corner
            if (showSkipButton) {
                TextButton(
                    onClick = onTimeout,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .alpha(0.8f)
                ) {
                    Text(
                        text = "Skip",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

