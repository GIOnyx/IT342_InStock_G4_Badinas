package com.example.instock

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        findViewById<TextView>(R.id.pageTitle).text = getString(R.string.settings_title)
        findViewById<TextView>(R.id.pageSubtitle).text = getString(R.string.settings_subtitle)
        FooterNavigation.setup(this, FooterPage.SETTINGS)
    }
}
