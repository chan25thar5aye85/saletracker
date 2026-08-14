package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.Expense
import com.hninakari.saletracker.data.model.ExpenseType
import com.hninakari.saletracker.data.repository.ExpenseRepository
import com.hninakari.saletracker.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    
    private val _selectedFilter = MutableStateFlow(DateUtils.DateFilter.TODAY)
    val selectedFilter: StateFlow<DateUtils.DateFilter> = _selectedFilter.asStateFlow()
    
    // Expenses filtered by selected date range
    val filteredExpenses: Flow<List<Expense>> = repository.getExpensesByDateRange(
        DateUtils.getFilterStartTime(_selectedFilter.value)
    )
    
    val allExpenses: Flow<List<Expense>> = repository.getAllExpenses()
    
    // Calculate business total from filtered expenses
    val filteredBusinessTotal: Flow<Double> = filteredExpenses.map { expenses ->
        expenses.filter { it.type == ExpenseType.BUSINESS }.sumOf { it.amount }
    }
    
    // Calculate personal total from filtered expenses
    val filteredPersonalTotal: Flow<Double> = filteredExpenses.map { expenses ->
        expenses.filter { it.type == ExpenseType.PERSONAL }.sumOf { it.amount }
    }
    
    fun setFilter(filter: DateUtils.DateFilter) {
        _selectedFilter.value = filter
    }
    
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }
    
    fun deleteExpense(expenseId: Int) {
        viewModelScope.launch {
            repository.deleteExpense(expenseId)
        }
    }
    
    suspend fun getTotalForCurrentFilter(): Double {
        val startDate = DateUtils.getFilterStartTime(_selectedFilter.value)
        return repository.getTotalByDateRange(startDate)
    }
    
    suspend fun getActiveCount(): Int {
        return repository.getActiveCount()
    }
}
