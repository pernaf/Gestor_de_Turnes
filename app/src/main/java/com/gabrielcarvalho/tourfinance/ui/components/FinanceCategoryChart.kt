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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

data class FinanceChartItem(
    val label: String,
    val value: Float,
    val color: Color
)

private val brazilLocale = Locale("pt", "BR")

@Composable
fun FinanceCategoryChart(
    items: List<FinanceChartItem>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val maxValue = remember(items) {
        items.maxOfOrNull { it.value }?.takeIf { it > 0f } ?: 1f
    }

    val totalValue = remember(items) {
        items.sumOf { it.value.toDouble() }.toFloat().takeIf { it > 0f } ?: 1f
    }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(brazilLocale)
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
                totalValue = totalValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(252.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    val percentage = remember(item.value, totalValue) {
                        ((item.value / totalValue) * 100f).coerceAtLeast(0f)
                    }

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
                            text = buildString {
                                append(currencyFormatter.format(item.value.toDouble()))
                                append(" • ")
                                append(
                                    String.format(
                                        brazilLocale,
                                        "%.1f%%",
                                        percentage
                                    )
                                )
                            },
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
    totalValue: Float,
    modifier: Modifier = Modifier
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val textColor = MaterialTheme.colorScheme.onSurface

    val percentageTextSizePx = with(density) { 11.sp.toPx() }
    val topPaddingPx = with(density) { 34.dp.toPx() }
    val bottomPaddingPx = with(density) { 24.dp.toPx() }
    val labelGapPx = with(density) { 8.dp.toPx() }
    val minBarWidthPx = with(density) { 18.dp.toPx() }
    val spacingPx = with(density) { 16.dp.toPx() }

    Canvas(modifier = modifier) {
        val usableHeight = size.height - topPaddingPx - bottomPaddingPx
        val totalSpacing = spacingPx * (items.size + 1)
        val barWidth = ((size.width - totalSpacing) / items.size).coerceAtLeast(minBarWidthPx)

        val textPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = textColor.toArgb()
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = percentageTextSizePx
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
        }

        val textHeight = textPaint.fontMetrics.run { bottom - top }

        items.forEachIndexed { index, item ->
            val left = spacingPx + index * (barWidth + spacingPx)
            val barHeight = (item.value / maxValue) * usableHeight
            val top = topPaddingPx + (usableHeight - barHeight)
            val centerX = left + (barWidth / 2f)

            val percentage = ((item.value / totalValue) * 100f).coerceAtLeast(0f)
            val percentageLabel = String.format(brazilLocale, "%.1f%%", percentage)

            val safeTextBaseline = top - labelGapPx
            val minAllowedBaseline = textHeight
            val textY = safeTextBaseline.coerceAtLeast(minAllowedBaseline)

            drawRoundRect(
                color = item.color,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(18f, 18f)
            )

            drawContext.canvas.nativeCanvas.drawText(
                percentageLabel,
                centerX,
                textY,
                textPaint
            )
        }
    }
}