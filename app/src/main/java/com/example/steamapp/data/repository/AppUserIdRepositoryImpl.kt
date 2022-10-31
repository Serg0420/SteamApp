package com.example.steamapp.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.steamapp.BuildConfig
import com.example.steamapp.domain.repository.AppUserIdRepository

class AppUserIdRepositoryImpl(private val context: Context) : AppUserIdRepository {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun getAppUserId(): String {
        return sharedPreferences
            .getString(KEY_STEAM_ID, BuildConfig.MY_STEAM_ID) ?: BuildConfig.MY_STEAM_ID
    }

    override fun setAppUserId(appUserId: String) {
        sharedPreferences.edit {
            putString(KEY_STEAM_ID, appUserId)
        }
    }

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_STEAM_ID = "STEAM_ID"
    }
}