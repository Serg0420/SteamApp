package com.example.steamapp.presentation.ui.details

import androidx.lifecycle.ViewModel
import com.example.steamapp.domain.model.UserLocation
import com.example.steamapp.domain.repository.UserLocationRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class DetailsViewModel(
    private val dataSource: UserLocationRepository
) : ViewModel() {
    private val locationFlow = MutableSharedFlow<UserLocation>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val dataFlow = locationFlow.onEach {
        dataSource.insertUsers(it)
    }

    fun addLocation(userLocation: UserLocation) {
        locationFlow.tryEmit(userLocation)
    }
}