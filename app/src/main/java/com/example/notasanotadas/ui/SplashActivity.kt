package com.example.notasanotadas.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notasanotadas.R

class SplashActivity : AppCompatActivity() {

    private val splashTime = 3000L
    private lateinit var myHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        myHandler = Handler()

        myHandler.postDelayed({
            goToMainActivity()
        }, splashTime)
    }

    private fun goToMainActivity() {
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }
}