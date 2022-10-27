package com.example.steamapp.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steamapp.domain.model.UserLocation
import com.example.steamapp.domain.repository.UserLocationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn

class DetailsViewModel(
    private val dataSource: UserLocationRepository,
    private val userLocation: UserLocation
) : ViewModel() {

    private val _dataFlow = flow {
        emit(runCatch())
    }.shareIn(
        viewModelScope,
        SharingStarted.Eagerly,
        replay = 1
    )

    val dataFlow = _dataFlow

    private suspend fun runCatch() {
        runCatching {
            dataSource.insertUsers(userLocation)
        }
    }
}