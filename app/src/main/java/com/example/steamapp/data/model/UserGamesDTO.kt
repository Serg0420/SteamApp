package com.example.steamapp.data.model

import com.google.gson.annotations.SerializedName

data class GameInfo(
    val appid: String,
    val name: String,
    @SerializedName("playtime_forever")
    val playtimeForever: String,
    @SerializedName("img_icon_url")
    val imgIconUrl: String,
    @SerializedName("rtime_last_played")
    val rtimeLastPlayed: String
)

data class AllGames(
    @SerializedName("game_count")
    val gameCount: String,
    val games: List<GameInfo>
)

data class GamesResponse(
    val response: AllGames
)