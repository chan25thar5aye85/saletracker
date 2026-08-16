package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.Tag
import com.hninakari.saletracker.data.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TagViewModel(private val repository: TagRepository) : ViewModel() {
    
    val allTags: Flow<List<Tag>> = repository.getAllTags()
    
    fun addTag(name: String, color: String? = null) {
        viewModelScope.launch {
            repository.addTag(name, color)
        }
    }
    
    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            repository.updateTag(tag)
        }
    }
    
    fun deleteTag(tagId: Int) {
        viewModelScope.launch {
            repository.deleteTag(tagId)
        }
    }
    
    fun restoreTag(tagId: Int) {
        viewModelScope.launch {
            repository.restoreTag(tagId)
        }
    }
    
    suspend fun getTagById(tagId: Int): Tag? {
        return repository.getTagById(tagId)
    }
    
    fun searchTags(query: String): Flow<List<Tag>> {
        return repository.searchTags(query)
    }
}

class TagViewModelFactory(
    private val repository: TagRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagViewModel::class.java)) {
            return TagViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
