package com.example.hakaton.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.ui.theme.firstFont

@Composable
fun SummaryScreen(onBack: () -> Unit, vm: SummaryViewModel = viewModel()) {
    BackHandler { onBack() }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.Black)
            }
            Box(
                Modifier.weight(1f).height(50.dp)
                    .background(Color(0xFFD9622B), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Сводка групп", color = Color.White, fontSize = 16.sp, fontFamily = firstFont)
            }
        }
        Spacer(Modifier.height(24.dp))

        when (val s = vm.state) {
            SummaryState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            is SummaryState.Error -> Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(s.message, fontFamily = firstFont)
                Spacer(Modifier.height(12.dp))
                Button(onClick = vm::load) { Text("Повторить") }
            }
            is SummaryState.Success ->
                if (s.groups.isEmpty()) Text("Групп пока нет", fontFamily = firstFont)
                else LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    itemsIndexed(s.groups) { i, g ->
                        Column {
                            Text("${i + 1}. Школа: ${g.schoolName}", fontFamily = firstFont, fontSize = 16.sp)
                            Text(
                                "Участников: ${g.checkedInParticipants} из ${g.totalParticipants}",
                                fontFamily = firstFont, fontSize = 14.sp
                            )
                        }
                    }
                }
        }
    }
}