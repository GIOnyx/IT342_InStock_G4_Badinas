package com.example.instock.core.navigation

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.instock.R
import com.example.instock.features.auth.LoginActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()
        val welcomeText: TextView = findViewById(R.id.welcomeText)
        val logoutButton: MaterialButton = findViewById(R.id.logoutButton)

        welcomeText.text = getString(R.string.welcome_user, email)

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}
