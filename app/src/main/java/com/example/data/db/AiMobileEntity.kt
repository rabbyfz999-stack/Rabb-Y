package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_mobile_items")
data class AiMobileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val prompt: String,
    val modelUsed: String,
    val category: String, // "Chat", "Document", "Vision", "Code", "Note"
    val tags: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val latencyMs: Long = 0L,
    val imageUri: String? = null
)
