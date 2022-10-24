package com.example.steamapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.steamapp.R
import com.example.steamapp.presentation.ui.fragments.BottomNavigationFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, BottomNavigationFragment())
            .commit()
    }
}