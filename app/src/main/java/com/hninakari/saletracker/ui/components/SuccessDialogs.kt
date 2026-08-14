package com.hninakari.saletracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.Expense
import com.hninakari.saletracker.data.model.Sale
import com.hninakari.saletracker.data.model.Transfer
import com.hninakari.saletracker.data.model.TransferDirection
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SuccessDialogs(
    showSaleSuccess: Boolean,
    showExpenseSuccess: Boolean,
    showTransferSuccess: Boolean,
    lastSale: Sale?,
    lastExpense: Expense?,
    lastTransfer: Transfer?,
    onSaleDismiss: () -> Unit,
    onExpenseDismiss: () -> Unit,
    onTransferDismiss: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    // Sale Success Dialog
    if (showSaleSuccess && lastSale != null) {
        val sale = lastSale!!
        val time = timeFormat.format(Date(sale.date))
        
        AlertDialog(
            onDismissRequest = onSaleDismiss,
            title = {
                Text(
                    text = stringResource(R.string.sale_added),
                    fontSize = 20.sp,
                    color = Primary
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = String.format(stringResource(R.string.amount_label), String.format("%.2f", sale.amount)),
                        fontSize = 24.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = String.format(stringResource(R.string.payment), sale.paymentType.name),
                        fontSize = 16.sp,
                        color = TextPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = stringResource(R.string.time) + ": $time",
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onSaleDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
    
    // Expense Success Dialog
    if (showExpenseSuccess && lastExpense != null) {
        val expense = lastExpense!!
        val time = timeFormat.format(Date(expense.date))
        
        AlertDialog(
            onDismissRequest = onExpenseDismiss,
            title = {
                Text(
                    text = stringResource(R.string.expense_added),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = String.format(stringResource(R.string.amount_label), String.format("%.2f", expense.amount)),
                        fontSize = 24.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = String.format(stringResource(R.string.category_label), expense.category.name),
                        fontSize = 16.sp,
                        color = TextPrimary.copy(alpha = 0.8f)
                    )
                    if (expense.description.isNotEmpty()) {
                        Text(
                            text = "Description: ${expense.description}",
                            fontSize = 14.sp,
                            color = TextPrimary.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = stringResource(R.string.time) + ": $time",
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onExpenseDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
    
    // Transfer Success Dialog
    if (showTransferSuccess && lastTransfer != null) {
        val transfer = lastTransfer!!
        val time = timeFormat.format(Date(transfer.date))
        val directionText = if (transfer.direction == TransferDirection.OUT) "Transfer Out" else "Transfer In"
        
        AlertDialog(
            onDismissRequest = onTransferDismiss,
            title = {
                Text(
                    text = stringResource(R.string.transfer_added),
                    fontSize = 20.sp,
                    color = Primary
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$directionText - ${transfer.service.name}",
                        fontSize = 16.sp,
                        color = Primary
                    )
                    Text(
                        text = "Amount: $${String.format("%.2f", transfer.amount)}",
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Fee: $${String.format("%.2f", transfer.fee)}",
                        fontSize = 18.sp,
                        color = Primary
                    )
                    Text(
                        text = stringResource(R.string.time) + ": $time",
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onTransferDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
