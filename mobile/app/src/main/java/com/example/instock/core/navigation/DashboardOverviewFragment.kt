package com.example.instock.core.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.instock.R

class DashboardOverviewFragment : Fragment() {

    private lateinit var email: String
    private lateinit var tabType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString(ARG_EMAIL).orEmpty()
        tabType = arguments?.getString(ARG_TAB_TYPE).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_overview, container, false)

        val pageTitle = view.findViewById<TextView>(R.id.pageTitle)
        val pageSubtitle = view.findViewById<TextView>(R.id.pageSubtitle)
        val featureTitle = view.findViewById<TextView>(R.id.featureTitle)
        val featureBody = view.findViewById<TextView>(R.id.featureBody)

        when (tabType) {
            "SETTINGS" -> {
                pageTitle.text = getString(R.string.settings_title)
                pageSubtitle.text = getString(R.string.settings_subtitle)
                featureTitle.text = getString(R.string.settings_feature_title)
                featureBody.text = getString(R.string.settings_feature_text)
            }
            "RECIPES" -> {
                pageTitle.text = getString(R.string.recipes_title)
                pageSubtitle.text = getString(R.string.recipes_subtitle)
                featureTitle.text = getString(R.string.dashboard_recipe_title)
                featureBody.text = getString(R.string.dashboard_recipe_text)
            }
            "FAVORITES" -> {
                pageTitle.text = getString(R.string.favorites_title)
                pageSubtitle.text = getString(R.string.favorites_subtitle)
                featureTitle.text = getString(R.string.favorites_feature_title)
                featureBody.text = getString(R.string.favorites_feature_text)
            }
            "PROFILE" -> {
                pageTitle.text = getString(R.string.profile_title)
                pageSubtitle.text = getString(R.string.profile_subtitle)
                featureTitle.text = getString(R.string.profile_feature_title)
                featureBody.text = getString(R.string.profile_feature_text)
            }
            else -> {
                pageTitle.text = getString(R.string.pantry_title)
                pageSubtitle.text = getString(R.string.dashboard_subtitle, email)
                featureTitle.text = getString(R.string.dashboard_inventory_title)
                featureBody.text = getString(R.string.dashboard_inventory_text)
            }
        }

        return view
    }

    companion object {
        private const val ARG_EMAIL = "arg_email"
        private const val ARG_TAB_TYPE = "arg_tab_type"

        fun newInstance(email: String, tabType: String) = DashboardOverviewFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_EMAIL, email)
                putString(ARG_TAB_TYPE, tabType)
            }
        }
    }
}
