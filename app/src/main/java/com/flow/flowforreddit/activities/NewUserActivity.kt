package com.flow.flowforreddit.activities

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import net.dean.jraw.oauth.OAuthException
import net.dean.jraw.oauth.StatefulAuthHelper
import android.os.AsyncTask
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import android.os.Build
import android.view.View
import android.webkit.CookieManager
import android.widget.Toast
import com.flow.flowforreddit.App
import com.flow.flowforreddit.R
import com.flow.flowforreddit.models.IS_LOGGED_OUT
import com.flow.flowforreddit.models.PREFS
import kotlinx.android.synthetic.main.activity_new_user.*
import java.lang.ref.WeakReference

class NewUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        // Don't save any cookies, cache, or history from previous sessions. If we don't, once the
        // first user logs in and authenticates, the next time we go to add a new user, the first
        // user will be automatically logged in, which is not what we want.
        val webView = newUserAct_webView
        webView.clearCache(true)
        webView.clearHistory()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        }

        val helper = App().getAccountHelper().switchToNewUser()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                if (helper.isFinalRedirectUrl(url)) {
                    // No need to continue loading, we've already got all the required information
                    webView.stopLoading()
                    webView.visibility = View.GONE

                    AuthenticateTask(this@NewUserActivity, helper).execute(url)
                }
            }
        }

        val requestRefreshToken = true
        val useMobileSite = true
        val scopes = arrayOf("read", "identity")
        val authUrl = helper.getAuthorizationUrl(requestRefreshToken, useMobileSite, *scopes)

        webView.loadUrl(authUrl)
    }

    private class AuthenticateTask internal constructor(context: Activity, private val helper: StatefulAuthHelper) :
        AsyncTask<String, Void, Boolean>() {

        private val context: WeakReference<Activity>

        init {
            this.context = WeakReference(context)
        }

        override fun doInBackground(vararg urls: String): Boolean {
            try {
                helper.onUserChallenge(urls[0])
                return true
            } catch (e: OAuthException) {
                Toast.makeText(context.get(), "Something happened", Toast.LENGTH_SHORT).show()
                return false
            }

        }

        override fun onPostExecute(success: Boolean?) {
            val host = this.context.get()
            saveLoggedOut(host)
            if (host != null) {
                host.setResult(if (success!!) Activity.RESULT_OK else Activity.RESULT_CANCELED, Intent())
                host.finish()
            }

            host!!.startActivity(Intent(host, FrontPageActivity::class.java))
        }

        fun saveLoggedOut(activity: Activity?) = activity?.getSharedPreferences(PREFS, 0)!!.edit().putBoolean(
            IS_LOGGED_OUT,
            false
        ).apply()
    }
}
