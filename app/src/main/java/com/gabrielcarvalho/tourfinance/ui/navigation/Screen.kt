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

    object AddExpense : Screen("add_expense/{tourId}?city={city}") {
        fun createRoute(tourId: Long, city: String = "") =
            "add_expense/$tourId?city=${Uri.encode(city)}"
    }

    object AddIncome : Screen("add_income/{tourId}?city={city}") {
        fun createRoute(tourId: Long, city: String = "") =
            "add_income/$tourId?city=${Uri.encode(city)}"
    }

    object EditExpense : Screen("edit_expense/{tourId}/{expenseId}") {
        fun createRoute(tourId: Long, expenseId: Long) = "edit_expense/$tourId/$expenseId"
    }

    object EditIncome : Screen("edit_income/{tourId}/{incomeId}") {
        fun createRoute(tourId: Long, incomeId: Long) = "edit_income/$tourId/$incomeId"
    }
    object Splash : Screen("splash")

    data object AddTourStop : Screen("add_tour_stop/{tourId}") {
        fun createRoute(tourId: Long) = "add_tour_stop/$tourId"
    }
}