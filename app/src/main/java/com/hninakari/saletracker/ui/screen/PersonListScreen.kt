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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.data.model.PersonType
import com.hninakari.saletracker.viewmodel.PersonViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PersonListScreen(
    personViewModel: PersonViewModel,
    onPersonClick: (Person) -> Unit,
    onAddClick: () -> Unit
) {
    val allPeople by personViewModel.allPeople.collectAsState(initial = emptyList())
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var showArchived by remember { mutableStateOf(false) }
    
    // Filter people based on search and archived status
    val filteredPeople = remember(allPeople, searchQuery, showArchived) {
        allPeople.filter { person ->
            val matchesSearch = searchQuery.isEmpty() || 
                person.name.lowercase().contains(searchQuery.lowercase()) ||
                person.phone.contains(searchQuery)
            val matchesArchive = if (showArchived) {
                person.isDeleted
            } else {
                !person.isDeleted
            }
            matchesSearch && matchesArchive
        }
    }

    // FAB position state
    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            // Title
            Text(
                text = "👥 People",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search people...") },
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
            
            // Show archived toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show archived: ${if (showArchived) "✅" else "❌"}",
                    fontSize = 14.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = showArchived,
                    onCheckedChange = { showArchived = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorScheme.primary,
                        checkedTrackColor = colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredPeople.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No results found" else "No people yet",
                            fontSize = 16.sp,
                            color = colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Tap + to add someone",
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
                    items(filteredPeople) { person ->
                        PersonCard(
                            person = person,
                            onClick = { onPersonClick(person) },
                            showArchived = showArchived
                        )
                    }
                }
            }
        }

        // ============================================================
        // FLOATING ADD BUTTON (moved up 200dp)
        // ============================================================
        
        FloatingActionButton(
            onClick = onAddClick,
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Person",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun PersonCard(
    person: Person,
    onClick: () -> Unit,
    showArchived: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val isArchived = person.isDeleted
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = if (isArchived) 1.dp else 2.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isArchived) 
                colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isArchived) 
                            colorScheme.onSurface.copy(alpha = 0.2f)
                        else 
                            colorScheme.primary.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = person.name.take(1).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isArchived) 
                        colorScheme.onSurface.copy(alpha = 0.4f)
                    else 
                        colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Person info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = person.name,
                        fontSize = 16.sp,
                        fontWeight = if (isArchived) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (isArchived) 
                            colorScheme.onSurface.copy(alpha = 0.5f)
                        else 
                            colorScheme.onSurface
                    )
                    
                    if (isArchived) {
                        Text(
                            text = "📦 Archived",
                            fontSize = 10.sp,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .background(
                                    color = colorScheme.onSurface.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (person.type) {
                            PersonType.CUSTOMER -> "👤 Customer"
                            PersonType.SUPPLIER -> "🏢 Supplier"
                            PersonType.OTHER -> "👤 Other"
                        },
                        fontSize = 13.sp,
                        color = if (isArchived)
                            colorScheme.onSurface.copy(alpha = 0.4f)
                        else
                            colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    if (person.phone.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${person.phone}",
                            fontSize = 13.sp,
                            color = if (isArchived)
                                colorScheme.onSurface.copy(alpha = 0.3f)
                            else
                                colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Arrow icon
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isArchived)
                    colorScheme.onSurface.copy(alpha = 0.2f)
                else
                    colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
