package com.example.steamapp.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.example.steamapp.presentation.ui.friendslst.FriendsViewModel
import com.example.steamapp.presentation.ui.profile.MyProfileViewModel

val viewModelModule= module {
    viewModelOf(::FriendsViewModel)
    viewModelOf(::MyProfileViewModel)
}