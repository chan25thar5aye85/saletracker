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
import com.hninakari.saletracker.data.model.PaymentType
import com.hninakari.saletracker.data.model.Sale
import com.hninakari.saletracker.utils.DateUtils
import com.hninakari.saletracker.viewmodel.SaleViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SalesListScreen(
    saleViewModel: SaleViewModel
) {
    val selectedFilter by saleViewModel.selectedFilter.collectAsState()
    val sales by saleViewModel.filteredSales.collectAsState(initial = emptyList())
    val total = sales.sumOf { it.amount }
    
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.total_sales), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                    Text(
                        "$${String.format("%.2f", total)}",
                        fontSize = 24.sp,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.entries), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                    Text(
                        "${sales.size}",
                        fontSize = 24.sp,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (sales.isEmpty()) {
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
                    Text("💰", fontSize = 48.sp)
                    Text(
                        text = stringResource(R.string.no_sales_for_filter),
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
                items(sales) { sale ->
                    SaleItem(
                        sale = sale,
                        dateFormat = dateFormat
                    )
                }
            }
        }
    }
}

@Composable
fun SaleItem(
    sale: Sale,
    dateFormat: SimpleDateFormat
) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$${String.format("%.2f", sale.amount)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    text = when (sale.paymentType) {
                        PaymentType.CASH -> "💵 ${stringResource(R.string.cash)}"
                        PaymentType.KPAY -> "📱 ${stringResource(R.string.kpay)}"
                        PaymentType.WAVEPAY -> "📱 ${stringResource(R.string.wavepay)}"
                    },
                    fontSize = 13.sp,
                    color = TextPrimary.copy(alpha = 0.5f)
                )
            }
            Text(
                text = dateFormat.format(Date(sale.date)),
                fontSize = 12.sp,
                color = TextPrimary.copy(alpha = 0.4f)
            )
        }
    }
}
