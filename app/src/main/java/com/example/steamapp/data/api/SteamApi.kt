package com.example.steamapp.data.api

import com.example.steamapp.data.model.FriendList
import com.example.steamapp.data.model.GamesResponse
import com.example.steamapp.data.model.PlayersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApi {

    @GET("ISteamUser/GetFriendList/v0001/")
    suspend fun getUsersFriends(
        @Query("key") key: String,
        @Query("steamid") steamid: Long,
        @Query("relationship") relationship: String
    ): FriendList

    @GET("ISteamUser/GetPlayerSummaries/v0002/")
    suspend fun getUser(
        @Query("key") key: String,
        @Query("steamids") steamids: String
    ): PlayersResponse

    @GET("IPlayerService/GetOwnedGames/v0001/")
    suspend fun getUserGames(
        @Query("key") key: String,
        @Query("steamid") steamid: String,
        @Query("include_appinfo") includeAppInfo: String
    ): GamesResponse
}