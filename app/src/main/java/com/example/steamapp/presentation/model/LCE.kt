package com.example.steamapp.presentation.model

sealed class LCE<out T> {
    data class Content<T>(val data: T) : LCE<T>()
    data class Error(val throwable: Throwable) : LCE<Nothing>()
}