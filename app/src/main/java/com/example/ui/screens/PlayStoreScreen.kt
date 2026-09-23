package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayStoreApp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.ModelFastColor
import com.example.ui.viewmodel.AiMobileViewModel

@Composable
fun PlayStoreScreen(
    viewModel: AiMobileViewModel,
    modifier: Modifier = Modifier
) {
    val apps by viewModel.playStoreApps.collectAsState()
    val searchQuery by viewModel.playStoreSearchQuery.collectAsState()
    val selectedCategory by viewModel.playStoreCategory.collectAsState()
    val systemSpecs by viewModel.systemSpecs.collectAsState()
    val isScanning by viewModel.isPlayProtectScanning.collectAsState()
    val playProtectStatus by viewModel.playProtectStatus.collectAsState()

    var selectedAppDetails by remember { mutableStateOf<PlayStoreApp?>(null) }
    var selectedStoreSection by remember { mutableIntStateOf(0) } // 0: For You, 1: Top Charts, 2: Manage Device

    val storeSections = listOf("For You", "Top Charts", "Manage & Device")
    val categories = listOf("All", "AI & Tools", "Productivity", "Graphics & Photo", "System", "Games")

    val filteredApps = apps.filter { app ->
        val matchesCategory = (selectedCategory == "All" || app.category.equals(selectedCategory, ignoreCase = true))
        val matchesSearch = if (searchQuery.isBlank()) true else {
            app.name.contains(searchQuery, ignoreCase = true) ||
            app.description.contains(searchQuery, ignoreCase = true) ||
            app.tags.any { it.contains(searchQuery, ignoreCase = true) }
        }
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Play Store Header & Search ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                // Play Store Brand Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Google Play Triangle Style Logo
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF00E5FF), Color(0xFF00C853), Color(0xFFFFB300), Color(0xFFFF4081))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "▶",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Google Play",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = EmeraldAccent.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "AI Edition",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldAccent,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "AI Mobile 8GB RAM • 128GB ROM",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Play Protect Quick Status Icon
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { viewModel.scanPlayProtect() }
                            .testTag("play_protect_scan_icon")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Play Protect",
                                tint = ModelFastColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updatePlayStoreSearch(it) },
                    placeholder = { Text("Search apps, games & AI models...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updatePlayStoreSearch("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("play_store_search_input")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Section Tabs: For You | Top Charts | Manage Device
                ScrollableTabRow(
                    selectedTabIndex = selectedStoreSection,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    storeSections.forEachIndexed { idx, title ->
                        Tab(
                            selected = selectedStoreSection == idx,
                            onClick = { selectedStoreSection = idx },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedStoreSection == idx) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // --- Categories Chips Bar ---
        if (selectedStoreSection != 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectPlayStoreCategory(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("chip_category_${cat.replace(" ", "_")}")
                    )
                }
            }
        }

        // --- Main Content By Selected Section ---
        Box(modifier = Modifier.weight(1f)) {
            when (selectedStoreSection) {
                0 -> ForYouSection(
                    apps = filteredApps,
                    onAppClick = { selectedAppDetails = it },
                    onInstall = { viewModel.installApp(it.id) },
                    onUninstall = { viewModel.uninstallApp(it.id) }
                )
                1 -> TopChartsSection(
                    apps = filteredApps.sortedByDescending { it.rating },
                    onAppClick = { selectedAppDetails = it },
                    onInstall = { viewModel.installApp(it.id) },
                    onUninstall = { viewModel.uninstallApp(it.id) }
                )
                2 -> ManageDeviceSection(
                    apps = apps,
                    systemSpecs = systemSpecs,
                    isScanning = isScanning,
                    playProtectStatus = playProtectStatus,
                    onScanPlayProtect = { viewModel.scanPlayProtect() },
                    onAppClick = { selectedAppDetails = it },
                    onUninstall = { viewModel.uninstallApp(it.id) }
                )
            }
        }
    }

    // --- App Details Dialog ---
    selectedAppDetails?.let { app ->
        AppDetailDialog(
            app = app,
            onDismiss = { selectedAppDetails = null },
            onInstall = {
                viewModel.installApp(app.id)
                selectedAppDetails = null
            },
            onUninstall = {
                viewModel.uninstallApp(app.id)
                selectedAppDetails = null
            },
            onOpenFreeFire = {
                viewModel.openFreeFireGame()
            }
        )
    }
}

