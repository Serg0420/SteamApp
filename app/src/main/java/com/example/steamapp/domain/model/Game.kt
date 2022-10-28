package com.example.steamapp.domain.model

data class Game(
    val appid: String,
    val name: String,
    val playtimeForever: String,
    val imgIconUrl: String,
    val timeLastPlayed: String
)