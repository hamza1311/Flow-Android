package com.flow.flowforreddit.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.flow.flowforreddit.App
import com.flow.flowforreddit.R
import com.flow.flowforreddit.activities.FrontPageActivity
import com.flow.flowforreddit.activities.LoginActivity
import com.flow.flowforreddit.models.IS_LOGGED_OUT
import com.flow.flowforreddit.models.LAST_LOGGED_IN
import com.flow.flowforreddit.models.PREFS
import kotlinx.android.synthetic.main.list_users.view.*
import net.dean.jraw.oauth.DeferredPersistentTokenStore
import java.lang.ref.WeakReference
import net.dean.jraw.models.PersistedAuthData
import java.util.*

class AuthDataAdapter(
    private var activity: WeakReference<LoginActivity>,
    private val context: Context,
    private val tokenStore: DeferredPersistentTokenStore
) : RecyclerView.Adapter<AuthDataAdapter.ViewHolder>() {
    private lateinit var usernames: List<String>
    private lateinit var data: TreeMap<String, PersistedAuthData>

    init {
        update()
    }

    private fun update() {
        data = TreeMap(tokenStore.data())
        usernames = ArrayList(this.data.keys)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_users, parent, false)
        view.setOnClickListener {
            ReauthenticationTask(activity).execute(usernames[position])
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return usernames.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindViews(usernames[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var usernameView: TextView

        fun bindViews(username: String) {
            usernameView = itemView.listUsers_username
            usernameView.text = username
        }

    }
}

class ReauthenticationTask internal constructor(private val activity: WeakReference<LoginActivity>) :
    AsyncTask<String, Void, Void>() {
//    var username = ""
    override fun doInBackground(vararg usernames: String): Void? {
        App().getAccountHelper().switchToUser(usernames[0])
//        username = usernames[0]
        Log.d("UsernameSS", "SSSS-UN ${usernames[0]}")
        saveLastLoggedInUser(activity = activity.get(), username = usernames[0])
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        val activity = this.activity.get()
        saveLoggedOut(activity)
        activity?.startActivity(Intent(activity, FrontPageActivity::class.java))
        activity?.finish()
    }

    fun saveLoggedOut(activity: Activity?) = activity?.getSharedPreferences(PREFS, 0)!!.edit().putBoolean(
        IS_LOGGED_OUT,
        false
    ).apply()

    fun saveLastLoggedInUser(activity: Activity?, username: String) = activity?.getSharedPreferences(PREFS, 0)!!.edit().putString(
        LAST_LOGGED_IN,
        username
    ).apply()
}