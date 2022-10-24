package com.example.steamapp.data.di

import com.example.steamapp.R
import com.example.steamapp.data.api.SteamApi
import com.example.steamapp.data.repository.UsersInfoRepositoryImpl
import com.example.steamapp.presentation.MySteamApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

private val baseSteamApiUrl by lazy {
    MySteamApplication.applicationContext().getString(R.string.base_steam_api_url)
}

val apiModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(baseSteamApiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create<SteamApi>() }

    single { UsersInfoRepositoryImpl(get()) }
}
