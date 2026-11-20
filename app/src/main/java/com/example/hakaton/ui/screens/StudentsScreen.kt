package com.example.hakaton.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.ui.QrCode
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont

private val Orange = Color(0xFFE98A5B)
private val OrangeDark = Color(0xFFE8743B)

@Composable
fun StudentsScreen(
    groupId: Long,
    vm: StudentsViewModel = viewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(groupId) { vm.load(groupId) }
    BackHandler { onBack() }

    Column(Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(horizontal = 24.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.Black)
            }
            Button(
                onClick = { vm.addEmpty() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeDark),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, null, Modifier.size(18.dp), tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text(
                    "Добавить ученика",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = firstFont
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(vm.students, key = { it.localId }) { item ->
                StudentCard(item, onSave = { vm.save(groupId, item) })
            }
        }
    }
}

@Composable
private fun StudentCard(item: StudentItem, onSave: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text("Введите ФИО ученика", fontSize = 13.sp, color = Color.Black, fontFamily = firstFont)
        Spacer(Modifier.height(4.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Orange)
                .padding(start = 16.dp, end = 8.dp)
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.saved) {
                Text(
                    item.name, color = Color.White, fontSize = 16.sp,
                    fontFamily = firstFont, modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { item.expanded = !item.expanded }) {
                    Icon(
                        if (item.expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        "Раскрыть", tint = Color.White
                    )
                }
            } else {
                TextField(
                    value = item.name,
                    onValueChange = { item.name = it },
                    placeholder = {
                        Text(
                            "Фамилия Имя Отчество",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            fontFamily = secondFont
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (item.loading) {
                    CircularProgressIndicator(
                        Modifier
                            .size(24.dp)
                            .padding(end = 4.dp),
                        color = Color.White, strokeWidth = 2.dp
                    )
                } else {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Add, "Сохранить", tint = Color.White)
                    }
                }
            }
        }

        if (item.error.isNotEmpty()) {
            Text(
                item.error, color = Color.Red, fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (item.saved && item.expanded) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                QrCode(text = item.code, modifier = Modifier.size(140.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Статус: ${item.status}", fontSize = 14.sp,
                            color = Color.Black, fontFamily = secondFont
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Outlined.Timer, null, Modifier.size(18.dp), tint = Color.Black)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Код: ${item.code}", fontSize = 14.sp,
                        color = Color.Black, fontFamily = secondFont
                    )
                }
            }
        }
    }
}