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
import com.hninakari.saletracker.data.model.Debt
import com.hninakari.saletracker.data.model.DebtType
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.viewmodel.DebtViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PersonDetailScreen(
    person: Person,
    debtViewModel: DebtViewModel,
    onBack: () -> Unit = {},
    onAddDebt: (Int) -> Unit = {},
    onPayDebt: (Debt) -> Unit = {},
    onPayAllDebt: (Debt) -> Unit = {},
    onViewHistory: (Int) -> Unit = {}
) {
    val debts by debtViewModel
        .getActiveDebtsForPerson(person.id)
        .collectAsState(initial = emptyList())

    val totalOwedToMe = debts
        .filter { it.type == DebtType.OWED_TO_ME }
        .sumOf { it.amount }

    val totalIOwe = debts
        .filter { it.type == DebtType.I_OWE }
        .sumOf { it.amount }

    val netDebt = totalOwedToMe - totalIOwe

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = person.phone.ifEmpty {
                stringResource(R.string.no_phone)
            },
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // ----------------------------------------------------
            // OWED TO ME
            // ----------------------------------------------------

            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(10.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        stringResource(R.string.owed_to_me),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        "$${String.format("%.2f", totalOwedToMe)}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ----------------------------------------------------
            // I OWE
            // ----------------------------------------------------

            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(10.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        stringResource(R.string.i_owe),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        "$${String.format("%.2f", totalIOwe)}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --------------------------------------------------------
        // NET BALANCE
        // --------------------------------------------------------

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(10.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.net_balance),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val balanceColor =
                        if (netDebt >= 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = balanceColor,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Text(
                        "$${String.format("%.2f", netDebt)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = balanceColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --------------------------------------------------------
        // ACTIVE DEBTS
        // --------------------------------------------------------

        Text(
            text = "${stringResource(R.string.active_debts)} (${debts.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (debts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.3f
                    )
                )
            ) {
                Text(
                    text = stringResource(R.string.no_active_debts),
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(debts) { debt ->
                    DebtItem(
                        debt = debt,
                        debtViewModel = debtViewModel,
                        onPay = { onPayDebt(debt) },
                        onPayAll = { onPayAllDebt(debt) },
                        onHistory = { onViewHistory(debt.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun DebtItem(
    debt: Debt,
    debtViewModel: DebtViewModel,
    onPay: () -> Unit,
    onPayAll: () -> Unit,
    onHistory: () -> Unit
) {
    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    val isOwedToMe = debt.type == DebtType.OWED_TO_ME

    val totalPaid = debt.originalAmount - debt.amount

    val progress =
        if (debt.originalAmount > 0) {
            (totalPaid / debt.originalAmount).toFloat()
        } else {
            0f
        }

    val debtColor =
        if (isOwedToMe) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        }

    val debtContentColor =
        if (isOwedToMe) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onError
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                clip = false
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // ----------------------------------------------------
            // DEBT HEADER
            // ----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .shadow(
                                elevation = 1.dp,
                                shape = RoundedCornerShape(50),
                                clip = false
                            )
                            .background(
                                color = debtColor,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Text(
                        text = if (isOwedToMe) {
                            "⬆️ ${stringResource(R.string.owed_to_me)}"
                        } else {
                            "⬇️ ${stringResource(R.string.i_owe)}"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = debtColor
                    )
                }

                Text(
                    text = dateFormat.format(Date(debt.date)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.6f
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ----------------------------------------------------
            // AMOUNTS
            // ----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$${String.format("%.2f", debt.amount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = debtColor
                    )

                    Text(
                        text = stringResource(R.string.remaining),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", totalPaid)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = stringResource(R.string.paid),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ----------------------------------------------------
            // PROGRESS
            // ----------------------------------------------------

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = debtColor,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.08f
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ----------------------------------------------------
            // NOTE
            // ----------------------------------------------------

            if (debt.note.isNotEmpty()) {
                Text(
                    text = "📝 ${debt.note}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.7f
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            // ----------------------------------------------------
            // ACTIONS
            // ----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onHistory,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        stringResource(R.string.history),
                        fontSize = 11.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (debt.amount > 0) {
                        Button(
                            onClick = onPayAll,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = debtColor,
                                contentColor = debtContentColor
                            ),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                stringResource(R.string.pay_all),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = debtColor.copy(alpha = 0.8f),
                            contentColor = debtContentColor
                        ),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            stringResource(R.string.pay),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
