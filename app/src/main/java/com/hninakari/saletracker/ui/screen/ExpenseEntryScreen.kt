package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.data.model.Expense
import com.hninakari.saletracker.data.model.ExpenseCategory
import com.hninakari.saletracker.data.model.ExpenseType
import com.hninakari.saletracker.utils.DateUtils
import com.hninakari.saletracker.utils.NumberUtils
import com.hninakari.saletracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun getCategoryDisplayName(category: ExpenseCategory): String {
    return when (category) {
        ExpenseCategory.INVENTORY -> "ကုန်ပစ္စည်း"
        ExpenseCategory.RENT -> "ငှားရမ်းခ"
        ExpenseCategory.SALARY -> "လစာ"
        ExpenseCategory.UTILITIES -> "အသုံးစရိတ်"
        ExpenseCategory.TRANSPORT -> "သယ်ယူပို့ဆောင်ရေး"
        ExpenseCategory.MARKETING -> "စျေးကွက်ရှာဖွေရေး"
        ExpenseCategory.MAINTENANCE -> "ပြုပြင်ထိန်းသိမ်းရေး"
        ExpenseCategory.OTHER -> "အခြား"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    expenseViewModel: ExpenseViewModel? = null,
    onExpenseAdded: (Expense) -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    val viewModel = expenseViewModel ?: return
    val colorScheme = MaterialTheme.colorScheme
    
    val allExpenses by viewModel.allExpenses.collectAsState(initial = emptyList())
    
    val todayExpenses = remember(allExpenses) {
        val todayStart = DateUtils.getFilterStartTime(DateUtils.DateFilter.TODAY)
        allExpenses.filter { 
            !it.isDeleted && it.date >= todayStart
        }
    }
    
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.OTHER) }
    var selectedType by remember { mutableStateOf(ExpenseType.BUSINESS) }
    var description by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "💸 Add Expense",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // ============================================================
            // FORM CARD
            // ============================================================
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Expense Type
                    Text(
                        text = stringResource(R.string.expense_type),
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedType == ExpenseType.BUSINESS,
                            onClick = { selectedType = ExpenseType.BUSINESS },
                            label = {
                                Text("🏢 ${stringResource(R.string.business)}", fontSize = 13.sp)
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor = colorScheme.primary
                            )
                        )

                        FilterChip(
                            selected = selectedType == ExpenseType.PERSONAL,
                            onClick = { selectedType = ExpenseType.PERSONAL },
                            label = {
                                Text("👤 ${stringResource(R.string.personal)}", fontSize = 13.sp)
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.error.copy(alpha = 0.2f),
                                selectedLabelColor = colorScheme.error
                            )
                        )
                    }

                    // Amount
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            val englishDigits = NumberUtils.toEnglishDigits(it)
                            val filtered = englishDigits.filter { char ->
                                char.isDigit() || char == '.'
                            }
                            amount = filtered
                            amountError = false
                        },
                        label = {
                            Text(stringResource(R.string.amount), fontSize = 12.sp)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        isError = amountError,
                        supportingText = {
                            if (amountError) {
                                Text(
                                    text = "Enter valid amount",
                                    fontSize = 11.sp,
                                    color = colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )

                    // Category Dropdown
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = getCategoryDisplayName(selectedCategory),
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(stringResource(R.string.category), fontSize = 12.sp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            ExpenseCategory.values().forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            getCategoryDisplayName(category),
                                            fontSize = 14.sp
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                        focusManager.clearFocus()
                                    }
                                )
                            }
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = {
                            Text(
                                stringResource(R.string.description_optional),
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )

                    // Add Expense Button
                    Button(
                        onClick = {
                            val cleanAmount = NumberUtils.toDouble(amount)

                            if (cleanAmount == null || cleanAmount <= 0.0) {
                                amountError = true
                            } else {
                                val expense = Expense(
                                    amount = cleanAmount,
                                    category = selectedCategory,
                                    type = selectedType,
                                    description = description.trim()
                                )

                                onExpenseAdded(expense)

                                amount = ""
                                description = ""
                                selectedCategory = ExpenseCategory.OTHER
                                selectedType = ExpenseType.BUSINESS
                                amountError = false

                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.add_expense_button),
                            fontSize = 16.sp
                        )
                    }

                    // Clear Button
                    TextButton(
                        onClick = {
                            amount = ""
                            description = ""
                            selectedCategory = ExpenseCategory.OTHER
                            selectedType = ExpenseType.BUSINESS
                            amountError = false
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.clear),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Today's Expenses History
            Spacer(modifier = Modifier.height(24.dp))
            
            Divider(
                color = colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 Today's Expenses",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                
                Text(
                    text = "${todayExpenses.size} entries",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (todayExpenses.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No expenses today",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val displayExpenses = todayExpenses
                    .sortedByDescending { it.date }
                    .take(4)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("#", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.width(30.dp))
                    Text("Amount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.weight(1f))
                    Text("Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.weight(0.6f))
                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.weight(1f))
                }
                
                displayExpenses.forEachIndexed { index, expense ->
                    val rowColor = if (index % 2 == 0) {
                        colorScheme.surface
                    } else {
                        colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowColor)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${displayExpenses.size - index}", fontSize = 13.sp, color = colorScheme.onSurface, modifier = Modifier.width(30.dp))
                        Text("${expense.amount}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colorScheme.onSurface, modifier = Modifier.weight(1f))
                        Text(
                            when (expense.type) {
                                ExpenseType.BUSINESS -> "🏢"
                                ExpenseType.PERSONAL -> "👤"
                            },
                            fontSize = 16.sp,
                            modifier = Modifier.weight(0.6f)
                        )
                        Text(
                            getCategoryDisplayName(expense.category),
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }

        // ============================================================
        // FLOATING HISTORY BUTTON (moved up 200dp)
        // ============================================================
        
        FloatingActionButton(
            onClick = onHistoryClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset { 
                    IntOffset(
                        x = fabOffsetX.roundToInt(),
                        y = fabOffsetY.roundToInt() - 200.dp.value.roundToInt()
                    )
                }
                .padding(16.dp)
                .size(52.dp)
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
            containerColor = colorScheme.primary.copy(alpha = 0.85f),
            contentColor = colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "View All Expense History",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
