package com.gabrielcarvalho.tourfinance.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TransactionItem(
    emoji: String,
    title: String,
    subtitle: String,
    amount: Double,
    isExpense: Boolean,
    currency: String = "R$",
    onClick: () -> Unit = {}
) {
    val amountColor = if (isExpense) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    val prefix = if (isExpense) "- " else "+ "


    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ){
        ListItem(
            headlineContent = { Text("$emoji  $title") },
            supportingContent = {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$prefix$currency ${"%,.2f".format(amount)}",
                        color = amountColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        )
        HorizontalDivider(thickness = 0.5.dp)
    }
}