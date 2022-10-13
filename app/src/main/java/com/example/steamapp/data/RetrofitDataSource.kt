package com.example.steamapp.data

import com.example.steamapp.domain.UsersFriend
import com.example.steamapp.domain.UsersInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitDataSource {

    private val retrofit = Retrofit.Builder()
        .baseUrl(STEAM_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val steamApi = retrofit.create<SteamApi>()

    suspend fun loadData(): List<UsersInfo> {
        val steamidStrLst = mutableListOf<String>()
        val playerInfoLst = mutableListOf<UsersInfo>()

        val lstOfFriends = getUserFriendsIds()

        steamidStrLst.addAll(
            lstOfFriends.map { it.steamId }
        )

        steamidStrLst.forEach {
            val playersResp = getUserInfoById(it)
            playerInfoLst.add(playersResp)
        }
        return playerInfoLst
    }

    private suspend fun getUserFriendsIds(): List<UsersFriend> {
        return steamApi.getUsersFriends(
            STEAM_API_KEY,
            MY_STEAM_ID,
            RELATIONSHIP
        ).toDomain()
    }

    private suspend fun getUserInfoById(friendId: String): UsersInfo {
        return steamApi.getUser(STEAM_API_KEY, friendId).toDomain()
    }

    companion object {
        /*ссылка на кореь апи и ключ, его стим даёт бесплатно, но только зареганым
        юзерам с активированным аккаунтом
        */
        private const val STEAM_BASE_URL = " http://api.steampowered.com/"
        private const val STEAM_API_KEY = "EA8CD62FA7C21007003DEF64FF96E20C"

        //это мой стим ID, соотвественно и друзей выводит именно моих
        private const val MY_STEAM_ID = 76561198277362398
        private const val RELATIONSHIP = "friend"
    }
}