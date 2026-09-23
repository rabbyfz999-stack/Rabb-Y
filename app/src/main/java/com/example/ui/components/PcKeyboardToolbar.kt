package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PcKeyboardToolbar(
    currentText: String,
    onTextChange: (String) -> Unit,
    onShortcutTriggered: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pc_keyboard_toolbar"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [CTRL + A] - Select All
            PcKeyCap(
                mainLabel = "Ctrl+A",
                subLabel = "SELECT ALL",
                accentColor = Color(0xFF00B0FF),
                testTag = "key_ctrl_a"
            ) {
                onShortcutTriggered?.invoke("SELECT_ALL")
            }

            // [CTRL + C] - Copy
            PcKeyCap(
                mainLabel = "Ctrl+C",
                subLabel = "COPY",
                accentColor = Color(0xFF00E676),
                testTag = "key_ctrl_c"
            ) {
                if (currentText.isNotEmpty()) {
                    val clip = ClipData.newPlainText("AI Mobile", currentText)
                    clipboard.setPrimaryClip(clip)
                }
                onShortcutTriggered?.invoke("COPY")
            }

            // [CTRL + V] - Paste
            PcKeyCap(
                mainLabel = "Ctrl+V",
                subLabel = "PASTE",
                accentColor = Color(0xFFFF9100),
                testTag = "key_ctrl_v"
            ) {
                val clip = clipboard.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val pasted = clip.getItemAt(0).text?.toString() ?: ""
                    onTextChange(currentText + pasted)
                }
                onShortcutTriggered?.invoke("PASTE")
            }

            // [CTRL + X] - Cut
            PcKeyCap(
                mainLabel = "Ctrl+X",
                subLabel = "CUT",
                accentColor = Color(0xFFFF5252),
                testTag = "key_ctrl_x"
            ) {
                if (currentText.isNotEmpty()) {
                    val clip = ClipData.newPlainText("AI Mobile", currentText)
                    clipboard.setPrimaryClip(clip)
                    onTextChange("")
                }
                onShortcutTriggered?.invoke("CUT")
            }

            // ESC key
            PcKeyButton(label = "ESC") {
                onTextChange("")
            }

            // TAB key
            PcKeyButton(label = "TAB") {
                onTextChange(currentText + "    ")
            }

            // UNDO key
            PcKeyButton(label = "UNDO") {
                if (currentText.isNotEmpty()) {
                    onTextChange(currentText.dropLast(1))
                }
            }

            // SYMBOLS
            PcKeyButton(label = "->") {
                onTextChange("$currentText -> ")
            }

            PcKeyButton(label = "{ }") {
                onTextChange("$currentText{}")
            }

            PcKeyButton(label = "[ ]") {
                onTextChange("$currentText[]")
            }

            PcKeyButton(label = "\" \"") {
                onTextChange("$currentText\"\"")
            }

            // CLEAR
            PcKeyButton(label = "CLR") {
                onTextChange("")
            }
        }
    }
}

@Composable
private fun PcKeyCap(
    mainLabel: String,
    subLabel: String,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = mainLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = accentColor
            )
            Text(
                text = subLabel,
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PcKeyButton(
    label: String,
    isAccent: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isAccent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            )
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (isAccent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
