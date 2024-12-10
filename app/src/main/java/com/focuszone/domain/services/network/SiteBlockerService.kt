package com.focuszone.domain.services.network

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class SiteBlockerService : Service() {

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var blockedSites: List<BlockedSiteEntity>
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var networkInterceptorJob: Job? = null

    val TAG = "FOCUSZONE_NET_INTERCEPTOR"

    init {
        Logger.debug(TAG, "Network interceptor is enabled...")
    }

    enum class Actions {
        START,
        STOP,
        RESTART
    }

    override fun onCreate() {
        super.onCreate()

        preferencesManager = PreferencesManager(this)

        blockedSites = preferencesManager.getBlockedSites()

        Logger.debug(TAG, "Building network interceptor")
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        preferencesManager.addBlockedSitesChangedListener(object : PreferencesManager.OnBlockedSitesChangedListener {
            override fun onBlockedSitesChanged(newBlockedSites: List<BlockedSiteEntity>) {
                blockedSites = newBlockedSites
                restartInterceptor()
            }
        })
    }

    private fun startInterceptor() {
        Logger.debug(TAG, "Starting network interceptor")
        networkInterceptorJob = serviceScope.launch {
            blockedSites = preferencesManager.getBlockedSites() // refresh list
            okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
                .build()
            runNetworkInterceptor()
        }
    }

    private fun runNetworkInterceptor() {
        networkInterceptorJob = serviceScope.launch {
            Logger.debug(TAG, "Launching network interceptor")
        }
    }

    private fun stopInterceptor() {
        Logger.debug(TAG, "Stopping network interceptor")
        networkInterceptorJob?.cancel()
        stopSelf()
    }

    private fun restartInterceptor() {
        Logger.debug(TAG, "Restarting network interceptor")
        stopInterceptor()
        startInterceptor()
    }

    // Intents to manage service state
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> startInterceptor()
            Actions.STOP.toString() -> stopInterceptor()
            Actions.RESTART.toString() -> restartInterceptor()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Service is not bound to any activity, so return null (no need to fetch/pass/control service data)
    }

    // Method to run service from other Activities
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SiteBlockerService::class.java).apply {
                action = Actions.START.toString()
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, SiteBlockerService::class.java).apply {
                action = Actions.STOP.toString()
            }
            context.startService(intent)
        }

        fun restart(context: Context) {
            val intent = Intent(context, SiteBlockerService::class.java).apply {
                action = Actions.RESTART.toString()
            }
            context.startService(intent)
        }
    }
}