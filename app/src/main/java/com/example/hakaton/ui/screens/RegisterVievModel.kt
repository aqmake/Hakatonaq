package com.example.hakaton.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hakaton.data.AddGroupRequest
import com.example.hakaton.data.ApiClient
import com.example.hakaton.data.Excursion
import kotlinx.coroutines.launch
import java.time.LocalDate

class RegisterViewModel : ViewModel() {
    var school by mutableStateOf("")
    var phone by mutableStateOf("")
    var visitDate by mutableStateOf<LocalDate?>(null)
    var status by mutableStateOf("")

    fun send(excursion: Excursion, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val r = ApiClient.api.addGroup(
                    AddGroupRequest(
                        schoolName = school.trim(),
                        visitDate = excursion.startsAt,
                        contactPhone = phone.trim(),
                        excursionId = excursion.id
                    )
                )
                val id = r.body()?.id
                if (r.isSuccessful && id != null) {
                    status = "Группа добавлена"
                    onSuccess(id)
                } else status = "Ошибка: ${r.code()}"
            } catch (e: Throwable) {
                var root: Throwable = e
                while (root.cause != null) root = root.cause!!
                status = "Ошибка: ${root::class.simpleName}: ${root.message}"
            }
        }
    }
}