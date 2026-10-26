package com.deepindex.ai.data.remote

import com.deepindex.ai.BuildConfig
import com.deepindex.ai.data.db.ArticleEntity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NeonApiService {
    @GET("feed_latest.json")
    suspend fun getArticles(
        @Query("category") category: String? = null
    ): List<ArticleDto>

    companion object {
        val instance: NeonApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NeonApiService::class.java)
        }
    }
}

data class ArticleDto(
    val id: String,
    val title: String,
    val source_name: String,
    val source_url: String,
    val category: String,
    val depth_level: String,
    val tldr: String,
    val takeaways: List<String>,
    val code_snippet: String?,
    val full_analysis: String?,
    val github_repo_name: String?,
    val github_repo_url: String?,
    val github_stars: String?,
    val metric_label: String?,
    val metric_value: String?,
    val published_at: String
) {
    fun toEntity(): ArticleEntity {
        return ArticleEntity(
            id = id,
            title = title,
            sourceName = source_name,
            sourceUrl = source_url,
            category = category,
            depthLevel = depth_level,
            tldr = tldr,
            takeawaysJson = com.google.gson.Gson().toJson(takeaways),
            codeSnippet = code_snippet,
            fullAnalysis = full_analysis,
            githubRepoName = github_repo_name,
            githubRepoUrl = github_repo_url,
            githubStars = github_stars,
            metricLabel = metric_label,
            metricValue = metric_value,
            publishedAt = published_at,
            isBookmarked = false
        )
    }
}
