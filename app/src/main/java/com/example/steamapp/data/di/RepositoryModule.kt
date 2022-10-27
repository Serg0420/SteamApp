package com.example.steamapp.data.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.example.steamapp.domain.repository.UserInfoRepository
import com.example.steamapp.data.repository.UsersInfoRepositoryImpl
import com.example.steamapp.domain.repository.GamesRepository
import com.example.steamapp.data.repository.GamesRepositoryImpl
import com.example.steamapp.domain.repository.UserLocationRepository
import com.example.steamapp.data.repository.UserLocationRepositoryImpl
import org.koin.core.module.dsl.bind

val repositoryModule=module{
    singleOf(::UsersInfoRepositoryImpl){
        bind<UserInfoRepository>()
    }
    singleOf(::GamesRepositoryImpl){
        bind<GamesRepository>()
    }
    singleOf(::UserLocationRepositoryImpl){
        bind<UserLocationRepository>()
    }
}