package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.data.model.PersonType
import com.hninakari.saletracker.data.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PersonViewModel(private val repository: PersonRepository) : ViewModel() {
    
    val allPeople = repository.getAllPeople()
    
    fun addPerson(name: String, phone: String = "", type: PersonType = PersonType.OTHER, notes: String = "") {
        viewModelScope.launch {
            val person = Person(
                name = name,
                phone = phone,
                type = type,
                notes = notes
            )
            repository.addPerson(person)
        }
    }
    
    suspend fun getPersonById(personId: Int): Person? {
        return repository.getPersonById(personId)
    }
}

class PersonViewModelFactory(
    private val repository: PersonRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonViewModel::class.java)) {
            return PersonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
