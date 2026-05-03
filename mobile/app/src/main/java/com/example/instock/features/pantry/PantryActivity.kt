package com.example.instock.features.pantry

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.instock.R
import com.example.instock.core.navigation.FooterNavigation
import com.example.instock.core.navigation.FooterPage

class PantryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        findViewById<TextView>(R.id.pageTitle).text = getString(R.string.pantry_title)
        findViewById<TextView>(R.id.pageSubtitle).text = getString(R.string.pantry_subtitle)
        FooterNavigation.setup(this, FooterPage.PANTRY)
    }
}
