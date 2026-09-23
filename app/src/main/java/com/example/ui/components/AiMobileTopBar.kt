package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiClient
import com.example.data.model.GeminiTaskTier
import com.example.ui.theme.ModelDeepColor
import com.example.ui.theme.ModelFastColor
import com.example.ui.theme.ModelGeneralColor

@Composable
fun AiMobileTopBar(
    currentTier: GeminiTaskTier,
    onOpenModelSelector: () -> Unit,
    onOpenKeyInfo: () -> Unit,
    onOpenPcKeyboard: () -> Unit,
    onOpenPlayStore: () -> Unit,
    onOpenFreeFire: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tierColor = when (currentTier) {
        GeminiTaskTier.FAST -> ModelFastColor
        GeminiTaskTier.GENERAL, GeminiTaskTier.GENERAL_38 -> ModelGeneralColor
        GeminiTaskTier.DEEP -> ModelDeepColor
    }

    val isKeyConfigured = GeminiClient.isApiKeyConfigured()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Logo & Title
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Mobile Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Mobile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "RAM 8GB • ROM 128GB",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Free Fire Quick Game Launch Button
            IconButton(
                onClick = onOpenFreeFire,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("topbar_free_fire_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(Color(0xFFFF3D00), Color(0xFFFF9100))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Play Store Shortcut Button
            IconButton(
                onClick = onOpenPlayStore,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("topbar_play_store_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(Color(0xFF00E5FF), Color(0xFF00C853), Color(0xFFFFB300), Color(0xFFFF4081))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(2.dp))

            // PC Keyboard Bridge Button
            IconButton(
                onClick = onOpenPcKeyboard,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("pc_keyboard_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DesktopWindows,
                    contentDescription = "PC Keyboard Link",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Model Selection Chip
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenModelSelector() }
                    .testTag("model_selector_chip"),
                color = tierColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(tierColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentTier.badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = tierColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Change Model",
                        tint = tierColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Key Info Icon Button
            IconButton(
                onClick = onOpenKeyInfo,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("key_info_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "API Key Status",
                    tint = if (isKeyConfigured) ModelFastColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
