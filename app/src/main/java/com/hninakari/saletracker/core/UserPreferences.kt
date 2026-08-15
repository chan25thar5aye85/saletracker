package com.hninakari.saletracker.core

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "user_settings"

        private const val KEY_USER_ID = "user_id"
        private const val KEY_THEME_MODE = "theme_mode"

        private const val DEFAULT_USER_ID = "default-user"
        private const val DEFAULT_THEME_MODE = "dark"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private val prefs =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ---------------------------------------------------------
    // USER ID
    // ---------------------------------------------------------

    private val _userId = MutableStateFlow(getUserId())
    val userId: StateFlow<String> = _userId.asStateFlow()

    fun getUserId(): String {
        val id = prefs.getString(
            KEY_USER_ID,
            DEFAULT_USER_ID
        ) ?: DEFAULT_USER_ID

        Log.d("UserPreferences", "getUserId returning: '$id'")

        return id
    }

    fun saveUserId(userId: String) {
        Log.d("UserPreferences", "saveUserId: '$userId'")

        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .apply()

        _userId.value = userId
    }

    fun resetUserId() {
        Log.d("UserPreferences", "resetUserId")

        prefs.edit()
            .putString(KEY_USER_ID, DEFAULT_USER_ID)
            .apply()

        _userId.value = DEFAULT_USER_ID
    }

    fun resetToDefault() {
        resetUserId()
    }

    // ---------------------------------------------------------
    // THEME - SUPPORT ALL THEMES
    // ---------------------------------------------------------

    private val _themeMode = MutableStateFlow(getThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun getThemeMode(): String {
        val theme = prefs.getString(
            KEY_THEME_MODE,
            DEFAULT_THEME_MODE
        ) ?: DEFAULT_THEME_MODE
        
        Log.d("UserPreferences", "getThemeMode returning: '$theme'")
        return theme
    }

    fun saveThemeMode(themeMode: String) {
        // ⭐ Allow all theme values: dark, light, purple, green
        val validThemes = listOf("dark", "light", "purple", "green")
        val mode = if (themeMode in validThemes) themeMode else DEFAULT_THEME_MODE

        Log.d("UserPreferences", "saveThemeMode: '$mode' (requested: '$themeMode')")

        prefs.edit()
            .putString(KEY_THEME_MODE, mode)
            .apply()

        _themeMode.value = mode
    }
}
