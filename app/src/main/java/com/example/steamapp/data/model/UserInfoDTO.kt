package com.example.steamapp.data.model

import com.google.gson.annotations.SerializedName

data class PlayerInfo(
    @SerializedName("personaname")
    val personaName: String,
    @SerializedName("avatarfull")
    val avatarFull: String,
    val steamid: String,
    @SerializedName("personastate")
    val personaState: String
)

data class Players(
    val players: List<PlayerInfo>
)

data class PlayersResponse(
    val response: Players
)