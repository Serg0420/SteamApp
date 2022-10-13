package com.example.steamapp.domain

import com.example.steamapp.data.RetrofitDataSource

object ServiceLocator {
    private val retroDataSource by lazy{
        RetrofitDataSource()
    }

    fun provideDataSource(): RetrofitDataSource = retroDataSource
}