// -------------------------------------------------------------
// SECTION 1: For You (Featured & Grouped)
// -------------------------------------------------------------
@Composable
private fun ForYouSection(
    apps: List<PlayStoreApp>,
    onAppClick: (PlayStoreApp) -> Unit,
    onInstall: (PlayStoreApp) -> Unit,
    onUninstall: (PlayStoreApp) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Spotlight Featured Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("featured_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    ElectricViolet.copy(alpha = 0.25f),
                                    CyanAccent.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ElectricViolet
                            ) {
                                Text(
                                    text = "AI SPOTLIGHT",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Optimized for 8GB LPDDR5X RAM",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Next-Gen AI Apps Ecosystem",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Download neural assistants, image upscalers, and local LLMs tailored for 128GB ROM.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Horizontal Carousel: Popular on AI Mobile
        item {
            Column {
                Text(
                    text = "Recommended for AI Mobile",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(apps.take(5)) { app ->
                        FeaturedMiniAppCard(
                            app = app,
                            onClick = { onAppClick(app) },
                            onInstall = { onInstall(app) }
                        )
                    }
                }
            }
        }

        // App List
        item {
            Text(
                text = "All Available Apps (${apps.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(apps, key = { it.id }) { app ->
            PlayStoreAppRow(
                app = app,
                onClick = { onAppClick(app) },
                onInstall = { onInstall(app) },
                onUninstall = { onUninstall(app) }
            )
        }
    }
}

// -------------------------------------------------------------
// SECTION 2: Top Charts
// -------------------------------------------------------------
@Composable
private fun TopChartsSection(
    apps: List<PlayStoreApp>,
    onAppClick: (PlayStoreApp) -> Unit,
    onInstall: (PlayStoreApp) -> Unit,
    onUninstall: (PlayStoreApp) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(apps, key = { _, app -> app.id }) { index, app ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAppClick(app) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank number
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (index < 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(28.dp)
                )

                // App Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(app.iconBgColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = app.iconEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // App Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${app.developer} • ${app.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${app.rating} ★",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberAccent
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "•  ${app.sizeMb} MB",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Install/Open Button
                AppActionButton(
                    app = app,
                    onInstall = { onInstall(app) },
                    onUninstall = { onUninstall(app) }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SECTION 3: Manage Apps & Device (Play Protect & 128GB ROM)
// -------------------------------------------------------------
@Composable
private fun ManageDeviceSection(
    apps: List<PlayStoreApp>,
    systemSpecs: com.example.data.model.DeviceSystemSpecs,
    isScanning: Boolean,
    playProtectStatus: String,
    onScanPlayProtect: () -> Unit,
    onAppClick: (PlayStoreApp) -> Unit,
    onUninstall: (PlayStoreApp) -> Unit
) {
    val installedApps = apps.filter { it.isInstalled }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Play Protect Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("play_protect_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ModelFastColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Play Protect",
                                tint = ModelFastColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Play Protect",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = playProtectStatus,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        OutlinedButton(
                            onClick = onScanPlayProtect,
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("scan_play_protect_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Scan",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Storage & 128GB ROM Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("play_store_storage_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Device Storage (AI Mobile)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${systemSpecs.usedStorageGb} GB / ${systemSpecs.totalStorageGb} GB",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val storageRatio = (systemSpecs.usedStorageGb / systemSpecs.totalStorageGb).toFloat().coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { storageRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Free Space: ${String.format(java.util.Locale.US, "%.1f", systemSpecs.totalStorageGb - systemSpecs.usedStorageGb)} GB available on 128.0 GB high-speed UFS 3.1 storage.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        // Installed Apps Section
        Column {
            Text(
                text = "Installed Apps (${installedApps.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (installedApps.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No apps installed yet. Browse 'For You' or 'Top Charts' to download.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    installedApps.forEach { app ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAppClick(app) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(app.iconBgColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = app.iconEmoji, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${app.sizeMb} MB • RAM ~${app.ramRequirementMb} MB",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                                IconButton(
                                    onClick = { onUninstall(app) },
                                    modifier = Modifier.size(32.dp).testTag("uninstall_icon_${app.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Uninstall",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-COMPONENTS
// -------------------------------------------------------------
@Composable
private fun FeaturedMiniAppCard(
    app: PlayStoreApp,
    onClick: () -> Unit,
    onInstall: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("featured_mini_app_${app.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(app.iconBgColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = app.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${app.sizeMb} MB",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (app.isInstalled) {
                Text(
                    text = "Installed",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldAccent,
                    fontWeight = FontWeight.Bold
                )
            } else if (app.downloadProgress != null) {
                LinearProgressIndicator(
                    progress = { app.downloadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(
                    onClick = onInstall,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                ) {
                    Text("Install", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PlayStoreAppRow(
    app: PlayStoreApp,
    onClick: () -> Unit,
    onInstall: () -> Unit,
    onUninstall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("app_row_${app.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(app.iconBgColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = app.iconEmoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${app.developer} • ${app.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${app.rating} ★",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "•  ${app.sizeMb} MB  •  ${app.downloadCount}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // If downloading, show progress bar
                if (app.downloadProgress != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { app.downloadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            AppActionButton(
                app = app,
                onInstall = onInstall,
                onUninstall = onUninstall
            )
        }
    }
}

@Composable
private fun AppActionButton(
    app: PlayStoreApp,
    onInstall: () -> Unit,
    onUninstall: () -> Unit
) {
    if (app.downloadProgress != null) {
        val pct = (app.downloadProgress * 100).toInt()
        Text(
            text = "$pct%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    } else if (app.isInstalled) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EmeraldAccent.copy(alpha = 0.15f),
                modifier = Modifier.height(30.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Open",
                        color = EmeraldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        Button(
            onClick = onInstall,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            modifier = Modifier
                .height(32.dp)
                .testTag("install_btn_${app.id}")
        ) {
            Text("Install", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// DIALOG: Full App Details
// -------------------------------------------------------------
@Composable
private fun AppDetailDialog(
    app: PlayStoreApp,
    onDismiss: () -> Unit,
    onInstall: () -> Unit,
    onUninstall: () -> Unit,
    onOpenFreeFire: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(app.iconBgColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = app.iconEmoji, fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = app.developer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stats Row: Rating, Size, Downloads
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${app.rating} ★",
                            fontWeight = FontWeight.Bold,
                            color = AmberAccent,
                            fontSize = 14.sp
                        )
                        Text(text = app.reviewsCount, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${app.sizeMb} MB",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(text = "App Size", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = app.downloadCount,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(text = "Downloads", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Description
                Text(
                    text = "About this app",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // What's New
                Text(
                    text = "What's new (${app.version})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = app.whatIsNew,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Play Protect Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldAccent.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Safe",
                            tint = EmeraldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verified by Play Protect • No harmful permissions",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Impact on Hardware
                Text(
                    text = "Hardware Impact: ~${app.sizeMb} MB of 128GB ROM • ~${app.ramRequirementMb} MB on 8GB RAM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (app.id == "free_fire") {
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenFreeFire()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00))
                    ) {
                        Text("Play (PC Set) 🔥", color = Color.White)
                    }
                }
                if (app.isInstalled) {
                    OutlinedButton(
                        onClick = onUninstall,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Uninstall")
                    }
                } else {
                    Button(onClick = onInstall) {
                        Text("Install (${app.sizeMb} MB)")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
