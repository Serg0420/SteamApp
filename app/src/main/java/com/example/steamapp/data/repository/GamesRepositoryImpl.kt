package com.example.steamapp.data.repository

import com.example.steamapp.BuildConfig
import com.example.steamapp.data.api.SteamApi
import com.example.steamapp.data.mapper.toDomain
import com.example.steamapp.domain.model.Game
import com.example.steamapp.domain.repository.GamesRepository

class GamesRepositoryImpl(private val steamApi: SteamApi) : GamesRepository {
    override suspend fun getUsersOwnedGamesLst(steamId: String): List<Game> = getUsersGames(steamId)

    private suspend fun getUsersGames(steamId: String): List<Game> {
        return steamApi.getUserGames(
            BuildConfig.STEAM_API_KEY,
            steamId,
            BuildConfig.INCLUDE_APP_INFO_FLAG
        ).toDomain()
    }
}