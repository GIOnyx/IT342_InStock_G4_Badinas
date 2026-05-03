package com.example.instock.features.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instock.R
import com.example.instock.core.navigation.DashboardActivity
import com.example.instock.core.network.ApiClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var goToRegisterText: TextView
    private lateinit var progressBar: ProgressBar
    private var isRequestInFlight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        goToRegisterText = findViewById(R.id.goToRegisterText)
        progressBar = findViewById(R.id.loginProgress)

        val prefilledEmail = intent.getStringExtra(EXTRA_REGISTERED_EMAIL)
        if (!prefilledEmail.isNullOrBlank()) {
            emailInput.setText(prefilledEmail)
        }

        loginButton.setOnClickListener { attemptLogin() }
        goToRegisterText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        if (isRequestInFlight) return

        val email = emailInput.text?.toString()?.trim().orEmpty()
        val password = passwordInput.text?.toString().orEmpty()

        if (!isInputValid(email, password)) {
            return
        }

        setLoading(true)

        ApiClient.authApi.login(LoginRequest(email = email, password = password))
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    setLoading(false)

                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        intent.putExtra(DashboardActivity.EXTRA_EMAIL, email)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = parseErrorMessage(response.errorBody())
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.network_error, t.localizedMessage ?: getString(R.string.unknown_error)),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun isInputValid(email: String, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = getString(R.string.invalid_email)
            emailInput.requestFocus()
            return false
        }

        if (password.length < 8) {
            passwordInput.error = getString(R.string.invalid_password)
            passwordInput.requestFocus()
            return false
        }

        emailInput.error = null
        passwordInput.error = null
        return true
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val json = JSONObject(errorBody?.string().orEmpty())
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
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        loginButton.isEnabled = !isLoading
        goToRegisterText.isEnabled = !isLoading
    }

    companion object {
        const val EXTRA_REGISTERED_EMAIL = "extra_registered_email"
    }
}
