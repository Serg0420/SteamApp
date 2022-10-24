package com.example.steamapp.data.mapper

import com.example.steamapp.data.model.FriendList
import com.example.steamapp.data.model.PlayersResponse
import com.example.steamapp.domain.model.UsersFriend
import com.example.steamapp.domain.model.UsersInfo

fun PlayersResponse.toDomain(): UsersInfo {
    return UsersInfo(
        nickName = this.response.players[0].personaName,
        avatar = this.response.players[0].avatarFull,
        steamId = this.response.players[0].steamid,
        state = this.response.players[0].personaState
    )
}

fun FriendList.toDomain(): List<UsersFriend> {
    return this.friendsList.friends.map {
        UsersFriend(
            steamId = it.steamid,
            relationship = it.relationship,
            friendSince = it.friendSince
        )
    }
}