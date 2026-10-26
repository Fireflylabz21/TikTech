package com.deepindex.ai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.deepindex.ai.ui.screens.MainScreen
import com.deepindex.ai.ui.theme.Slate950
import com.deepindex.ai.ui.theme.TikTechTheme
import com.deepindex.ai.ui.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TikTechTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Slate950
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
