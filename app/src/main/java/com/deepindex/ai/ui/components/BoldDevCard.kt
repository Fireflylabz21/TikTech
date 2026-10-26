package com.deepindex.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepindex.ai.data.db.ArticleEntity
import com.deepindex.ai.ui.theme.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun BoldDevCard(
    article: ArticleEntity,
    onCardClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
    onCopyCode: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Summary, 1: Takeaways, 2: Kernel

    val takeaways: List<String> = remember(article.takeawaysJson) {
        try {
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(article.takeawaysJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    val depthColor = when {
        article.depthLevel.contains("L3") -> BrandRose
        article.depthLevel.contains("L2") -> BrandYellow
        else -> BrandCyan
    }

    val depthTextColor = if (article.depthLevel.contains("L2")) Slate950 else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .border(2.dp, Slate800, RoundedCornerShape(24.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.sourceName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = BrandCyan
                )

                Box(
                    modifier = Modifier
                        .background(depthColor, RoundedCornerShape(10.dp))
                        .border(2.dp, Slate950, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = article.depthLevel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = depthTextColor
                    )
                }
            }

            // Headline
            Text(
                text = article.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                lineHeight = 22.sp
            )

            // In-Card Segmented Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate950, RoundedCornerShape(16.dp))
                    .border(2.dp, Slate800, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SegmentButton(
                    text = "Summary",
                    selected = selectedTab == 0,
                    modifier = Modifier.weight(1f)
                ) { selectedTab = 0 }

                SegmentButton(
                    text = "Takeaways",
                    selected = selectedTab == 1,
                    modifier = Modifier.weight(1f)
                ) { selectedTab = 1 }

                if (!article.codeSnippet.isNullOrBlank()) {
                    SegmentButton(
                        text = "Kernel",
                        selected = selectedTab == 2,
                        modifier = Modifier.weight(1f)
                    ) { selectedTab = 2 }
                }
            }

            // Tab Content Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate950, RoundedCornerShape(16.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        Text(
                            text = article.tldr,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate300,
                            lineHeight = 18.sp
                        )
                    }
                    1 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            takeaways.forEach { takeaway ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "▶", color = BrandYellow, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    Text(
                                        text = takeaway,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Slate300,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PYTHON / CUDA KERNEL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Slate300
                                )
                                TextButton(
                                    onClick = { article.codeSnippet?.let { onCopyCode(it) } },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BrandYellow, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "COPY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandYellow)
                                }
                            }
                            Text(
                                text = article.codeSnippet ?: "",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Slate100,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Metric Highlight Pill
            if (!article.metricLabel.isNullOrBlank() && !article.metricValue.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandBlue.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .border(2.dp, BrandBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.metricLabel.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Slate300
                    )
                    Text(
                        text = article.metricValue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = BrandCyan
                    )
                }
            }

            // Actions Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.clickable { onBookmarkClick() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (article.isBookmarked) BrandYellow else Slate300,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (article.isBookmarked) "Saved" else "Save",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (article.isBookmarked) BrandYellow else Slate300
                        )
                    }

                    Row(
                        modifier = Modifier.clickable { onShareClick() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Slate300, modifier = Modifier.size(16.dp))
                        Text(text = "Share", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Slate300)
                    }
                }

                TextButton(onClick = onCardClick, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = "Full View →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = BrandYellow
                    )
                }
            }
        }
    }
}

@Composable
fun SegmentButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) BrandBlue else Color.Transparent
    val textColor = if (selected) Color.White else Slate300
    val shadowModifier = if (selected) Modifier.shadow(2.dp, RoundedCornerShape(12.dp)) else Modifier

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .then(shadowModifier)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            color = textColor
        )
    }
}
