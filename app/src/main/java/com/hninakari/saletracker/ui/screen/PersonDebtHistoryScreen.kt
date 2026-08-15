package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.hninakari.saletracker.data.model.Debt
import com.hninakari.saletracker.data.model.DebtType
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.viewmodel.DebtViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PersonDebtHistoryScreen(
    person: Person,
    debtViewModel: DebtViewModel,
    onBack: () -> Unit
) {
    // Get all debts for this person (both active and paid)
    val allDebts by debtViewModel.getDebtsForPerson(person.id).collectAsState(initial = emptyList())
    
    val colorScheme = MaterialTheme.colorScheme
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Separate into active and paid debts
    val activeDebts = allDebts.filter { !it.isPaid }
    val paidDebts = allDebts.filter { it.isPaid }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ============================================================
            // HEADER WITH BACK BUTTON AND TITLE
            // ============================================================
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.onSurface
                        )
                    }
                    
                    Text(
                        text = "📜 ${person.name}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }
                
                // Person type badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = when (person.type) {
                            com.hninakari.saletracker.data.model.PersonType.CUSTOMER -> "👤 Customer"
                            com.hninakari.saletracker.data.model.PersonType.SUPPLIER -> "🏢 Supplier"
                            com.hninakari.saletracker.data.model.PersonType.OTHER -> "👤 Other"
                        },
                        fontSize = 12.sp,
                        color = colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // Phone
            if (person.phone.isNotEmpty()) {
                Text(
                    text = "📞 ${person.phone}",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 56.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ============================================================
            // ACTIVE DEBTS
            // ============================================================
            
            Text(
                text = "Active Debts (${activeDebts.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            if (activeDebts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "No active debts",
                        modifier = Modifier.padding(12.dp),
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.height(150.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(activeDebts) { debt ->
                        DebtHistoryItem(
                            debt = debt,
                            dateFormat = dateFormat,
                            colorScheme = colorScheme,
                            isActive = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ============================================================
            // PAID DEBTS (HISTORY)
            // ============================================================
            
            Text(
                text = "Paid Debts (${paidDebts.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            if (paidDebts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "No paid debts yet",
                        modifier = Modifier.padding(12.dp),
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(paidDebts) { debt ->
                        DebtHistoryItem(
                            debt = debt,
                            dateFormat = dateFormat,
                            colorScheme = colorScheme,
                            isActive = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DebtHistoryItem(
    debt: Debt,
    dateFormat: SimpleDateFormat,
    colorScheme: ColorScheme,
    isActive: Boolean
) {
    val debtColor =
        if (debt.type == DebtType.OWED_TO_ME) {
            colorScheme.primary
        } else {
            colorScheme.error
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(8.dp),
                clip = false
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (debt.type == DebtType.OWED_TO_ME) "⬆️" else "⬇️",
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (debt.type == DebtType.OWED_TO_ME) {
                            stringResource(R.string.owed_to_me)
                        } else {
                            stringResource(R.string.i_owe)
                        },
                        fontSize = 12.sp,
                        color = debtColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (debt.note.isNotEmpty()) {
                    Text(
                        text = "📝 ${debt.note}",
                        fontSize = 10.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                Text(
                    text = dateFormat.format(Date(debt.date)),
                    fontSize = 10.sp,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${String.format("%.2f", debt.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = debtColor
                )
                
                if (debt.originalAmount > debt.amount && !debt.isPaid) {
                    Text(
                        text = "Paid: $${String.format("%.2f", debt.originalAmount - debt.amount)}",
                        fontSize = 10.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                
                if (debt.isPaid) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Paid",
                            modifier = Modifier.size(12.dp),
                            tint = colorScheme.primary
                        )
                        Text(
                            text = "Paid",
                            fontSize = 10.sp,
                            color = colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
