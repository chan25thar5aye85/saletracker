package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.hninakari.saletracker.data.model.PaymentType
import com.hninakari.saletracker.data.model.Sale
import com.hninakari.saletracker.utils.DateUtils
import com.hninakari.saletracker.viewmodel.SaleViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun SaleEntryScreen(
    saleViewModel: SaleViewModel? = null,
    onSaleAdded: (Sale) -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    
    val viewModel = saleViewModel ?: return
    
    val allSales by viewModel.allSales.collectAsState(initial = emptyList())
    
    // Filter only today's sales
    val todaySales = remember(allSales) {
        val todayStart = DateUtils.getFilterStartTime(DateUtils.DateFilter.TODAY)
        allSales.filter { 
            !it.isDeleted && it.date >= todayStart
        }
    }
    
    var amount by remember { mutableStateOf("") }
    var selectedPaymentType by remember { mutableStateOf(PaymentType.CASH) }
    var amountError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Check if form is valid
    val isFormValid = remember(amount) {
        val cleanAmount = amount.toDoubleOrNull()
        cleanAmount != null && cleanAmount > 0.0
    }

    // FAB position state
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
                text = "💰 Add Sale",
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
                    // Amount Field
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            val filtered = it.filter { char ->
                                char.isDigit() || char == '.'
                            }
                            amount = filtered
                            amountError = false
                        },
                        label = {
                            Text(text = stringResource(R.string.amount))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        isError = amountError,
                        supportingText = {
                            if (amountError) {
                                Text(
                                    text = stringResource(R.string.enter_valid_amount),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )

                    // Payment Type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.payment_type_short),
                            fontSize = 14.sp,
                            color = colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.width(90.dp),
                            maxLines = 1
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isLoading) {
                                    selectedPaymentType = PaymentType.CASH
                                },
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                selected = selectedPaymentType == PaymentType.CASH,
                                onClick = { 
                                    if (!isLoading) selectedPaymentType = PaymentType.CASH 
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorScheme.primary,
                                    unselectedColor = colorScheme.onSurfaceVariant
                                ),
                                enabled = !isLoading,
                                modifier = Modifier.size(35.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.cash),
                                fontSize = 14.sp,
                                color = if (selectedPaymentType == PaymentType.CASH) 
                                    colorScheme.primary 
                                else 
                                    colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isLoading) {
                                    selectedPaymentType = PaymentType.KPAY
                                },
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                selected = selectedPaymentType == PaymentType.KPAY,
                                onClick = { 
                                    if (!isLoading) selectedPaymentType = PaymentType.KPAY 
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorScheme.primary,
                                    unselectedColor = colorScheme.onSurfaceVariant
                                ),
                                enabled = !isLoading,
                                modifier = Modifier.size(35.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.kpay),
                                fontSize = 14.sp,
                                color = if (selectedPaymentType == PaymentType.KPAY) 
                                    colorScheme.primary 
                                else 
                                    colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isLoading) {
                                    selectedPaymentType = PaymentType.WAVEPAY
                                },
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                selected = selectedPaymentType == PaymentType.WAVEPAY,
                                onClick = { 
                                    if (!isLoading) selectedPaymentType = PaymentType.WAVEPAY 
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorScheme.primary,
                                    unselectedColor = colorScheme.onSurfaceVariant
                                ),
                                enabled = !isLoading,
                                modifier = Modifier.size(35.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.wavepay),
                                fontSize = 14.sp,
                                color = if (selectedPaymentType == PaymentType.WAVEPAY) 
                                    colorScheme.primary 
                                else 
                                    colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    // Add Sale Button
                    Button(
                        onClick = {
                            if (!isLoading) {
                                val cleanAmount = amount.toDoubleOrNull()

                                if (cleanAmount == null || cleanAmount <= 0.0) {
                                    amountError = true
                                } else {
                                    isLoading = true
                                    
                                    val sale = Sale(
                                        amount = cleanAmount,
                                        paymentType = selectedPaymentType
                                    )

                                    try {
                                        onSaleAdded(sale)
                                        
                                        amount = ""
                                        selectedPaymentType = PaymentType.CASH
                                        amountError = false
                                        focusManager.clearFocus()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = isFormValid && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid && !isLoading) 
                                colorScheme.primary 
                            else 
                                colorScheme.onSurface.copy(alpha = 0.3f),
                            contentColor = if (isFormValid && !isLoading) 
                                colorScheme.onPrimary 
                            else 
                                colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    ) {
                        when {
                            isLoading -> {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Saving...",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            else -> {
                                Text(
                                    text = stringResource(R.string.add_sale_button),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    if (!isFormValid && amount.isNotEmpty()) {
                        Text(
                            text = "Please enter a valid amount",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Clear Button
                    TextButton(
                        onClick = {
                            amount = ""
                            selectedPaymentType = PaymentType.CASH
                            amountError = false
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = stringResource(R.string.clear),
                            fontSize = 14.sp,
                            color = if (!isLoading) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            // Today's Sales History
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
                    text = "📋 Today's Sales",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                
                Text(
                    text = "${todaySales.size} entries",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (todaySales.isEmpty()) {
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
                            text = "No sales today",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val displaySales = todaySales
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
                    Text("Payment", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.weight(1f))
                    Text("Time", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.weight(1.2f))
                }
                
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                
                displaySales.forEachIndexed { index, sale ->
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
                        Text("${displaySales.size - index}", fontSize = 13.sp, color = colorScheme.onSurface, modifier = Modifier.width(30.dp))
                        Text("${sale.amount}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colorScheme.onSurface, modifier = Modifier.weight(1f))
                        Text(
                            when (sale.paymentType) {
                                PaymentType.CASH -> "💵 Cash"
                                PaymentType.KPAY -> "📱 KPay"
                                PaymentType.WAVEPAY -> "📱 WavePay"
                            },
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            dateFormat.format(Date(sale.date)),
                            fontSize = 11.sp,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.2f)
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
                contentDescription = "View All Sales History",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
