package com.example.steamapp.data.di

import com.example.steamapp.data.api.SteamApi
import com.example.steamapp.data.repository.UsersInfoRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val apiModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("http://api.steampowered.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create<SteamApi>() }

    single { UsersInfoRepositoryImpl(get()) }
}
