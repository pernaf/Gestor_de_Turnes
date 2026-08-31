package com.gabrielcarvalho.tourfinance.ui.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyInputUtils {

    fun parseMoneyValue(text: String): Double? {
        val cleaned = text
            .trim()
            .replace("R$", "", ignoreCase = true)
            .replace("\\s+".toRegex(), "")
            .replace("[^\\d,.-]".toRegex(), "")

        if (cleaned.isBlank()) return null

        val lastComma = cleaned.lastIndexOf(',')
        val lastDot = cleaned.lastIndexOf('.')
        val decimalSeparatorIndex = maxOf(lastComma, lastDot)

        val normalized = if (decimalSeparatorIndex >= 0) {
            val integerPart = cleaned
                .substring(0, decimalSeparatorIndex)
                .replace(".", "")
                .replace(",", "")

            val decimalPart = cleaned
                .substring(decimalSeparatorIndex + 1)
                .replace(".", "")
                .replace(",", "")

            if (integerPart.isBlank() && decimalPart.isBlank()) return null

            buildString {
                append(if (integerPart.isBlank()) "0" else integerPart)
                if (decimalPart.isNotEmpty()) {
                    append(".")
                    append(decimalPart)
                }
            }
        } else {
            cleaned
                .replace(".", "")
                .replace(",", "")
        }

        return normalized.toDoubleOrNull()
    }

    fun formatMoneyForInput(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("pt", "BR")).apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }

        return DecimalFormat("#,##0.00", symbols).format(value)
    }
}