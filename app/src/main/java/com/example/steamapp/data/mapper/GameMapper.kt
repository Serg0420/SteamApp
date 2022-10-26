package com.example.steamapp.data.mapper

import com.example.steamapp.BuildConfig
import com.example.steamapp.data.model.GamesResponse
import com.example.steamapp.domain.model.Game

fun GamesResponse.toDomain(): List<Game> {
    return this.response.games.map {
        Game(
            appid = it.appid,
            name = it.name,
            playtimeForever = it.playtimeForever,
            imgIconUrl = BuildConfig.STEAM_API_IMG_LOGO_URL+it.appid+"/"+it.imgIconUrl+".jpg",
            timeLastPlayed = it.rtimeLastPlayed
        )
    }
}