package com.example.steamapp.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steamapp.domain.model.UsersInfo
import com.example.steamapp.domain.repository.UserInfoRepository
import com.example.steamapp.presentation.model.LCE
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class FriendsViewModel(
    private val retroDataSource: UserInfoRepository
) : ViewModel() {

    private val refreshedFlow = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _dataFlow = flow {
        emit(runCatch())
    }.shareIn(
        viewModelScope,
        SharingStarted.Eagerly,
        replay = 1
    )

    val dataFlow = combine(
        _dataFlow,
        refreshedFlow
            .onStart { emit((Unit)) }
            .map { runCatch() }
    ) { _, refreshedfl -> refreshedfl }

    fun onRefreshed() {
        refreshedFlow.tryEmit(Unit)
    }

    private suspend fun runCatch(): LCE<List<UsersInfo>> {
        return runCatching {
            retroDataSource.getUsersInfoLst()
        }
            .fold(
                onSuccess = { LCE.Content(it) },
                onFailure = { LCE.Error(it) }
            )
    }
}