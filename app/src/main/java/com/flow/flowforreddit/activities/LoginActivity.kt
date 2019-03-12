package com.flow.flowforreddit.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.flow.flowforreddit.App
import com.flow.flowforreddit.adapters.AuthDataAdapter
import com.flow.flowforreddit.models.isUserless
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.ref.WeakReference
import com.flow.flowforreddit.R

class LoginActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: AuthDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        recyclerView = loginAct_recyclerView
        layoutManager = LinearLayoutManager(this)
        adapter = AuthDataAdapter(WeakReference(this), this, App().getTokenStore()!!)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        loginAct_floatingActionButton.setOnClickListener {
            startActivity(Intent(this, NewUserActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_login_menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.loginAct_userless_menuItem -> {
                isUserless = true
                startActivity(Intent(this, FrontPageActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
