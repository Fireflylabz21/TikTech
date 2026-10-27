package com.fireflylabz.tiktech.data.repository

import com.fireflylabz.tiktech.data.db.ArticleDao
import com.fireflylabz.tiktech.data.db.ArticleEntity
import com.fireflylabz.tiktech.data.remote.NeonApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleRepository(private val articleDao: ArticleDao) {
    val allArticles: Flow<List<ArticleEntity>> = articleDao.getAllArticles()

    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>> {
        return articleDao.getArticlesByCategory(category)
    }

    fun searchArticles(query: String): Flow<List<ArticleEntity>> {
        return articleDao.searchArticles(query)
    }

    suspend fun updateBookmark(id: String, isBookmarked: Boolean) {
        articleDao.updateBookmark(id, isBookmarked)
    }

    suspend fun refreshArticlesFromNeon() {
        withContext(Dispatchers.IO) {
            try {
                val dtos = NeonApiService.instance.getArticles()
                val entities = dtos.map { it.toEntity() }
                if (entities.isNotEmpty()) {
                    articleDao.insertArticles(entities)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
