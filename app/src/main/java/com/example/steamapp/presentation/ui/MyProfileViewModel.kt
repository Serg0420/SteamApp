package com.example.steamapp.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steamapp.data.repository.UsersInfoRepositoryImpl
import com.example.steamapp.domain.model.UsersInfo
import com.example.steamapp.presentation.model.LCE
import kotlinx.coroutines.flow.*

class MyProfileViewModel(
    private val retroDataSource: UsersInfoRepositoryImpl
) : ViewModel() {

    private val _dataFlow = flow {
        emit(runCatch())
    }.shareIn(
        viewModelScope,
        SharingStarted.Eagerly,
        replay = 1
    )

    val dataFlow = _dataFlow

    private suspend fun runCatch(): LCE<UsersInfo> {
        return runCatching {
            retroDataSource.getUserInfoById("76561198277362398")
        }
            .fold(
                onSuccess = { LCE.Content(it) },
                onFailure = { LCE.Error(it) }
            )
    }
}