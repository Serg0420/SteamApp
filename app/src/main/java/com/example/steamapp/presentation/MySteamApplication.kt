package com.example.steamapp.presentation


import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.example.steamapp.data.di.apiModule
import com.example.steamapp.data.di.repositoryModule
import com.example.steamapp.presentation.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MySteamApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MySteamApplication)
            modules(viewModelModule, apiModule, repositoryModule)
        }
    }
}