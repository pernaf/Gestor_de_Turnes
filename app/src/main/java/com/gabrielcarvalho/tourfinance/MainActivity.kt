package com.gabrielcarvalho.tourfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.gabrielcarvalho.tourfinance.ui.navigation.AppNavHost
import com.gabrielcarvalho.tourfinance.ui.theme.TourFinanceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TourFinanceTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}