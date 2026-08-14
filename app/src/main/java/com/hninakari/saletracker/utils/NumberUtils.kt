package com.hninakari.saletracker.utils

object NumberUtils {
    
    // Myanmar to English digit mapping
    private val myanmarDigits = mapOf(
        '၀' to '0',
        '၁' to '1',
        '၂' to '2',
        '၃' to '3',
        '၄' to '4',
        '၅' to '5',
        '၆' to '6',
        '၇' to '7',
        '၈' to '8',
        '၉' to '9'
    )
    
    // Convert Myanmar digits to English digits
    fun toEnglishDigits(input: String): String {
        return input.map { char ->
            myanmarDigits[char] ?: char
        }.joinToString("")
    }
    
    // Check if string contains only valid number characters
    fun isValidNumber(input: String): Boolean {
        val clean = toEnglishDigits(input)
        return clean.matches(Regex("^[0-9]*\\.?[0-9]*$"))
    }
    
    // Convert to Double safely
    fun toDouble(input: String): Double? {
        val clean = toEnglishDigits(input)
        return clean.toDoubleOrNull()
    }
}
