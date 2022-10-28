package com.example.steamapp.data.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomUserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "steam")
    val steamId: String,
    @ColumnInfo(name = "personaname")
    val personaName: String,
    @ColumnInfo(name = "avatarfull")
    val avatarFull: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double
)