package com.deepindex.ai.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val sourceName: String,
    val sourceUrl: String = "https://github.com",
    val category: String, // research, dev, silicon, enterprise, policy, business, benchmarks
    val depthLevel: String, // L1 OVERVIEW, L2 DEEP DIVE, L3 MATH/KERNEL
    val tldr: String,
    val takeawaysJson: String,
    val codeSnippet: String?,
    val fullAnalysis: String?,
    val githubRepoName: String?,
    val githubRepoUrl: String?,
    val githubStars: String?,
    val metricLabel: String?,
    val metricValue: String?,
    val entityTagsJson: String? = "[]",
    val publishedAt: String,
    var isBookmarked: Boolean = false
)
