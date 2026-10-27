package com.deepindex.ai.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.deepindex.ai.data.db.ArticleEntity
import com.deepindex.ai.ui.components.BoldDevCard
import com.deepindex.ai.ui.components.DetailModal
import com.deepindex.ai.ui.components.SettingsModal
import com.deepindex.ai.ui.theme.*
import com.deepindex.ai.ui.viewmodel.NewsViewModel

@Composable
fun MainScreen(viewModel: NewsViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val articles by viewModel.articles.collectAsState()
    val modalArticle by viewModel.selectedArticleForModal.collectAsState()
    val toastMsg by viewModel.toastMessage.collectAsState()

    var isSettingsVisible by remember { mutableStateOf(false) }
    var activeMomentDialog by remember { mutableStateOf<String?>(null) } // "WOW" or "AWW" or null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Big & Bold Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate950)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "Tik",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Tech",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = BrandBlue,
                                letterSpacing = (-0.5).sp
                            )
                        }
                    }

                    // Header Action Buttons (Saved & Settings)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Saved / Bookmarked Filter Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .background(if (selectedCategory == "saved") BrandYellow else Slate900, RoundedCornerShape(16.dp))
                                .border(2.dp, Slate950, RoundedCornerShape(16.dp))
                                .clickable { viewModel.selectCategory("saved") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Saved Articles",
                                tint = if (selectedCategory == "saved") Slate950 else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Settings Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .background(BrandBlue, RoundedCornerShape(16.dp))
                                .border(2.dp, Slate950, RoundedCornerShape(16.dp))
                                .clickable { isSettingsVisible = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // 7-Pillar Taxonomy Categories Carousel (Silicon & Infra and Policy & Law moved to the end)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip("🏠 Home", selectedCategory == "home") { viewModel.selectCategory("home") }
                    CategoryChip("🔬 Research", selectedCategory == "research") { viewModel.selectCategory("research") }
                    CategoryChip("⚡ Dev & Tooling", selectedCategory == "dev") { viewModel.selectCategory("dev") }
                    CategoryChip("🏢 Enterprise", selectedCategory == "enterprise") { viewModel.selectCategory("enterprise") }
                    CategoryChip("📈 Biz & Capital", selectedCategory == "business") { viewModel.selectCategory("business") }
                    CategoryChip("📊 Benchmarks", selectedCategory == "benchmarks") { viewModel.selectCategory("benchmarks") }
                    CategoryChip("💻 Silicon & Infra", selectedCategory == "silicon") { viewModel.selectCategory("silicon") }
                    CategoryChip("⚖️ Policy & Law", selectedCategory == "policy") { viewModel.selectCategory("policy") }
                }
            }

            // Main Feed Area
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Slate950),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedCategory == "business" && searchQuery.isBlank()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(20.dp))
                                .border(2.dp, Slate950, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "💡 FINANCIAL & CAPITAL HUB TIPS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = BrandYellow
                                        )
                                        Text(
                                            text = "DEV ROI",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = BrandCyan
                                        )
                                    }
                                    Text(
                                        text = "• GPU Cloud Arbitrage: Leverage spot instances & reserved multi-node clusters to cut training inference costs by 65%.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        lineHeight = 18.sp
                                    )
                                    Text(
                                        text = "• Vector Cache Economics: Aggressively cache query embeddings to eliminate redundant LLM API gateway token burn.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate100,
                                        lineHeight = 18.sp
                                    )
                                    Text(
                                        text = "• Token-Tiered Pricing: Structure developer API monetization tiers around context windows and GPU memory footprints.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate100,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedCategory == "home" && searchQuery.isBlank()) {
                    // Hero Briefing Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(24.dp))
                                .border(2.dp, Slate950, RoundedCornerShape(24.dp))
                                .clickable {
                                    if (articles.isNotEmpty()) viewModel.openModal(articles.first())
                                },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(BrandBlueDark, BrandBlue)
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⚡ DAILY ECOSYSTEM BRIEFING",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = BrandYellow
                                        )
                                        Text(
                                            text = "2026",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White
                                        )
                                    }
                                    Text(
                                        text = "AI ECOSYSTEM INTELLIGENCE: 7-PILLAR TAXONOMY & MULTI-AGENT SYNC",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        lineHeight = 22.sp
                                    )
                                    Text(
                                        text = "Synthesizing research pre-prints, silicon scaling, open weights, and regulatory compliance.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate100,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    // Redesigned AI Tech News Banner as Horizontal Scroll Ticker
                    item {
                        val platformNewsList = listOf(
                            Triple("CLAUDE 3.7 SONNET", "Anthropic releases Claude 3.7 Sonnet featuring hybrid instant & extended step-by-step thinking reasoning mode.", BrandCyan),
                            Triple("OPENAI GPT-4.5 & O3", "OpenAI launches GPT-4.5 & O3 mini with 94.2% AIME math accuracy and zero-shot code synthesis.", BrandYellow),
                            Triple("DEEPSEEK-R1", "DeepSeek-R1 open weights match proprietary frontier LLMs at 90% lower inference cost.", Color(0xFF10B981)),
                            Triple("GEMINI 2.5 PRO", "Google expands Gemini 2.5 Pro context window to 4M tokens with sub-100ms first token latency.", BrandRose),
                            Triple("META LLAMA 4", "Meta AI previews Llama 4 multi-modal agentic architecture across vision and CUDA kernel generation.", BrandCyan)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(20.dp))
                                .border(2.dp, Slate800, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate900)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📰 AI TECH NEWS",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = BrandCyan
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(BrandCyan.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "SWIPE →",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace,
                                                color = BrandCyan
                                            )
                                        }
                                    }
                                    Text(
                                        text = "LIVE PLATFORM LAUNCHES",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Slate300
                                    )
                                }

                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(platformNewsList) { (provider, summary, accentColor) ->
                                        Box(
                                            modifier = Modifier
                                                .width(260.dp)
                                                .background(Slate950, RoundedCornerShape(16.dp))
                                                .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                                                .padding(14.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = provider,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Black,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = accentColor
                                                    )
                                                    Text(
                                                        text = "NEW",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = BrandYellow
                                                    )
                                                }
                                                Text(
                                                    text = summary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Slate100,
                                                    lineHeight = 16.sp,
                                                    maxLines = 3,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Redesigned Wow Moment & Aww Moment as Concise Summaries with Click Pop-up
                    item {
                        val wowText = articles.firstOrNull { !it.wowMoment.isNullOrBlank() }?.wowMoment
                            ?: "Claude 3.7 Sonnet & DeepSeek-R1 achieve 92.4% SWE-bench accuracy, matching domain experts via hybrid instant/extended reasoning!"
                        val awwText = articles.firstOrNull { !it.awwMoment.isNullOrBlank() }?.awwMoment
                            ?: "Autonomous AI dev agent hallucinated production DB migration parameters and recursively dropped live customer schema during automated pipeline release!"

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Wow Moment Summary Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .shadow(6.dp, RoundedCornerShape(20.dp))
                                    .border(2.dp, Color(0xFF10B981), RoundedCornerShape(20.dp))
                                    .clickable { activeMomentDialog = "WOW" },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate900)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🚀 WOW MOMENT",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF10B981)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Info",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = wowText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        lineHeight = 16.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Tap for Pop-up Details →",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }

                            // Aww Moment Summary Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .shadow(6.dp, RoundedCornerShape(20.dp))
                                    .border(2.dp, BrandRose, RoundedCornerShape(20.dp))
                                    .clickable { activeMomentDialog = "AWW" },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate900)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "💔 AWW MOMENT",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = BrandRose
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Warning",
                                            tint = BrandRose,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = awwText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        lineHeight = 16.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Tap for Pop-up Details →",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = BrandRose
                                    )
                                }
                            }
                        }
                    }
                }

                if (articles.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.SentimentDissatisfied, contentDescription = "Empty", tint = BrandYellow, modifier = Modifier.size(48.dp))
                            Text(
                                text = "NO STORIES FOUND",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Slate300
                            )
                            Button(
                                onClick = { viewModel.setSearchQuery(""); viewModel.selectCategory("home") },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                            ) {
                                Text(text = "Reset Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                } else {
                    val displayList = if (selectedCategory == "home" && searchQuery.isBlank()) {
                        articles.take(2)
                    } else {
                        articles
                    }

                    items(displayList, key = { it.id }) { article ->
                        BoldDevCard(
                            article = article,
                            onCardClick = { viewModel.openModal(article) },
                            onBookmarkClick = { viewModel.toggleBookmark(article) },
                            onShareClick = {
                                val md = "**${article.title}** (${article.sourceName})\n• ${article.tldr}"
                                copyToClipboard(context, md)
                                viewModel.showToast("Copied Markdown for Slack!")
                            },
                            onCopyCode = { code ->
                                copyToClipboard(context, code)
                                viewModel.showToast("Code copied!")
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Toast Notification Overlay
        if (toastMsg != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(BrandYellow, RoundedCornerShape(16.dp))
                    .border(2.dp, Slate950, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Slate950, modifier = Modifier.size(18.dp))
                    Text(
                        text = toastMsg!!.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Slate950
                    )
                }
            }
        }

        // Pop-up Dialog for Wow & Aww Moment Details
        if (activeMomentDialog != null) {
            val isWow = activeMomentDialog == "WOW"
            val wowText = articles.firstOrNull { !it.wowMoment.isNullOrBlank() }?.wowMoment
                ?: "Claude 3.7 Sonnet & DeepSeek-R1 achieve 92.4% SWE-bench accuracy, matching domain experts via hybrid instant/extended reasoning!"
            val awwText = articles.firstOrNull { !it.awwMoment.isNullOrBlank() }?.awwMoment
                ?: "Autonomous AI dev agent hallucinated production DB migration parameters and recursively dropped live customer schema during automated pipeline release!"

            val title = if (isWow) "🚀 WOW MOMENT: FRONTIER AI ADVANCEMENT" else "💔 AWW MOMENT: AGENT FAILURE & HALLUCINATION"
            val accentColor = if (isWow) Color(0xFF10B981) else BrandRose
            val contentText = if (isWow) wowText else awwText
            val categoryLabel = if (isWow) "ADVANCEMENTS • FRONTIER LLMS • ACCURACY LEAPS" else "AGENT FAILURES • HALLUCINATIONS • HACKS"

            AlertDialog(
                onDismissRequest = { activeMomentDialog = null },
                containerColor = Slate900,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.border(2.dp, accentColor, RoundedCornerShape(24.dp)),
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = categoryLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = accentColor
                            )
                        }
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate950, RoundedCornerShape(16.dp))
                                .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = contentText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate100,
                                lineHeight = 20.sp
                            )
                        }

                        if (isWow) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF10B981).copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("BENCHMARK SCORE", fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Slate300)
                                Text("92.4% SWE-BENCH", fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color(0xFF10B981))
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BrandRose.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("IMPACT SEVERITY", fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Slate300)
                                Text("HIGH RISK / OUTAGE", fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = BrandRose)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { activeMomentDialog = null },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Close",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            )
        }

        // Detail Modal Bottom Sheet
        if (modalArticle != null) {
            DetailModal(
                article = modalArticle!!,
                onDismiss = { viewModel.closeModal() },
                onBookmarkToggle = { viewModel.toggleBookmark(modalArticle!!) },
                onShare = {
                    val md = "**${modalArticle!!.title}** (${modalArticle!!.sourceName})\n• ${modalArticle!!.tldr}"
                    copyToClipboard(context, md)
                    viewModel.showToast("Copied Markdown for Slack!")
                },
                onCopyCode = { code ->
                    copyToClipboard(context, code)
                    viewModel.showToast("Code copied!")
                }
            )
        }

        // Settings Modal Bottom Sheet
        if (isSettingsVisible) {
            SettingsModal(
                onDismiss = { isSettingsVisible = false },
                onOpenUrl = { url ->
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) BrandBlue else Slate900
    val borderColor = if (selected) Slate950 else Slate800
    val textColor = if (selected) Color.White else Slate300
    val shadowModifier = if (selected) Modifier.shadow(2.dp, RoundedCornerShape(12.dp)) else Modifier

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .then(shadowModifier)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = textColor
        )
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("DeepIndex AI", text)
    clipboard?.setPrimaryClip(clip)
}
