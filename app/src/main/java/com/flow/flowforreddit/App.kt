package com.flow.flowforreddit

import net.dean.jraw.android.SharedPreferencesTokenStore
import net.dean.jraw.oauth.AccountHelper
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.android.SimpleAndroidLogAdapter
import net.dean.jraw.http.LogAdapter
import net.dean.jraw.android.AndroidHelper
import java.util.UUID.randomUUID
import net.dean.jraw.android.ManifestAppInfoProvider
import android.app.Application
import android.util.Log
import java.util.*


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Get UserAgent and OAuth2 data from AndroidManifest.xml
        val provider = ManifestAppInfoProvider(applicationContext)


        // Ideally, this should be unique to every device
        val deviceUuid = UUID.randomUUID()

        // Store our access tokens and refresh tokens in shared preferences
        tokenStore = SharedPreferencesTokenStore(applicationContext)
        // Load stored tokens into memory
        tokenStore!!.load()
        // Automatically save new tokens as they arrive
        tokenStore!!.autoPersist = true

        // An AccountHelper manages switching between accounts and into/out of userless mode.
        accountHelper = AndroidHelper.accountHelper(provider, deviceUuid, tokenStore!!)

        // Every time we use the AccountHelper to switch between accounts (from one account to
        // another, or into/out of userless mode), call this function
        accountHelper!!.onSwitch { redditClient ->
            // By default, JRAW logs HTTP activity to System.out. We're going to use Log.i()
            // instead.
            val logAdapter = SimpleAndroidLogAdapter(Log.INFO)

            // We're going to use the LogAdapter to write down the summaries produced by
            // SimpleHttpLogger
            redditClient.logger = SimpleHttpLogger(SimpleHttpLogger.DEFAULT_LINE_LENGTH, logAdapter)

            // If you want to disable logging, use a NoopHttpLogger instead:
            // redditClient.setLogger(new NoopHttpLogger());
        }
    }

    companion object {
        lateinit var accountHelper: AccountHelper
            private set
        var tokenStore: SharedPreferencesTokenStore? = null
            private set
    }

    fun getAccountHelper() = accountHelper
    fun getTokenStore() = tokenStore
}