package com.example.steamapp.data.di

import com.example.steamapp.data.api.SteamApi
import com.example.steamapp.data.repository.UsersInfoRepositoryImpl
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

private const val BASE_STEAM_API_URL = "http://api.steampowered.com/"

val apiModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_STEAM_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create<SteamApi>() }

    single { UsersInfoRepositoryImpl(get()) }
}
