package com.example.savor_recipe_app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.savor_recipe_app.auth.AuthViewModel
import com.example.savor_recipe_app.data.UserProfile
import com.example.savor_recipe_app.data.UserRepository
import com.example.savor_recipe_app.model.Recipe
import com.example.savor_recipe_app.network.SpoonacularService
import com.example.savor_recipe_app.util.ApiConfig
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Scaffold

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val user = authViewModel.currentUser.collectAsState().value
    val userRepository = remember { UserRepository() }
    val coroutineScope = rememberCoroutineScope()

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var nameField by remember { mutableStateOf(TextFieldValue("")) }
    var isEditing by remember { mutableStateOf(false) }
    var favoriteRecipes by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var favoriteRecipeDetails by remember { mutableStateOf(listOf<Recipe>()) }
    var photoUrl by remember { mutableStateOf<String?>(null) }
    var uploading by remember { mutableStateOf(false) }
    val storage = FirebaseStorage.getInstance()
    val context = LocalContext.current

    var successMessage by remember { mutableStateOf<String?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploading = true
            coroutineScope.launch {
                try {
                    val ref = storage.reference.child("profile_photos/${user?.uid}.jpg")
                    ref.putFile(uri).await()
                    val downloadUrl = ref.downloadUrl.await().toString()
                    user?.uid?.let { uid ->
                        userRepository.updateUserProfile(uid, mapOf("photoUrl" to downloadUrl))
                        photoUrl = downloadUrl
                        userProfile = userProfile?.copy(photoUrl = downloadUrl)
                        successMessage = "Profile photo updated!"
                    }
                } catch (e: Exception) {
                    errorMessage = "Failed to upload photo: ${e.message}"
                } finally {
                    uploading = false
                }
            }
        }
    }

    fun removeProfilePhoto() {
        coroutineScope.launch {
            uploading = true
            try {
                val ref = storage.reference.child("profile_photos/${user?.uid}.jpg")
                ref.delete().await()
                user?.uid?.let { uid ->
                    userRepository.updateUserProfile(uid, mapOf("photoUrl" to ""))
                    photoUrl = null
                    userProfile = userProfile?.copy(photoUrl = null)
                    successMessage = "Profile photo removed."
                }
            } catch (e: Exception) {
                errorMessage = "Failed to remove photo: ${e.message}"
            } finally {
                uploading = false
            }
        }
    }

    LaunchedEffect(user) {
        isLoading = true
        errorMessage = null
        user?.uid?.let { uid ->
            try {
                userProfile = userRepository.getUserProfile(uid)
                nameField = TextFieldValue(userProfile?.name ?: "")
                favoriteRecipes = userRepository.getFavoriteRecipes(uid)
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
        isLoading = false
    }

    LaunchedEffect(favoriteRecipes) {
        if (favoriteRecipes.isNotEmpty()) {
            val api = SpoonacularService.api
            val apiKey = ApiConfig.SPOONACULAR_API_KEY
            val details = mutableListOf<Recipe>()
            for (id in favoriteRecipes) {
                try {
                    val recipe = api.getRecipeById(id.toInt(), apiKey)
                    details.add(recipe)
                } catch (_: Exception) {}
            }
            favoriteRecipeDetails = details
        } else {
            favoriteRecipeDetails = emptyList()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
                errorMessage = null
            }
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
                successMessage = null
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Profile", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (userProfile != null) {
                // Profile photo
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = photoUrl ?: userProfile?.photoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                    if (uploading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Change Photo")
                }
                if (photoUrl != null || userProfile?.photoUrl != null) {
                    TextButton(onClick = { removeProfilePhoto() }) {
                        Text("Remove Photo")
                    }
                }
                OutlinedTextField(
                    value = nameField,
                    onValueChange = { nameField = it },
                    label = { Text("Name") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Email: ${userProfile?.email ?: "-"}")
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    if (isEditing) {
                        Button(onClick = {
                            coroutineScope.launch {
                                user?.uid?.let { uid ->
                                    userRepository.updateUserProfile(uid, mapOf("name" to nameField.text))
                                    userProfile = userProfile?.copy(name = nameField.text)
                                    isEditing = false
                                    successMessage = "Name updated successfully!"
                                }
                            }
                        }) {
                            Text("Save")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { isEditing = false; nameField = TextFieldValue(userProfile?.name ?: "") }) {
                            Text("Cancel")
                        }
                    } else {
                        OutlinedButton(onClick = { isEditing = true }) {
                            Text("Edit Name")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Favorite Recipes", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                if (favoriteRecipeDetails.isEmpty()) {
                    Text("No favorites yet.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(favoriteRecipeDetails) { recipe ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = recipe.image,
                                        contentDescription = recipe.title,
                                        modifier = Modifier.size(64.dp).padding(8.dp)
                                    )
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(recipe.title, style = MaterialTheme.typography.bodyLarge)
                                        Text("${recipe.readyInMinutes} min | ${recipe.servings} servings", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        authViewModel.signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Log Out", color = MaterialTheme.colorScheme.onError)
                }
            } else if (errorMessage != null) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        }
    }
} 