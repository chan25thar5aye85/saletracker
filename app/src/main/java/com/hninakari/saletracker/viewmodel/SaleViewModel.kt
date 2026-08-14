package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.Sale
import com.hninakari.saletracker.data.repository.SaleRepository
import com.hninakari.saletracker.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SaleViewModel(private val repository: SaleRepository) : ViewModel() {
    
    private val _selectedFilter = MutableStateFlow(DateUtils.DateFilter.TODAY)
    val selectedFilter: StateFlow<DateUtils.DateFilter> = _selectedFilter.asStateFlow()
    
    // Sales filtered by selected date range
    val filteredSales: Flow<List<Sale>> = repository.getSalesByDateRange(
        DateUtils.getFilterStartTime(_selectedFilter.value)
    )
    
    val allSales: Flow<List<Sale>> = repository.getAllSales()
    
    fun setFilter(filter: DateUtils.DateFilter) {
        _selectedFilter.value = filter
    }
    
    fun addSale(sale: Sale) {
        viewModelScope.launch {
            repository.addSale(sale)
        }
    }
    
    fun deleteSale(saleId: Int) {
        viewModelScope.launch {
            repository.deleteSale(saleId)
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
