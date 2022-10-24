package com.example.steamapp.data.di

import com.example.steamapp.BuildConfig
import com.example.steamapp.data.api.SteamApi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val apiModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.STEAM_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create<SteamApi>() }
}
