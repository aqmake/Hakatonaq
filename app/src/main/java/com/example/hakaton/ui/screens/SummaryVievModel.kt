package com.example.hakaton.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hakaton.data.ApiClient
import com.example.hakaton.data.GroupSummaryResponse
import com.example.hakaton.data.StaffCode
import kotlinx.coroutines.launch

sealed interface SummaryState {
    object Loading : SummaryState
    data class Success(val groups: List<GroupSummaryResponse>) : SummaryState
    data class Error(val message: String) : SummaryState
}

class SummaryViewModel : ViewModel() {
    var state by mutableStateOf<SummaryState>(SummaryState.Loading)
        private set

    init { load() }

    fun load() {
        state = SummaryState.Loading
        viewModelScope.launch {
            state = try {
                val r = ApiClient.api.getGroupsSummary(StaffCode.get())   // <-- вот здесь
                val body = r.body()
                if (r.isSuccessful && body != null) SummaryState.Success(body)
                else SummaryState.Error("Ошибка: ${r.code()}")
            } catch (e: Exception) {
                SummaryState.Error("Нет связи с сервером")
            }
        }
    }
}