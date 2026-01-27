package com.aman.aiprompts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aman.aiprompts.ConsentManager.ConsentManager
import com.aman.aiprompts.ViewModel.PromptViewModel
import com.aman.aiprompts.ViewModel.PromptsViewModelFactory


@Composable
fun MyApp(consentManager: ConsentManager, adManager: InterstitialAdManager) {
    AppNavHost(consentManager,adManager)
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{id}")
}
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Favorite : BottomNavItem("favorite", Icons.Default.Favorite, "Favorite")
    object More : BottomNavItem("more", Icons.Default.Menu, "More")
}



@Composable
fun AppNavHost(consentManager : ConsentManager, adManager: InterstitialAdManager) {
    val bottomBarRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Favorite.route,
        BottomNavItem.More.route
    )
    val context = LocalContext.current
    val navController = rememberNavController()


    val viewModel: PromptViewModel = viewModel(
        factory = PromptsViewModelFactory(context)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes
    Scaffold(
        containerColor = Color(0xFF020617),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it }
                ) + fadeIn(
                    animationSpec = tween(300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut(
                    animationSpec = tween(200)
                )
            ) {
                BottomNavBar(navController)
            }
        }
    ) { padding ->
    NavHost(
        navController = navController,
        startDestination = "splashScreen"
    ) {
composable ("splashScreen" ){
SplashScreen(navController)
}
        composable(Screen.Home.route) {
            HomeScreen(  navController=navController,viewModel = viewModel, paddingValues=padding)
        }

        composable(BottomNavItem.Favorite.route) {
            FavoriteScreen(navController,viewModel)
        }

        composable(BottomNavItem.More.route) {
            MoreScreen()
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id")!!
            PromptDetailScreen(id,viewModel,consentManager,adManager)
        }
    }
}
    }

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorite,
        BottomNavItem.More
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(
        containerColor = Color(0xFF020617), // deep black
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(BottomNavItem.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(if (selected) 26.dp else 24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF1E293B)
                )
            )
        }
    }
}