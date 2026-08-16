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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.data.model.Debt
import com.hninakari.saletracker.data.model.DebtType
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.viewmodel.DebtViewModel
import com.hninakari.saletracker.viewmodel.PersonViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun PersonDetailScreen(
    person: Person,
    debtViewModel: DebtViewModel,
    personViewModel: PersonViewModel,
    onBack: () -> Unit = {},
    onAddDebt: (Int) -> Unit = {},
    onPayDebt: (Debt) -> Unit = {},
    onPayAllDebt: (Debt) -> Unit = {},
    onViewHistory: (Person) -> Unit = {},
    onEditPerson: (Person) -> Unit = {}
) {
    val debts by debtViewModel
        .getActiveDebtsForPerson(person.id)
        .collectAsState(initial = emptyList())
    
    // Get all debts including paid for history count
    val allDebts by debtViewModel.getDebtsForPerson(person.id).collectAsState(initial = emptyList())
    val paidDebtsCount = allDebts.count { it.isPaid }

    val totalOwedToMe = debts
        .filter { it.type == DebtType.OWED_TO_ME }
        .sumOf { it.amount }

    val totalIOwe = debts
        .filter { it.type == DebtType.I_OWE }
        .sumOf { it.amount }

    val netDebt = totalOwedToMe - totalIOwe
    
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    
    // Show confirmation dialog
    var showArchiveDialog by remember { mutableStateOf(false) }

    // FAB position states
    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }
    var historyFabOffsetX by remember { mutableStateOf(0f) }
    var historyFabOffsetY by remember { mutableStateOf(0f) }

    // Solid background to prevent overlap
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 80.dp)
            ) {
                // ============================================================
                // HEADER WITH BACK BUTTON, TITLE, AND ACTIONS
                // ============================================================
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colorScheme.onSurface
                            )
                        }
                        
                        Text(
                            text = person.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    
                    // Action buttons (Edit and Archive)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Edit button
                        IconButton(
                            onClick = { onEditPerson(person) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Person",
                                tint = colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        
                        // Archive/Unarchive button
                        IconButton(
                            onClick = { showArchiveDialog = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                if (person.isDeleted) Icons.Default.Unarchive else Icons.Default.Archive,
                                contentDescription = if (person.isDeleted) "Unarchive" else "Archive",
                                tint = if (person.isDeleted) colorScheme.primary else colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Phone
                if (person.phone.isNotEmpty()) {
                    Text(
                        text = "📞 ${person.phone}",
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 56.dp, bottom = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // ============================================================
                // SUMMARY CARDS
                // ============================================================
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(10.dp),
                                clip = false
                            ),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = colorScheme.primary,
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                stringResource(R.string.owed_to_me),
                                fontSize = 10.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "$${String.format("%.2f", totalOwedToMe)}",
                                fontSize = 16.sp,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(10.dp),
                                clip = false
                            ),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = colorScheme.error,
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                stringResource(R.string.i_owe),
                                fontSize = 10.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "$${String.format("%.2f", totalIOwe)}",
                                fontSize = 16.sp,
                                color = colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Net Balance with Debt History count
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(10.dp),
                            clip = false
                        ),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.net_balance),
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Debt history count
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = "Debt history",
                                    modifier = Modifier.size(14.dp),
                                    tint = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${paidDebtsCount} paid",
                                    fontSize = 11.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                            
                            val balanceColor =
                                if (netDebt >= 0) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.error
                                }

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = balanceColor,
                                        shape = RoundedCornerShape(50)
                                    )
                            )

                            Text(
                                "$${String.format("%.2f", netDebt)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = balanceColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Debts
                Text(
                    text = "${stringResource(R.string.active_debts)} (${debts.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (debts.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant.copy(
                                alpha = 0.3f
                            )
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.no_active_debts),
                            modifier = Modifier.padding(12.dp),
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(debts) { debt ->
                            DebtItem(
                                debt = debt,
                                debtViewModel = debtViewModel,
                                onPay = { onPayDebt(debt) },
                                onPayAll = { onPayAllDebt(debt) },
                                onHistory = { onViewHistory(person) }
                            )
                        }
                    }
                }
            }

            // ============================================================
            // FLOATING ADD DEBT BUTTON (bottom-right, moved up 200dp)
            // ============================================================
            
            FloatingActionButton(
                onClick = { onAddDebt(person.id) },
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
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Debt",
                    modifier = Modifier.size(28.dp)
                )
            }

            // ============================================================
            // FLOATING HISTORY BUTTON - Opens PersonDebtHistoryScreen
            // ============================================================
            
            FloatingActionButton(
                onClick = { 
                    onViewHistory(person)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset { 
                        IntOffset(
                            x = historyFabOffsetX.roundToInt(),
                            y = historyFabOffsetY.roundToInt() - 400.dp.value.roundToInt()
                        )
                    }
                    .padding(16.dp)
                    .size(48.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                historyFabOffsetX += dragAmount.x * 0.8f
                                historyFabOffsetY += dragAmount.y * 0.8f
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
                                        historyFabOffsetX = historyFabOffsetX * easedProgress
                                        historyFabOffsetY = historyFabOffsetY * easedProgress
                                        delay(stepDuration.toLong())
                                    }
                                    historyFabOffsetX = 0f
                                    historyFabOffsetY = 0f
                                }
                            }
                        )
                    },
                containerColor = colorScheme.primary.copy(alpha = 0.85f),
                contentColor = colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp
                )
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "View Full Debt History",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
    
    // ============================================================
    // ARCHIVE/UNARCHIVE CONFIRMATION DIALOG
    // ============================================================
    
    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = {
                Text(
                    if (person.isDeleted) "Unarchive Person" else "Archive Person",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (person.isDeleted) {
                    Text("Unarchive this person and restore them to the active list?")
                } else {
                    Text("Archive this person? They will be hidden from the main list but can be viewed in archived mode.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (person.isDeleted) {
                            // Unarchive - restore
                            personViewModel.restorePerson(person.id)
                        } else {
                            // Archive - soft delete
                            personViewModel.deletePerson(person.id)
                        }
                        showArchiveDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (person.isDeleted) colorScheme.primary else colorScheme.error
                    )
                ) {
                    Text(if (person.isDeleted) "Unarchive" else "Archive")
                }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DebtItem(
    debt: Debt,
    debtViewModel: DebtViewModel,
    onPay: () -> Unit,
    onPayAll: () -> Unit,
    onHistory: () -> Unit
) {
    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    val isOwedToMe = debt.type == DebtType.OWED_TO_ME

    val totalPaid = debt.originalAmount - debt.amount

    val progress =
        if (debt.originalAmount > 0) {
            (totalPaid / debt.originalAmount).toFloat()
        } else {
            0f
        }

    val debtColor =
        if (isOwedToMe) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        }

    val debtContentColor =
        if (isOwedToMe) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onError
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                clip = false
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // Debt Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .shadow(
                                elevation = 1.dp,
                                shape = RoundedCornerShape(50),
                                clip = false
                            )
                            .background(
                                color = debtColor,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Text(
                        text = if (isOwedToMe) {
                            "⬆️ ${stringResource(R.string.owed_to_me)}"
                        } else {
                            "⬇️ ${stringResource(R.string.i_owe)}"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = debtColor
                    )
                }

                Text(
                    text = dateFormat.format(Date(debt.date)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.6f
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Amounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$${String.format("%.2f", debt.amount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = debtColor
                    )

                    Text(
                        text = stringResource(R.string.remaining),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", totalPaid)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = stringResource(R.string.paid),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Progress
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = debtColor,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.08f
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Note
            if (debt.note.isNotEmpty()) {
                Text(
                    text = "📝 ${debt.note}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.7f
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onHistory,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        stringResource(R.string.history),
                        fontSize = 11.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (debt.amount > 0) {
                        Button(
                            onClick = onPayAll,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = debtColor,
                                contentColor = debtContentColor
                            ),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                stringResource(R.string.pay_all),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = debtColor.copy(alpha = 0.8f),
                            contentColor = debtContentColor
                        ),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            stringResource(R.string.pay),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
