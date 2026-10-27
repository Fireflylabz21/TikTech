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

data class MetricDto(
    val label: String?,
    @com.google.gson.annotations.SerializedName("val")
    val value: String?
)

data class RepoDto(
    val name: String?,
    val url: String?,
    val stars: String?
)

data class ArticleDto(
    val id: String,
    val title: String,
    val source: String,
    val category: String,
    val depth: String,
    val tldr: String,
    val takeaways: List<String>,
    val code: String?,
    val fullAnalysis: String?,
    val metric: MetricDto?,
    val repo: RepoDto?,
    val entity_tags: List<String>?,
    val published_at: String?,
    val ai_tech_news_summary: String?,
    val wow_moment: String?,
    val aww_moment: String?
) {
    fun toEntity(): ArticleEntity {
        val gson = com.google.gson.Gson()
        return ArticleEntity(
            id = id,
            title = title,
            sourceName = source,
            sourceUrl = repo?.url ?: "https://arxiv.org/abs/$id",
            category = category,
            depthLevel = depth,
            tldr = tldr,
            takeawaysJson = gson.toJson(takeaways),
            codeSnippet = code,
            fullAnalysis = fullAnalysis,
            githubRepoName = repo?.name,
            githubRepoUrl = repo?.url,
            githubStars = repo?.stars,
            metricLabel = metric?.label,
            metricValue = metric?.value,
            entityTagsJson = gson.toJson(entity_tags ?: emptyList<String>()),
            publishedAt = published_at ?: "2026-10-27",
            isBookmarked = false,
            aiTechNewsSummary = ai_tech_news_summary,
            wowMoment = wow_moment,
            awwMoment = aww_moment
        )
    }
}
