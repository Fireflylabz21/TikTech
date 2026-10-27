package com.deepindex.ai.data.repository

import com.deepindex.ai.data.db.ArticleDao
import com.deepindex.ai.data.db.ArticleEntity
import com.deepindex.ai.data.remote.NeonApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleRepository(private val articleDao: ArticleDao, private val context: android.content.Context? = null) {
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
            var success = false
            try {
                val dtos = NeonApiService.instance.getArticles()
                val entities = dtos.map { it.toEntity() }
                if (entities.isNotEmpty()) {
                    articleDao.insertArticles(entities)
                    success = true
                }
            } catch (e: Exception) {
                e.printStackTrace() // Fallback to local cache on offline/network error
            }

            // Fallback to local asset feed_latest.json if network failed
            if (!success && context != null) {
                try {
                    val inputStream = context.assets.open("feed_latest.json")
                    val reader = java.io.InputStreamReader(inputStream)
                    val type = object : com.google.gson.reflect.TypeToken<List<com.deepindex.ai.data.remote.ArticleDto>>() {}.type
                    val dtos: List<com.deepindex.ai.data.remote.ArticleDto> = com.google.gson.Gson().fromJson(reader, type)
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
}
