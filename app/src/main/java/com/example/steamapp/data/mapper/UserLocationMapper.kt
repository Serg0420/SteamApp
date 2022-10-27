package com.example.steamapp.data.mapper

import com.example.steamapp.data.roomdb.RoomUserEntity
import com.example.steamapp.domain.model.UserLocation

fun UserLocation.toRoomEntity(): RoomUserEntity {
    return RoomUserEntity(
        id=this.id,
        steamId = this.steamId,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun RoomUserEntity.toDomain(): UserLocation {
    return UserLocation(
        id = this.id,
        steamId = this.steamId,
        latitude = this.latitude,
        longitude = this.longitude
    )
}