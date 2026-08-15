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
import com.hninakari.saletracker.data.model.Expense
import com.hninakari.saletracker.data.model.ExpenseType
import com.hninakari.saletracker.utils.DateUtils
import com.hninakari.saletracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpenseListScreen(
    expenseViewModel: ExpenseViewModel
) {
    val selectedFilter by expenseViewModel.selectedFilter.collectAsState()
    val expenses by expenseViewModel.filteredExpenses.collectAsState(initial = emptyList())
    val businessTotal by expenseViewModel.filteredBusinessTotal.collectAsState(initial = 0.0)
    val personalTotal by expenseViewModel.filteredPersonalTotal.collectAsState(initial = 0.0)
    val total = businessTotal + personalTotal
    
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
                    Text(stringResource(R.string.business), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(
                        "$${String.format("%.2f", businessTotal)}",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
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
                    Text(stringResource(R.string.personal), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(
                        "$${String.format("%.2f", personalTotal)}",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (expenses.isEmpty()) {
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
                    Text("💸", fontSize = 48.sp)
                    Text(
                        text = stringResource(R.string.no_expenses_for_filter),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.try_changing_filter),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        dateFormat = dateFormat
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
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
                                if (expense.type == ExpenseType.BUSINESS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                RoundedCornerShape(50)
                            )
                    )
                    Text(
                        text = if (expense.type == ExpenseType.BUSINESS) "🏢 စီးပွားရေး" else "👤 ကိုယ်ပိုင်",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (expense.type == ExpenseType.BUSINESS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                
                Text(
                    text = dateFormat.format(Date(expense.date)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", expense.amount)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (expense.type == ExpenseType.BUSINESS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = expense.category.name.replace("_", " ").lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            
            if (expense.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 ${expense.description}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
