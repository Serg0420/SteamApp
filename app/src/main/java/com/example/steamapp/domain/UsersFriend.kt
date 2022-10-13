package com.example.steamapp.domain

data class UsersFriend(
    val steamId: String,
    val relationship: String,
    val friendSince: String
)