package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FreeFireGameState
import com.example.ui.viewmodel.AiMobileViewModel

@Composable
fun FreeFireGameScreen(
    viewModel: AiMobileViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.freeFireState.collectAsState()
    val serverState by viewModel.pcServerState.collectAsState()
    var showHelpDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F141C))
            .testTag("free_fire_game_screen")
    ) {
        if (!gameState.isMatchActive && !gameState.isBooyah && !gameState.isGameOver) {
            // Match Lobby / Start Screen
            FreeFireLobbyView(
                gameState = gameState,
                serverUrl = serverState.url,
                isPcConnected = serverState.isRunning,
                onStartMatch = { viewModel.startFreeFireMatch() },
                onTogglePcKeymap = { viewModel.toggleFreeFirePcKeymap() },
                onShowHelp = { showHelpDialog = true }
            )
        } else {
            // Live Battle Royale Match
            FreeFireBattleView(
                gameState = gameState,
                serverUrl = serverState.url,
                isPcConnected = serverState.isRunning,
                onGameAction = { viewModel.handleFreeFireInput(it) },
                onTogglePcKeymap = { viewModel.toggleFreeFirePcKeymap() },
                onRestart = { viewModel.startFreeFireMatch() },
                onShowHelp = { showHelpDialog = true }
            )
        }

        if (showHelpDialog) {
            FreeFirePcSetupDialog(
                serverUrl = serverState.url,
                isServerRunning = serverState.isRunning,
                onDismiss = { showHelpDialog = false }
            )
        }
    }
}

