package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.Expense
import com.hninakari.saletracker.data.model.ExpenseCategory
import com.hninakari.saletracker.data.model.ExpenseType
import com.hninakari.saletracker.utils.NumberUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    onExpenseAdded: (Expense) -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.OTHER) }
    var selectedType by remember { mutableStateOf(ExpenseType.BUSINESS) }
    var description by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.expense_type),
            fontSize = 14.sp,
            color = TextPrimary.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedType == ExpenseType.BUSINESS,
                onClick = { selectedType = ExpenseType.BUSINESS },
                label = { Text("🏢 ${stringResource(R.string.business)}") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary.copy(alpha = 0.2f),
                    selectedLabelColor = Primary
                )
            )
            FilterChip(
                selected = selectedType == ExpenseType.PERSONAL,
                onClick = { selectedType = ExpenseType.PERSONAL },
                label = { Text("👤 ${stringResource(R.string.personal)}") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                    selectedLabelColor = MaterialTheme.colorScheme.error
                )
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        OutlinedTextField(
            value = amount,
            onValueChange = { 
                val englishDigits = NumberUtils.toEnglishDigits(it)
                val filtered = englishDigits.filter { char -> char.isDigit() || char == '.' }
                amount = filtered
                amountError = false
            },
            label = { Text(stringResource(R.string.amount)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = amountError,
            supportingText = {
                if (amountError) {
                    Text("Enter valid amount")
                }
            },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = stringResource(R.string.category),
            fontSize = 14.sp,
            color = TextPrimary.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        var expanded by remember { mutableStateOf(false) }
        
        fun getCategoryDisplayName(category: ExpenseCategory): String {
            return when (category) {
                ExpenseCategory.INVENTORY -> "ကုန်ပစ္စည်း"
                ExpenseCategory.RENT -> "ငှားရမ်းခ"
                ExpenseCategory.SALARY -> "လစာ"
                ExpenseCategory.UTILITIES -> "အသုံးစရိတ်"
                ExpenseCategory.TRANSPORT -> "သယ်ယူပို့ဆောင်ရေး"
                ExpenseCategory.MARKETING -> "စျေးကွက်ရှာဖွေရေး"
                ExpenseCategory.MAINTENANCE -> "ပြုပြင်ထိန်းသိမ်းရေး"
                ExpenseCategory.OTHER -> "အခြား"
            }
        }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = getCategoryDisplayName(selectedCategory),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.category)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                singleLine = true
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ExpenseCategory.values().forEach { category ->
                    DropdownMenuItem(
                        text = { Text(getCategoryDisplayName(category)) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description_optional)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                val cleanAmount = NumberUtils.toDouble(amount)
                if (cleanAmount == null || cleanAmount <= 0.0) {
                    amountError = true
                } else {
                    val expense = Expense(
                        amount = cleanAmount,
                        category = selectedCategory,
                        type = selectedType,
                        description = description.trim()
                    )
                    onExpenseAdded(expense)
                    amount = ""
                    description = ""
                    selectedCategory = ExpenseCategory.OTHER
                    selectedType = ExpenseType.BUSINESS
                    amountError = false
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(stringResource(R.string.add_expense_button), fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        TextButton(
            onClick = {
                amount = ""
                description = ""
                selectedCategory = ExpenseCategory.OTHER
                selectedType = ExpenseType.BUSINESS
                amountError = false
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.clear), fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}
