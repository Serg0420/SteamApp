package com.example.steamapp.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.example.steamapp.presentation.ui.friendslst.FriendsViewModel
import com.example.steamapp.presentation.ui.profile.MyProfileViewModel
import com.example.steamapp.presentation.ui.ownedgames.GamesViewModel
import com.example.steamapp.presentation.ui.map.GoogleMapViewModel
import com.example.steamapp.presentation.ui.details.DetailsViewModel

val viewModelModule = module {
    viewModelOf(::FriendsViewModel)
    viewModelOf(::MyProfileViewModel)
    viewModelOf(::GamesViewModel)
    viewModelOf(::GoogleMapViewModel)
    viewModelOf(::DetailsViewModel)
}