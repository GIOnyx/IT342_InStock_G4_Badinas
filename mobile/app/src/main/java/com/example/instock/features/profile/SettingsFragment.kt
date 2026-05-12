package com.example.instock.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.instock.R
import com.example.instock.core.network.AllergenPrefs
import com.example.instock.core.network.ApiClient
import com.example.instock.features.auth.AuthMeResponse
import com.example.instock.features.auth.ChangePasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment : Fragment() {

    private lateinit var etCurrent: EditText
    private lateinit var etNew: EditText
    private lateinit var etConfirm: EditText
    private lateinit var btnChange: View
    private lateinit var spinnerLimit: Spinner
    private lateinit var btnSaveAllergens: View

    // Allergen checkboxes — keys match Spoonacular intolerances param values
    private val allergenCheckboxIds = listOf(
        "dairy"     to R.id.cbDairy,
        "egg"       to R.id.cbEgg,
        "gluten"    to R.id.cbGluten,
        "grain"     to R.id.cbGrain,
        "peanut"    to R.id.cbPeanut,
        "seafood"   to R.id.cbSeafood,
        "sesame"    to R.id.cbSesame,
        "shellfish" to R.id.cbShellfish,
        "soy"       to R.id.cbSoy,
        "sulfite"   to R.id.cbSulfite,
        "tree nut"  to R.id.cbTreeNut,
        "wheat"     to R.id.cbWheat
    )

    private val checkboxViews = mutableMapOf<String, CheckBox>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        etCurrent = view.findViewById(R.id.etCurrentPassword)
        etNew = view.findViewById(R.id.etNewPassword)
        etConfirm = view.findViewById(R.id.etConfirmPassword)
        btnChange = view.findViewById(R.id.btnChangePassword)
        spinnerLimit = view.findViewById(R.id.spinnerLimit)
        btnSaveAllergens = view.findViewById(R.id.btnSaveAllergens)

        // ── Recipe limit spinner ──────────────────────────────────────
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.recipe_limit_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLimit.adapter = adapter

        // ── Change password ───────────────────────────────────────────
        btnChange.setOnClickListener { changePassword() }

        // ── Allergen checkboxes ───────────────────────────────────────
        val savedAllergens = AllergenPrefs.getAllergens()
        allergenCheckboxIds.forEach { (key, resId) ->
            val cb = view.findViewById<CheckBox>(resId)
            cb.isChecked = savedAllergens.contains(key)
            checkboxViews[key] = cb
        }

        btnSaveAllergens.setOnClickListener { saveAllergens() }

        return view
    }

    private fun saveAllergens() {
        val selected = checkboxViews
            .filter { (_, cb) -> cb.isChecked }
            .keys
            .toSet()

        AllergenPrefs.saveAllergens(selected)

        val message = if (selected.isEmpty()) {
            "Dietary preferences cleared."
        } else {
            "Saved: ${selected.joinToString(", ")}"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun changePassword() {
        val current = etCurrent.text.toString()
        val new = etNew.text.toString()
        val confirm = etConfirm.text.toString()

        if (current.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (new != confirm) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ChangePasswordRequest(current, new)
        ApiClient.authApi.changePassword(request).enqueue(object : Callback<AuthMeResponse> {
            override fun onResponse(call: Call<AuthMeResponse>, response: Response<AuthMeResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Password changed!", Toast.LENGTH_SHORT).show()
                    etCurrent.text.clear()
                    etNew.text.clear()
                    etConfirm.text.clear()
                } else {
                    Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthMeResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
