package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hninakari.saletracker.R
import com.hninakari.saletracker.data.model.DebtType
import com.hninakari.saletracker.utils.NumberUtils

@Composable
fun DebtEntryDialog(
    personName: String,
    onDismiss: () -> Unit,
    onAddDebt: (DebtType, Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedType by remember {
        mutableStateOf(DebtType.OWED_TO_ME)
    }
    var note by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 450.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ------------------------------------------------
                // TITLE
                // ------------------------------------------------

                Text(
                    text = "${stringResource(R.string.add_debt)} - $personName",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ------------------------------------------------
                // AMOUNT
                // ------------------------------------------------

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        val englishDigits =
                            NumberUtils.toEnglishDigits(it)

                        val filtered = englishDigits.filter { char ->
                            char.isDigit() || char == '.'
                        }

                        amount = filtered
                        amountError = false
                    },
                    label = {
                        Text(
                            stringResource(R.string.amount),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = amountError,
                    supportingText = {
                        if (amountError) {
                            Text(
                                stringResource(R.string.required),
                                fontSize = 10.sp
                            )
                        }
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor =
                            MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor =
                            MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.3f
                            )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ------------------------------------------------
                // DEBT TYPE
                // ------------------------------------------------

                Text(
                    text = stringResource(R.string.debt_type),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected =
                            selectedType == DebtType.OWED_TO_ME,
                        onClick = {
                            selectedType = DebtType.OWED_TO_ME
                        },
                        label = {
                            Text(
                                stringResource(R.string.owed_to_me),
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor =
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.2f
                                ),
                            selectedLabelColor =
                                MaterialTheme.colorScheme.primary
                        )
                    )

                    FilterChip(
                        selected =
                            selectedType == DebtType.I_OWE,
                        onClick = {
                            selectedType = DebtType.I_OWE
                        },
                        label = {
                            Text(
                                stringResource(R.string.i_owe),
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor =
                                MaterialTheme.colorScheme.error.copy(
                                    alpha = 0.2f
                                ),
                            selectedLabelColor =
                                MaterialTheme.colorScheme.error
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ------------------------------------------------
                // NOTE
                // ------------------------------------------------

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                    },
                    label = {
                        Text(
                            stringResource(R.string.notes_optional),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor =
                            MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor =
                            MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.3f
                            )
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ------------------------------------------------
                // ACTIONS
                // ------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            val amountValue =
                                NumberUtils.toDouble(amount)

                            if (
                                amountValue == null ||
                                amountValue <= 0.0
                            ) {
                                amountError = true
                            } else {
                                focusManager.clearFocus()

                                onAddDebt(
                                    selectedType,
                                    amountValue,
                                    note.trim()
                                )

                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            stringResource(R.string.add_debt_button),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
