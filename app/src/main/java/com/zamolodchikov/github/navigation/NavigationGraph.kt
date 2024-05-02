package com.zamolodchikov.github.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zamolodchikov.github.screens.user_details.view.UserDetailsScreen
import com.zamolodchikov.github.screens.users_list.view.UsersListScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.UsersList.destination
    ) {
        composable(Screen.UsersList.destination) {
            UsersListScreen(
                onNavigateToUser = {
                    navController.navigate(route = Screen.UserDetails.navigate(it))
                }
            )
        }

        composable(
            route = Screen.UserDetails.destination,
            arguments = listOf(
                navArgument(Screen.UserDetails.KEY_USER_LOGIN) {
                    type = NavType.StringType
                }
            ),
        ) {
            UserDetailsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}