@Composable
private fun FreeFireLobbyView(
    gameState: FreeFireGameState,
    serverUrl: String,
    isPcConnected: Boolean,
    onStartMatch: () -> Unit,
    onTogglePcKeymap: () -> Unit,
    onShowHelp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FREE FIRE MAX",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFF5722),
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "AI Mobile Battleground • Bermuda Map",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }

            IconButton(
                onClick = onShowHelp,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E2638))
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "PC Setup Guide",
                    tint = Color(0xFFFF9800)
                )
            }
        }

        // Hero Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF182030),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFF3D00), Color(0xFFFF9100), Color(0xFFFFEA00))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 44.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PC Keymapping & Keyboard Set Active",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Play with PC Keyboard & Mouse via WiFi bridge or physical keyboard (WASD, Space, Shift, 1/2/3, Left Click)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
                )

                // Specs Chip
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpecPill(label = "8GB RAM", color = Color(0xFF7C4DFF))
                    SpecPill(label = "120 FPS Ultra", color = Color(0xFF00E5FF))
                    SpecPill(label = "PC Set Link", color = Color(0xFF00E676))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PC Connection Badge
                Surface(
                    color = if (isPcConnected) Color(0x2200E676) else Color(0x22FF5252),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPcConnected) Color(0xFF00E676) else Color(0xFFFF5252)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DesktopWindows,
                            contentDescription = null,
                            tint = if (isPcConnected) Color(0xFF00E676) else Color(0xFFFF5252),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPcConnected) "PC Bridge Live: $serverUrl" else "PC Bridge Offline",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Open on PC browser to use physical keyboard keys",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Quick Keymap preview
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF141924)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🎮 PC KEYBOARD MAPPINGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9100)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    KeyBadge("[W/A/S/D]", "Move")
                    KeyBadge("[SPACE]", "Jump")
                    KeyBadge("[CLICK]", "Fire")
                    KeyBadge("[R]", "Reload")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    KeyBadge("[1/2/3]", "Guns")
                    KeyBadge("[G]", "Gloo Wall")
                    KeyBadge("[H]", "Medkit")
                    KeyBadge("[SHIFT]", "Sprint")
                }
            }
        }

        // Bottom Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onStartMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_free_fire_match_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "START BERMUDA BATTLE ROYALE",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onTogglePcKeymap,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222C3D)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (gameState.showPcKeymap) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (gameState.showPcKeymap) "Keymap HUD: ON" else "Keymap HUD: OFF",
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onShowHelp,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222C3D)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PC Set Guide", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun FreeFireBattleView(
    gameState: FreeFireGameState,
    serverUrl: String,
    isPcConnected: Boolean,
    onGameAction: (String) -> Unit,
    onTogglePcKeymap: () -> Unit,
    onRestart: () -> Unit,
    onShowHelp: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = maxWidth
        val height = maxHeight

        Column(modifier = Modifier.fillMaxSize()) {
            // Battle Header HUD
            Surface(
                color = Color(0xCC0B0E14),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Alive & Kills
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            color = Color(0xFF1E2638),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "👤 ALIVE: ${gameState.aliveCount}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFF880E4F),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "☠️ KILLS: ${gameState.kills}",
                                color = Color(0xFFFF80AB),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // PC Setup Active indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable { onShowHelp() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isPcConnected) Color(0xFF00E676) else Color(0xFFFF9100))
                        )
                        Text(
                            text = if (isPcConnected) "PC KEYBOARD: LINKED" else "PC: OFF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPcConnected) Color(0xFF00E676) else Color(0xFFFF9100)
                        )
                    }
                }
            }

            // Central Interactive Battlefield Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Tactical Top-Down Battleground Map
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                if (dragAmount.x > 10) onGameAction("MOVE_RIGHT")
                                else if (dragAmount.x < -10) onGameAction("MOVE_LEFT")
                                else if (dragAmount.y > 10) onGameAction("MOVE_BACKWARD")
                                else if (dragAmount.y < -10) onGameAction("MOVE_FORWARD")
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Ground terrain: Bermuda grass & grid
                    drawRect(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF2E7D32), Color(0xFF1B5E20), Color(0xFF0D3813)),
                            center = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f),
                            radius = canvasWidth * 0.8f
                        )
                    )

                    // Safe zone circle
                    drawCircle(
                        color = Color(0x3300E5FF),
                        radius = canvasWidth * 0.42f,
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.48f)
                    )
                    drawCircle(
                        color = Color(0xFF00E5FF),
                        radius = canvasWidth * 0.42f,
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.48f),
                        style = Stroke(width = 3f)
                    )

                    // Draw Obstacles / Crates / Rocks
                    drawRect(
                        color = Color(0xFF5D4037),
                        topLeft = Offset(canvasWidth * 0.2f, canvasHeight * 0.3f),
                        size = Size(40f, 40f)
                    )
                    drawRect(
                        color = Color(0xFF5D4037),
                        topLeft = Offset(canvasWidth * 0.75f, canvasHeight * 0.25f),
                        size = Size(50f, 50f)
                    )

                    // Draw Active Gloo Walls
                    if (gameState.activeGlooWalls > 0) {
                        drawRoundRect(
                            color = Color(0xFF00E5FF),
                            topLeft = Offset(canvasWidth * gameState.playerX - 50f, canvasHeight * gameState.playerY - 45f),
                            size = Size(100f, 16f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                        )
                    }

                    // Draw Enemies
                    gameState.enemies.filter { it.isAlive }.forEach { bot ->
                        val ex = canvasWidth * bot.x
                        val ey = canvasHeight * bot.y

                        // Enemy Body
                        drawCircle(
                            color = Color(bot.colorHex),
                            radius = 18f,
                            center = Offset(ex, ey)
                        )
                        // Enemy HP Bar
                        drawRect(
                            color = Color(0x66000000),
                            topLeft = Offset(ex - 22f, ey - 32f),
                            size = Size(44f, 7f)
                        )
                        drawRect(
                            color = Color(0xFFFF5252),
                            topLeft = Offset(ex - 22f, ey - 32f),
                            size = Size(44f * (bot.hp / bot.maxHp.toFloat()), 7f)
                        )
                    }

                    // Draw Player
                    val px = canvasWidth * gameState.playerX
                    val py = canvasHeight * gameState.playerY

                    // Scope Aim Beam
                    if (gameState.isAimingScope) {
                        drawLine(
                            color = Color(0xFFFF1744),
                            start = Offset(px, py),
                            end = Offset(px, py - 300f),
                            strokeWidth = 2f
                        )
                    }

                    // Player Outer Halo
                    drawCircle(
                        color = if (gameState.isSprinting) Color(0xFFFFEA00) else Color(0xFF00E676),
                        radius = 24f,
                        center = Offset(px, py),
                        style = Stroke(width = 3f)
                    )
                    // Player Body
                    drawCircle(
                        color = Color(0xFF00C853),
                        radius = 18f,
                        center = Offset(px, py)
                    )
                }

                // PC Keymap floating overlay badges
                if (gameState.showPcKeymap) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // WASD indicator on bottom left
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            color = Color(0x99000000),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🎮 PC KEYMAP ACTIVE:\n[W] UP  [S] DOWN\n[A] LEFT [D] RIGHT\n[SPACE] JUMP",
                                color = Color(0xFF00E676),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(6.dp)
                            )
                        }

                        // Fire keymap on right
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            color = Color(0x99000000),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🔥 COMBAT KEYS:\n[CLICK / ENTER] SHOOT\n[R-CLICK / E] SCOPE\n[R] RELOAD\n[1, 2, 3] WEAPONS",
                                color = Color(0xFFFF9100),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }

                // Recent PC Key Trigger Banner
                gameState.lastPcKeyTriggered?.let { key ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xE6FF5722)
                    ) {
                        Text(
                            text = "⌨️ PC Key Executed: $key",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                // Kill feed overlay
                if (gameState.killFeed.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        gameState.killFeed.takeLast(3).forEach { feed ->
                            Surface(
                                color = Color(0xCC000000),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = feed,
                                    fontSize = 10.sp,
                                    color = Color(0xFFFFD54F),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Player HP & Armor Bars
            Surface(
                color = Color(0xFF141924),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    // Health Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "HP ${gameState.playerHp}/${gameState.maxHp}",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "ARMOR ${gameState.playerArmor}%",
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { gameState.playerHp / gameState.maxHp.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (gameState.playerHp > 60) Color(0xFF00E676) else Color(0xFFFF1744),
                        trackColor = Color(0xFF263238)
                    )
                }
            }

            // Weapon Slots (1, 2, 3)
            Surface(
                color = Color(0xFF0B0E14),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gameState.weapons.forEachIndexed { index, weapon ->
                        val isSelected = gameState.selectedWeaponIndex == index
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onGameAction("WEAPON_${index + 1}") },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF2C384E) else Color(0xFF161B26),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) Color(0xFFFF9100) else Color(0xFF2A3447)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "[${weapon.keyShortcut}]",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFFFF9100) else Color(0xFF78909C)
                                    )
                                    Text(
                                        text = "${weapon.currentClip}/${weapon.totalAmmo}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "${weapon.iconEmoji} ${weapon.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFFB0BEC5)
                                )
                            }
                        }
                    }
                }
            }

            // On-screen Touch Controls (Also works with PC Keyboard!)
            Surface(
                color = Color(0xFF080B10),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Movement & Utilities
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TouchGameBtn(label = "W", sub = "▲", onClick = { onGameAction("MOVE_FORWARD") })
                        TouchGameBtn(label = "S", sub = "▼", onClick = { onGameAction("MOVE_BACKWARD") })
                        TouchGameBtn(label = "A", sub = "◀", onClick = { onGameAction("MOVE_LEFT") })
                        TouchGameBtn(label = "D", sub = "▶", onClick = { onGameAction("MOVE_RIGHT") })
                    }

                    // Center: Medkit & Gloo Wall
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TouchUtilityBtn(
                            icon = "🛡️",
                            tag = "[G] Gloo",
                            count = gameState.glooWallsCount,
                            onClick = { onGameAction("GLOO_WALL") }
                        )
                        TouchUtilityBtn(
                            icon = "💊",
                            tag = "[H] Med",
                            count = gameState.medkitsCount,
                            onClick = { onGameAction("MEDKIT") }
                        )
                    }

                    // Right: Primary Combat
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TouchActionBtn(
                            label = "SCOPE",
                            sub = "[R-CLICK/E]",
                            color = Color(0xFF1565C0),
                            onClick = { onGameAction("SCOPE") }
                        )
                        TouchActionBtn(
                            label = "RELOAD",
                            sub = "[R]",
                            color = Color(0xFF37474F),
                            onClick = { onGameAction("RELOAD") }
                        )
                        TouchActionBtn(
                            label = "FIRE",
                            sub = "[L-CLICK]",
                            color = Color(0xFFD50000),
                            isPrimary = true,
                            onClick = { onGameAction("SHOOT") }
                        )
                    }
                }
            }
        }

        // BOOYAH! Victory Screen
        if (gameState.isBooyah) {
            BooyahCelebrationOverlay(
                kills = gameState.kills,
                onPlayAgain = onRestart
            )
        }

        // Game Over Screen
        if (gameState.isGameOver) {
            GameOverOverlay(
                kills = gameState.kills,
                onRetry = onRestart
            )
        }
    }
}

