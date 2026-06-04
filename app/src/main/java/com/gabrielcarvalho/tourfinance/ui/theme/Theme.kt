package com.gabrielcarvalho.tourfinance.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = MossGreen,
    onPrimary = OffWhite,
    primaryContainer = MossGreenLight,
    onPrimaryContainer = OffWhite,
    background = OffWhite,
    onBackground = DarkText,
    surface = LightSurface,
    onSurface = DarkText,
    onSurfaceVariant = MutedText,
    error = Color(0xFFB00020),
    onError = OffWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = MossGreenLight,
    onPrimary = DarkText,
    background = MossGreenDark,
    onBackground = OffWhite,
    surface = MossGreen,
    onSurface = OffWhite,
    onSurfaceVariant = OffWhite.copy(alpha = 0.7f)
)



@Composable
fun TourFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}