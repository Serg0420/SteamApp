package com.example.steamapp

fun PlayersResponse.toDomain(): UsersInfo {
    return UsersInfo(
        nickName =this.response.players[0].personaName,
        avatar =this.response.players[0].avatarFull,
        steamId =this.response.players[0].steamid,
        state =this.response.players[0].personaState
    )
}