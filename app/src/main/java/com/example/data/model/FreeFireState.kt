package com.example.data.model

data class FreeFireWeapon(
    val name: String,
    val type: String, // AR, SMG, Sniper
    val damage: Int,
    val fireRateMs: Long,
    val maxClip: Int,
    val currentClip: Int,
    val totalAmmo: Int,
    val iconEmoji: String,
    val keyShortcut: String
)

data class EnemyBot(
    val id: String,
    val name: String,
    val x: Float, // 0..1 map coordinate
    val y: Float,
    var hp: Int = 100,
    val maxHp: Int = 100,
    val isAlive: Boolean = true,
    val isTargeted: Boolean = false,
    val colorHex: Long = 0xFFEF5350
)

data class DamagePopup(
    val id: Long = System.currentTimeMillis(),
    val damage: Int,
    val isHeadshot: Boolean,
    val x: Float,
    val y: Float
)

data class FreeFireGameState(
    val isMatchActive: Boolean = false,
    val isBooyah: Boolean = false,
    val isGameOver: Boolean = false,
    val playerHp: Int = 200,
    val maxHp: Int = 200,
    val playerArmor: Int = 100,
    val medkitsCount: Int = 3,
    val glooWallsCount: Int = 3,
    val activeGlooWalls: Int = 0,
    val kills: Int = 0,
    val aliveCount: Int = 50,
    val playerX: Float = 0.5f,
    val playerY: Float = 0.7f,
    val isAimingScope: Boolean = false,
    val isCrouching: Boolean = false,
    val isSprinting: Boolean = false,
    val selectedWeaponIndex: Int = 0,
    val weapons: List<FreeFireWeapon> = listOf(
        FreeFireWeapon("AK47", "Assault Rifle", 42, 130L, 30, 30, 150, "💥", "1"),
        FreeFireWeapon("MP40", "Submachine Gun", 24, 70L, 32, 32, 180, "⚡", "2"),
        FreeFireWeapon("AWM", "Sniper Rifle", 150, 900L, 5, 5, 25, "🎯", "3")
    ),
    val enemies: List<EnemyBot> = emptyList(),
    val killFeed: List<String> = emptyList(),
    val showPcKeymap: Boolean = true,
    val lastPcKeyTriggered: String? = null,
    val pcSetupConnected: Boolean = false,
    val damagePopups: List<DamagePopup> = emptyList()
)
