package com.example.steamapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitDataSource {

    private val retrofit = Retrofit.Builder()
        .baseUrl(STEAM_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val steamApi = retrofit.create<SteamApi>()

    suspend fun loadData(): List<InputItem.PlayerInfo> {
        val steamidStrLst = mutableListOf<String>()
        val playerInfoLst = mutableListOf<InputItem.PlayerInfo>()

        val lstOfFriends = getUserFriendsIds()

        steamidStrLst.addAll(
            lstOfFriends.friendList.friendsList.friends.map { it.steamid }
        )

        steamidStrLst.forEach {
            val playersResp = getUserInfoById(it)
            playerInfoLst.add(playersResp.user.response.players[0])
        }
        return playerInfoLst
    }

    private suspend fun getUserFriendsIds(): UsersFriendsResponseDTO {
        return steamApi.getUsersFriends(
            STEAM_API_KEY,
            MY_STEAM_ID,
            RELATIONSHIP
        )
    }

    private suspend fun getUserInfoById(friendId: String): UserInfoResponseDTO {
        return steamApi.getUser(STEAM_API_KEY, friendId)
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