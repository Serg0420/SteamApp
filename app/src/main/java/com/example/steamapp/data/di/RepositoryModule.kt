package com.example.steamapp.data.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.example.steamapp.data.repository.UsersInfoRepositoryImpl
import com.example.steamapp.domain.repository.UserInfoRepository
import org.koin.core.module.dsl.bind

val repositoryModule=module{
    singleOf(::UsersInfoRepositoryImpl){
        bind<UserInfoRepository>()
    }
}