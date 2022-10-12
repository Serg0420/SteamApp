package com.example.steamapp

import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApi {

    @GET("ISteamUser/GetFriendList/v0001/")
    suspend fun getUsersFriends(
        @Query("key") key: String,
        @Query("steamid") steamid: Long,
        @Query("relationship") relationship: String
    ): UsersFriendsResponseDTO

    @GET("ISteamUser/GetPlayerSummaries/v0002/")
    suspend fun getUser(
        @Query("key") key: String,
        @Query("steamids") steamids: String
    ): UserInfoResponseDTO
}