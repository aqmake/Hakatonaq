package com.example.hakaton.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hakaton.data.ApiClient
import com.example.hakaton.data.GroupSummaryResponse
import com.example.hakaton.data.ParticipantResponse
import com.example.hakaton.data.StaffCode
import kotlinx.coroutines.launch

class GroupUi(val summary: GroupSummaryResponse) {
    var expanded by mutableStateOf(false)
    var loading by mutableStateOf(false)
    var error by mutableStateOf("")
    var participants by mutableStateOf<List<ParticipantResponse>>(emptyList())
}

class SummaryViewModel : ViewModel() {
    val groups = mutableStateListOf<GroupUi>()

    var loading by mutableStateOf(true)
        private set
    var error by mutableStateOf("")
        private set

    fun load() {
        loading = true
        error = ""
        viewModelScope.launch {
            try {
                val r = ApiClient.api.getGroupsSummary(StaffCode.get())
                val body = r.body()
                if (r.isSuccessful && body != null) {
                    groups.clear()
                    groups.addAll(body.map { GroupUi(it) })
                } else error = "Ошибка: ${r.code()}"
            } catch (e: Exception) {
                error = "Нет связи с сервером"
            }
            loading = false
        }
    }

    // Раскрыть или свернуть группу; при раскрытии грузим учеников заново
    fun toggle(g: GroupUi) {
        g.expanded = !g.expanded
        if (g.expanded) loadParticipants(g)
    }

    private fun loadParticipants(g: GroupUi) {
        g.loading = true
        g.error = ""
        viewModelScope.launch {
            try {
                val r = ApiClient.api.getParticipants(g.summary.id)
                val body = r.body()
                if (r.isSuccessful && body != null) g.participants = body
                else g.error = "Ошибка: ${r.code()}"
            } catch (e: Exception) {
                g.error = "Нет связи с сервером"
            }
            g.loading = false
        }
    }
}