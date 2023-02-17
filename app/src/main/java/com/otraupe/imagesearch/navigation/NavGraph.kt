package com.otraupe.imagesearch.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.otraupe.imagesearch.ui.view.detail.DetailView
import com.otraupe.imagesearch.ui.view.search.SearchView

@Composable
fun NavGraph (
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.Search.route,
    scaffoldPaddingValues: PaddingValues
){
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screens.Search.route) {
            SearchView(
                scaffoldPaddingValues,
                onNavigateToDetails = { string -> navController.navigate(Screens.Detail.route
                    .replace("{imageId}", string,false)) },
            )
        }
        composable(
            route = Screens.Detail.route,
            arguments = listOf(navArgument("imageId") { type = NavType.StringType })
        ) {
            DetailView(
                scaffoldPaddingValues,
                navController = navController,
                it.arguments?.getString("imageId")
            )
        }
    }
}