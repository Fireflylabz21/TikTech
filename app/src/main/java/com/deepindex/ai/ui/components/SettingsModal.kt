package com.deepindex.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepindex.ai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal(
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Slate900,
        tonalElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Settings Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = BrandCyan, modifier = Modifier.size(20.dp))
                        Text(
                            text = "TIKTECH SETTINGS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(BrandBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "v2.0.0 (2026)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = BrandCyan
                        )
                    }
                }

                // 1. About Us Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate950)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = "About Us", tint = BrandYellow, modifier = Modifier.size(16.dp))
                            Text(
                                text = "ABOUT US",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = BrandYellow
                            )
                        }
                        Text(
                            text = "TikTech is engineered by DeepIndex AI Lab. We build autonomous multi-agent pipelines to synthesize frontier AI research, open-weights releases, and silicon hardware benchmarks into real-time developer intelligence.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate100,
                            lineHeight = 18.sp
                        )
                    }
                }

                // 2. About App Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate950)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Smartphone, contentDescription = "About App", tint = BrandCyan, modifier = Modifier.size(16.dp))
                            Text(
                                text = "ABOUT APP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = BrandCyan
                            )
                        }
                        Text(
                            text = "• 7-Pillar AI Taxonomy Engine\n• Real-time Neon Serverless Postgres Vector Search\n• Knowledge Graph Triplet Extraction\n• Offline Fallback Cache & Room Database Sync",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate100,
                            lineHeight = 20.sp
                        )
                    }
                }

                // 3. Disclaimer Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate950)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Gavel, contentDescription = "Disclaimer", tint = BrandRose, modifier = Modifier.size(16.dp))
                            Text(
                                text = "DISCLAIMER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = BrandRose
                            )
                        }
                        Text(
                            text = "All pre-prints, code snippets, benchmarks, and platform news summaries are aggregated and analyzed for educational, academic, and research purposes. Brand names (Claude, OpenAI, Gemini, DeepSeek, Meta) belong to their respective owners.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate300,
                            lineHeight = 16.sp
                        )
                    }
                }

                // 4. Support Us Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                        .border(2.dp, BrandBlue, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate950)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = "Support Us", tint = BrandRose, modifier = Modifier.size(16.dp))
                            Text(
                                text = "SUPPORT US",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Help us maintain open-source AI infrastructure, scale GPU compute clusters, and keep TikTech ad-free for the developer community!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate100,
                            lineHeight = 18.sp
                        )
                        Button(
                            onClick = { onOpenUrl("https://github.com/Fireflylabz21/TikTech") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Star", tint = BrandYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STAR ON GITHUB ⭐",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Action Bar
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Slate800)
            ) {
                Text(
                    text = "Close Settings",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            }
        }
    }
}
