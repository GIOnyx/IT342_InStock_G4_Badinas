package com.example.instock

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.instock.network.ApiClient
import com.example.instock.network.RegisterRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var registerButton: MaterialButton
    private lateinit var goToLoginText: TextView
    private lateinit var progressBar: ProgressBar
    private var isRequestInFlight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        registerButton = findViewById(R.id.registerButton)
        goToLoginText = findViewById(R.id.goToLoginText)
        progressBar = findViewById(R.id.registerProgress)

        registerButton.setOnClickListener { attemptRegister() }
        goToLoginText.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        if (isRequestInFlight) return

        val name = nameInput.text?.toString()?.trim().orEmpty()
        val email = emailInput.text?.toString()?.trim().orEmpty()
        val password = passwordInput.text?.toString().orEmpty()

        if (!isInputValid(name, email, password)) {
            return
        }

        setLoading(true)

        ApiClient.authApi.register(RegisterRequest(fullName = name, email = email, password = password))
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    setLoading(false)

                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.putExtra(LoginActivity.EXTRA_REGISTERED_EMAIL, email)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = parseErrorMessage(response.errorBody())
                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(
                        this@RegisterActivity,
                        getString(R.string.network_error, t.localizedMessage ?: getString(R.string.unknown_error)),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun isInputValid(name: String, email: String, password: String): Boolean {
        if (name.isBlank()) {
            nameInput.error = getString(R.string.invalid_name)
            nameInput.requestFocus()
            return false
        }

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

        nameInput.error = null
        emailInput.error = null
        passwordInput.error = null
        return true
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val json = JSONObject(errorBody?.string().orEmpty())
            when {
                json.optJSONObject("error")
                    ?.optJSONObject("details")
                    ?.keys()
                    ?.asSequence()
                    ?.toList()
                    ?.isNotEmpty() == true -> {
                    val details = json.optJSONObject("error")?.optJSONObject("details")
                    val firstKey = details?.keys()?.asSequence()?.firstOrNull()
                    details?.optString(firstKey, getString(R.string.register_failed))
                        ?: getString(R.string.register_failed)
                }
                json.has("message") -> json.getString("message")
                json.has("error") -> json.getString("error")
                else -> getString(R.string.register_failed)
            }
        } catch (_: Exception) {
            getString(R.string.register_failed)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        isRequestInFlight = isLoading
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        registerButton.isEnabled = !isLoading
        goToLoginText.isEnabled = !isLoading
    }
}
