package com.example.steamapp.data.roomdb

import androidx.room.*

@Dao
interface RoomUserDao {
    @Query("SELECT * FROM roomuserentity")
    suspend fun getAllUsers(): List<RoomUserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg roomUser: RoomUserEntity)

    @Delete
    suspend fun deleteUser(roomUser: RoomUserEntity)
}