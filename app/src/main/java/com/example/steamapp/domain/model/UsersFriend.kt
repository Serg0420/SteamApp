package com.example.steamapp.domain.model

data class UsersFriend(
    val steamId: String,
    val relationship: String,
    val friendSince: String
)