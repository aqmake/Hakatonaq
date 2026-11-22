package com.example.hakaton.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont

private val CardOrange = Color(0xFFE98A5B)
private val TextGray = Color(0xFF4E4E4E)
private val Green = Color(0xFF4E9A2B)
private val Red = Color(0xFFC0485A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(onBack: () -> Unit, vm: SummaryViewModel = viewModel()) {
    BackHandler { onBack() }
    LaunchedEffect(Unit) { vm.load() }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // Стрелка назад
        Box(Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.Black)
            }
        }

        // Плашка
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD9622B))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("Сводка групп", color = Color.White, fontSize = 20.sp, fontFamily = firstFont)
            }
        }
        Spacer(Modifier.height(16.dp))

        // Список с обновлением свайпом вниз
        PullToRefreshBox(
            isRefreshing = vm.loading,
            onRefresh = { vm.load() },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                when {
                    vm.error.isNotEmpty() -> item {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(vm.error, color = Color.Red, fontFamily = firstFont)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { vm.load() }) { Text("Повторить") }
                        }
                    }

                    vm.groups.isEmpty() && !vm.loading -> item {
                        Text("Групп пока нет", fontFamily = firstFont)
                    }

                    else -> itemsIndexed(vm.groups, key = { _, g -> g.summary.id }) { i, g ->
                        GroupCard(i + 1, g, onClick = { vm.toggle(g) })
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupCard(number: Int, g: GroupUi, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardOrange)
    ) {
        // Шапка: название, счётчик, стрелка
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "$number. ${g.summary.schoolName}",
                    fontSize = 18.sp, color = Color.Black, fontFamily = firstFont
                )
                Text(
                    "Участников: ${g.summary.checkedInParticipants} из ${g.summary.totalParticipants}",
                    fontSize = 13.sp, color = TextGray, fontFamily = secondFont
                )
            }
            Icon(
                imageVector = if (g.expanded) Icons.Filled.KeyboardArrowUp
                else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (g.expanded) "Свернуть" else "Развернуть",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

        // Ученики
        if (g.expanded) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when {
                    g.loading -> Box(Modifier.fillMaxWidth(), Alignment.Center) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                    }

                    g.error.isNotEmpty() -> Text(g.error, color = Red, fontFamily = secondFont)
                    g.participants.isEmpty() ->
                        Text("Учеников пока нет", color = TextGray, fontFamily = secondFont)

                    else -> g.participants.forEach { p ->
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    p.fullName,
                                    fontSize = 15.sp, color = Color.Black, fontFamily = firstFont
                                )
                                Text(
                                    p.passCode,
                                    fontSize = 13.sp, color = TextGray, fontFamily = secondFont
                                )
                            }
                            Text(
                                if (p.checkedIn) "Пришёл" else "Не пришёл",
                                fontSize = 13.sp,
                                color = if (p.checkedIn) Green else Red,
                                fontFamily = firstFont
                            )
                        }
                    }
                }
            }
        }
    }
}