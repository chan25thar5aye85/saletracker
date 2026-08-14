package com.hninakari.saletracker.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

object LanguageManager {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    
    // Language codes
    const val MYANMAR = "my"
    const val ENGLISH = "en"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // Default language is now MYANMAR
    fun getLanguage(context: Context): String {
        return getPreferences(context).getString(KEY_LANGUAGE, MYANMAR) ?: MYANMAR
    }
    
    fun setLanguage(context: Context, languageCode: String) {
        getPreferences(context).edit().putString(KEY_LANGUAGE, languageCode).apply()
        setLocale(context, languageCode)
    }
    
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
    
    fun getCurrentLanguageDisplay(languageCode: String): String {
        return when (languageCode) {
            MYANMAR -> "🇲🇲 မြန်မာ"
            ENGLISH -> "🇬🇧 English"
            else -> "🇲🇲 မြန်မာ"
        }
    }
}
