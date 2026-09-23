package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GeminiTaskTier
import com.example.ui.theme.ModelDeepColor
import com.example.ui.theme.ModelFastColor
import com.example.ui.theme.ModelGeneralColor

@Composable
fun ModelSelectorDialog(
    selectedTier: GeminiTaskTier,
    onTierSelected: (GeminiTaskTier) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("model_selector_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Intelligence Engine",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select active Gemini model tier",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ModelOptionCard(
                    tier = GeminiTaskTier.FAST,
                    title = "Flash-Lite 3.1 (Fast)",
                    modelId = "gemini-3.1-flash-lite-preview",
                    description = "Ultra-low latency for instant micro-edits, quick summaries, proofreading, and fast replies.",
                    icon = Icons.Default.Bolt,
                    color = ModelFastColor,
                    isSelected = selectedTier == GeminiTaskTier.FAST,
                    onClick = {
                        onTierSelected(GeminiTaskTier.FAST)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ModelOptionCard(
                    tier = GeminiTaskTier.GENERAL,
                    title = "Gemini 3.5 Flash (General)",
                    modelId = "gemini-3.5-flash",
                    description = "Well-rounded multimodal model for daily tasks, rich content creation, document synthesis & vision.",
                    icon = Icons.Default.Stars,
                    color = ModelGeneralColor,
                    isSelected = selectedTier == GeminiTaskTier.GENERAL,
                    onClick = {
                        onTierSelected(GeminiTaskTier.GENERAL)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ModelOptionCard(
                    tier = GeminiTaskTier.GENERAL_38,
                    title = "Gemini 3.8 Flash (General)",
                    modelId = "gemini-3.8-flash",
                    description = "Next-generation model specified for high-performance reasoning, advanced coding, and multimodal analysis.",
                    icon = Icons.Default.Stars,
                    color = ModelGeneralColor,
                    isSelected = selectedTier == GeminiTaskTier.GENERAL_38,
                    onClick = {
                        onTierSelected(GeminiTaskTier.GENERAL_38)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ModelOptionCard(
                    tier = GeminiTaskTier.DEEP,
                    title = "Gemini 3.1 Pro (Deep Complex)",
                    modelId = "gemini-3.1-pro-preview",
                    description = "Flagship reasoning engine for multi-step analysis, complex logic, research synthesis & strategic roadmaps.",
                    icon = Icons.Default.Psychology,
                    color = ModelDeepColor,
                    isSelected = selectedTier == GeminiTaskTier.DEEP,
                    onClick = {
                        onTierSelected(GeminiTaskTier.DEEP)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ModelOptionCard(
    tier: GeminiTaskTier,
    title: String,
    modelId: String,
    description: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("tier_${tier.name.lowercase()}_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, color)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(color),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Text(
                    text = modelId,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = color,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
