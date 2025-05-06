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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savor_recipe_app.R
import com.example.savor_recipe_app.ui.theme.OnPrimary
import com.example.savor_recipe_app.ui.theme.Primary
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
    
    // Pulse animation for the circle
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseSize by pulseAnim.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse size"
    )

    // Shimmer animation for tagline
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.6f),
        Color.White,
        Color.White.copy(alpha = 0.6f),
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "translate"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1500)
        onTimeout()
    }

    // Main container
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
                    .fillMaxHeight()
                    .fillMaxWidth(),
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
                            Color(0xE6000000),
                            Color(0xB3000000),
                            Color(0x80000000)
                        )
                    )
                )
        )
        
        // Main content with better spacing
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App name with enhanced styling
                    Text(
                        text = "SAVOR",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 8.sp,
                        modifier = Modifier
                            .alpha(alpha)
                            .shadow(4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tagline with shimmer effect
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .alpha(alpha)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "WE HELP YOU",
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = shimmerColors,
                                    start = Offset(translateAnim - 1000, 0f),
                                    end = Offset(translateAnim, 0f)
                                )
                            ),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.widthIn(max = 280.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Middle section - Animated plate image
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale * pulseSize)
                        .alpha(alpha)
                ) {
                    // Outer glow effect
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(20.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                    
                    // Inner circle with gradient
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_recipe_plate),
                            contentDescription = "Recipe Plate",
                            modifier = Modifier.size(140.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // "MAKE IT DELICIOUS!!" tagline with stylish font
                Text(
                    text = "MAKE IT DELICIOUS!!",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Cursive,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .alpha(alpha)
                        .shadow(2.dp)
                )
            }
            
            // Version text at the bottom
            Text(
                text = "Savor Recipe App v1.0",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .alpha(alpha * 0.7f)
            )
        }
    }
}

val WindowBackground = Color(0xFFF8FAFF)
val SplashBackground = Color(0xFFF8FAFF)
val CardBackground = Color(0xFFFFFFFF)
// ...other colors as needed

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    background = WindowBackground, // or SplashBackground if you want
    surface = CardBackground,
    // ...other roles
)