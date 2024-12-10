package com.focuszone.domain.services.network

import com.focuszone.data.preferences.entities.BlockedSiteEntity
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
class BlockedSitesInterceptor(private val blockedSites: List<BlockedSiteEntity>) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        val isBlocked = blockedSites.any { blockedSite ->
            val normalizedBlockedUrl = blockedSite.url.trim().let {
                if (!it.startsWith("https://")) "https://$it" else it
            }
            url.startsWith(normalizedBlockedUrl)
        }

        return if (isBlocked) {
            val fakeResponse = chain.proceed(request)

            fakeResponse.newBuilder()
                .code(403)
                .message("This site is blocked via FocusZone: $url")
                .body(ResponseBody.create(null, "Blocked by FocusZone"))
                .build()
        } else {
            chain.proceed(request)
        }
    }
}
