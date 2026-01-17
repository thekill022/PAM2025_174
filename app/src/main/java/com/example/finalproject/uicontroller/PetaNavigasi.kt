package com.example.finalproject.uicontroller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalproject.ui.view.HomeScreen
import com.example.finalproject.uicontroller.route.DestinasiHome
import com.example.finalproject.uicontroller.route.DestinasiLogin
import com.example.finalproject.uicontroller.route.DestinasiRegister
import com.example.finalproject.view.LoginScreen
import com.example.finalproject.view.RegisterScreen
import com.example.finalproject.viewmodel.MainViewModel
import com.example.finalproject.viewmodel.PenyediaViewModel

@Composable
fun CalorieApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val mainUiState by mainViewModel.uiState.collectAsState()

    if (mainUiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val startRoute = if (mainUiState.isLoggedIn) {
            DestinasiHome.route
        } else {
            DestinasiLogin.route
        }

        HostNavigasi(
            navController = navController,
            startDestination = startRoute
        )
    }
}

@Composable
fun HostNavigasi(
    navController: NavHostController,
    startDestination : String,
    modifier: Modifier = Modifier
) {
NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {

    composable(DestinasiLogin.route) {
        LoginScreen(onNavigateToRegister = { navController.navigate(DestinasiRegister.route) }, onLoginSuccess = { navController.navigate(DestinasiHome.route) })
    }
    composable(DestinasiRegister.route) {
        RegisterScreen(onNavigateUp = { navController.navigateUp() })
    }
    composable(DestinasiHome.route) {
        HomeScreen(navigateToItemEntry = { /*TODO*/ })
    }
}
}