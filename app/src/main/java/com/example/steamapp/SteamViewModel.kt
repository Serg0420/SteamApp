package com.example.steamapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class SteamViewModel(
    private val retroDataSource: RetrofitDataSource
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO+ SupervisorJob())
    private val _dataFlow = MutableStateFlow(emptyList<InputItem>())
    val dataFlow: Flow<List<InputItem>> = _dataFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val deferred=scope.async {
                retroDataSource.loadDataInRV()
            }
            scope.launch {
                val d=deferred.await()
                _dataFlow.value =d
            }
        }
    }
}