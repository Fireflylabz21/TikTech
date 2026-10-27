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
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepindex.ai.data.db.ArticleEntity
import com.deepindex.ai.ui.components.BoldDevCard
import com.deepindex.ai.ui.components.DetailModal
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

    var isSearchVisible by remember { mutableStateOf(false) }

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

                    // Header Action Buttons (Saved & Search)
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

                        // Search Toggle Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .background(BrandBlue, RoundedCornerShape(16.dp))
                                .border(2.dp, Slate950, RoundedCornerShape(16.dp))
                                .clickable { isSearchVisible = !isSearchVisible },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Expandable Search Bar
                AnimatedVisibility(visible = isSearchVisible) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        placeholder = { Text("Search paper, PyTorch, vLLM, CUDA...", color = Slate700, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900,
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Slate800,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate300)
                            }
                        }
                    )
                }

                // 7-Pillar Taxonomy Categories Carousel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip("🏠 Home", selectedCategory == "home") { viewModel.selectCategory("home") }
                    CategoryChip("🔬 Research", selectedCategory == "research") { viewModel.selectCategory("research") }
                    CategoryChip("⚡ Dev & Tooling", selectedCategory == "dev") { viewModel.selectCategory("dev") }
                    CategoryChip("💻 Silicon & Infra", selectedCategory == "silicon") { viewModel.selectCategory("silicon") }
                    CategoryChip("🏢 Enterprise", selectedCategory == "enterprise") { viewModel.selectCategory("enterprise") }
                    CategoryChip("⚖️ Policy & Law", selectedCategory == "policy") { viewModel.selectCategory("policy") }
                    CategoryChip("📈 Biz & Capital", selectedCategory == "business") { viewModel.selectCategory("business") }
                    CategoryChip("📊 Benchmarks", selectedCategory == "benchmarks") { viewModel.selectCategory("benchmarks") }
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

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Code, contentDescription = "Code", tint = BrandCyan, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "LIVE INTELLIGENCE STREAM",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Slate300
                                )
                            }
                            TextButton(onClick = { viewModel.selectCategory("research") }) {
                                Text(
                                    text = "View All →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = BrandBlue
                                )
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
