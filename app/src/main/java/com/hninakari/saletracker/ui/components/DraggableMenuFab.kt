package com.hninakari.saletracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DraggableMenuFab(
    onMenuSelected: (String) -> Unit,
    onTap: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    data class MenuItem(
        val id: String,
        val icon: ImageVector,
        val label: String
    )

    val menuItems = listOf(
        MenuItem("sale", Icons.Default.Add, "Sale"),
        MenuItem("expense", Icons.Default.Remove, "Expense"),
        MenuItem("transfer", Icons.Default.SwapHoriz, "Transfer"),
        MenuItem("people", Icons.Default.People, "People"),
        MenuItem("tobuy", Icons.Default.ShoppingCart, "To Buy"),
        MenuItem("settings", Icons.Default.Settings, "Settings")
    )

    Box(modifier = modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset {
                    IntOffset(
                        fabOffsetX.roundToInt(),
                        fabOffsetY.roundToInt()
                    )
                }
                .padding(start = 16.dp, bottom = 70.dp)
        ) {

            AnimatedVisibility(
                visible = isMenuExpanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(bottom = 64.dp)
            ) {
                Column(
                    modifier = Modifier
                        .width(170.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 4.dp)
                ) {
                    menuItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isMenuExpanded = false
                                    onMenuSelected(item.id)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (item.id == "settings") 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = item.label,
                                fontSize = 14.sp,
                                color = if (item.id == "settings")
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (index != menuItems.lastIndex) {
                            Divider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    if (!isDragging) {
                        onTap()
                        isMenuExpanded = !isMenuExpanded
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(56.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                isMenuExpanded = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                fabOffsetX += dragAmount.x
                                fabOffsetY += dragAmount.y
                            },
                            onDragEnd = {
                                isDragging = false
                                scope.launch {
                                    val startX = fabOffsetX
                                    val startY = fabOffsetY

                                    repeat(30) { step ->
                                        val t = (step + 1) / 30f
                                        val eased = (1f - t) * (1f - t)
                                        fabOffsetX = startX * eased
                                        fabOffsetY = startY * eased
                                        delay(10)
                                    }

                                    fabOffsetX = 0f
                                    fabOffsetY = 0f
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                fabOffsetX = 0f
                                fabOffsetY = 0f
                            }
                        )
                    },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isMenuExpanded)
                        Icons.Default.Close
                    else
                        Icons.Default.Menu,
                    contentDescription = null
                )
            }
        }
    }
}
