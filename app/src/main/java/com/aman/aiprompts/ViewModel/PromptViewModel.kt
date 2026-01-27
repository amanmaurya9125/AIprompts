package com.aman.aiprompts.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.aiprompts.Repository.PromptRepository
import com.aman.aiprompts.RoomDatabase.PromptEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PromptViewModel(
    private val repository: PromptRepository
) : ViewModel() {
    private var copycount = 0

    private val _showAd = MutableStateFlow(false)
    val showAd = _showAd.asStateFlow()
    private val _selectedCategory = MutableStateFlow("New")
    val selectedCategory = _selectedCategory.asStateFlow()
    val prompts: StateFlow<List<PromptEntity>> =
        repository.getAllPrompts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val favoritePrompts: StateFlow<List<PromptEntity>> =
        repository.getFavoritePrompts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.preloadPromptsIfNeeded()
        }
    }

    val categoryPrompts: StateFlow<List<PromptEntity>> =
        _selectedCategory
            .flatMapLatest { category ->
                repository.getPromptsByCategory(category)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
    fun getPrompt(id: String): Flow<PromptEntity?> =
        prompts.map { list ->
            list.find { it.id == id }
        }
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            val prompt = repository.getPromptById(id)
            repository.updatePrompt(
                prompt.copy(isFavorite = !prompt.isFavorite)
            )
        }
    }

    fun onCopyClicked(){
        copycount++
        if ( copycount % 2 ==0 ){
            copycount = 0
            _showAd.value = true
        }
    }

    fun onAdShown(){
        _showAd.value = false
    }
}
