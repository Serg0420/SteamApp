package com.example.steamapp.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steamapp.domain.model.UserLocation
import com.example.steamapp.domain.repository.UserLocationRepository
import kotlinx.coroutines.flow.*

class DetailsViewModel(
    private val dataSource: UserLocationRepository
) : ViewModel() {
    fun addLocation(userLocation: UserLocation) {
        flow<Nothing> {
            runCatching {
                dataSource.insertUsers(userLocation)
            }
        }.shareIn(
            viewModelScope,
            SharingStarted.Eagerly,
            replay = 1
        )
    }
}