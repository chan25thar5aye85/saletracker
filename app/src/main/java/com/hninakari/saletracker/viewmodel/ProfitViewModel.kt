package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.repository.ExpenseRepository
import com.hninakari.saletracker.data.repository.SaleRepository
import com.hninakari.saletracker.data.repository.TransferRepository
import com.hninakari.saletracker.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfitViewModel(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository,
    private val transferRepository: TransferRepository
) : ViewModel() {
    
    private val _todayProfit = MutableStateFlow(0.0)
    val todayProfit: StateFlow<Double> = _todayProfit.asStateFlow()
    
    private val _totalProfit = MutableStateFlow(0.0)
    val totalProfit: StateFlow<Double> = _totalProfit.asStateFlow()
    
    init {
        calculateProfits()
    }
    
    fun calculateProfits() {
        viewModelScope.launch {
            val startOfDay = DateUtils.getStartOfDay(java.util.Date())
            
            // Get today's sales
            val todaySales = saleRepository.getTotalByDateRange(startOfDay)
            
            // Get today's business expenses
            val todayExpenses = expenseRepository.getTotalByDateRange(startOfDay)
            
            // Get today's transfer fees
            val todayTransferFees = transferRepository.getTotalFeesByDateRange(startOfDay)
            
            // Calculate profit: Sales - Expenses + Transfer Fees
            _todayProfit.value = todaySales - todayExpenses + todayTransferFees
            
            // For total profit (all time)
            val allSales = saleRepository.getTotalByDateRange(0)
            val allExpenses = expenseRepository.getTotalByDateRange(0)
            val allTransferFees = transferRepository.getTotalFeesByDateRange(0)
            _totalProfit.value = allSales - allExpenses + allTransferFees
        }
    }
}

class ProfitViewModelFactory(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository,
    private val transferRepository: TransferRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfitViewModel::class.java)) {
            return ProfitViewModel(saleRepository, expenseRepository, transferRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
