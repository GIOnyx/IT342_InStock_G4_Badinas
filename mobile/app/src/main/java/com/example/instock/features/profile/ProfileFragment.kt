package com.example.instock.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.instock.R
import com.example.instock.core.network.ApiClient
import com.example.instock.features.auth.AuthMeResponse
import com.example.instock.features.auth.UpdateProfileRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnUpdate: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        etFullName = view.findViewById(R.id.etFullName)
        etEmail = view.findViewById(R.id.etEmail)
        btnUpdate = view.findViewById(R.id.btnUpdateProfile)

        btnUpdate.setOnClickListener {
            updateProfile()
        }

        fetchProfile()

        return view
    }

    private fun fetchProfile() {
        ApiClient.authApi.getMe().enqueue(object : Callback<AuthMeResponse> {
            override fun onResponse(call: Call<AuthMeResponse>, response: Response<AuthMeResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    user?.let {
                        etFullName.setText(it.fullName)
                        etEmail.setText(it.email)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthMeResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateProfile() {
        val newName = etFullName.text.toString().trim()
        if (newName.isEmpty()) return

        ApiClient.authApi.updateMe(UpdateProfileRequest(newName)).enqueue(object : Callback<AuthMeResponse> {
            override fun onResponse(call: Call<AuthMeResponse>, response: Response<AuthMeResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthMeResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
