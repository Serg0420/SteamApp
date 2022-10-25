package com.example.steamapp.domain.model

import com.google.gson.annotations.SerializedName

data class Game(
    val appid: String,
    val name: String,
    val playtimeForever: String,
    val imgIconUrl: String,
    val timeLastPlayed: String
)