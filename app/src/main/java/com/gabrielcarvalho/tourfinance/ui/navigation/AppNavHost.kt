package com.gabrielcarvalho.tourfinance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gabrielcarvalho.tourfinance.ui.screens.band.BandListScreen
import com.gabrielcarvalho.tourfinance.ui.screens.city.CityDetailScreen
import com.gabrielcarvalho.tourfinance.ui.screens.city.ExpenseCategoryDetailScreen
import com.gabrielcarvalho.tourfinance.ui.screens.city.IncomeCategoryDetailScreen
import com.gabrielcarvalho.tourfinance.ui.screens.expense.AddExpenseScreen
import com.gabrielcarvalho.tourfinance.ui.screens.income.AddIncomeScreen
import com.gabrielcarvalho.tourfinance.ui.screens.splash.SplashScreen
import com.gabrielcarvalho.tourfinance.ui.screens.tour.CreateTourScreen
import com.gabrielcarvalho.tourfinance.ui.screens.tour.TourDetailScreen
import com.gabrielcarvalho.tourfinance.ui.screens.tour.TourListScreen
import com.gabrielcarvalho.tourfinance.ui.screens.tourstop.AddTourStopScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
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
                    navController.navigate(
                        Screen.TourList.createRoute(
                            bandId = bandId,
                            bandName = bandName
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.TourList.route,
            arguments = listOf(
                navArgument("bandId") { type = NavType.LongType },
                navArgument("bandName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bandId = backStackEntry.arguments?.getLong("bandId") ?: return@composable
            val bandName = backStackEntry.arguments?.getString("bandName").orEmpty()

            TourListScreen(
                bandId = bandId,
                bandName = bandName,
                onTourClick = { tourId ->
                    navController.navigate(Screen.TourDetail.createRoute(tourId))
                },
                onCreateTour = {
                    navController.navigate(Screen.CreateTour.createRoute(bandId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CreateTour.route,
            arguments = listOf(
                navArgument("bandId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val bandId = backStackEntry.arguments?.getLong("bandId") ?: return@composable

            CreateTourScreen(
                bandId = bandId,
                onTourCreated = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TourDetail.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable

            TourDetailScreen(
                tourId = tourId,
                onAddExpense = { city ->
                    navController.navigate(
                        Screen.AddExpense.createRoute(
                            tourId = tourId,
                            city = city
                        )
                    )
                },
                onAddIncome = { city ->
                    navController.navigate(
                        Screen.AddIncome.createRoute(
                            tourId = tourId,
                            city = city
                        )
                    )
                },
                onAddTourStop = {
                    navController.navigate(Screen.AddTourStop.createRoute(tourId))
                },
                onEditExpense = { expenseId ->
                    navController.navigate(
                        Screen.EditExpense.createRoute(
                            tourId = tourId,
                            expenseId = expenseId
                        )
                    )
                },
                onEditIncome = { incomeId ->
                    navController.navigate(
                        Screen.EditIncome.createRoute(
                            tourId = tourId,
                            incomeId = incomeId
                        )
                    )
                },
                onNavigateToCity = { cityName ->
                    navController.navigate(
                        Screen.CityDetail.createRoute(
                            tourId = tourId,
                            cityName = cityName
                        )
                    )
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CityDetail.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("cityName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            val cityName = backStackEntry.arguments?.getString("cityName").orEmpty()

            CityDetailScreen(
                tourId = tourId,
                cityName = cityName,
                onAddIncome = { city ->
                    navController.navigate(
                        Screen.AddIncome.createRoute(
                            tourId = tourId,
                            city = city
                        )
                    )
                },
                onAddExpense = { city ->
                    navController.navigate(
                        Screen.AddExpense.createRoute(
                            tourId = tourId,
                            city = city
                        )
                    )
                },
                onOpenIncomeCategory = { city, typeName ->
                    navController.navigate(
                        Screen.IncomeCategoryDetail.createRoute(
                            tourId = tourId,
                            cityName = city,
                            typeName = typeName
                        )
                    )
                },
                onOpenExpenseCategory = { city, categoryName ->
                    navController.navigate(
                        Screen.ExpenseCategoryDetail.createRoute(
                            tourId = tourId,
                            cityName = city,
                            categoryName = categoryName
                        )
                    )
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.IncomeCategoryDetail.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("cityName") { type = NavType.StringType },
                navArgument("typeName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            val cityName = backStackEntry.arguments?.getString("cityName").orEmpty()
            val typeName = backStackEntry.arguments?.getString("typeName").orEmpty()

            IncomeCategoryDetailScreen(
                tourId = tourId,
                cityName = cityName,
                typeName = typeName,
                onEditIncome = { incomeId ->
                    navController.navigate(
                        Screen.EditIncome.createRoute(
                            tourId = tourId,
                            incomeId = incomeId
                        )
                    )
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ExpenseCategoryDetail.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("cityName") { type = NavType.StringType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            val cityName = backStackEntry.arguments?.getString("cityName").orEmpty()
            val categoryName = backStackEntry.arguments?.getString("categoryName").orEmpty()

            ExpenseCategoryDetailScreen(
                tourId = tourId,
                cityName = cityName,
                categoryName = categoryName,
                onEditExpense = { expenseId ->
                    navController.navigate(
                        Screen.EditExpense.createRoute(
                            tourId = tourId,
                            expenseId = expenseId
                        )
                    )
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddTourStop.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable

            AddTourStopScreen(
                tourId = tourId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddExpense.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("city") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            val city = backStackEntry.arguments?.getString("city").orEmpty()

            AddExpenseScreen(
                tourId = tourId,
                preselectedCity = city,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddIncome.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("city") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            val city = backStackEntry.arguments?.getString("city").orEmpty()

            AddIncomeScreen(
                tourId = tourId,
                preselectedCity = city,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("expenseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: return@composable

            AddExpenseScreen(
                tourId = tourId,
                expenseId = expenseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditIncome.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.LongType },
                navArgument("incomeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getLong("tourId") ?: return@composable
            val incomeId = backStackEntry.arguments?.getLong("incomeId") ?: return@composable

            AddIncomeScreen(
                tourId = tourId,
                incomeId = incomeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}