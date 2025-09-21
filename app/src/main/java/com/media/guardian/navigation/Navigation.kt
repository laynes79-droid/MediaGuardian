package com.media.guardian.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.media.guardian.screens.DetailScreen
import com.media.guardian.screens.FolderPickerScreen
import com.media.guardian.screens.MainScreen
import com.media.guardian.viewmodel.MediaViewModel

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Detail : Screen("detail/{mediaItemId}") {
        fun createRoute(mediaItemId: Long) = "detail/$mediaItemId"
    }
    object FolderPicker : Screen("folder_picker/{mediaItemId}/{operationType}") {
        fun createRoute(mediaItemId: Long, operationType: String) = "folder_picker/$mediaItemId/$operationType"
    }
}

@Composable
fun AppNavigation(viewModel: MediaViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onMediaClick = { mediaItem ->
                    navController.navigate(Screen.Detail.createRoute(mediaItem.id))
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("mediaItemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val mediaItemId = backStackEntry.arguments?.getLong("mediaItemId")
            DetailScreen(
                viewModel = viewModel,
                mediaItemId = mediaItemId,
                navController = navController
            )
        }
        composable(
            route = Screen.FolderPicker.route,
            arguments = listOf(
                navArgument("mediaItemId") { type = NavType.LongType },
                navArgument("operationType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mediaItemId = backStackEntry.arguments?.getLong("mediaItemId")
            val operationType = backStackEntry.arguments?.getString("operationType")

            // A null check is important here
            if (mediaItemId != null && operationType != null) {
                FolderPickerScreen(
                    navController = navController,
                    viewModel = viewModel,
                    mediaItemId = mediaItemId,
                    operationType = operationType
                )
            }
        }
    }
}
