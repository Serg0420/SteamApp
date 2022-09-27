package com.example.steamapp


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration

fun RecyclerView.addVerticalSeparation() {

    this.addItemDecoration(

        MaterialDividerItemDecoration(
            this.context,
            MaterialDividerItemDecoration.VERTICAL
        )

    )
}

fun DetailsFragmentArgs.getStatus(): String {
    return when (this.personaState.toInt()) {
        //сетевые статусы игрока
        1 -> ONLINE
        2 -> BUSY
        3 -> AWAY
        4 -> SNOOZE
        5 -> LOOKING_TO_TRADE
        6 -> LOOKING_TO_PLAY
        else -> OFFLINE
    }
}

private const val ONLINE = "Online"
private const val BUSY = "Busy"
private const val AWAY = "Away"
private const val SNOOZE = "Snooze"
private const val LOOKING_TO_TRADE = "looking to trade"
private const val LOOKING_TO_PLAY = "looking to play"
private const val OFFLINE = "Offline"