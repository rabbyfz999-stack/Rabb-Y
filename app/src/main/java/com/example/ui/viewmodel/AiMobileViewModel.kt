package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiExecutionResult
import com.example.data.db.AiMobileDatabase
import com.example.data.db.AiMobileEntity
import com.example.data.model.DeviceSystemSpecs
import com.example.data.model.EnemyBot
import com.example.data.model.FreeFireGameState
import com.example.data.model.FreeFireWeapon
import com.example.data.model.GeminiTaskTier
import com.example.data.model.PlayStoreApp
import com.example.data.repository.AiMobileRepository
import com.example.server.PcKeyboardServer
import com.example.server.PcServerState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "gemini"
    val text: String,
    val modelUsed: String? = null,
    val latencyMs: Long = 0L,
    val isSimulation: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class UiFeedback(
    val message: String
)

class AiMobileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AiMobileRepository
    private val pcServer: PcKeyboardServer
    val pcServerState: StateFlow<PcServerState>

    private val _isPcToolbarEnabled = MutableStateFlow(true)
    val isPcToolbarEnabled: StateFlow<Boolean> = _isPcToolbarEnabled.asStateFlow()

    fun togglePcToolbar() {
        _isPcToolbarEnabled.value = !_isPcToolbarEnabled.value
    }

    // --- AI Mobile Hardware & System Specs (8GB RAM / 128GB Storage) ---
    private val _systemSpecs = MutableStateFlow(DeviceSystemSpecs())
    val systemSpecs: StateFlow<DeviceSystemSpecs> = _systemSpecs.asStateFlow()

    fun boostRam() {
        viewModelScope.launch {
            val current = _systemSpecs.value
            val freed = (0.7 + (Math.random() * 0.7)).coerceAtMost((current.usedRamGb - 2.0).coerceAtLeast(0.1))
            val newUsed = (current.usedRamGb - freed).coerceAtLeast(2.1)
            val roundedUsed = (Math.round(newUsed * 10.0) / 10.0)
            _systemSpecs.value = current.copy(
                usedRamGb = roundedUsed,
                performanceMode = "AI Turbo (8GB Boosted)"
            )
            val freedStr = String.format(java.util.Locale.US, "%.1f", freed)
            _feedback.emit(UiFeedback("🚀 RAM Boosted! ${freedStr} GB freed. 8.0 GB RAM optimized for AI Mobile."))
        }
    }

    fun cleanStorage() {
        viewModelScope.launch {
            val current = _systemSpecs.value
            val cleaned = (1.1 + (Math.random() * 0.9)).coerceAtMost((current.usedStorageGb - 25.0).coerceAtLeast(0.1))
            val newUsed = (current.usedStorageGb - cleaned).coerceAtLeast(32.0)
            val roundedUsed = (Math.round(newUsed * 10.0) / 10.0)
            _systemSpecs.value = current.copy(
                usedStorageGb = roundedUsed
            )
            val cleanStr = String.format(java.util.Locale.US, "%.1f", cleaned)
            _feedback.emit(UiFeedback("🧹 Storage Cleaned! ${cleanStr} GB junk cleared from 128 GB Storage."))
        }
    }

    fun setPerformanceProfile(mode: String) {
        _systemSpecs.value = _systemSpecs.value.copy(performanceMode = mode)
        viewModelScope.launch {
            _feedback.emit(UiFeedback("Performance profile updated: $mode"))
        }
    }

    // --- Google Play Store Ecosystem State ---
    private val _playStoreApps = MutableStateFlow<List<PlayStoreApp>>(
        listOf(
            PlayStoreApp(
                id = "free_fire",
                name = "Garena Free Fire MAX (PC Set)",
                developer = "Garena International I",
                category = "Games",
                rating = 4.9f,
                reviewsCount = "120M reviews",
                downloadCount = "1B+",
                sizeMb = 750,
                iconEmoji = "🔥",
                iconBgColor = 0xFFFF3D00,
                description = "Battle Royale in Bermuda! Ultra-smooth 120 FPS powered by 8GB RAM Turbo. Complete PC Keyboard & Mouse Setup (WASD movement, Space Jump, Left Click Fire, 1/2/3 Weapons).",
                tags = listOf("Battle Royale", "Action", "Shooter", "PC Set"),
                isInstalled = true
            ),
            PlayStoreApp(
                id = "gemini_code",
                name = "Gemini Code Assistant",
                developer = "Google LLC",
                category = "AI & Tools",
                rating = 4.9f,
                reviewsCount = "480K reviews",
                downloadCount = "10M+",
                sizeMb = 62,
                iconEmoji = "⚡",
                iconBgColor = 0xFF7C4DFF,
                description = "Intelligent coding co-pilot with syntax comprehension, bug repair, and multi-language snippets powered by Gemini.",
                tags = listOf("AI", "Code", "Editor"),
                isInstalled = true
            ),
            PlayStoreApp(
                id = "photo_upscaler",
                name = "AI Photo Remaster 4K",
                developer = "Neural Lab Inc.",
                category = "Graphics & Photo",
                rating = 4.8f,
                reviewsCount = "215K reviews",
                downloadCount = "5M+",
                sizeMb = 86,
                iconEmoji = "🎨",
                iconBgColor = 0xFF00E5FF,
                description = "Upscale vintage images, enhance sharpness, remove blemishes, and reconstruct lighting with high-precision neural networks.",
                tags = listOf("Photos", "AI", "Editor"),
                isInstalled = true
            ),
            PlayStoreApp(
                id = "ram_booster",
                name = "RAM 8GB Booster Ultra",
                developer = "AI Mobile Core",
                category = "System",
                rating = 4.9f,
                reviewsCount = "92K reviews",
                downloadCount = "1M+",
                sizeMb = 18,
                iconEmoji = "🚀",
                iconBgColor = 0xFF00E676,
                description = "LPDDR5X memory manager optimized specifically for AI Mobile 8.0 GB RAM architecture. Reclaims inactive caches in milliseconds.",
                tags = listOf("RAM", "System", "Boost"),
                isInstalled = true
            ),
            PlayStoreApp(
                id = "voice_transcribe",
                name = "Neural Voice Transcribe",
                developer = "DeepAudio Systems",
                category = "Productivity",
                rating = 4.7f,
                reviewsCount = "140K reviews",
                downloadCount = "2M+",
                sizeMb = 48,
                iconEmoji = "🎙️",
                iconBgColor = 0xFFFFB300,
                description = "Real-time speech-to-text with automated summarization, speaker diarization, and instant multi-language translation.",
                tags = listOf("Audio", "Transcription", "Productivity"),
                isInstalled = false
            ),
            PlayStoreApp(
                id = "offline_llm",
                name = "Offline LLM Core 2B",
                developer = "LocalAI Research",
                category = "AI & Tools",
                rating = 4.8f,
                reviewsCount = "64K reviews",
                downloadCount = "500K+",
                sizeMb = 380,
                iconEmoji = "🧠",
                iconBgColor = 0xFFFF4081,
                description = "On-device private large language model running fully offline on AI Mobile 8GB RAM without internet access.",
                tags = listOf("Offline", "LLM", "Privacy"),
                isInstalled = false
            ),
            PlayStoreApp(
                id = "cyber_rpg",
                name = "CyberPulse AI 2026",
                developer = "Neon Nebula Games",
                category = "Games",
                rating = 4.6f,
                reviewsCount = "180K reviews",
                downloadCount = "800K+",
                sizeMb = 210,
                iconEmoji = "🎮",
                iconBgColor = 0xFF9C27B0,
                description = "Immersive futuristic RPG with procedural storylines and dynamic character dialogues generated on-the-fly by neural dialogue models.",
                tags = listOf("RPG", "Sci-Fi", "Game"),
                isInstalled = false
            ),
            PlayStoreApp(
                id = "super_terminal",
                name = "SuperTerminal ADB & Shell",
                developer = "Android Dev Labs",
                category = "System",
                rating = 4.9f,
                reviewsCount = "310K reviews",
                downloadCount = "3M+",
                sizeMb = 32,
                iconEmoji = "💻",
                iconBgColor = 0xFF3F51B5,
                description = "Full Linux bash environment with package manager, curl, git, and direct link to AI Mobile PC Keyboard bridge.",
                tags = listOf("Terminal", "Dev", "Shell"),
                isInstalled = false
            ),
            PlayStoreApp(
                id = "cloud_128gb",
                name = "AI Cloud Backup 128GB",
                developer = "Google Cloud Ecosystem",
                category = "Productivity",
                rating = 4.8f,
                reviewsCount = "520K reviews",
                downloadCount = "25M+",
                sizeMb = 42,
                iconEmoji = "☁️",
                iconBgColor = 0xFF0288D1,
                description = "Seamless continuous background synchronization matching the 128.0 GB internal storage of your AI Mobile device.",
                tags = listOf("Cloud", "Backup", "Sync"),
                isInstalled = false
            ),
            PlayStoreApp(
                id = "neural_canvas",
                name = "Neural Diffusion Canvas",
                developer = "Creative AI Foundry",
                category = "Graphics & Photo",
                rating = 4.7f,
                reviewsCount = "95K reviews",
                downloadCount = "1.5M+",
                sizeMb = 95,
                iconEmoji = "✨",
                iconBgColor = 0xFFE91E63,
                description = "Prompt-driven artistic illustration generator with direct export to gallery, layer blending, and custom style embeddings.",
                tags = listOf("Art", "Drawing", "AI"),
                isInstalled = false
            ),
            PlayStoreApp(
                id = "polyglot_translate",
                name = "Polyglot AI Translator",
                developer = "Global NLP Group",
                category = "Productivity",
                rating = 4.9f,
                reviewsCount = "890K reviews",
                downloadCount = "50M+",
                sizeMb = 54,
                iconEmoji = "🌐",
                iconBgColor = 0xFF4CAF50,
                description = "Zero-latency translation across 108 languages with contextual slang detection and natural voice synthesis.",
                tags = listOf("Translate", "Language", "Travel"),
                isInstalled = false
            )
        )
    )
    val playStoreApps: StateFlow<List<PlayStoreApp>> = _playStoreApps.asStateFlow()

    private val _playStoreSearchQuery = MutableStateFlow("")
    val playStoreSearchQuery: StateFlow<String> = _playStoreSearchQuery.asStateFlow()

    private val _playStoreCategory = MutableStateFlow("All")
    val playStoreCategory: StateFlow<String> = _playStoreCategory.asStateFlow()

    private val _isPlayProtectScanning = MutableStateFlow(false)
    val isPlayProtectScanning: StateFlow<Boolean> = _isPlayProtectScanning.asStateFlow()

    private val _playProtectStatus = MutableStateFlow("Play Protect verified: No harmful apps found")
    val playProtectStatus: StateFlow<String> = _playProtectStatus.asStateFlow()

    fun updatePlayStoreSearch(query: String) {
        _playStoreSearchQuery.value = query
    }

    fun selectPlayStoreCategory(cat: String) {
        _playStoreCategory.value = cat
    }

    fun scanPlayProtect() {
        if (_isPlayProtectScanning.value) return
        _isPlayProtectScanning.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1600)
            _isPlayProtectScanning.value = false
            _playProtectStatus.value = "Scanned just now • All apps verified safe by Play Protect"
            _feedback.emit(UiFeedback("🛡️ Play Protect Scan Complete: All apps are verified safe!"))
        }
    }

    fun installApp(appId: String) {
        viewModelScope.launch {
            val app = _playStoreApps.value.find { it.id == appId } ?: return@launch
            if (app.isInstalled || app.downloadProgress != null) return@launch

            // Download simulation
            for (step in 1..8) {
                kotlinx.coroutines.delay(100)
                _playStoreApps.value = _playStoreApps.value.map {
                    if (it.id == appId) it.copy(downloadProgress = step / 8f) else it
                }
            }

            _playStoreApps.value = _playStoreApps.value.map {
                if (it.id == appId) it.copy(isInstalled = true, downloadProgress = null) else it
            }

            // Deduct from 128GB ROM
            val addedGb = app.sizeMb / 1024.0
            val current = _systemSpecs.value
            val newStorage = (current.usedStorageGb + addedGb).coerceAtMost(128.0)
            val roundedStorage = (Math.round(newStorage * 10.0) / 10.0)
            _systemSpecs.value = current.copy(usedStorageGb = roundedStorage)

            _feedback.emit(UiFeedback("✅ ${app.name} installed! ${app.sizeMb} MB allocated on 128GB Storage."))
        }
    }

    fun uninstallApp(appId: String) {
        viewModelScope.launch {
            val app = _playStoreApps.value.find { it.id == appId } ?: return@launch
            if (!app.isInstalled) return@launch

            _playStoreApps.value = _playStoreApps.value.map {
                if (it.id == appId) it.copy(isInstalled = false, downloadProgress = null) else it
            }

            // Free from 128GB ROM
            val freedGb = app.sizeMb / 1024.0
            val current = _systemSpecs.value
            val newStorage = (current.usedStorageGb - freedGb).coerceAtLeast(20.0)
            val roundedStorage = (Math.round(newStorage * 10.0) / 10.0)
            _systemSpecs.value = current.copy(usedStorageGb = roundedStorage)

            _feedback.emit(UiFeedback("🗑️ ${app.name} uninstalled. ${app.sizeMb} MB freed on 128GB Storage."))
        }
    }

    init {
        val db = AiMobileDatabase.getDatabase(application)
        repository = AiMobileRepository(db.aiMobileDao())

        pcServer = PcKeyboardServer(application) { text, action ->
            viewModelScope.launch {
                when (action) {
                    "type" -> {
                        if (_currentTab.value == 0) {
                            _chatInput.value = text
                        } else if (_currentTab.value == 1) {
                            _studioInput.value = text
                        }
                    }
                    "chat" -> {
                        _currentTab.value = 0
                        sendChatMessage(text)
                    }
                    "studio" -> {
                        _currentTab.value = 1
                        _studioInput.value = text
                    }
                    "free_fire" -> {
                        _currentTab.value = 3
                        handleFreeFireInput(text)
                    }
                }
            }
        }
        pcServerState = pcServer.serverState
        pcServer.startServer(8888)
    }

    // --- Free Fire Game State & PC Set Simulation ---
    private val _freeFireState = MutableStateFlow(FreeFireGameState())
    val freeFireState: StateFlow<FreeFireGameState> = _freeFireState.asStateFlow()

    fun openFreeFireGame() {
        _currentTab.value = 3
        if (!_freeFireState.value.isMatchActive) {
            startFreeFireMatch()
        }
    }

    fun toggleFreeFirePcKeymap() {
        _freeFireState.value = _freeFireState.value.copy(showPcKeymap = !_freeFireState.value.showPcKeymap)
    }

    fun startFreeFireMatch() {
        val initialBots = listOf(
            EnemyBot("bot1", "Bot_Kev99", 0.3f, 0.25f, 100, 100, colorHex = 0xFFEF5350),
            EnemyBot("bot2", "SniperAce", 0.75f, 0.2f, 100, 100, colorHex = 0xFFFF7043),
            EnemyBot("bot3", "ProGamer_BD", 0.2f, 0.5f, 100, 100, colorHex = 0xFFAB47BC),
            EnemyBot("bot4", "ShadowHunter", 0.8f, 0.55f, 100, 100, colorHex = 0xFFEC407A)
        )
        _freeFireState.value = FreeFireGameState(
            isMatchActive = true,
            isBooyah = false,
            isGameOver = false,
            playerHp = 200,
            playerArmor = 100,
            medkitsCount = 3,
            glooWallsCount = 3,
            activeGlooWalls = 0,
            kills = 0,
            aliveCount = 50,
            playerX = 0.5f,
            playerY = 0.7f,
            enemies = initialBots,
            killFeed = listOf("🔥 Dropped into Bermuda with Parachute!", "🎮 PC Keyboard & Mouse Link Active")
        )
        viewModelScope.launch {
            _feedback.emit(UiFeedback("🔥 Free Fire Match Started! PC Set: WASD to move, Space to Jump, Left Click to Shoot!"))
        }
    }

    fun handleFreeFireInput(actionKey: String) {
        val current = _freeFireState.value
        _freeFireState.value = current.copy(lastPcKeyTriggered = actionKey)

        if (!current.isMatchActive && (actionKey in listOf("START", "SHOOT", "JUMP") || actionKey.contains("FIRE"))) {
            startFreeFireMatch()
            return
        }

        when (actionKey.uppercase()) {
            "MOVE_FORWARD", "W", "UP" -> {
                val newY = (current.playerY - 0.06f).coerceIn(0.12f, 0.88f)
                _freeFireState.value = current.copy(playerY = newY, isAimingScope = false)
            }
            "MOVE_BACKWARD", "S", "DOWN" -> {
                val newY = (current.playerY + 0.06f).coerceIn(0.12f, 0.88f)
                _freeFireState.value = current.copy(playerY = newY)
            }
            "MOVE_LEFT", "A", "LEFT" -> {
                val newX = (current.playerX - 0.06f).coerceIn(0.08f, 0.92f)
                _freeFireState.value = current.copy(playerX = newX)
            }
            "MOVE_RIGHT", "D", "RIGHT" -> {
                val newX = (current.playerX + 0.06f).coerceIn(0.08f, 0.92f)
                _freeFireState.value = current.copy(playerX = newX)
            }
            "JUMP", "SPACE" -> {
                viewModelScope.launch {
                    val jumpY = (current.playerY - 0.08f).coerceAtLeast(0.12f)
                    _freeFireState.value = current.copy(playerY = jumpY)
                    kotlinx.coroutines.delay(180)
                    _freeFireState.value = _freeFireState.value.copy(playerY = current.playerY)
                }
            }
            "CROUCH", "C" -> {
                _freeFireState.value = current.copy(isCrouching = !current.isCrouching)
            }
            "SPRINT", "SHIFT" -> {
                _freeFireState.value = current.copy(isSprinting = !current.isSprinting)
            }
            "SCOPE", "E", "RIGHT_CLICK" -> {
                _freeFireState.value = current.copy(isAimingScope = !current.isAimingScope)
            }
            "WEAPON_1", "1" -> {
                _freeFireState.value = current.copy(selectedWeaponIndex = 0)
            }
            "WEAPON_2", "2" -> {
                _freeFireState.value = current.copy(selectedWeaponIndex = 1)
            }
            "WEAPON_3", "3" -> {
                _freeFireState.value = current.copy(selectedWeaponIndex = 2)
            }
            "GLOO_WALL", "G" -> {
                if (current.glooWallsCount > 0) {
                    _freeFireState.value = current.copy(
                        glooWallsCount = current.glooWallsCount - 1,
                        activeGlooWalls = current.activeGlooWalls + 1,
                        killFeed = current.killFeed + "🛡️ Gloo Wall deployed!"
                    )
                }
            }
            "MEDKIT", "H" -> {
                if (current.medkitsCount > 0 && current.playerHp < current.maxHp) {
                    val restored = (current.playerHp + 75).coerceAtMost(current.maxHp)
                    _freeFireState.value = current.copy(
                        medkitsCount = current.medkitsCount - 1,
                        playerHp = restored,
                        killFeed = current.killFeed + "💊 Restored 75 HP with Medkit"
                    )
                }
            }
            "RELOAD", "R" -> {
                val weapon = current.weapons[current.selectedWeaponIndex]
                val updatedWeapons = current.weapons.toMutableList()
                updatedWeapons[current.selectedWeaponIndex] = weapon.copy(currentClip = weapon.maxClip)
                _freeFireState.value = current.copy(
                    weapons = updatedWeapons,
                    killFeed = current.killFeed + "⚡ Reloaded ${weapon.name}"
                )
            }
            "SHOOT", "FIRE", "ENTER", "CLICK" -> {
                executePlayerShoot()
            }
            "LOOT", "F" -> {
                _freeFireState.value = current.copy(
                    medkitsCount = current.medkitsCount + 1,
                    glooWallsCount = current.glooWallsCount + 1,
                    playerArmor = 100,
                    killFeed = current.killFeed + "📦 Looted Level 3 Vest & Ammo"
                )
            }
        }
    }

    private fun executePlayerShoot() {
        val current = _freeFireState.value
        val weapon = current.weapons[current.selectedWeaponIndex]

        if (weapon.currentClip <= 0) {
            _freeFireState.value = current.copy(
                killFeed = current.killFeed + "⚠️ Empty magazine! Press [R] to Reload"
            )
            return
        }

        // Decrement ammo
        val updatedWeapons = current.weapons.toMutableList()
        updatedWeapons[current.selectedWeaponIndex] = weapon.copy(currentClip = weapon.currentClip - 1)

        // Find closest alive enemy
        val aliveEnemies = current.enemies.filter { it.isAlive }
        if (aliveEnemies.isEmpty()) {
            _freeFireState.value = current.copy(
                isBooyah = true,
                isMatchActive = false,
                killFeed = current.killFeed + "🏆 BOOYAH! All enemies eliminated in Bermuda!"
            )
            return
        }

        // Hit closest enemy
        val target = aliveEnemies.minByOrNull {
            val dx = it.x - current.playerX
            val dy = it.y - current.playerY
            dx * dx + dy * dy
        } ?: aliveEnemies.first()

        val isHeadshot = Math.random() < 0.35 || current.isAimingScope
        val dmg = if (isHeadshot) weapon.damage * 2 + 30 else weapon.damage
        val newHp = (target.hp - dmg).coerceAtLeast(0)

        val updatedEnemies = current.enemies.map {
            if (it.id == target.id) it.copy(hp = newHp, isAlive = newHp > 0) else it
        }

        var newKills = current.kills
        var newAlive = current.aliveCount
        val newFeed = current.killFeed.toMutableList()

        if (newHp <= 0) {
            newKills += 1
            newAlive = (newAlive - (1 + (Math.random() * 3).toInt())).coerceAtLeast(1)
            val tag = if (isHeadshot) "💥 HEADSHOT" else "🎯 KILLED"
            newFeed.add("$tag: ${target.name} with ${weapon.name}")
        } else {
            val tag = if (isHeadshot) "CRITICAL -${dmg}" else "-${dmg}"
            newFeed.add("Hit ${target.name} ($tag)")
        }

        val allDead = updatedEnemies.none { it.isAlive } || newAlive <= 1

        _freeFireState.value = current.copy(
            weapons = updatedWeapons,
            enemies = updatedEnemies,
            kills = newKills,
            aliveCount = if (allDead) 1 else newAlive,
            killFeed = newFeed,
            isBooyah = allDead,
            isMatchActive = !allDead
        )
    }

    fun togglePcServer() {
        if (pcServerState.value.isRunning) {
            pcServer.stopServer()
        } else {
            pcServer.startServer(8888)
        }
    }

    override fun onCleared() {
        super.onCleared()
        pcServer.stopServer()
    }

    // --- Active Model Tier ---
    private val _selectedModel = MutableStateFlow(GeminiTaskTier.GENERAL)
    val selectedModel: StateFlow<GeminiTaskTier> = _selectedModel.asStateFlow()

    fun selectModel(tier: GeminiTaskTier) {
        _selectedModel.value = tier
    }

    // --- Navigation Tab (0: Chat, 1: Studio, 2: Vision, 3: System, 4: Saved) ---
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    fun setCurrentTab(tab: Int) {
        _currentTab.value = tab
    }

    // --- Feedback / Snackbars ---
    private val _feedback = MutableSharedFlow<UiFeedback>()
    val feedback: SharedFlow<UiFeedback> = _feedback.asSharedFlow()

    // --- Chat Screen State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "gemini",
                text = "Hello! Welcome to **AI Mobile** (8GB RAM | 128GB Storage).\n\nEquipped with Gemini multi-model intelligence and PC Keyboard linking. Choose **Flash-Lite** for instant responses, **3.5/3.8 Flash** for daily tasks, or **3.1 Pro** for deep reasoning. How can I help you today?",
                modelUsed = GeminiTaskTier.GENERAL.modelId
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun updateChatInput(text: String) {
        _chatInput.value = text
    }

    fun sendChatMessage(overridePrompt: String? = null) {
        val promptToSend = (overridePrompt ?: _chatInput.value).trim()
        if (promptToSend.isBlank() || _isChatLoading.value) return

        val userMsg = ChatMessage(
            sender = "user",
            text = promptToSend
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _chatInput.value = ""
        _isChatLoading.value = true

        val tier = _selectedModel.value
        viewModelScope.launch {
            val result = GeminiClient.executeGenerate(
                modelId = tier.modelId,
                prompt = promptToSend
            )

            result.onSuccess { exec ->
                val aiMsg = ChatMessage(
                    sender = "gemini",
                    text = exec.text,
                    modelUsed = exec.modelUsed,
                    latencyMs = exec.latencyMs,
                    isSimulation = exec.isSimulation
                )
                _chatMessages.value = _chatMessages.value + aiMsg
            }.onFailure { err ->
                val errorMsg = ChatMessage(
                    sender = "gemini",
                    text = "Request failed: ${err.message ?: "Unknown error"}. Please check your connection or API key.",
                    modelUsed = tier.modelId
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            }

            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "gemini",
                text = "Chat history cleared. What would you like to explore next?",
                modelUsed = _selectedModel.value.modelId
            )
        )
    }

    // --- Document Studio State ---
    private val _studioInput = MutableStateFlow("")
    val studioInput: StateFlow<String> = _studioInput.asStateFlow()

    private val _studioResult = MutableStateFlow<GeminiExecutionResult?>(null)
    val studioResult: StateFlow<GeminiExecutionResult?> = _studioResult.asStateFlow()

    private val _isStudioLoading = MutableStateFlow(false)
    val isStudioLoading: StateFlow<Boolean> = _isStudioLoading.asStateFlow()

    private val _selectedTone = MutableStateFlow("Professional")
    val selectedTone: StateFlow<String> = _selectedTone.asStateFlow()

    private val _selectedTargetLang = MutableStateFlow("Spanish")
    val selectedTargetLang: StateFlow<String> = _selectedTargetLang.asStateFlow()

    fun updateStudioInput(text: String) {
        _studioInput.value = text
    }

    fun selectTone(tone: String) {
        _selectedTone.value = tone
    }

    fun selectTargetLang(lang: String) {
        _selectedTargetLang.value = lang
    }

    fun executeRewrite() {
        val input = _studioInput.value.trim()
        if (input.isBlank() || _isStudioLoading.value) return

        _isStudioLoading.value = true
        val tone = _selectedTone.value
        val model = GeminiTaskTier.FAST.modelId // fast task
        val prompt = "Rewrite the following text in a $tone tone. Make it compelling, clear, and well-structured:\n\n\"$input\""

        viewModelScope.launch {
            val res = GeminiClient.executeGenerate(
                modelId = model,
                prompt = prompt,
                systemInstruction = "You are an expert editor who enhances tone and clarity."
            )
            _studioResult.value = res.getOrNull()
            _isStudioLoading.value = false
        }
    }

    fun executeSummarize(format: String = "bullet") {
        val input = _studioInput.value.trim()
        if (input.isBlank() || _isStudioLoading.value) return

        _isStudioLoading.value = true
        val model = GeminiTaskTier.FAST.modelId // fast task
        val prompt = if (format == "bullet") {
            "Provide a concise summary of the following text with 3 to 5 key bullet points:\n\n$input"
        } else {
            "Provide a punchy 1-sentence TL;DR summary of the following text:\n\n$input"
        }

        viewModelScope.launch {
            val res = GeminiClient.executeGenerate(modelId = model, prompt = prompt)
            _studioResult.value = res.getOrNull()
            _isStudioLoading.value = false
        }
    }

    fun executeActionItems() {
        val input = _studioInput.value.trim()
        if (input.isBlank() || _isStudioLoading.value) return

        _isStudioLoading.value = true
        val model = GeminiTaskTier.GENERAL.modelId
        val prompt = "Extract all actionable tasks, responsibilities, and next steps from this text as a checklist with [ ] checkboxes:\n\n$input"

        viewModelScope.launch {
            val res = GeminiClient.executeGenerate(modelId = model, prompt = prompt)
            _studioResult.value = res.getOrNull()
            _isStudioLoading.value = false
        }
    }

    fun executeTranslate() {
        val input = _studioInput.value.trim()
        if (input.isBlank() || _isStudioLoading.value) return

        _isStudioLoading.value = true
        val lang = _selectedTargetLang.value
        val model = GeminiTaskTier.FAST.modelId
        val prompt = "Translate the following text accurately into $lang while preserving its tone and context:\n\n$input"

        viewModelScope.launch {
            val res = GeminiClient.executeGenerate(modelId = model, prompt = prompt)
            _studioResult.value = res.getOrNull()
            _isStudioLoading.value = false
        }
    }

    fun executeDeepAnalysis() {
        val input = _studioInput.value.trim()
        if (input.isBlank() || _isStudioLoading.value) return

        _isStudioLoading.value = true
        val model = GeminiTaskTier.DEEP.modelId // Deep reasoning with Gemini 3.1 Pro
        val prompt = "Conduct an in-depth analytical breakdown of this topic or problem. Address root causes, trade-offs, potential failure modes, and provide a 3-phase strategic roadmap:\n\n$input"

        viewModelScope.launch {
            val res = GeminiClient.executeGenerate(modelId = model, prompt = prompt)
            _studioResult.value = res.getOrNull()
            _isStudioLoading.value = false
        }
    }

    // --- Multimodal Vision State ---
    private val _visionImageUri = MutableStateFlow<Uri?>(null)
    val visionImageUri: StateFlow<Uri?> = _visionImageUri.asStateFlow()

    private val _visionBitmap = MutableStateFlow<Bitmap?>(null)
    val visionBitmap: StateFlow<Bitmap?> = _visionBitmap.asStateFlow()

    private val _visionPrompt = MutableStateFlow("Analyze this image in detail and describe what you observe.")
    val visionPrompt: StateFlow<String> = _visionPrompt.asStateFlow()

    private val _visionResult = MutableStateFlow<GeminiExecutionResult?>(null)
    val visionResult: StateFlow<GeminiExecutionResult?> = _visionResult.asStateFlow()

    private val _isVisionLoading = MutableStateFlow(false)
    val isVisionLoading: StateFlow<Boolean> = _isVisionLoading.asStateFlow()

    fun setVisionImage(uri: Uri?) {
        _visionImageUri.value = uri
        if (uri != null) {
            try {
                val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
                val bmp = BitmapFactory.decodeStream(inputStream)
                _visionBitmap.value = bmp
            } catch (_: Exception) {
                _visionBitmap.value = null
            }
        } else {
            _visionBitmap.value = null
        }
    }

    fun updateVisionPrompt(text: String) {
        _visionPrompt.value = text
    }

    fun executeVisionAnalysis() {
        val prompt = _visionPrompt.value.trim()
        if (_isVisionLoading.value) return

        _isVisionLoading.value = true
        val model = _selectedModel.value.modelId
        val bmp = _visionBitmap.value
        val base64 = bmp?.let { GeminiClient.bitmapToBase64(it) }

        viewModelScope.launch {
            val res = GeminiClient.executeGenerate(
                modelId = model,
                prompt = prompt.ifEmpty { "Analyze this image in detail." },
                base64Image = base64
            )
            _visionResult.value = res.getOrNull()
            _isVisionLoading.value = false
        }
    }

    // --- Saved Items / History State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    val savedItems: StateFlow<List<AiMobileEntity>> = combine(
        repository.allItems,
        _searchQuery,
        _selectedCategoryFilter
    ) { items, query, category ->
        items.filter { item ->
            val matchesCategory = when (category) {
                "All" -> true
                "Favorites" -> item.isFavorite
                else -> item.category.equals(category, ignoreCase = true)
            }
            val matchesQuery = if (query.isBlank()) true else {
                item.title.contains(query, ignoreCase = true) ||
                item.content.contains(query, ignoreCase = true) ||
                item.prompt.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveToHistory(
        title: String,
        content: String,
        prompt: String,
        category: String,
        modelUsed: String,
        latencyMs: Long = 0L,
        imageUri: String? = null
    ) {
        viewModelScope.launch {
            val entity = AiMobileEntity(
                title = title.ifBlank { "Generated Content" },
                content = content,
                prompt = prompt,
                category = category,
                modelUsed = modelUsed,
                latencyMs = latencyMs,
                imageUri = imageUri
            )
            repository.saveItem(entity)
            _feedback.emit(UiFeedback("Saved to your AI Mobile Library"))
        }
    }

    fun toggleFavorite(item: AiMobileEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(item.id, item.isFavorite)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItem(id)
            _feedback.emit(UiFeedback("Item deleted"))
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _feedback.emit(UiFeedback("History cleared"))
        }
    }
}
