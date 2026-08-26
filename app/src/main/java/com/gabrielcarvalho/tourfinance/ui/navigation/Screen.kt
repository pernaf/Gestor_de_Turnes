package com.gabrielcarvalho.tourfinance.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object BandList : Screen("band_list")

    data object TourList : Screen("tour_list/{bandId}/{bandName}") {
        fun createRoute(bandId: Long, bandName: String): String {
            return "tour_list/$bandId/${Uri.encode(bandName)}"
        }
    }

    data object CreateTour : Screen("create_tour/{bandId}") {
        fun createRoute(bandId: Long): String {
            return "create_tour/$bandId"
        }
    }

    data object TourDetail : Screen("tour_detail/{tourId}") {
        fun createRoute(tourId: Long): String {
            return "tour_detail/$tourId"
        }
    }

    data object CityDetail : Screen("city_detail/{tourId}/{cityName}") {
        fun createRoute(tourId: Long, cityName: String): String {
            return "city_detail/$tourId/${Uri.encode(cityName)}"
        }
    }

    data object IncomeCategoryDetail :
        Screen("income_category_detail/{tourId}/{cityName}/{typeName}") {
        fun createRoute(tourId: Long, cityName: String, typeName: String): String {
            return "income_category_detail/$tourId/${Uri.encode(cityName)}/${Uri.encode(typeName)}"
        }
    }

    data object ExpenseCategoryDetail :
        Screen("expense_category_detail/{tourId}/{cityName}/{categoryName}") {
        fun createRoute(tourId: Long, cityName: String, categoryName: String): String {
            return "expense_category_detail/$tourId/${Uri.encode(cityName)}/${Uri.encode(categoryName)}"
        }
    }

    data object AddTourStop : Screen("add_tour_stop/{tourId}") {
        fun createRoute(tourId: Long): String {
            return "add_tour_stop/$tourId"
        }
    }

    data object AddExpense : Screen("add_expense/{tourId}?city={city}") {
        fun createRoute(tourId: Long, city: String = ""): String {
            return "add_expense/$tourId?city=${Uri.encode(city)}"
        }
    }

    data object AddIncome : Screen("add_income/{tourId}?city={city}") {
        fun createRoute(tourId: Long, city: String = ""): String {
            return "add_income/$tourId?city=${Uri.encode(city)}"
        }
    }

    data object EditExpense : Screen("edit_expense/{tourId}/{expenseId}") {
        fun createRoute(tourId: Long, expenseId: Long): String {
            return "edit_expense/$tourId/$expenseId"
        }
    }

    data object EditIncome : Screen("edit_income/{tourId}/{incomeId}") {
        fun createRoute(tourId: Long, incomeId: Long): String {
            return "edit_income/$tourId/$incomeId"
        }
    }
}