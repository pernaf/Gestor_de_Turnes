package com.gabrielcarvalho.tourfinance.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FinanceChartItem(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun FinanceCategoryChart(
    items: List<FinanceChartItem>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val maxValue = remember(items) {
        items.maxOfOrNull { it.value }?.takeIf { it > 0f } ?: 1f
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Visão por categoria",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            SimpleBarChart(
                items = items,
                maxValue = maxValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(item.color, CircleShape)
                            )

                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            text = "R$ ${"%,.2f".format(item.value)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = item.color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleBarChart(
    items: List<FinanceChartItem>,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val spacing = 20f
        val bottomPadding = 24f
        val usableHeight = size.height - bottomPadding
        val totalSpacing = spacing * (items.size + 1)
        val barWidth = ((size.width - totalSpacing) / items.size).coerceAtLeast(18f)

        items.forEachIndexed { index, item ->
            val left = spacing + index * (barWidth + spacing)
            val barHeight = (item.value / maxValue) * usableHeight
            val top = usableHeight - barHeight

            drawRoundRect(
                color = item.color,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(18f, 18f)
            )
        }
    }
}