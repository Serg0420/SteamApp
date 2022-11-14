package com.example.steamapp.data.di

import androidx.room.Room
import com.example.steamapp.data.roomdb.RoomUserDatabase
import org.koin.dsl.module

val roomModule = module {
    single {
        Room
            .databaseBuilder(
                get(),
                RoomUserDatabase::class.java,
                "database_of_users_locations"
            )
            .build()
    }

    single { get<RoomUserDatabase>().userDao() }
}