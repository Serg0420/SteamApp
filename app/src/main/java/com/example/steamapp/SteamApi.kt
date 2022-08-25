package com.example.steamapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApi {
    @GET("ISteamUser/GetFriendList/v0001/")
    fun getUsersFriends(
        @Query("key")key:String,
        @Query("steamid")steamid:Long,
        @Query("relationship")relationship:String
    ): Call<FriendList>

    @GET("ISteamUser/GetPlayerSummaries/v0002/")
    fun getUser(
        @Query("key")key:String,
        @Query("steamids")steamids:String
    ): Call<PlayersResponse>
}

//http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=EA8CD62FA7C21007003DEF64FF96E20C&steamids=76561198277362398
