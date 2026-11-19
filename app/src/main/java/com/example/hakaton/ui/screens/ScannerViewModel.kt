package com.example.hakaton.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hakaton.data.ApiClient
import com.example.hakaton.data.ErrorResponse
import com.example.hakaton.data.LoginRequest
import com.example.hakaton.data.StaffCode
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

sealed interface ScanState {
    object Idle : ScanState
    object Loading : ScanState
    data class Success(val name: String) : ScanState
    data class Error(val message: String) : ScanState
}

class ScannerViewModel : ViewModel() {

    var state by mutableStateOf<ScanState>(ScanState.Idle)
        private set

    var manualCode by mutableStateOf("")

    private var busy = false

    // Вызывается и от сканера, и от кнопки ручного ввода
    fun submit(raw: String) {
        val code = raw.trim()
        if (code.isBlank() || busy) return
        busy = true
        state = ScanState.Loading

        viewModelScope.launch {
            state = try {

                val r = ApiClient.api.login(LoginRequest(code))
                val name = r.body()?.participant?.fullName

                if (!r.isSuccessful) {
                    if (r.code() == 404 || r.code() == 400) ScanState.Error("Код не найден")
                    else ScanState.Error(r.serverMessage())
                } else if (name == null) {
                    ScanState.Error("Код не найден")
                } else {
                    val staff = StaffCode.get()
                    if (staff.isBlank()) {
                        ScanState.Error("Войдите как сотрудник")
                    } else {
                        val c = ApiClient.api.checkIn(code, staff)
                        if (c.isSuccessful) ScanState.Success(name)
                        else ScanState.Error(c.serverMessage())
                    }
                }
            } catch (e: Exception) {
                ScanState.Error("Нет связи с сервером")
            }
        }
    }

    // Вернуться к сканеру
    fun reset() {
        busy = false
        manualCode = ""
        state = ScanState.Idle
    }
}

private fun <T> Response<T>.serverMessage(): String {
    val msg = try {
        Gson().fromJson(errorBody()?.string(), ErrorResponse::class.java)?.message
    } catch (e: Exception) {
        null
    }
    return if (msg.isNullOrBlank()) "Ошибка: ${code()}" else msg
}