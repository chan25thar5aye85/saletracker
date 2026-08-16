package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.data.model.Tag
import com.hninakari.saletracker.viewmodel.TagViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TagListScreen(
    viewModel: TagViewModel,
    onAddTag: () -> Unit,
    onEditTag: (Tag) -> Unit,
    onDeleteTag: (Tag) -> Unit
) {
    val tags by viewModel.allTags.collectAsState(initial = emptyList())
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    
    var searchQuery by remember { mutableStateOf("") }
    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }

    val filteredTags = remember(tags, searchQuery) {
        if (searchQuery.isEmpty()) tags
        else tags.filter { it.name.lowercase().contains(searchQuery.lowercase()) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            // Title
            Text(
                text = "🏷️ Tags",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search tags...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )

            if (filteredTags.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🏷️", fontSize = 48.sp)
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No tags found" else "No tags yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Tap + to add a tag",
                                fontSize = 14.sp,
                                color = colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTags) { tag ->
                        TagItem(
                            tag = tag,
                            onEdit = { onEditTag(tag) },
                            onDelete = { onDeleteTag(tag) }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onAddTag,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset { 
                    IntOffset(
                        x = fabOffsetX.roundToInt(),
                        y = fabOffsetY.roundToInt() - 200.dp.value.roundToInt()
                    )
                }
                .padding(16.dp)
                .size(56.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            fabOffsetX += dragAmount.x * 0.8f
                            fabOffsetY += dragAmount.y * 0.8f
                        },
                        onDragEnd = {
                            scope.launch {
                                delay(100)
                                val steps = 20
                                val duration = 150
                                val stepDuration = duration / steps
                                for (i in 1..steps) {
                                    val progress = 1f - (i.toFloat() / steps)
                                    val easedProgress = progress * progress
                                    fabOffsetX = fabOffsetX * easedProgress
                                    fabOffsetY = fabOffsetY * easedProgress
                                    delay(stepDuration.toLong())
                                }
                                fabOffsetX = 0f
                                fabOffsetY = 0f
                            }
                        }
                    )
                },
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Tag", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun TagItem(
    tag: Tag,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Color indicator
                val color = try {
                    Color(tag.color?.removePrefix("#")?.toInt(16) ?: 0x7B1FA2)
                } catch (e: Exception) {
                    Color(0xFF7B1FA2)
                }
                
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = color,
                            shape = CircleShape
                        )
                )
                
                Text(
                    text = tag.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colorScheme.error.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
