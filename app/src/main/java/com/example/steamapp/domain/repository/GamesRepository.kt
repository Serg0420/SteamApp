package com.example.steamapp.domain.repository

import com.example.steamapp.domain.model.Game

interface GamesRepository {
    suspend fun getUsersOwnedGamesLst():List<Game>
}