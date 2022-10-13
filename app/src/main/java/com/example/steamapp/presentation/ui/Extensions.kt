package com.example.steamapp.presentation.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.steamapp.R
import com.google.android.material.divider.MaterialDividerItemDecoration

fun RecyclerView.addVerticalSeparation() {
    this.addItemDecoration(
        MaterialDividerItemDecoration(
            this.context,
            MaterialDividerItemDecoration.VERTICAL
        )
    )
}

fun DetailsFragmentArgs.getStatus(context: Context): String {
    return when (this.personaState.toInt()) {
        //сетевые статусы игрока
        1 -> context.getString(R.string.online)
        2 -> context.getString(R.string.busy)
        3 -> context.getString(R.string.away)
        4 -> context.getString(R.string.snooze)
        5 -> context.getString(R.string.lookingToTrade)
        6 -> context.getString(R.string.lookingToPlay)
        else -> context.getString(R.string.offline)
    }
}