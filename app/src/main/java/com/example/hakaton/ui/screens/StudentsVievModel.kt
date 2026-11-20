package com.example.hakaton.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hakaton.data.AddParticipantRequest
import com.example.hakaton.data.ApiClient
import kotlinx.coroutines.launch

class StudentItem(val localId: Long) {
    var name by mutableStateOf("")
    var saved by mutableStateOf(false)
    var expanded by mutableStateOf(false)
    var code by mutableStateOf("")
    var status by mutableStateOf("")
    var loading by mutableStateOf(false)
    var error by mutableStateOf("")
}

class StudentsViewModel : ViewModel() {
    val students = mutableStateListOf<StudentItem>()
    private var counter = 0L

    var listLoading by mutableStateOf(false)
        private set
    var listError by mutableStateOf("")
        private set
    private var loadedGroupId = -1L

    // Загрузить участников группы с сервера
    fun load(groupId: Long) {
        if (groupId != loadedGroupId) students.clear()
        loadedGroupId = groupId
        listLoading = true
        listError = ""
        viewModelScope.launch {
            try {
                val r = ApiClient.api.getParticipants(groupId)
                val body = r.body()
                if (r.isSuccessful && body != null) {
                    val unsaved = students.filter { !it.saved }
                    students.clear()
                    body.forEach { p ->
                        students.add(StudentItem(counter++).apply {
                            name = p.fullName
                            code = p.passCode
                            status = if (p.checkedIn) "checked_in" else "pending"
                            saved = true
                        })
                    }
                    students.addAll(unsaved)
                } else listError = "Ошибка: ${r.code()}"
            } catch (e: Throwable) {
                listError = "Нет связи с сервером"
            }
            listLoading = false
        }
    }

    fun addEmpty() {
        students.add(StudentItem(counter++))
    }

    fun save(groupId: Long, item: StudentItem) {
        if (item.name.isBlank()) {
            item.error = "Введите ФИО"
            return
        }
        viewModelScope.launch {
            item.loading = true
            item.error = ""
            try {
                val r = ApiClient.api.addParticipant(
                    groupId,
                    AddParticipantRequest(item.name.trim())
                )
                val body = r.body()
                if (r.isSuccessful && body != null) {
                    item.code = body.passCode
                    item.status = if (body.checkedIn) "Отмечен" else "Ожидается"
                    item.saved = true
                    item.expanded = true
                } else item.error = "Ошибка: ${r.code()}"
            } catch (e: Throwable) {
                item.error = "Нет связи с сервером"
            }
            item.loading = false
        }
    }
}