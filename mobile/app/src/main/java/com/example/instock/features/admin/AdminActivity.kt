package com.example.instock.features.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instock.R
import com.example.instock.core.network.ApiClient
import com.example.instock.core.network.TokenManager
import com.example.instock.features.auth.LoginActivity
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var statsContainer: View
    private lateinit var tvTotalUsers: TextView
    private lateinit var tvTotalPantryItems: TextView
    private lateinit var tvTotalFavorites: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // RBAC Security Check: Kick if not ADMIN
        if (TokenManager.getRole() != "ADMIN") {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_admin)

        progressBar = findViewById(R.id.progressBar)
        statsContainer = findViewById(R.id.statsContainer)
        tvTotalUsers = findViewById(R.id.tvTotalUsers)
        tvTotalPantryItems = findViewById(R.id.tvTotalPantryItems)
        tvTotalFavorites = findViewById(R.id.tvTotalFavorites)

        findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            logout()
        }

        fetchAdminStats()
    }

    private fun fetchAdminStats() {
        progressBar.visibility = View.VISIBLE
        statsContainer.visibility = View.GONE

        ApiClient.adminApi.getStats().enqueue(object : Callback<AdminStatsResponse> {
            override fun onResponse(call: Call<AdminStatsResponse>, response: Response<AdminStatsResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!.data
                    statsContainer.visibility = View.VISIBLE
                    tvTotalUsers.text = stats.totalUsers.toString()
                    tvTotalPantryItems.text = stats.totalPantryItems.toString()
                    tvTotalFavorites.text = stats.totalFavorites.toString()
                } else {
                    Toast.makeText(this@AdminActivity, "Failed to load stats: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AdminStatsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@AdminActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout() {
        getSharedPreferences("instock_session", MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply()
        com.example.instock.core.network.AllergenPrefs.clear()
        com.example.instock.core.network.OfflineCache.clear()

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
