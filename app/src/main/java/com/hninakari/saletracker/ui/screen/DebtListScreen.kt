package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.clickable
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
import com.hninakari.saletracker.data.model.DebtType
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.viewmodel.DebtViewModel
import com.hninakari.saletracker.viewmodel.PersonViewModel

@Composable
fun DebtListScreen(
    personViewModel: PersonViewModel,
    debtViewModel: DebtViewModel,
    onPersonClick: (Person) -> Unit = {}
) {
    val people by personViewModel.allPeople.collectAsState(initial = emptyList())
    val allDebts by debtViewModel.allDebts.collectAsState(initial = emptyList())
    
    val peopleWithDebt = people.map { person ->
        val personDebts = allDebts.filter { it.personId == person.id && !it.isPaid }
        val owedToMe = personDebts.filter { it.type == DebtType.OWED_TO_ME }.sumOf { it.amount }
        val iOwe = personDebts.filter { it.type == DebtType.I_OWE }.sumOf { it.amount }
        PersonDebtSummary(
            person = person,
            owedToMe = owedToMe,
            iOwe = iOwe,
            hasDebt = personDebts.isNotEmpty()
        )
    }.filter { it.hasDebt }
    
    val sortedPeople = peopleWithDebt.sortedByDescending { maxOf(it.owedToMe, it.iOwe) }
    
    val totalOwedToMe = sortedPeople.sumOf { it.owedToMe }
    val totalIOwe = sortedPeople.sumOf { it.iOwe }
    val totalNetDebt = totalOwedToMe - totalIOwe
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp), clip = false),
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
                    Text(stringResource(R.string.owed_to_me), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                    Text(
                        "$${String.format("%.2f", totalOwedToMe)}",
                        fontSize = 20.sp,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp), clip = false),
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
                    Text(stringResource(R.string.i_owe), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                    Text(
                        "$${String.format("%.2f", totalIOwe)}",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp), clip = false),
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
                    Text(stringResource(R.string.net_balance), fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.6f))
                    Text(
                        "$${String.format("%.2f", totalNetDebt)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalNetDebt >= 0) Primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "${stringResource(R.string.people)} ${sortedPeople.size}",
            fontSize = 14.sp,
            color = TextPrimary.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (sortedPeople.isEmpty()) {
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
                        text = stringResource(R.string.no_active_debts),
                        fontSize = 18.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.all_good),
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sortedPeople) { personDebt ->
                    DebtPersonCard(
                        personDebt = personDebt,
                        onClick = { onPersonClick(personDebt.person) }
                    )
                }
            }
        }
    }
}

data class PersonDebtSummary(
    val person: Person,
    val owedToMe: Double,
    val iOwe: Double,
    val hasDebt: Boolean
)

@Composable
fun DebtPersonCard(
    personDebt: PersonDebtSummary,
    onClick: () -> Unit
) {
    val netDebt = personDebt.owedToMe - personDebt.iOwe
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp), clip = false),
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
                Text(
                    text = personDebt.person.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${stringResource(R.string.owed_to_me)}: $${String.format("%.2f", personDebt.owedToMe)}",
                        fontSize = 13.sp,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${stringResource(R.string.i_owe)}: $${String.format("%.2f", personDebt.iOwe)}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", netDebt)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (netDebt >= 0) Primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = if (netDebt >= 0) stringResource(R.string.owed_to_me) else stringResource(R.string.i_owe),
                    fontSize = 12.sp,
                    color = TextPrimary.copy(alpha = 0.5f)
                )
            }
        }
    }
}
