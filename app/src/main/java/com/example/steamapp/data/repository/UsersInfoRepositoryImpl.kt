package com.example.steamapp.data.repository

import com.example.steamapp.BuildConfig
import com.example.steamapp.data.api.SteamApi
import com.example.steamapp.data.mapper.toDomain
import com.example.steamapp.domain.model.UsersFriend
import com.example.steamapp.domain.model.UsersInfo
import com.example.steamapp.domain.repository.UserInfoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class UsersInfoRepositoryImpl(private val steamApi: SteamApi) : UserInfoRepository {

    override suspend fun getUsersInfoLstById(userId: String): List<UsersInfo> = coroutineScope {
        val steamidStrLst = getUserFriendsIds(userId).map { it.steamId }
        steamidStrLst
            .map { id ->
                async { getUserInfoById(id) }
            }
            .awaitAll()
    }

    private suspend fun getUserFriendsIds(userId: String): List<UsersFriend> {
        return steamApi.getUsersFriends(
            BuildConfig.STEAM_API_KEY,
            userId,
            BuildConfig.RELATIONSHIP
        ).toDomain()
    }

    override suspend fun getUserInfoById(userId: String): UsersInfo {
        return steamApi.getUser(BuildConfig.STEAM_API_KEY, userId).toDomain()
    }
}