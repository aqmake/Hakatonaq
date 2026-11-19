package com.example.hakaton.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakaton.data.DATE_ONLY_FORMAT
import com.example.hakaton.data.ParticipantWithGroup
import com.example.hakaton.ui.QrCode
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont

// Здесь хранится участник, который вошёл по коду.
// CodeViewModel кладёт сюда данные из ответа сервера (login).
object Session {
    var participant by mutableStateOf<ParticipantWithGroup?>(null)
}

private val ButtonBlue = Color(0xFF3AA0E0)

@Composable
fun ScParticipant(
    onBack: () -> Unit = {},
    onExcursions: () -> Unit = {}
) {
    BackHandler { onBack() }

    val p = Session.participant

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))

        if (p == null) {
            Text(
                "Данные участника не найдены",
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = secondFont
            )
            return@Column
        }

        // ФИО
        Text(
            text = p.fullName,
            fontSize = 18.sp,
            color = Color.Black,
            fontFamily = firstFont,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        BlueButton("Мой пропуск") {}
        Spacer(Modifier.height(16.dp))

        // QR хранит только код участника, остальное сервер возвращает по этому коду
        QrCode(text = p.passCode, modifier = Modifier.size(180.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            text = p.passCode,
            fontSize = 13.sp,
            color = Color.Black,
            fontFamily = secondFont
        )

        Spacer(Modifier.height(32.dp))

        BlueButton("Мои экскурсии", onExcursions)
        Spacer(Modifier.height(8.dp))

        Column(
            Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Школа: ${p.group.schoolName}",
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = secondFont
            )
            // Названия экскурсии в ответе сервера пока нет, показываем дату визита.
            // Когда бэкенд добавит название, поменяй эту строку.
            Text(
                text = "Экскурсия: ${p.group.visitDate.format(DATE_ONLY_FORMAT)}",
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = secondFont
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BlueButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.65f)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = firstFont
        )
    }
}