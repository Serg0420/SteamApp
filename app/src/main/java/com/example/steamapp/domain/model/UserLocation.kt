package com.example.steamapp.domain.model

data class UserLocation(
    val id: Long = 0,
    val steamId: String,
    val nickName: String,
    val avatarUrl: String,
    val latitude: Double,
    val longitude: Double
)