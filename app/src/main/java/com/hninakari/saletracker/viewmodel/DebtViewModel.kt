package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.Debt
import com.hninakari.saletracker.data.model.DebtPayment
import com.hninakari.saletracker.data.model.DebtType
import com.hninakari.saletracker.data.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DebtViewModel(private val repository: DebtRepository) : ViewModel() {
    
    val allDebts = repository.getAllDebts()
    val activeDebts = repository.getActiveDebts()
    
    fun getDebtsForPerson(personId: Int): Flow<List<Debt>> {
        return repository.getDebtsByPerson(personId)
    }
    
    fun getActiveDebtsForPerson(personId: Int): Flow<List<Debt>> {
        return repository.getActiveDebtsByPerson(personId)
    }
    
    fun getPaidDebtsForPerson(personId: Int): Flow<List<Debt>> {
        return repository.getPaidDebtsByPerson(personId)
    }
    
    fun getPaymentsForDebt(debtId: Int): Flow<List<DebtPayment>> {
        return repository.getPaymentsForDebt(debtId)
    }
    
    fun addDebt(personId: Int, type: DebtType, amount: Double, note: String = "") {
        viewModelScope.launch {
            val debt = Debt(
                personId = personId,
                type = type,
                amount = amount,
                originalAmount = amount,  // Store original amount
                note = note,
                isPaid = false
            )
            repository.addDebt(debt)
        }
    }
    
    fun makePayment(debtId: Int, paymentAmount: Double, note: String = "") {
        viewModelScope.launch {
            val debts = allDebts.first()
            val debt = debts.find { it.id == debtId }
            
            if (debt != null) {
                // Add payment record
                val payment = DebtPayment(
                    debtId = debtId,
                    amount = paymentAmount,
                    note = note
                )
                repository.addPayment(payment)
                
                // Update debt amount (remaining balance)
                val newAmount = debt.amount - paymentAmount
                if (newAmount > 0) {
                    val updatedDebt = debt.copy(amount = newAmount)
                    repository.updateDebt(updatedDebt)
                } else {
                    // Fully paid - mark as paid, keep originalAmount
                    repository.markDebtAsPaid(debtId)
                }
            }
        }
    }
    
    fun payAllDebt(debtId: Int) {
        viewModelScope.launch {
            val debts = allDebts.first()
            val debt = debts.find { it.id == debtId }
            
            if (debt != null && debt.amount > 0) {
                // Add payment record for full amount
                val payment = DebtPayment(
                    debtId = debtId,
                    amount = debt.amount,
                    note = "Paid in full"
                )
                repository.addPayment(payment)
                
                // Mark as paid (amount stays as remaining, but we use originalAmount for display)
                repository.markDebtAsPaid(debtId)
            }
        }
    }
    
    suspend fun getTotalPaid(debtId: Int): Double {
        return repository.getTotalPaidForDebt(debtId)
    }
}

class DebtViewModelFactory(
    private val repository: DebtRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebtViewModel::class.java)) {
            return DebtViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
