package com.example.steamapp.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steamapp.R
import com.example.steamapp.domain.model.UsersInfo
import com.example.steamapp.domain.repository.UserInfoRepository
import com.example.steamapp.presentation.MySteamApplication
import com.example.steamapp.presentation.model.LCE
import kotlinx.coroutines.flow.*

class MyProfileViewModel(
    private val retroDataSource: UserInfoRepository
) : ViewModel() {
    private val mySteamId by lazy {
        MySteamApplication.applicationContext().getString(R.string.my_steam_id)
    }

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
            retroDataSource.getUserInfoById(mySteamId)
        }
            .fold(
                onSuccess = { LCE.Content(it) },
                onFailure = { LCE.Error(it) }
            )
    }
}