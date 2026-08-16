package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.data.model.Tag

@Composable
fun AddTagDialog(
    tag: Tag? = null,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(tag?.name ?: "") }
    var selectedColor by remember { mutableStateOf(tag?.color ?: "#7B1FA2") }
    
    val isEdit = tag != null
    val colors = listOf(
        "#7B1FA2", "#E91E63", "#F44336", "#FF5722", "#FF9800",
        "#4CAF50", "#2196F3", "#3F51B5", "#9C27B0", "#00BCD4",
        "#009688", "#8BC34A", "#FFEB3B", "#FFC107", "#795548",
        "#607D8B", "#9E9E9E"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "Edit Tag" else "Add Tag",
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tag Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                
                Text(
                    text = "Choose Color",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Color picker
                Column {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        colors.take(9).forEach { colorHex ->
                            ColorOption(
                                colorHex = colorHex,
                                isSelected = selectedColor == colorHex,
                                onClick = { selectedColor = colorHex }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        colors.drop(9).forEach { colorHex ->
                            ColorOption(
                                colorHex = colorHex,
                                isSelected = selectedColor == colorHex,
                                onClick = { selectedColor = colorHex }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, selectedColor)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(selectedColor.removePrefix("#").toInt(16))
                )
            ) {
                Text(if (isEdit) "Update" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ColorOption(
    colorHex: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = Color(colorHex.removePrefix("#").toInt(16))
    
    Box(
        modifier = Modifier
            .size(if (isSelected) 36.dp else 32.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 32.dp else 28.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}
