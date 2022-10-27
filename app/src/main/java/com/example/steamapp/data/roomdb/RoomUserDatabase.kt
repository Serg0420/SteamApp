package com.example.steamapp.data.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomUserEntity::class], version = 1)
abstract class RoomUserDatabase : RoomDatabase() {
    abstract fun userDao(): RoomUserDao
}