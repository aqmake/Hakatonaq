package com.example.hakaton.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hakaton.data.ApiClient
import com.example.hakaton.data.Excursion
import kotlinx.coroutines.launch

class ExcursionsViewModel : ViewModel() {
    var items by mutableStateOf<List<Excursion>>(emptyList())
    var loading by mutableStateOf(false)
    var error by mutableStateOf("")

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            loading = true
            error = ""
            try {
                val r = ApiClient.api.getExcursions()
                if (r.isSuccessful) items = r.body().orEmpty()
                else error = "Ошибка: ${r.code()}"
            } catch (e: Throwable) {
                var root: Throwable = e
                while (root.cause != null) root = root.cause!!
                error = "Ошибка: ${root::class.simpleName}: ${root.message}"
            }
        }
        loading = false
    }
}
