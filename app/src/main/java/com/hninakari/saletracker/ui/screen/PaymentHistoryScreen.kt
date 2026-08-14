package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.DebtPayment
import com.hninakari.saletracker.viewmodel.DebtViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentHistoryScreen(
    debtId: Int,
    debtViewModel: DebtViewModel,
    onBack: () -> Unit = {}
) {
    val payments by debtViewModel.getPaymentsForDebt(debtId).collectAsState(initial = emptyList())
    val allDebts by debtViewModel.allDebts.collectAsState(initial = emptyList())
    val debt = allDebts.find { it.id == debtId }
    val totalPaid = payments.sumOf { it.amount }
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            Text(
                text = stringResource(R.string.payment_history),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp), clip = false),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (debt != null) {
                    Text(
                        text = "${stringResource(R.string.total)}: $${String.format("%.2f", debt.originalAmount)}",
                        fontSize = 16.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(stringResource(R.string.total_paid), fontSize = 14.sp, color = TextPrimary.copy(alpha = 0.6f))
                Text(
                    "$${String.format("%.2f", totalPaid)}",
                    fontSize = 28.sp,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                Text("${payments.size} ${stringResource(R.string.payments)}", fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.4f))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (payments.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("💳", fontSize = 48.sp)
                    Text(
                        text = stringResource(R.string.no_payments_recorded),
                        fontSize = 18.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(payments) { payment ->
                    PaymentItem(
                        payment = payment,
                        dateFormat = dateFormat
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentItem(
    payment: DebtPayment,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp), clip = false),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$${String.format("%.2f", payment.amount)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                if (payment.note.isNotEmpty()) {
                    Text(
                        text = payment.note,
                        fontSize = 12.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                }
            }
            Text(
                text = dateFormat.format(Date(payment.date)),
                fontSize = 12.sp,
                color = TextPrimary.copy(alpha = 0.4f)
            )
        }
    }
}
