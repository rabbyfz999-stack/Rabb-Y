package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pc_keyboard_toolbar"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ESC key
            PcKeyButton(label = "ESC") {
                // Clear input
                onTextChange("")
            }

            // TAB key
            PcKeyButton(label = "TAB") {
                onTextChange(currentText + "    ")
            }

            // CTRL key
            PcKeyButton(label = "CTRL") {
                // Info visual indicator
            }

            // PASTE key
            PcKeyButton(label = "PASTE", isAccent = true) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboard.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val pasted = clip.getItemAt(0).text?.toString() ?: ""
                    onTextChange(currentText + pasted)
                }
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
