package com.focuszone.network

import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import android.os.ParcelFileDescriptor
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import java.nio.ByteBuffer

class SiteBlocker : VpnService() {

    internal lateinit var preferencesManager: PreferencesManager
    internal var blockedSites: List<BlockedSiteEntity> = emptyList()
    private var vpnThread: Thread? = null

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)

        // Fetch blocked sites entity list
        blockedSites = preferencesManager.getBlockedSites()

        // Listen to changes in Shared Preferences
        preferencesManager.addBlockedSitesChangedListener(object : PreferencesManager.OnBlockedSitesChangedListener {
            override fun onBlockedSitesChanged(newBlockedSites: List<BlockedSiteEntity>) {
                blockedSites = newBlockedSites
                restartVpn()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        vpnThread = Thread { runVpn() }
        vpnThread?.start()
        return START_STICKY
    }

    override fun onDestroy() {
        vpnThread?.interrupt()
        vpnThread = null
        preferencesManager.removeBlockedSitesChangedListener(object : PreferencesManager.OnBlockedSitesChangedListener {
            override fun onBlockedSitesChanged(newBlockedSites: List<BlockedSiteEntity>) {
                // Nothing is required
            }
        })
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Service is not bound to any activity, so return null (no need to fetch/pass/control service data)
    }

    /**
     * Turn on VPN and intercept network traffic.
     */
    internal fun runVpn() {
        val builder = Builder()
        builder.addAddress("10.0.0.2", 24)
        builder.addRoute("0.0.0.0", 0)
        builder.setSession("SiteBlocker")
        val vpnInterface = builder.establish() ?: throw IllegalStateException("Could not enable VPN service - network blocking is not working")

        val inputStream = ParcelFileDescriptor.AutoCloseInputStream(vpnInterface)
        val outputStream = ParcelFileDescriptor.AutoCloseOutputStream(vpnInterface)

        val packet = ByteBuffer.allocate(32767)

        try {
            while (!Thread.interrupted()) {
                val length = inputStream.read(packet.array())
                if (length > 0) {
                    val destination = extractUrlFromPacket(packet, length)
                    if (isBlocked(destination)) {
                        // "Block package" - just ignore it
                        continue
                    } else {
                        // Pass packet
                        outputStream.write(packet.array(), 0, length)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream.close()
            outputStream.close()
        }
    }

    /**
     * Check if address is inside blocked list.
     */
    internal fun isBlocked(destination: String): Boolean {
        return blockedSites.any { destination.contains(it.url) }
    }

    internal fun extractUrlFromPacket(packet: ByteBuffer, length: Int): String {
        try {
            val packetArray = ByteArray(length)
            packet.rewind()
            packet.get(packetArray)

            val packetString = String(packetArray, Charsets.UTF_8)

            val hostMatch = Regex("Host:\\s*([^\r\n]+)").find(packetString)

            return hostMatch?.groupValues?.get(1)?.trim() ?: ""

        } catch (e: Exception) {
            return ""
        }
    }

    internal fun restartVpn() {
        vpnThread?.interrupt()
        vpnThread = Thread { runVpn() }
        vpnThread?.start()
    }
}
