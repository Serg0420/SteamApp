package com.example.steamapp.domain.repository

import com.example.steamapp.domain.model.UsersInfo

interface UserInfoRepository {
    suspend fun getUsersInfoLst():List<UsersInfo>
}