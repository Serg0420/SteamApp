package com.example.steamapp.presentation.ui.map

import androidx.lifecycle.*
import com.example.steamapp.domain.model.UserLocation
import com.example.steamapp.domain.repository.UserLocationRepository
import com.example.steamapp.presentation.model.LCE
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class GoogleMapViewModel(
    private val dataSource: UserLocationRepository
) : ViewModel() {
    private val markerFlow = MutableSharedFlow<Pair<Double, Double>>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _dataFlow = flow {
        emit(runCatch())
    }.shareIn(
        viewModelScope,
        SharingStarted.Eagerly,
        replay = 1
    )

    val dataFlow = _dataFlow

    val selectedMarkerFlow = dataFlow
        .combine(markerFlow) { userLocationLst, (latitude, longitude) ->
            when (userLocationLst) {
                is LCE.Error -> {
                    LCE.Error(userLocationLst.throwable)
                }
                is LCE.Content -> {
                    LCE.Content(
                        userLocationLst.data.first {
                            it.latitude == latitude && it.longitude == longitude
                        }
                    )
                }
            }
        }

    fun onMarkerClicked(latitude: Double, longitude: Double) {
        markerFlow.tryEmit(latitude to longitude)
    }

    fun onDeleteBtnClicked(userLocation: UserLocation) {
        flow<Nothing> {
            runCatching {
                dataSource.deleteUser(userLocation)
            }
        }.shareIn(
            viewModelScope,
            SharingStarted.Eagerly,
            replay = 1
        )
    }

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