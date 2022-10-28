package com.example.steamapp.domain.repository

import com.example.steamapp.domain.model.UsersInfo

interface UserInfoRepository {
    suspend fun getUsersInfoLstById(userId: String): List<UsersInfo>
    suspend fun getUserInfoById(userId: String): UsersInfo
}