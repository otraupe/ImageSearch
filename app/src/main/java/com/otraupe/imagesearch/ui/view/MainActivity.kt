package com.otraupe.imagesearch.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.otraupe.imagesearch.navigation.NavGraph
import com.otraupe.imagesearch.ui.theme.ImageSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            ImageSearchTheme {
                MainComposable()
            }
        }
    }
}

@Composable
fun MainComposable() {
    Scaffold(
        content = {
            val navController = rememberNavController()
            NavGraph(navController = navController, scaffoldPaddingValues = it)
        }
    )
}