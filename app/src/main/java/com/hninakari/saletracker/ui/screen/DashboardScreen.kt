package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.hninakari.saletracker.data.model.DebtType
import com.hninakari.saletracker.utils.DateUtils
import com.hninakari.saletracker.viewmodel.DebtViewModel
import com.hninakari.saletracker.viewmodel.ExpenseViewModel
import com.hninakari.saletracker.viewmodel.ProfitViewModel
import com.hninakari.saletracker.viewmodel.SaleViewModel
import com.hninakari.saletracker.viewmodel.ToBuyViewModel
import com.hninakari.saletracker.viewmodel.TransferViewModel

private fun Modifier.uniformCard(): Modifier = this
    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp), clip = false)

@Composable
fun DashboardScreen(
    saleViewModel: SaleViewModel,
    expenseViewModel: ExpenseViewModel,
    transferViewModel: TransferViewModel,
    profitViewModel: ProfitViewModel,
    debtViewModel: DebtViewModel? = null,
    toBuyViewModel: ToBuyViewModel? = null,
    onLanguageClick: () -> Unit = {},
    onDebtClick: () -> Unit = {},
    onToBuyClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val selectedFilter by saleViewModel.selectedFilter.collectAsState()
    val sales by saleViewModel.filteredSales.collectAsState(initial = emptyList())
    val expenses by expenseViewModel.filteredExpenses.collectAsState(initial = emptyList())
    val debts by debtViewModel?.allDebts?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    
    val toBuyItems by toBuyViewModel?.activeItemsWithDetails?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val toBuyCount = toBuyItems.size
    val toBuyTotal = toBuyItems.sumOf { 
        (it.product?.price ?: 0.0) * it.item.quantity 
    }
    
    val totalSales = sales.sumOf { it.amount }
    val totalExpenses = expenses.sumOf { it.amount }
    val profit = totalSales - totalExpenses
    
    val totalOwedToMe = debts.filter { !it.isPaid && it.type == DebtType.OWED_TO_ME }.sumOf { it.amount }
    val totalIOwe = debts.filter { !it.isPaid && it.type == DebtType.I_OWE }.sumOf { it.amount }
    val totalDebt = totalOwedToMe + totalIOwe
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedFilter) {
                        DateUtils.DateFilter.TODAY -> stringResource(R.string.today)
                        DateUtils.DateFilter.THIS_WEEK -> stringResource(R.string.this_week)
                        DateUtils.DateFilter.THIS_MONTH -> stringResource(R.string.this_month)
                        DateUtils.DateFilter.THIS_YEAR -> stringResource(R.string.this_year)
                        DateUtils.DateFilter.ALL_TIME -> stringResource(R.string.all_time)
                    },
                    fontSize = 14.sp,
                    color = TextPrimary.copy(alpha = 0.6f)
                )
                
                // Settings Button
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextPrimary.copy(alpha = 0.6f)
                    )
                }
            }
        }
        
        // Profit Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().uniformCard(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.total_profit), 
                        fontSize = 13.sp, 
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$${String.format("%.2f", profit)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (profit >= 0) Primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        
        // Sales and Expenses Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).uniformCard(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.sales), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                        Text(
                            "$${String.format("%.2f", totalSales)}", 
                            fontSize = 20.sp, 
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${sales.size} ${stringResource(R.string.entries)}", 
                            fontSize = 11.sp, 
                            color = TextPrimary.copy(alpha = 0.4f)
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f).uniformCard(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.expenses), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                        Text(
                            "$${String.format("%.2f", totalExpenses)}", 
                            fontSize = 20.sp, 
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${expenses.size} ${stringResource(R.string.entries)}", 
                            fontSize = 11.sp, 
                            color = TextPrimary.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
        
        // To Buy Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToBuyClick() }
                    .uniformCard(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🛒", fontSize = 18.sp)
                            Text(
                                text = stringResource(R.string.to_buy_list),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            if (toBuyCount > 0) {
                                Badge(
                                    containerColor = Primary,
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Text("$toBuyCount", fontSize = 10.sp)
                                }
                            }
                        }
                        Text(
                            text = "${stringResource(R.string.estimated_total)} $${String.format("%.2f", toBuyTotal)}",
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.5f)
                        )
                    }
                    
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = stringResource(R.string.to_buy),
                        tint = if (toBuyCount > 0) Primary else TextPrimary.copy(alpha = 0.3f)
                    )
                }
            }
        }
        
        // Debt Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDebtClick() }
                    .uniformCard(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💰", fontSize = 18.sp)
                            Text(
                                text = stringResource(R.string.debt_type),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            if (totalDebt > 0) {
                                Badge(
                                    containerColor = Primary,
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Text("${debts.filter { !it.isPaid }.size}", fontSize = 10.sp)
                                }
                            }
                        }
                        Text(
                            text = "${stringResource(R.string.owed_to_me)}/${stringResource(R.string.i_owe)} ${stringResource(R.string.total)}",
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.5f)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${String.format("%.2f", totalDebt)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalDebt > 0) Primary else TextPrimary.copy(alpha = 0.4f)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "${stringResource(R.string.owed_to_me)}: $${String.format("%.2f", totalOwedToMe)}",
                                fontSize = 11.sp,
                                color = Primary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${stringResource(R.string.i_owe)}: $${String.format("%.2f", totalIOwe)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
