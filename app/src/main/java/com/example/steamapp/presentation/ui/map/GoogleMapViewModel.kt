package com.example.steamapp.presentation.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steamapp.domain.model.UserLocation
import com.example.steamapp.domain.repository.UserLocationRepository
import com.example.steamapp.presentation.model.LCE
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn

class GoogleMapViewModel (
    private val dataSource: UserLocationRepository
) : ViewModel() {

    private val _dataFlow = flow {
        emit(runCatch())
    }.shareIn(
        viewModelScope,
        SharingStarted.Eagerly,
        replay = 1
    )

    val dataFlow = _dataFlow

    private suspend fun runCatch(): LCE<List<UserLocation>> {
        return runCatching {
            dataSource.getAllUsersLocation()
        }
            .fold(
                onSuccess = { LCE.Content(it) },
                onFailure = { LCE.Error(it) }
            )
    }
}