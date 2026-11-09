package com.example.abarrotapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.abarrotapp.ui.AppViewModelProvider
import com.example.abarrotapp.ui.screen.auth.LoginScreen
import com.example.abarrotapp.ui.screen.auth.RegisterScreen
import com.example.abarrotapp.ui.screen.home.HomeScreen
import com.example.abarrotapp.ui.screen.producto.ProductoDetailScreen
import com.example.abarrotapp.ui.screen.producto.ProductoFormScreen
import com.example.abarrotapp.ui.viewmodel.AuthViewModel
import com.example.abarrotapp.ui.viewmodel.HomeViewModel
import com.example.abarrotapp.ui.viewmodel.ProductoDetailViewModel
import com.example.abarrotapp.ui.viewmodel.ProductoFormViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProductoDetail : Screen("producto_detail/{productId}") {
        fun createRoute(productId: String) = "producto_detail/$productId"
    }
    object ProductoForm : Screen("producto_form?productId={productId}") {
        fun createRoute(productId: String? = null) = if (productId != null) "producto_form?productId=$productId" else "producto_form"
    }
}

@Composable
fun AbarrotesNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val startDestination = if (authViewModel.isLoggedIn()) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductoDetail.createRoute(productId))
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.ProductoForm.createRoute())
                },
                onNavigateToEdit = { productId ->
                    navController.navigate(Screen.ProductoForm.createRoute(productId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ProductoDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val detailViewModel: ProductoDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
            ProductoDetailScreen(
                productId = productId,
                viewModel = detailViewModel,
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.ProductoForm.createRoute(id))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ProductoForm.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val formViewModel: ProductoFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
            ProductoFormScreen(
                productId = productId,
                viewModel = formViewModel,
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
