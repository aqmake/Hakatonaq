package com.example.hakaton.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hakaton.data.AddExcursionRequest
import com.example.hakaton.data.ApiClient
import com.example.hakaton.data.DATE_ONLY_FORMAT
import com.example.hakaton.data.Excursion
import com.example.hakaton.data.StaffCode
import com.example.hakaton.ui.theme.firstFont
import com.example.hakaton.ui.theme.secondFont
import kotlinx.coroutines.launch
import java.time.LocalDate
class ManageExcursionsViewModel : ViewModel() {
    var items by mutableStateOf<List<Excursion>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf("")
        private set
    var actionError by mutableStateOf("")
        private set

    init { load() }

    fun load() {
        loading = true
        error = ""
        viewModelScope.launch {
            try {
                val r = ApiClient.api.getExcursions()
                val body = r.body()
                if (r.isSuccessful && body != null) items = body
                else error = "Ошибка: ${r.code()}"
            } catch (e: Exception) {
                error = "Нет связи с сервером"
            }
            loading = false
        }
    }

    fun create(title: String, description: String, date: LocalDate, onDone: () -> Unit) {
        actionError = ""
        viewModelScope.launch {
            try {
                val r = ApiClient.api.addExcursion(
                    StaffCode.get(),
                    AddExcursionRequest(title, description.ifBlank { null }, date)
                )
                if (r.isSuccessful) { onDone(); load() }
                else actionError = "Не удалось создать: ${r.code()}"
            } catch (e: Exception) {
                actionError = "Нет связи с сервером"
            }
        }
    }

    fun delete(id: Long) {
        actionError = ""
        viewModelScope.launch {
            try {
                val r = ApiClient.api.deleteExcursion(id, StaffCode.get())
                if (r.isSuccessful) load()
                else actionError = "Не удалось удалить: ${r.code()}"
            } catch (e: Exception) {
                actionError = "Нет связи с сервером"
            }
        }
    }
}

private val CardColors = listOf(Color(0xFFC9EBF9), Color(0xFFA8DB6A))

@Composable
fun ManageExcursionsScreen(
    onBack: () -> Unit,
    vm: ManageExcursionsViewModel = viewModel()
) {
    BackHandler { onBack() }
    var showCreate by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Excursion?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // Стрелка назад
        Box(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.Black)
            }
        }

        // Плашка сверху
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD9622B))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "Управление\nэкскурсиями", color = Color.White, fontSize = 20.sp,
                    textAlign = TextAlign.Center, fontFamily = firstFont
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        if (vm.actionError.isNotEmpty()) {
            Text(vm.actionError, color = Color.Red, fontFamily = firstFont)
            Spacer(Modifier.height(8.dp))
        }

        Box(Modifier.weight(1f).fillMaxWidth()) {
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
                vm.items.isEmpty() -> Text(
                    "Экскурсий пока нет", fontFamily = firstFont,modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(vm.items, key = { _, it -> it.id }) { i, ex ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardColors[i % CardColors.size])
                        ) {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(
                                    ex.title.uppercase(), fontSize = 24.sp, color = Color.Black,
                                    fontFamily = firstFont, modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                ex.description?.let {
                                    Spacer(Modifier.height(6.dp))
                                    Text(it, fontSize = 14.sp, color = Color.Black, fontFamily = secondFont)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    ex.startsAt.format(DATE_ONLY_FORMAT), fontSize = 13.sp,
                                    color = Color.Black, fontFamily = secondFont
                                )
                            }
                            IconButton(
                                onClick = { toDelete = ex },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Outlined.Delete, "Удалить", tint = Color(0xFFC0485A))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { showCreate = true },
            modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 0.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E9A2B))
        ) {
            Text("Создать экскурсию", color = Color.White, fontSize = 16.sp, fontFamily = firstFont)
        }
        Spacer(Modifier.height(16.dp))
    }

    // Подтверждение удаления
    toDelete?.let { ex ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Удалить экскурсию?") },
            text = { Text(ex.title) },
            confirmButton = {
                TextButton(onClick = { vm.delete(ex.id); toDelete = null }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) { Text("Отмена") }
            }
        )
    }

    // Создание
    if (showCreate) {
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var date by remember { mutableStateOf<LocalDate?>(null) }
        var formError by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("Новая экскурсия") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(title, { title = it }, label = { Text("Название") }, singleLine = true)
                    OutlinedTextField(desc, { desc = it }, label = { Text("Описание") })
                    DatePickerField(date, { date = it }, "Дата")
                    if (formError.isNotEmpty()) Text(formError, color = Color.Red, fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val d = date
                    when {
                        title.isBlank() -> formError = "Введите название"
                        d == null -> formError = "Выберите дату"
                        else -> vm.create(title.trim(), desc.trim(), d) { showCreate = false }
                    }
                }) { Text("Создать") }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Отмена") }
            }
        )
    }
}