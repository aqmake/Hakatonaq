package com.example.hakaton.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hakaton.data.ApiClient
import com.example.hakaton.data.ErrorResponse
import com.example.hakaton.data.LoginRequest
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CodeViewModel : ViewModel() {
    var code by mutableStateOf("")
    var error by mutableStateOf("")
    var loading by mutableStateOf(false)

    fun check(onParticipant: () -> Unit, onAdmin: () -> Unit) {
        if (code.isBlank()) {
            error = "Введите код"
            return
        }
        viewModelScope.launch {
            loading = true
            error = ""
            try {
                val r = ApiClient.api.login(LoginRequest(code.trim()))
                if (r.isSuccessful) {
                    when (r.body()?.role) {
                        "STAFF" -> onAdmin()
                        "PARTICIPANT" -> onParticipant()
                        else -> error = "Неизвестная роль"
                    }
                } else {
                    val err = try {
                        Gson().fromJson(r.errorBody()?.string(), ErrorResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }
                    error = err?.message ?: "Ошибка: ${r.code()}"
                }
            } catch (e: Exception) {
                error = "Нет связи с сервером"
            }
            loading = false
        }
    }
}