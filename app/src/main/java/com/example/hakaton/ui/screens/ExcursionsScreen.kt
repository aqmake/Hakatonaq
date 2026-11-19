package com.example.hakaton.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.data.DATE_ONLY_FORMAT
import com.example.hakaton.data.Excursion
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont

private val cardColors = listOf(Color(0xFFC9EBF9), Color(0xFFA8DB6A))

@Composable
fun ExcursionsScreen(
    vm: ExcursionsViewModel = viewModel(),
    onSelect: (Excursion) -> Unit,
    onTeach: () -> Unit,
    onCode: () -> Unit,

) {

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {

        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD9622B))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "Экскурсии и\nмастер-классы", color = Color.White, fontSize = 20.sp,
                    textAlign = TextAlign.Center, fontFamily = firstFont
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Выберите экскурсию", fontSize = 22.sp, color = Color.Black, fontFamily = firstFont)
        Spacer(Modifier.height(12.dp))


        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                vm.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                vm.error.isNotEmpty() -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(vm.error, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.load() }) { Text("Повторить") }
                }

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(vm.items, key = { _, it -> it.id }) { i, ex ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(cardColors[i % cardColors.size])
                                .clickable { onSelect(ex) }
                                .padding(12.dp)
                        ) {
                            Text(
                                ex.title.uppercase(), fontSize = 28.sp, color = Color.Black,
                                fontFamily = firstFont, modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            ex.description?.let {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    it,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontFamily = secondFont
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                ex.startsAt.format(DATE_ONLY_FORMAT), fontSize = 13.sp,
                                color = Color.Black, fontFamily = secondFont
                            )
                        }
                    }
                }
            }
        }


        Spacer(Modifier.height(12.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoleButton(
                Icons.Outlined.Edit, "Войти \nпо коду",
                Modifier.weight(1f), onClick = onCode
            )
            RoleButton(
                Icons.Outlined.EditNote, "Войти \nкак учитель",
                Modifier.weight(1f), onClick = onTeach
            )
        }
    }
}