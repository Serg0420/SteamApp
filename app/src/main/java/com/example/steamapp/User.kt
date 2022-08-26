package com.example.steamapp

import com.google.gson.annotations.SerializedName

sealed class InputItem {

    data class PlayerInfo(
        @SerializedName("personaname")
        val personaName: String,
        @SerializedName("avatarfull")
        val avatarFull: String
    ) : InputItem()

    object LoadingElement : InputItem()
}

data class Players(
    val players: List<InputItem.PlayerInfo>
)

data class PlayersResponse(
    val response: Players
)

//------------------------------------------

    data class UserFriend(
        val steamid: String,
        val relationship:String,
        @SerializedName("friend_since")
        val friendSince:String
    )





data class Friends(
    val friends: List<UserFriend>
)

data class FriendList(
    @SerializedName("friendslist")
    val friendsList: Friends
)