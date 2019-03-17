package com.flow.flowforreddit.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.flow.flowforreddit.App
import com.flow.flowforreddit.R
import com.flow.flowforreddit.fragments.FrontPageFragment
import com.flow.flowforreddit.models.IS_LOGGED_OUT
import com.flow.flowforreddit.models.LAST_LOGGED_IN
import com.flow.flowforreddit.models.PREFS
import com.flow.flowforreddit.models.isUserless
import kotlinx.android.synthetic.main.activity_front_page.*
import kotlinx.android.synthetic.main.app_bar_front_page.*
import java.lang.ref.WeakReference
import java.util.*

class FrontPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_page)
        setSupportActionBar(toolbar)
        prefs = getSharedPreferences(PREFS, 0)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        login()
    }

    private fun login() {
        val users = ArrayList(TreeMap(App().getTokenStore()!!.data()).keys)
        if (users.contains(getLastLoggedInUser()) && !isUserless && !isLoggedOut()) {
            ReauthenticationTask(WeakReference(this)).execute(getLastLoggedInUser())
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.front_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.frontPageAct_settings_menuItem -> true
            R.id.frontPageAct_logout_menuItem -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        App().getAccountHelper().logout()
        saveLoggedOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun saveLoggedOut() = getSharedPreferences(PREFS, 0)!!.edit().putBoolean(
        IS_LOGGED_OUT,
        true
    ).apply()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.frontPage_frontPageDrawer -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, FrontPageFragment()).commit()
            }
            R.id.profile_frontPageDrawer -> {
//                TODO: Implement feature
            }
            R.id.saved_frontPageDrawer -> {
//                TODO: Implement feature
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()
    }

    private fun getLastLoggedInUser(): String? = prefs.getString(LAST_LOGGED_IN, "")

    private fun isLoggedOut() = prefs.getBoolean(IS_LOGGED_OUT, false)


    private class ReauthenticationTask internal constructor(private val activity: WeakReference<FrontPageActivity>) :
        AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg usernames: String): Void? {
            App().getAccountHelper().switchToUser(usernames[0])
            Log.d("donnnnne", "done")
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            saveLastLoggedInUser()
            val activity = this.activity.get()
            activity?.supportFragmentManager!!.beginTransaction().replace(R.id.fragmentContainer, FrontPageFragment())
                .commit()
        }

        fun saveLastLoggedInUser() {
            val activity = this.activity.get()
            activity?.getSharedPreferences(PREFS, 0)!!.edit().putString(
                LAST_LOGGED_IN,
                App().getAccountHelper().reddit.me().username
            ).apply()
        }
    }
}
