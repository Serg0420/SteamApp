package com.example.steamapp

import com.google.gson.annotations.SerializedName

sealed class InputItem {

    data class PlayerInfo(
        @SerializedName("personaname")
        val personaName: String,
        @SerializedName("avatarfull")
        val avatarFull: String,
        val steamid: String,
        @SerializedName("personastate")
        val personaState: String
    ) : InputItem()

    object ErrorElement : InputItem()
}

data class Players(
    val players: List<InputItem.PlayerInfo>
)

data class PlayersResponse(
    val response: Players
)