package com.example.steamapp


import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MySteamApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MySteamApplication)
            modules(viewModelModule)
        }
    }
}