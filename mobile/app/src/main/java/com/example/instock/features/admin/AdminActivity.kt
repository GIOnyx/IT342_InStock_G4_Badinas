package com.example.instock.features.admin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instock.core.network.ApiClient
import com.example.instock.databinding.ActivityAdminBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Admin Panel Activity — SDD Journey 3: Administrator System Management.
 *
 * Uses ViewBinding exclusively (zero findViewByIdcalls).
 * Fires fetchAdminStats() and fetchUsers() concurrently via async/await
 * so the screen populates in a single round-trip time.
 */
class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var userAdapter: AdminUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadAdminData()
    }

    // ── RecyclerView ─────────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        userAdapter = AdminUserAdapter(emptyList())
        binding.rvAdminUsers.apply {
            layoutManager = LinearLayoutManager(this@AdminActivity)
            adapter = userAdapter
            // nestedScrollingEnabled is already false in XML; belt-and-suspenders.
            isNestedScrollingEnabled = false
        }
    }

    // ── Data loading (parallel coroutines) ───────────────────────────────────

    private fun loadAdminData() {
        lifecycleScope.launch {
            // Fire both network calls at the same time.
            val statsDeferred = async { runCatching { ApiClient.adminApi.getStats() } }
            val usersDeferred = async { runCatching { ApiClient.adminApi.getUsers() } }

            fetchAdminStats(statsDeferred.await())
            fetchUsers(usersDeferred.await())
        }
    }

    /**
     * Populates the two stat cards (totalUsers, totalIngredients).
     * Mirrors the backend key names from AdminController#getStats().
     */
    private fun fetchAdminStats(result: Result<retrofit2.Response<AdminStatsResponse>>) {
        result.getOrNull()?.body()?.data?.let { stats ->
            binding.tvTotalUsers.text = stats.totalUsers?.toString() ?: "—"
            binding.tvTotalIngredients.text = stats.totalPantryItems?.toString() ?: "—"
        }
    }

    /**
     * Populates the RecyclerView with the user list returned by
     * AdminController#getUsers(). Hides the loading label when data arrives.
     */
    private fun fetchUsers(result: Result<retrofit2.Response<AdminUsersResponse>>) {
        val users = result.getOrNull()?.body()?.data.orEmpty()
        if (users.isNotEmpty()) {
            binding.tvUsersEmpty.visibility = View.GONE
            userAdapter.updateUsers(users)
        } else {
            binding.tvUsersEmpty.text = "No users found."
        }
    }
}
