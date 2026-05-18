package com.example.instock.features.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.instock.R
import com.example.instock.core.navigation.DashboardActivity
import com.example.instock.core.navigation.MainActivity
import com.example.instock.core.network.ApiClient
import com.example.instock.core.network.TokenManager
import com.example.instock.databinding.ActivityLoginBinding
import org.json.JSONObject
import retrofit2.HttpException
import kotlinx.coroutines.launch
import com.example.instock.features.admin.AdminActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isRequestInFlight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefilledEmail = intent.getStringExtra(EXTRA_REGISTERED_EMAIL)
        if (!prefilledEmail.isNullOrBlank()) {
            binding.emailInput.setText(prefilledEmail)
        }

        binding.loginButton.setOnClickListener { attemptLogin() }
        binding.goToRegisterText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        if (isRequestInFlight) return

        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()

        if (!isInputValid(email, password)) {
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.authApi.loginSuspend(LoginRequest(email = email, password = password))
                if (response.success && response.data != null) {
                    val token = response.data.token.orEmpty()
                    if (token.isNotEmpty()) {
                        TokenManager.saveToken(token)
                    }
                    TokenManager.saveRole(response.data.role)

                    Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

                    val destination = if (response.data.role == "ADMIN") {
                        AdminActivity::class.java
                    } else {
                        com.example.instock.core.navigation.DashboardActivity::class.java
                    }
                    val intent = Intent(this@LoginActivity, destination)
                    val extraKey = if (destination == AdminActivity::class.java) {
                        "extra_email"
                    } else {
                        com.example.instock.core.navigation.DashboardActivity.EXTRA_EMAIL
                    }
                    intent.putExtra(extraKey, email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_LONG).show()
                }
            } catch (error: HttpException) {
                val message = if (error.code() == 401) {
                    getString(R.string.invalid_credentials)
                } else {
                    parseErrorMessage(error.response()?.errorBody()?.string())
                }
                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
            } catch (error: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.network_error, error.localizedMessage ?: getString(R.string.unknown_error)),
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun isInputValid(email: String, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = getString(R.string.invalid_email)
            binding.emailInput.requestFocus()
            return false
        }

        if (password.length < 8) {
            binding.passwordInput.error = getString(R.string.invalid_password)
            binding.passwordInput.requestFocus()
            return false
        }

        binding.emailInput.error = null
        binding.passwordInput.error = null
        return true
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val json = JSONObject(errorBody.orEmpty())
            when {
                json.has("message") -> json.getString("message")
                json.has("error") -> json.getString("error")
                else -> getString(R.string.invalid_credentials)
            }
        } catch (_: Exception) {
            getString(R.string.invalid_credentials)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        isRequestInFlight = isLoading
        binding.loginProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
        binding.goToRegisterText.isEnabled = !isLoading
    }

    companion object {
        const val EXTRA_REGISTERED_EMAIL = "extra_registered_email"
    }
}
