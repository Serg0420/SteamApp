package com.example.steamapp.data.model

import com.google.gson.annotations.SerializedName

data class UserFriend(
    val steamid: String,
    val relationship: String,
    @SerializedName("friend_since")
    val friendSince: String
)

data class Friends(
    val friends: List<UserFriend>
)

data class FriendList(
    @SerializedName("friendslist")
    val friendsList: Friends
)