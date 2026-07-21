package com.gabrielcarvalho.tourfinance.data.local.staticdata

import android.content.Context
import org.json.JSONArray

object BrazilCitiesAssetDataSource {

    fun loadCities(context: Context): List<String> {
        val json = context.assets
            .open("brazil_cities.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(json)

        return buildList {
            for (i in 0 until jsonArray.length()) {
                add(jsonArray.getString(i))
            }
        }
    }
}