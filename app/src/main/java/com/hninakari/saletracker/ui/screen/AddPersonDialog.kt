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
import com.hninakari.saletracker.data.model.PersonType

@Composable
fun AddPersonDialog(
    onDismiss: () -> Unit,
    onAddPerson: (String, String, PersonType, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PersonType.OTHER) }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 420.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.add_person),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = {
                        Text(
                            stringResource(R.string.name),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text(
                                stringResource(R.string.required),
                                fontSize = 10.sp
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor =
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = {
                        Text(
                            stringResource(R.string.phone_optional),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor =
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.person_type),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PersonType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                            },
                            label = {
                                Text(
                                    when (type) {
                                        PersonType.CUSTOMER ->
                                            stringResource(R.string.customer)

                                        PersonType.SUPPLIER ->
                                            stringResource(R.string.supplier)

                                        PersonType.OTHER ->
                                            stringResource(R.string.other)
                                    },
                                    fontSize = 10.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor =
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor =
                                    MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = {
                        Text(
                            stringResource(R.string.notes),
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
                        fontSize = 13.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor =
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = true
                            } else {
                                focusManager.clearFocus()

                                onAddPerson(
                                    name.trim(),
                                    phone.trim(),
                                    selectedType,
                                    notes.trim()
                                )

                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            stringResource(R.string.add_person),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
