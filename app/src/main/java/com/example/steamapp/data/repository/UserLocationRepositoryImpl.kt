package com.example.steamapp.data.repository

import com.example.steamapp.data.mapper.toDomain
import com.example.steamapp.data.mapper.toRoomEntity
import com.example.steamapp.data.roomdb.RoomUserDao
import com.example.steamapp.domain.model.UserLocation
import com.example.steamapp.domain.repository.UserLocationRepository

class UserLocationRepositoryImpl(private val userDao: RoomUserDao) : UserLocationRepository {

    override suspend fun getAllUsersLocation(): List<UserLocation> {
        return userDao.getAllUsers().map { it.toDomain() }
    }

    override suspend fun insertUsers(userLocation: UserLocation) {
        userDao.insertUser(userLocation.toRoomEntity())
    }

    override suspend fun deleteUser(userLocation: UserLocation) {
        userDao.deleteUser(userLocation.toRoomEntity())
    }
}