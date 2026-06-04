package com.gabrielcarvalho.tourfinance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gabrielcarvalho.tourfinance.ui.screens.band.BandListScreen
import com.gabrielcarvalho.tourfinance.ui.screens.expense.AddExpenseScreen
import com.gabrielcarvalho.tourfinance.ui.screens.income.AddIncomeScreen
import com.gabrielcarvalho.tourfinance.ui.screens.splash.SplashScreen
import com.gabrielcarvalho.tourfinance.ui.screens.tour.CreateTourScreen
import com.gabrielcarvalho.tourfinance.ui.screens.tour.TourDetailScreen
import com.gabrielcarvalho.tourfinance.ui.screens.tour.TourListScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route   // ← era Screen.BandList.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.BandList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BandList.route) {
            BandListScreen(
                onNavigateToBand = { bandId, bandName ->
                    navController.navigate(Screen.TourList.createRoute(bandId, bandName))
                }
            )
        }

        // Lista de turnês da banda
        composable(
            route = Screen.TourList.route,
            arguments = listOf(
                navArgument("bandId") { type = NavType.LongType },
                navArgument("bandName") { type = NavType.StringType }
            )
        ) { backStack ->
            val bandId = backStack.arguments?.getLong("bandId") ?: return@composable
            val bandName = backStack.arguments?.getString("bandName") ?: ""
            TourListScreen(
                bandId = bandId,
                bandName = bandName,
                onTourClick = { tourId ->                          // ← era onNavigateToTour
                    navController.navigate(Screen.TourDetail.createRoute(tourId))
                },
                onCreateTour = {
                    navController.navigate(Screen.CreateTour.createRoute(bandId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Criar turnê
        composable(
            route = Screen.CreateTour.route,
            arguments = listOf(navArgument("bandId") { type = NavType.LongType })
        ) { backStack ->
            val bandId = backStack.arguments?.getLong("bandId") ?: return@composable
            CreateTourScreen(
                bandId = bandId,
                onTourCreated = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Detalhe da turnê
        composable(
            route = Screen.TourDetail.route,
            arguments = listOf(navArgument("tourId") { type = NavType.LongType })
        ) { backStack ->
            val tourId = backStack.arguments?.getLong("tourId") ?: return@composable
            TourDetailScreen(
                tourId = tourId,
                onAddExpense = { navController.navigate(Screen.AddExpense.createRoute(tourId)) },
                onAddIncome = { navController.navigate(Screen.AddIncome.createRoute(tourId)) },
                onEditExpense = { expenseId ->
                    navController.navigate(Screen.EditExpense.createRoute(tourId, expenseId))
                },
                onEditIncome = { incomeId ->
                    navController.navigate(Screen.EditIncome.createRoute(tourId, incomeId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Adicionar despesa
        composable(
            route = Screen.AddExpense.route,
            arguments = listOf(navArgument("tourId") { type = NavType.LongType })
        ) { backStack ->
            val tourId = backStack.arguments?.getLong("tourId") ?: return@composable
            AddExpenseScreen(
                tourId = tourId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Adicionar receita
        composable(
            route = Screen.AddIncome.route,
            arguments = listOf(navArgument("tourId") { type = NavType.LongType })
        ) { backStack ->
            val tourId = backStack.arguments?.getLong("tourId") ?: return@composable
            AddIncomeScreen(
                tourId = tourId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Editar despesa
        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("expenseId") { type = NavType.LongType }
            )
        ) { backStack ->
            val tourId = backStack.arguments?.getLong("tourId") ?: return@composable
            val expenseId = backStack.arguments?.getLong("expenseId") ?: return@composable
            AddExpenseScreen(
                tourId = tourId,
                expenseId = expenseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Editar receita
        composable(
            route = Screen.EditIncome.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("incomeId") { type = NavType.LongType }
            )
        ) { backStack ->
            val tourId = backStack.arguments?.getLong("tourId") ?: return@composable
            val incomeId = backStack.arguments?.getLong("incomeId") ?: return@composable
            AddIncomeScreen(
                tourId = tourId,
                incomeId = incomeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}