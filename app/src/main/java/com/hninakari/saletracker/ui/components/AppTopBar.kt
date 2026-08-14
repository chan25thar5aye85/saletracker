package com.hninakari.saletracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    subtitle: String = "",
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    showFilter: Boolean = false,
    onFilterClick: () -> Unit = {},
    filterExpanded: Boolean = false,
    onFilterSelected: (DateUtils.DateFilter) -> Unit = {},
    currentFilter: DateUtils.DateFilter = DateUtils.DateFilter.TODAY,
    showLanguage: Boolean = false,
    onLanguageClick: () -> Unit = {},
    showAddPerson: Boolean = false,
    onAddPersonClick: () -> Unit = {},
    showAddDebt: Boolean = false,
    onAddDebtClick: () -> Unit = {},
    showSettings: Boolean = false,
    onSettingsClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (showFilter) {
                FilterDropdown(
                    expanded = filterExpanded,
                    onExpandChange = onFilterClick,
                    onFilterSelected = onFilterSelected,
                    currentFilter = currentFilter
                )
            }
            
            if (showLanguage) {
                IconButton(onClick = onLanguageClick) {
                    Text("🌐", fontSize = 20.sp)
                }
            }
            
            if (showAddPerson) {
                IconButton(onClick = onAddPersonClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Person")
                }
            }
            
            if (showAddDebt) {
                IconButton(onClick = onAddDebtClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Debt")
                }
            }
            
            if (showSettings) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun FilterDropdown(
    expanded: Boolean,
    onExpandChange: () -> Unit,
    onFilterSelected: (DateUtils.DateFilter) -> Unit,
    currentFilter: DateUtils.DateFilter
) {
    Box {
        IconButton(onClick = onExpandChange) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter")
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onExpandChange,
            modifier = Modifier.width(160.dp)
        ) {
            DateUtils.DateFilter.values().forEach { filter ->
                val label = when (filter) {
                    DateUtils.DateFilter.TODAY -> "ယနေ့"
                    DateUtils.DateFilter.THIS_WEEK -> "ဤတစ်ပတ်"
                    DateUtils.DateFilter.THIS_MONTH -> "ဤလ"
                    DateUtils.DateFilter.THIS_YEAR -> "ဤနှစ်"
                    DateUtils.DateFilter.ALL_TIME -> "အားလုံး"
                }
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onFilterSelected(filter)
                    },
                    trailingIcon = {
                        if (currentFilter == filter) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}
