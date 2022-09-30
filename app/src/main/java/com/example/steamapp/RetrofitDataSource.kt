package com.example.steamapp

import com.example.steamapp.databinding.FragmentFriendsBinding
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitDataSource {

    private var getFriendsIdsRequest: FriendList? = null
    private var getUserProfileRequest: PlayersResponse? = null
    private val handler= CoroutineExceptionHandler { _, exception ->
        friendsFragment.handleError(exception.toString())
    }
    private val friendsFragment=FriendsFragment()
    private val scope = CoroutineScope(Dispatchers.IO+handler+ SupervisorJob())

    suspend fun loadDataInRV(onRefreshFinished: () -> Unit = {}):List<InputItem.PlayerInfo> {
        val steamidStrLst = mutableListOf<String>()
        val playerInfoLst = mutableListOf<InputItem.PlayerInfo>()

        scope.launch {
            val retrofit = Retrofit.Builder()
                .baseUrl(STEAM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val steamApi = retrofit.create<SteamApi>()

            val resultFriendListDownload= runCatching {

                val lstOfFriends = getUserFriendsIds(steamApi)
                if (lstOfFriends != null) {
                    (lstOfFriends.friendsList.friends).forEach {
                        steamidStrLst.add(it.steamid)
                    }
                }
            }
            resultFriendListDownload
                .onSuccess {

                    val resultInfoDownload= runCatching {

                        steamidStrLst.forEach {
                            val playersResp = getUserInfoById(it,steamApi)
                            if (playersResp != null) {
                                playerInfoLst.add(playersResp.response.players[0])
                            }
                        }
                    }
                    resultInfoDownload
                        .onSuccess {
                            //onRefreshFinished()
                        }
                        .onFailure {
                            onRefreshFinished()
                            friendsFragment.handleError("Something went wrong! Can't download friend info")
                        }
                }
                .onFailure {
                    friendsFragment.handleError("Something went wrong! Can't download friends id list")
                    onRefreshFinished()
                }

        }

        return playerInfoLst
    }

    private suspend fun getUserFriendsIds(steamApi:SteamApi): FriendList? {
        getFriendsIdsRequest = steamApi.getUsersFriends(
            STEAM_API_KEY,
            MY_STEAM_ID,
            RELATIONSHIP
        )
        return getFriendsIdsRequest
    }

    private suspend fun getUserInfoById(friendId: String, steamApi:SteamApi): PlayersResponse? {
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