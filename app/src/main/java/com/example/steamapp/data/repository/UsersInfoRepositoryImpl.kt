package com.example.steamapp.data.repository

import com.example.steamapp.R
import com.example.steamapp.data.api.SteamApi
import com.example.steamapp.data.mapper.toDomain
import com.example.steamapp.domain.model.UsersFriend
import com.example.steamapp.domain.model.UsersInfo
import com.example.steamapp.domain.repository.UserInfoRepository
import com.example.steamapp.presentation.MySteamApplication

class UsersInfoRepositoryImpl(private val steamApi: SteamApi) : UserInfoRepository {
    override suspend fun getUsersInfoLst(): List<UsersInfo> {
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

    override suspend fun getUserInfoById(userId: String): UsersInfo {
        return steamApi.getUser(STEAM_API_KEY, userId).toDomain()
    }

    companion object {
        private val context by lazy { MySteamApplication.applicationContext() }

        /*апи ключ, его стим даёт бесплатно, но только зареганым
        юзерам с активированным аккаунтом
        */
        private val STEAM_API_KEY = context.getString(R.string.steam_api_key)

        //это мой стим ID, соотвественно и друзей выводит именно моих
        private val MY_STEAM_ID = context.getString(R.string.my_steam_id).toLong()
        private val RELATIONSHIP = context.getString(R.string.relationship_is_friend)
    }
}