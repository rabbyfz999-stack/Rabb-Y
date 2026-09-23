package com.example.data.model

data class PlayStoreApp(
    val id: String,
    val name: String,
    val developer: String,
    val category: String, // "AI & Tools", "Productivity", "Graphics & Photo", "System", "Games"
    val rating: Float,
    val reviewsCount: String,
    val downloadCount: String,
    val sizeMb: Int,
    val iconEmoji: String,
    val iconBgColor: Long,
    val description: String,
    val tags: List<String>,
    val isInstalled: Boolean = false,
    val downloadProgress: Float? = null, // null = idle, 0..1 = downloading
    val version: String = "v2.5.0",
    val ramRequirementMb: Int = 220,
    val whatIsNew: String = "Improved neural processing engine, dark mode polish, and stability enhancements.",
    val isVerifiedPlayProtect: Boolean = true
)
