package com.focuszone.domain.services.network

import androidx.test.core.app.ApplicationProvider
import com.focuszone.data.preferences.PreferencesManager
import com.focuszone.data.preferences.entities.BlockedSiteEntity
import com.focuszone.util.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException

class BlockedSitesInterceptorTest {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var blockedSites: List<BlockedSiteEntity>

    @Before
    fun setUp() {
        preferencesManager = PreferencesManager(ApplicationProvider.getApplicationContext())
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com"),
            BlockedSiteEntity("https://www.instagram.com"),
            BlockedSiteEntity("http://www.xhamster.com") // strony z chomikami też blokujemy
        )

        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()
    }

    // HTTPS Tests
    @Test
    fun `should block traffic to blocked sites`() {
        val url = blockedSites[2].url
        val request = Request.Builder().url(url).build()

        val response: Response = okHttpClient.newCall(request).execute()

        assertEquals(403, response.code)
    }

    @Test
    fun `should allow traffic to allowed sites`() {
        val url = "https://www.google.com"
        val request = Request.Builder().url(url).build()

        val response: Response = makeRequest(request)

        assertEquals(200, response.code)
    }

    @Test
    fun `should return 403 when site is on blocked list with extra spaces`() {
        blockedSites = listOf(
            BlockedSiteEntity("   https://www.facebook.com   ")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        val request = Request.Builder().url("https://www.facebook.com").build()
        val response = makeRequest(request)

        assertEquals(403, response.code)
    }

    @Test
    fun `should allow traffic when blocked sites list is empty`() {
        blockedSites = emptyList()
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        val request = Request.Builder().url("https://www.wp.pl").build()
        val response = makeRequest(request)

        assertEquals(200, response.code)
    }

    @Test
    fun `should return 403 for http version of blocked site`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        val request = Request.Builder().url("http://www.facebook.com").build()
        val response = makeRequest(request)

        assertEquals(403, response.code)
    }

    @Test
    fun `should allow traffic to sites not matching exact domain`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        val request = Request.Builder().url("https://www.intern.facebook.com").build()
        val response = makeRequest(request)

        assertEquals(200, response.code)
    }

    @Test
    fun `should handle malformed urls gracefully`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        try {
            val request = Request.Builder().url("https://").build()
            makeRequest(request)
            fail("Malformed URL should throw an exception")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Invalid URL") == true)
        }
    }

    @Test
    fun `should block traffic when site URL contains blocked domain as substring`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        val request = Request.Builder().url("https://www.facebook.com/posts").build()
        val response = makeRequest(request)

        assertEquals(403, response.code)
    }

    @Test
    fun `should block traffic even when URL has query parameters`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        val request = Request.Builder().url("https://www.facebook.com?query=test").build()
        val response = makeRequest(request)

        assertEquals(403, response.code)
    }


    @Test
    fun `should block traffic when site URL contains blocked domain with subpath`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        val request = Request.Builder().url("https://www.facebook.com/page/about").build()
        val response = makeRequest(request)

        assertEquals(403, response.code)
    }

    // HTTP Tests
    @Test
    fun `should allow traffic for http URLs that do not match blocked domain`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        val request = Request.Builder().url("http://www.example.com").build()
        val response = makeRequest(request)

        assertEquals(200, response.code)
    }


    @Test
    fun `should handle incomplete URLs without protocol`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()


        try {
            val request = Request.Builder().url("facebook.com").build()
            makeRequest(request)
            fail("Incomplete URL should throw an exception")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Expected URL scheme 'http' or 'https'") == true)
        }
    }

    @Test
    fun `should handle URLs with non-standard protocols`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        try {
            val request = Request.Builder().url("ftp://www.facebook.com").build()
            makeRequest(request)
            fail("Non-standard protocol should be rejected")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Expected URL scheme 'http' or 'https'") == true)
        }
    }

    @Test
    fun `should handle malformed URLs with extra characters`() {
        blockedSites = listOf(
            BlockedSiteEntity("https://www.facebook.com")
        )
        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(BlockedSitesInterceptor(blockedSites))
            .build()

        try {
            val request = Request.Builder().url("https://www.facebook.com!!!").build()
            makeRequest(request)
            fail("Malformed URL should throw an exception")
        } catch (e: UnknownHostException) {
            assertTrue(e.message?.contains("Unable to resolve host") == true)
        }
    }

    private fun makeRequest(request: Request): Response {
        return try {
            okHttpClient.newCall(request).execute()
        } catch (e: IOException) {
            Logger.error("REQUEST_FAILED","Request failed: ${e.message}")
            throw e
        }
    }
}