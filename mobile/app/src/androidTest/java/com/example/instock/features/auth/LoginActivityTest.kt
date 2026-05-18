package com.example.instock.features.auth

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.instock.R
import com.example.instock.core.network.ApiClient
import com.example.instock.core.network.ApiResponse
import com.example.instock.features.admin.AdminActivity
import com.example.instock.core.navigation.DashboardActivity
import com.google.gson.JsonObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
        ApiClient.authApiForTest = null
    }

    @Test
    fun loginAsUser_routesToDashboardActivity() {
        ApiClient.authApiForTest = object : AuthApiService {
            override fun register(request: RegisterRequest): Call<JsonObject> = TODO()
            override suspend fun registerSuspend(request: RegisterRequest): ApiResponse<AuthResponse> = TODO()
            override fun login(request: LoginRequest): Call<JsonObject> = TODO()
            override suspend fun loginSuspend(request: LoginRequest): ApiResponse<AuthResponse> {
                return ApiResponse(
                    success = true,
                    message = "Success",
                    data = AuthResponse(1, request.email, "John Doe", "USER", null, "mockToken", null)
                )
            }
            override fun getMe(): Call<AuthMeResponse> = TODO()
            override suspend fun getMeSuspend(): ApiResponse<AuthResponse> = TODO()
            override fun updateMe(request: UpdateProfileRequest): Call<AuthMeResponse> = TODO()
            override suspend fun updateMeSuspend(request: UpdateProfileRequest): ApiResponse<AuthResponse> = TODO()
            override fun changePassword(request: ChangePasswordRequest): Call<AuthMeResponse> = TODO()
            override suspend fun changePasswordSuspend(request: ChangePasswordRequest): ApiResponse<AuthResponse> = TODO()
        }

        onView(withId(R.id.emailInput)).perform(replaceText("user@example.com"), closeSoftKeyboard())
        onView(withId(R.id.passwordInput)).perform(replaceText("password123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        intended(hasComponent(DashboardActivity::class.java.name))
    }

    @Test
    fun loginAsAdmin_routesToAdminActivity() {
        ApiClient.authApiForTest = object : AuthApiService {
            override fun register(request: RegisterRequest): Call<JsonObject> = TODO()
            override suspend fun registerSuspend(request: RegisterRequest): ApiResponse<AuthResponse> = TODO()
            override fun login(request: LoginRequest): Call<JsonObject> = TODO()
            override suspend fun loginSuspend(request: LoginRequest): ApiResponse<AuthResponse> {
                return ApiResponse(
                    success = true,
                    message = "Success",
                    data = AuthResponse(2, request.email, "Admin Jane", "ADMIN", null, "mockToken", null)
                )
            }
            override fun getMe(): Call<AuthMeResponse> = TODO()
            override suspend fun getMeSuspend(): ApiResponse<AuthResponse> = TODO()
            override fun updateMe(request: UpdateProfileRequest): Call<AuthMeResponse> = TODO()
            override suspend fun updateMeSuspend(request: UpdateProfileRequest): ApiResponse<AuthResponse> = TODO()
            override fun changePassword(request: ChangePasswordRequest): Call<AuthMeResponse> = TODO()
            override suspend fun changePasswordSuspend(request: ChangePasswordRequest): ApiResponse<AuthResponse> = TODO()
        }

        onView(withId(R.id.emailInput)).perform(replaceText("admin@example.com"), closeSoftKeyboard())
        onView(withId(R.id.passwordInput)).perform(replaceText("password123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        intended(hasComponent(AdminActivity::class.java.name))
    }
}
