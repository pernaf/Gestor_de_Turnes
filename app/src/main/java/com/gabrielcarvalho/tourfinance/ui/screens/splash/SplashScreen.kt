package com.gabrielcarvalho.tourfinance.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabrielcarvalho.tourfinance.R
import com.gabrielcarvalho.tourfinance.ui.theme.MossGreen
import com.gabrielcarvalho.tourfinance.ui.theme.OffWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000L)
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MossGreen)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.van_logo),
            contentDescription = "Logo TourFinance",
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .sizeIn(maxWidth = 280.dp, maxHeight = 280.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "GESTÃO FINANCEIRA\nDE TURNÊS PARA BANDAS",
            color = OffWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "——  FOCO NA MÚSICA.  ——\nCONTROLE NA ESTRADA.",
            color = OffWhite.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp,
            lineHeight = 18.sp
        )
    }
}