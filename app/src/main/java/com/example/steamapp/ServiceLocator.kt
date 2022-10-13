package com.example.steamapp

object ServiceLocator {
    private val retroDataSource by lazy{
        RetrofitDataSource()
    }

    fun provideDataSource(): RetrofitDataSource=retroDataSource
}