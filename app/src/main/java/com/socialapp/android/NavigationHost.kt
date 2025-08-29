package com.socialapp.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.socialapp.android.data.repository.MediaRepository
import com.socialapp.android.data.repository.UserRepository
import com.socialapp.android.ui.album.AlbumScreen
import com.socialapp.android.ui.friends.FriendsScreen
import com.socialapp.android.ui.media.MediaViewModel
import com.socialapp.android.ui.onboarding.OnboardingScreen
import com.socialapp.android.ui.user.UserViewModel

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    
    // Check if user is authenticated
    var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }
    
    // Check if user data exists
    var userDataExists by remember { mutableStateOf(false) }
    val userRepository = remember { UserRepository() }
    
    // ViewModels
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )
    
    val mediaRepository = remember { MediaRepository() }
    val mediaViewModel: MediaViewModel = viewModel(
        factory = MediaViewModelFactory(mediaRepository, userRepository)
    )
    
    // Check if user data exists
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            val userId = auth.currentUser?.uid ?: ""
            val user = userRepository.getUser(userId)
            userDataExists = user != null
        }
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Handle permission result
        }
    )
    
    // Request storage permission
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
            
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated && userDataExists) "main" else "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen { name, dob ->
                // Save user data and navigate to main screen
                userViewModel.saveUser(name, dob)
                isAuthenticated = true
                userDataExists = true
                navController.navigate("main") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }
        
        composable("main") {
            MainScreen(
                userViewModel = userViewModel,
                mediaViewModel = mediaViewModel,
                onSignOut = {
                    auth.signOut()
                    isAuthenticated = false
                    userDataExists = false
                    navController.navigate("onboarding") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}