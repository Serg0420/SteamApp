package com.example.steamapp

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.example.steamapp.presentation.ui.FriendsViewModel

val viewModelModule= module {
    viewModelOf(::FriendsViewModel)
}