@Composable
private fun TouchGameBtn(label: String, sub: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E2838),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.size(42.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = sub, fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun TouchUtilityBtn(icon: String, tag: String, count: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1E2838),
        modifier = Modifier.size(46.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "$icon $count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = tag, fontSize = 8.sp, color = Color(0xFFFF9100))
        }
    }
}

@Composable
private fun TouchActionBtn(
    label: String,
    sub: String,
    color: Color,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = color,
        modifier = Modifier
            .height(48.dp)
            .width(if (isPrimary) 62.dp else 52.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = if (isPrimary) 12.sp else 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(text = sub, fontSize = 7.sp, color = Color(0xCCFFFFFF))
        }
    }
}

@Composable
private fun BooyahCelebrationOverlay(
    kills: Int,
    onPlayAgain: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE0B0E14)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "🏆 BOOYAH! 🏆",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD600),
                letterSpacing = 2.sp
            )
            Text(
                text = "#1 VICTORY ROYALE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E2638),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "MATCH STATS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9100))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Total Kills: $kills", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = "Hardware: AI Mobile (8GB RAM / 128GB ROM)", fontSize = 12.sp, color = Color(0xFF00E5FF))
                    Text(text = "Input: PC Keyboard & Mouse Setup", fontSize = 12.sp, color = Color(0xFF00E676))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPlayAgain,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = "PLAY AGAIN (PC SET READY)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GameOverOverlay(
    kills: Int,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE0B0E14)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "GAME OVER",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFF1744)
            )
            Text(
                text = "Better luck next time in Bermuda!",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = "RESPAWN & RETRY", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FreeFirePcSetupDialog(
    serverUrl: String,
    isServerRunning: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = Color(0xFFFF9100))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "PC Keyboard Setup for Free Fire", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "How to play Free Fire with your PC Keyboard & Mouse:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "1. Connect your PC and AI Mobile to the same Wi-Fi network.", fontSize = 12.sp)
                        Text(text = "2. Open this URL in your PC browser (Chrome / Edge / Firefox):", fontSize = 12.sp)
                        Surface(
                            color = Color(0xFF0F141C),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = serverUrl,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Text(text = "3. Click on the '🔥 Free Fire PC Setup' tab on your PC screen.", fontSize = 12.sp)
                        Text(text = "4. Press WASD to move, Space to Jump, Left Click to Fire, and 1/2/3 to switch weapons!", fontSize = 12.sp)
                    }
                }

                Text(
                    text = "💡 Physical USB or Bluetooth keyboards connected to the device also work directly with standard key events!",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("GOT IT, LET'S PLAY!")
            }
        }
    )
}

@Composable
private fun SpecPill(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun KeyBadge(key: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = key, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp)
    }
}
