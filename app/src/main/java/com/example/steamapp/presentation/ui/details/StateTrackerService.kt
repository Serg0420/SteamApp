package com.example.steamapp.presentation.ui.details

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import com.example.steamapp.domain.model.UsersInfo
import com.example.steamapp.domain.repository.UserInfoRepository
import com.example.steamapp.presentation.ui.getStatus
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class StateTrackerService : Service() {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationHelper by lazy { NotificationHelper(this) }
    private val retroDataSource by inject<UserInfoRepository>()

    private var serviceState: ServiceState? = null
    private var currentOnlineStatus: String = INIT_STATUS
    private var userSteamId: String = ""
    private var userSteamNick: String = ""

    private var statusJob: Job? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.getSerializableExtra(SERVICE_STATE_COMMAND)) {
            ServiceState.START -> startTracking()
            ServiceState.PAUSE -> pauseTracking()
            ServiceState.STOP -> endTrackerService()
        }

        userSteamId = intent?.getSerializableExtra(TRACKING_ID).toString()
        userSteamNick = intent?.getSerializableExtra(TRACKING_NICK).toString()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        isServiceRunning = false
    }

    private fun startTracking() {
        serviceState = ServiceState.START
        currentOnlineStatus = INIT_STATUS

        startForeground(NOTIFICATION_ID, notificationHelper.notificationBuilder.build())
        updateServiceState()
        startStatusTracking()
    }

    private fun updateServiceState() {
        when (serviceState) {
            ServiceState.START -> {
                sendBroadcast(
                    Intent(INTENT_ACTION)
                        .putExtra(TRACKER_VALUE, currentOnlineStatus)
                )
                notificationHelper.makeTextNotification(
                    NOTIFICATION_ID,
                    "$userSteamNick: $currentOnlineStatus"
                )
            }
            ServiceState.PAUSE -> {
                notificationHelper.makeTextNotification(
                    NOTIFICATION_ID,
                    "Service paused"
                )
            }
            ServiceState.STOP -> {
                stopService()
            }
        }
    }

    private fun pauseTracking() {
        serviceState = ServiceState.PAUSE
        statusJob?.cancel()
        updateServiceState()
    }

    private fun endTrackerService() {
        serviceState = ServiceState.STOP
        statusJob?.cancel()
        updateServiceState()
    }

    private fun stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }

    private fun startStatusTracking() {
        statusJob = coroutineScope.launch {
            while (true) {
                runCatch()
                    .onSuccess {
                        currentOnlineStatus = it.getStatus(this@StateTrackerService)
                    }
                    .onFailure { handleError(it.toString()) }

                updateServiceState()
                delay(5000)
            }
        }
    }

    private suspend fun runCatch(): Result<UsersInfo> {
        return runCatching {
            retroDataSource.getUserInfoById(userSteamId)
        }
    }

    private fun handleError(errorStr: String) {
        Toast.makeText(this, errorStr, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        const val INTENT_ACTION = "INTENT_ACTION"
        const val SERVICE_STATE_COMMAND = "SERVICE_STATE_COMMAND"
        const val TRACKER_VALUE = "TRACKER_VALUE"
        const val TRACKING_ID = "TRACKING_ID"
        const val TRACKING_NICK = "TRACKING_NICK"

        var isServiceRunning = false
            private set

        private const val INIT_STATUS = "..."
    }
}