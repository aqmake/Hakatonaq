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
                    item.status = if (body.checkedIn) "checked_in" else "pending"
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