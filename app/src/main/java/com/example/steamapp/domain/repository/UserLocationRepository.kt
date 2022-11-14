package com.example.steamapp.domain.repository

import com.example.steamapp.domain.model.UserLocation

interface UserLocationRepository {
    suspend fun getAllUsersLocation(): List<UserLocation>
    suspend fun insertUsers(userLocation: UserLocation)
    suspend fun deleteUser(userLocation: UserLocation)
}