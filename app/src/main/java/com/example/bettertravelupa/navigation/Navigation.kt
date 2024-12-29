package com.example.bettertravelupa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bettertravelupa.model.AppDatabase
import com.example.bettertravelupa.model.ImageDao
import com.example.bettertravelupa.screen.GalleryScreen
import com.example.bettertravelupa.screen.GreetingScreen
import com.example.bettertravelupa.screen.LoginScreen
import com.example.bettertravelupa.screen.RegisterScreen
import com.example.bettertravelupa.screen.RekomendasiTempatScreen
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Greeting : Screen("greeting")
    object Login : Screen("login")
    object Register : Screen("register")
    object RekomendasiTempat : Screen("rekomendasi_tempat")
    object Gallery : Screen("gallery")
}

@Composable
fun AppNavigation(imageDao: ImageDao) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appDatabase = AppDatabase.getInstance(context)
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser // Get the current user

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) Screen.RekomendasiTempat.route else Screen.Greeting.route
    ) {
        composable(Screen.Greeting.route) {
            GreetingScreen(
                onStart = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Greeting.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { userId ->
                    navController.navigate(Screen.RekomendasiTempat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.RekomendasiTempat.route) {
            RekomendasiTempatScreen(
                firestore = FirebaseFirestore.getInstance(),
                appDatabase = appDatabase,
                onBackToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.RekomendasiTempat.route) { inclusive = true }
                    }
                },
                onGallerySelected = {
                    navController.navigate(Screen.Gallery.route) {
                        popUpTo(Screen.RekomendasiTempat.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Gallery.route) {
            GalleryScreen(
                imageDao = imageDao,
                onImageSelected = { uri ->
                    // Implement handling image selection
                },
                onBack = {
                    navController.navigate(Screen.RekomendasiTempat.route) {
                        popUpTo(Screen.Gallery.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
