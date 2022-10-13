package com.example.steamapp.data

import com.example.steamapp.domain.UsersFriend
import com.example.steamapp.domain.UsersInfo

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