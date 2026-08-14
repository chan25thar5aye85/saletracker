package com.hninakari.saletracker.utils

import java.util.*

object DateUtils {
    
    enum class DateFilter {
        TODAY,
        THIS_WEEK,
        THIS_MONTH,
        THIS_YEAR,
        ALL_TIME
    }
    
    fun getStartOfDay(date: Date): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun getEndOfDay(date: Date): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    fun getStartOfWeek(): Long {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun getStartOfYear(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun getFilterStartTime(filter: DateFilter): Long {
        return when (filter) {
            DateFilter.TODAY -> getStartOfDay(Date())
            DateFilter.THIS_WEEK -> getStartOfWeek()
            DateFilter.THIS_MONTH -> getStartOfMonth()
            DateFilter.THIS_YEAR -> getStartOfYear()
            DateFilter.ALL_TIME -> 0
        }
    }
    
    fun formatDateRange(filter: DateFilter): String {
        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return when (filter) {
            DateFilter.TODAY -> "Today"
            DateFilter.THIS_WEEK -> "This Week"
            DateFilter.THIS_MONTH -> "This Month"
            DateFilter.THIS_YEAR -> "This Year"
            DateFilter.ALL_TIME -> "All Time"
        }
    }
}
