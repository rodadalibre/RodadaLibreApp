package com.rodrigocarreon.rodadalibre.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import com.rodrigocarreon.rodadalibre.R
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodrigocarreon.rodadalibre.ui.screens.MapScreen
import com.rodrigocarreon.rodadalibre.ui.screens.ProfileScreen
import com.rodrigocarreon.rodadalibre.ui.viewmodel.PlacesViewModel

val ic_map = R.drawable.ic_map
val ic_search = R.drawable.ic_search
val ic_account = R.drawable.ic_account

sealed class BottomNavItem(val route: String, val tittle:String, val icon: Int){
    object Map: BottomNavItem("map", "Mapa", ic_map)
    object Search: BottomNavItem("search", "Estaciones", ic_search)
    object Account: BottomNavItem("account", "Perfil", ic_account)
}

@Composable
fun AppNavigation(viewModel: PlacesViewModel){
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem.Map,
        BottomNavItem.Search,
        BottomNavItem.Account
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(80.dp),
                containerColor = Color(0xFF2D2D2D),
                contentColor = Color.White
            ){
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.tittle) },
                        label = {Text(item.tittle)},
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route){
                                navController.navigate(item.route){
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) {saveState = true}
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colorScheme.primary,
                            unselectedIconColor = Color.White,
                            selectedTextColor = colorScheme.primary,
                            unselectedTextColor = Color.White,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ){ paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Map.route,
        ) {
            composable(BottomNavItem.Map.route) {
                MapScreen(viewModel = viewModel, modifier = Modifier.padding(paddingValues))
            }
            composable(BottomNavItem.Account.route){
                ProfileScreen()
            }
        }
    }
}