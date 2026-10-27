package com.deepindex.ai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deepindex.ai.TikTechApplication
import com.deepindex.ai.data.db.ArticleEntity
import com.deepindex.ai.data.repository.ArticleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ArticleRepository

    init {
        val articleDao = (application as TikTechApplication).database.articleDao()
        repository = ArticleRepository(articleDao, application)
        
        // Sync live data from Neon DB on app startup
        viewModelScope.launch {
            repository.refreshArticlesFromNeon()
        }
    }

    private val _selectedCategory = MutableStateFlow("home")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedArticleForModal = MutableStateFlow<ArticleEntity?>(null)
    val selectedArticleForModal: StateFlow<ArticleEntity?> = _selectedArticleForModal.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val articles: StateFlow<List<ArticleEntity>> = combine(
        _selectedCategory,
        _searchQuery,
        repository.allArticles
    ) { category, query, all ->
        when {
            query.isNotBlank() -> {
                all.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.tldr.contains(query, ignoreCase = true) 
                }
            }
            category == "saved" -> {
                all.filter { it.isBookmarked }
            }
            category == "home" || category == "all" -> {
                all
            }
            else -> {
                all.filter { it.category.equals(category, ignoreCase = true) }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openModal(article: ArticleEntity) {
        _selectedArticleForModal.value = article
    }

    fun closeModal() {
        _selectedArticleForModal.value = null
    }

    fun toggleBookmark(article: ArticleEntity) {
        viewModelScope.launch {
            val newState = !article.isBookmarked
            repository.updateBookmark(article.id, newState)
            showToast(if (newState) "Saved to queue" else "Removed from queue")
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            if (_toastMessage.value == message) {
                _toastMessage.value = null
            }
        }
    }
}
