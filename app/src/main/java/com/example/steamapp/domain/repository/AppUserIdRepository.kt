package com.example.steamapp.domain.repository

interface AppUserIdRepository {
    fun getAppUserId(): String
    fun setAppUserId(appUserId: String)
}