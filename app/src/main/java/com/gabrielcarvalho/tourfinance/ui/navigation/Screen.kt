package com.gabrielcarvalho.tourfinance.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object BandList : Screen("band_list")

    object TourList : Screen("tour_list/{bandId}/{bandName}") {
        fun createRoute(bandId: Long, bandName: String) =
            "tour_list/$bandId/${Uri.encode(bandName)}"
    }

    object CreateTour : Screen("create_tour/{bandId}") {
        fun createRoute(bandId: Long) = "create_tour/$bandId"
    }

    object TourDetail : Screen("tour_detail/{tourId}") {
        fun createRoute(tourId: Long) = "tour_detail/$tourId"
    }

    object AddExpense : Screen("add_expense/{tourId}") {
        fun createRoute(tourId: Long) = "add_expense/$tourId"
    }

    object AddIncome : Screen("add_income/{tourId}") {
        fun createRoute(tourId: Long) = "add_income/$tourId"
    }

    object EditExpense : Screen("edit_expense/{tourId}/{expenseId}") {
        fun createRoute(tourId: Long, expenseId: Long) = "edit_expense/$tourId/$expenseId"
    }

    object EditIncome : Screen("edit_income/{tourId}/{incomeId}") {
        fun createRoute(tourId: Long, incomeId: Long) = "edit_income/$tourId/$incomeId"
    }
    object Splash : Screen("splash")
}