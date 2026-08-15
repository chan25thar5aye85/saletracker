package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.data.model.PaymentType
import com.hninakari.saletracker.data.model.Sale
import com.hninakari.saletracker.viewmodel.SaleViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SaleEntryScreen(
    saleViewModel: SaleViewModel? = null,
    onSaleAdded: (Sale) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    
    val viewModel = saleViewModel ?: return
    
    val recentSales by viewModel.allSales.collectAsState(initial = emptyList())
    
    var amount by remember { mutableStateOf("") }
    var selectedPaymentType by remember { mutableStateOf(PaymentType.CASH) }
    var amountError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Check if form is valid
    val isFormValid = remember(amount) {
        val cleanAmount = amount.toDoubleOrNull()
        cleanAmount != null && cleanAmount > 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Payment Type - Label + Radio buttons in ONE ROW
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

            // Cash - Clickable Row
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
                    modifier = Modifier.size(35.dp)  // Increased to 35.dp
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

            // KPay - Clickable Row
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
                    modifier = Modifier.size(35.dp)  // Increased to 35.dp
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

            // WavePay - Clickable Row
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
                    modifier = Modifier.size(35.dp)  // Increased to 35.dp
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

        Spacer(modifier = Modifier.height(16.dp))

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
                .height(52.dp),
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Please enter a valid amount",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

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

        // ============================================================
        // RECENT SALES HISTORY (SHOW ONLY 4)
        // ============================================================
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Divider(
            color = colorScheme.onSurface.copy(alpha = 0.2f),
            thickness = 1.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // History Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📋 Recent Sales",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
            
            Text(
                text = "Last 4 entries",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (recentSales.isEmpty()) {
            // Empty state
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
                        text = "No sales yet",
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Get the most recent 4 sales (or fewer if less exist)
            val displaySales = recentSales
                .filter { !it.isDeleted }
                .sortedByDescending { it.date }
                .take(4)
            
            // Table Header
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
                Text(
                    text = "#",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier.width(30.dp)
                )
                Text(
                    text = "Amount",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Payment",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Time",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier.weight(1.2f)
                )
            }
            
            // Table Rows
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
                    Text(
                        text = "${displaySales.size - index}",
                        fontSize = 13.sp,
                        color = colorScheme.onSurface,
                        modifier = Modifier.width(30.dp)
                    )
                    Text(
                        text = "${sale.amount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = when (sale.paymentType) {
                            PaymentType.CASH -> "💵 Cash"
                            PaymentType.KPAY -> "📱 KPay"
                            PaymentType.WAVEPAY -> "📱 WavePay"
                        },
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = dateFormat.format(Date(sale.date)),
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}
