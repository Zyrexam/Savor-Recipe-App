package com.example.savor_recipe_app.activiity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.savor_recipe_app.ui.theme.SavorRecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SavorRecipeAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen { ingredients, searchTerm ->
                        val intent = Intent(this, RecipeList::class.java).apply {
                            putExtra("ingredients", ingredients)
                            putExtra("search", searchTerm)
                        }
                        startActivity(intent)
                    }
                }
            }

        }
    }
}