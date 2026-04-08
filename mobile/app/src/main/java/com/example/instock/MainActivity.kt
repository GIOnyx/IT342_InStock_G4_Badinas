package com.example.instock

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity

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