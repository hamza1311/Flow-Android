package com.flow.flowforreddit.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.flow.flowforreddit.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainAct_login_button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
