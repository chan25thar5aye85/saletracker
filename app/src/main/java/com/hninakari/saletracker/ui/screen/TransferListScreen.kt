package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.hninakari.saletracker.data.model.Transfer
import com.hninakari.saletracker.data.model.TransferDirection
import com.hninakari.saletracker.data.model.TransferService
import com.hninakari.saletracker.utils.DateUtils
import com.hninakari.saletracker.viewmodel.TransferViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransferListScreen(
    transferViewModel: TransferViewModel
) {
    val selectedFilter by transferViewModel.selectedFilter.collectAsState()
    val transfers by transferViewModel.filteredTransfers.collectAsState(initial = emptyList())
    val totalFees = transfers.sumOf { it.fee }
    
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Filter info
        Text(
            text = when (selectedFilter) {
                DateUtils.DateFilter.TODAY -> stringResource(R.string.today)
                DateUtils.DateFilter.THIS_WEEK -> stringResource(R.string.this_week)
                DateUtils.DateFilter.THIS_MONTH -> stringResource(R.string.this_month)
                DateUtils.DateFilter.THIS_YEAR -> stringResource(R.string.this_year)
                DateUtils.DateFilter.ALL_TIME -> stringResource(R.string.all_time)
            },
            fontSize = 14.sp,
            color = TextPrimary.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Summary - Unified style
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.total_fees), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                    Text(
                        "$${String.format("%.2f", totalFees)}",
                        fontSize = 20.sp,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.transactions), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                    Text(
                        "${transfers.size}",
                        fontSize = 20.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (transfers.isEmpty()) {
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
                    Text("🔄", fontSize = 48.sp)
                    Text(
                        text = stringResource(R.string.no_transfers_for_filter),
                        fontSize = 18.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.try_changing_filter),
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(transfers) { transfer ->
                    TransferItem(
                        transfer = transfer,
                        dateFormat = dateFormat
                    )
                }
            }
        }
    }
}

@Composable
fun TransferItem(
    transfer: Transfer,
    dateFormat: SimpleDateFormat
) {
    val isIn = transfer.direction == TransferDirection.IN
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status indicator
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(50),
                                clip = false
                            )
                            .background(
                                if (isIn) Primary else MaterialTheme.colorScheme.error,
                                RoundedCornerShape(50)
                            )
                    )
                    Text(
                        text = if (isIn) "📥 ဝင်ငွေ" else "📤 ထွက်ငွေ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isIn) Primary else MaterialTheme.colorScheme.error
                    )
                }
                
                Text(
                    text = when (transfer.service) {
                        TransferService.KPAY -> stringResource(R.string.kpay)
                        TransferService.WAVEPAY -> stringResource(R.string.wavepay)
                    },
                    fontSize = 12.sp,
                    color = TextPrimary.copy(alpha = 0.4f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", transfer.amount)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isIn) Primary else MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = "${stringResource(R.string.fee)}: $${String.format("%.2f", transfer.fee)}",
                    fontSize = 13.sp,
                    color = TextPrimary.copy(alpha = 0.5f)
                )
            }
            
            Text(
                text = dateFormat.format(Date(transfer.date)),
                fontSize = 12.sp,
                color = TextPrimary.copy(alpha = 0.4f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
