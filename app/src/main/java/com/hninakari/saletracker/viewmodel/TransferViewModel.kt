package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.Transfer
import com.hninakari.saletracker.data.repository.TransferRepository
import com.hninakari.saletracker.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransferViewModel(private val repository: TransferRepository) : ViewModel() {
    
    private val _selectedFilter = MutableStateFlow(DateUtils.DateFilter.TODAY)
    val selectedFilter: StateFlow<DateUtils.DateFilter> = _selectedFilter.asStateFlow()
    
    val filteredTransfers: Flow<List<Transfer>> = repository.getTransfersByDateRange(
        DateUtils.getFilterStartTime(_selectedFilter.value)
    )
    
    fun setFilter(filter: DateUtils.DateFilter) {
        _selectedFilter.value = filter
    }
    
    fun addTransfer(transfer: Transfer) {
        viewModelScope.launch {
            repository.addTransfer(transfer)
        }
    }
    
    fun deleteTransfer(transferId: Int) {
        viewModelScope.launch {
            repository.deleteTransfer(transferId)
        }
    }
    
    suspend fun getTotalFeesForCurrentFilter(): Double {
        val startDate = DateUtils.getFilterStartTime(_selectedFilter.value)
        return repository.getTotalFeesByDateRange(startDate)
    }
}

class TransferViewModelFactory(
    private val repository: TransferRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransferViewModel::class.java)) {
            return TransferViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
