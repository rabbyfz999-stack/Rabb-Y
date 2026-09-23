package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiMobileTopBar
import com.example.ui.components.ApiKeyInfoDialog
import com.example.ui.components.ModelSelectorDialog
import com.example.ui.components.PcKeyboardBridgeDialog
import com.example.ui.viewmodel.AiMobileViewModel

data class NavTabItem(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val tag: String
)

@Composable
fun AiMobileMainScreen(
    viewModel: AiMobileViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedTier by viewModel.selectedModel.collectAsState()
    val pcServerState by viewModel.pcServerState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showModelSelector by remember { mutableStateOf(false) }
    var showKeyInfo by remember { mutableStateOf(false) }
    var showPcBridge by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.feedback.collect { event ->
            snackbarHostState.showSnackbar(event.message)
        }
    }

    val tabs = listOf(
        NavTabItem(
            title = "Chat",
            selectedIcon = Icons.AutoMirrored.Filled.Chat,
            unselectedIcon = Icons.AutoMirrored.Outlined.Chat,
            tag = "tab_chat"
        ),
        NavTabItem(
            title = "Studio",
            selectedIcon = Icons.Filled.AutoAwesome,
            unselectedIcon = Icons.Outlined.AutoAwesome,
            tag = "tab_studio"
        ),
        NavTabItem(
            title = "Vision",
            selectedIcon = Icons.Filled.DocumentScanner,
            unselectedIcon = Icons.Outlined.DocumentScanner,
            tag = "tab_vision"
        ),
        NavTabItem(
            title = "Free Fire",
            selectedIcon = Icons.Filled.LocalFireDepartment,
            unselectedIcon = Icons.Outlined.LocalFireDepartment,
            tag = "tab_free_fire"
        ),
        NavTabItem(
            title = "Store",
            selectedIcon = Icons.Filled.ShoppingBag,
            unselectedIcon = Icons.Outlined.ShoppingBag,
            tag = "tab_store"
        ),
        NavTabItem(
            title = "System",
            selectedIcon = Icons.Filled.Memory,
            unselectedIcon = Icons.Outlined.Memory,
            tag = "tab_system"
        ),
        NavTabItem(
            title = "Saved",
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Filled.BookmarkBorder,
            tag = "tab_saved"
        )
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    if (event.isCtrlPressed) {
                        when (event.key) {
                            Key.A -> { viewModel.handleGlobalClipboardAction("SELECT_ALL"); true }
                            Key.C -> { viewModel.handleGlobalClipboardAction("COPY"); true }
                            Key.V -> { viewModel.handleGlobalClipboardAction("PASTE"); true }
                            Key.X -> { viewModel.handleGlobalClipboardAction("CUT"); true }
                            Key.One -> { viewModel.setCurrentTab(0); true }
                            Key.Two -> { viewModel.setCurrentTab(1); true }
                            Key.Three -> { viewModel.setCurrentTab(2); true }
                            Key.Four -> { viewModel.setCurrentTab(3); true }
                            Key.Five -> { viewModel.setCurrentTab(4); true }
                            Key.Six -> { viewModel.setCurrentTab(5); true }
                            Key.Seven -> { viewModel.setCurrentTab(6); true }
                            Key.K -> { showModelSelector = true; true }
                            Key.P -> { showPcBridge = true; true }
                            else -> false
                        }
                    } else if (currentTab == 3) {
                        // Direct PC Physical Keyboard set for Free Fire
                        when (event.key) {
                            Key.W, Key.DirectionUp -> { viewModel.handleFreeFireInput("MOVE_FORWARD"); true }
                            Key.S, Key.DirectionDown -> { viewModel.handleFreeFireInput("MOVE_BACKWARD"); true }
                            Key.A, Key.DirectionLeft -> { viewModel.handleFreeFireInput("MOVE_LEFT"); true }
                            Key.D, Key.DirectionRight -> { viewModel.handleFreeFireInput("MOVE_RIGHT"); true }
                            Key.Spacebar -> { viewModel.handleFreeFireInput("JUMP"); true }
                            Key.Enter, Key.NumPadEnter -> { viewModel.handleFreeFireInput("SHOOT"); true }
                            Key.R -> { viewModel.handleFreeFireInput("RELOAD"); true }
                            Key.C -> { viewModel.handleFreeFireInput("CROUCH"); true }
                            Key.ShiftLeft, Key.ShiftRight -> { viewModel.handleFreeFireInput("SPRINT"); true }
                            Key.E -> { viewModel.handleFreeFireInput("SCOPE"); true }
                            Key.G -> { viewModel.handleFreeFireInput("GLOO_WALL"); true }
                            Key.H -> { viewModel.handleFreeFireInput("MEDKIT"); true }
                            Key.F -> { viewModel.handleFreeFireInput("LOOT"); true }
                            Key.One -> { viewModel.handleFreeFireInput("WEAPON_1"); true }
                            Key.Two -> { viewModel.handleFreeFireInput("WEAPON_2"); true }
                            Key.Three -> { viewModel.handleFreeFireInput("WEAPON_3"); true }
                            else -> false
                        }
                    } else false
                } else false
            },
        topBar = {
            AiMobileTopBar(
                currentTier = selectedTier,
                onOpenModelSelector = { showModelSelector = true },
                onOpenKeyInfo = { showKeyInfo = true },
                onOpenPcKeyboard = { showPcBridge = true },
                onOpenPlayStore = { viewModel.setCurrentTab(4) },
                onOpenFreeFire = { viewModel.openFreeFireGame() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = currentTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setCurrentTab(index) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title, fontSize = 9.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> ChatScreen(viewModel = viewModel)
                1 -> DocumentStudioScreen(viewModel = viewModel)
                2 -> VisionScreen(viewModel = viewModel)
                3 -> FreeFireGameScreen(viewModel = viewModel)
                4 -> PlayStoreScreen(viewModel = viewModel)
                5 -> SystemSpecsScreen(viewModel = viewModel)
                6 -> HistoryScreen(viewModel = viewModel)
            }
        }
    }

    if (showModelSelector) {
        ModelSelectorDialog(
            selectedTier = selectedTier,
            onTierSelected = { viewModel.selectModel(it) },
            onDismiss = { showModelSelector = false }
        )
    }

    if (showKeyInfo) {
        ApiKeyInfoDialog(
            onDismiss = { showKeyInfo = false }
        )
    }

    if (showPcBridge) {
        PcKeyboardBridgeDialog(
            serverState = pcServerState,
            onToggleServer = { viewModel.togglePcServer() },
            onDismiss = { showPcBridge = false }
        )
    }
}
