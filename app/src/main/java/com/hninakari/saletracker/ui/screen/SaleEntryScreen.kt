package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.PaymentType
import com.hninakari.saletracker.data.model.Sale

@Composable
fun SaleEntryScreen(
    onSaleAdded: (Sale) -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    var selectedPaymentType by remember { mutableStateOf(PaymentType.CASH) }
    var amountError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = { 
                // Only allow digits and decimal point
                val filtered = it.filter { char -> char.isDigit() || char == '.' }
                amount = filtered
                amountError = false
            },
            label = { Text(stringResource(R.string.amount)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = amountError,
            supportingText = {
                if (amountError) {
                    Text("Enter valid amount", fontSize = 12.sp)
                }
            },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = stringResource(R.string.payment_type),
            fontSize = 14.sp,
            color = TextPrimary.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedPaymentType = PaymentType.CASH },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPaymentType == PaymentType.CASH) Primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (selectedPaymentType == PaymentType.CASH) MaterialTheme.colorScheme.onPrimary else TextPrimary
                )
            ) {
                Text(stringResource(R.string.cash))
            }
            Button(
                onClick = { selectedPaymentType = PaymentType.KPAY },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPaymentType == PaymentType.KPAY) Primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (selectedPaymentType == PaymentType.KPAY) MaterialTheme.colorScheme.onPrimary else TextPrimary
                )
            ) {
                Text(stringResource(R.string.kpay))
            }
            Button(
                onClick = { selectedPaymentType = PaymentType.WAVEPAY },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPaymentType == PaymentType.WAVEPAY) Primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (selectedPaymentType == PaymentType.WAVEPAY) MaterialTheme.colorScheme.onPrimary else TextPrimary
                )
            ) {
                Text(stringResource(R.string.wavepay))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                val cleanAmount = amount.toDoubleOrNull()
                if (cleanAmount == null || cleanAmount <= 0.0) {
                    amountError = true
                } else {
                    val sale = Sale(
                        amount = cleanAmount,
                        paymentType = selectedPaymentType
                    )
                    onSaleAdded(sale)
                    amount = ""
                    selectedPaymentType = PaymentType.CASH
                    amountError = false
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(stringResource(R.string.add_sale_button), fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        TextButton(
            onClick = {
                amount = ""
                selectedPaymentType = PaymentType.CASH
                amountError = false
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.clear), fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}
