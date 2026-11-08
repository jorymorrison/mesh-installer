package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.CustomFirmwareFile
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CoralRed

/**
 * Full-width "install custom firmware" control for the Flasher screen.
 *
 * Before a file is chosen it is a single obvious button. After a file is loaded it becomes a card
 * showing the file, any compatibility warning, and Replace / Clear actions. Import errors are shown
 * inline instead of only going to the serial console log.
 */
@Composable
fun CustomFirmwareSection(
    customFile: CustomFirmwareFile?,
    isImporting: Boolean,
    importError: String?,
    boardName: String,
    onPickFile: () -> Unit,
    onClearFile: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (customFile == null) {
            OutlinedButton(
                onClick = onPickFile,
                enabled = !isImporting,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BlueAccent.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                    .testTag("import_custom_firmware_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isImporting) "Loading firmware file…" else "Install Custom Firmware",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueAccent
                        )
                        Text(
                            text = "Pick a .bin, .uf2 or .zip from your phone to flash to $boardName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        } else {
            LoadedCustomFirmwareCard(
                customFile = customFile,
                isImporting = isImporting,
                onReplace = onPickFile,
                onClear = onClearFile
            )
        }

        if (importError != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_firmware_import_error"),
                shape = RoundedCornerShape(10.dp),
                color = CoralRed.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, CoralRed.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp, end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = CoralRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = importError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDismissError) {
                        Text("OK", color = CoralRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadedCustomFirmwareCard(
    customFile: CustomFirmwareFile,
    isImporting: Boolean,
    onReplace: () -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_firmware_loaded_card"),
        shape = RoundedCornerShape(12.dp),
        color = BlueAccent.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, BlueAccent.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CUSTOM FIRMWARE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = customFile.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BlueAccent,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${formatSize(customFile.sizeBytes)} • ${customFile.format} • SHA256 ${customFile.sha256Hex.take(12)}…",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onClear() }
                        .testTag("clear_custom_firmware_button"),
                    color = CoralRed.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove custom firmware",
                            tint = CoralRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            customFile.warning?.let { warning ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AmberSignal,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = warning,
                        style = MaterialTheme.typography.bodySmall,
                        color = AmberSignal
                    )
                }
            }

            Text(
                text = "Pressing Flash will write this file instead of the selected release.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = onReplace,
                enabled = !isImporting,
                modifier = Modifier.testTag("replace_custom_firmware_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = BlueAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isImporting) "  Loading…" else "  Choose a different file",
                    color = BlueAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatSize(bytes: Long): String = when {
    bytes >= 1024L * 1024L -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    bytes >= 1024L -> "${bytes / 1024} KB"
    else -> "$bytes B"
}
