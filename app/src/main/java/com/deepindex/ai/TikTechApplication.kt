package com.deepindex.ai

import android.app.Application
import com.deepindex.ai.data.db.AppDatabase

class TikTechApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}
