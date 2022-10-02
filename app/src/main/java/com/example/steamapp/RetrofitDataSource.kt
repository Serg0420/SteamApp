package com.example.steamapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitDataSource {
    private var getFriendsIdsRequest: FriendList? = null
    private var getUserProfileRequest: PlayersResponse? = null

    suspend fun loadData(): List<InputItem.PlayerInfo> {
        val steamidStrLst = mutableListOf<String>()
        val playerInfoLst = mutableListOf<InputItem.PlayerInfo>()

        val retrofit = Retrofit.Builder()
            .baseUrl(STEAM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val steamApi = retrofit.create<SteamApi>()

        val lstOfFriends = getUserFriendsIds(steamApi)
        if (lstOfFriends != null) {
            (lstOfFriends.friendsList.friends).forEach {
                steamidStrLst.add(it.steamid)
            }
        }

        steamidStrLst.forEach {
            val playersResp = getUserInfoById(it, steamApi)
            if (playersResp != null) {
                playerInfoLst.add(playersResp.response.players[0])
            }
        }
        return playerInfoLst
    }

    private suspend fun getUserFriendsIds(steamApi: SteamApi): FriendList? {
        getFriendsIdsRequest = steamApi.getUsersFriends(
            STEAM_API_KEY,
            MY_STEAM_ID,
            RELATIONSHIP
        )
        return getFriendsIdsRequest
    }

    private suspend fun getUserInfoById(friendId: String, steamApi: SteamApi): PlayersResponse? {
        getUserProfileRequest = steamApi.getUser(STEAM_API_KEY, friendId)
        return getUserProfileRequest
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