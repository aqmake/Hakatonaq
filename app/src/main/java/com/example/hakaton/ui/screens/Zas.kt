package com.example.hakaton.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakaton.R
import com.example.hakaton.ui.nav.NavState
import com.example.hakaton.ui.theme.firstFont
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onNextScreen: (NavState) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "",
            modifier = Modifier.size(127.dp, 114.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            "QR-Квантум",
            color = Color(0xff000000),
            fontSize = 20.sp,
            fontFamily = firstFont
        )
    }

    LaunchedEffect(Unit) {
        delay(2500)
        onNextScreen(NavState.Log)
    }
}