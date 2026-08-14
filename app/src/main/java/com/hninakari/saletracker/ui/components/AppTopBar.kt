package com.hninakari.saletracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
    onSettingsClick: () -> Unit = {},
    showMenu: Boolean = true,
    onMenuClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    )
                }
            }
        },
        navigationIcon = {
            if (showMenu && !showBack) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            if (showFilter) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (showLanguage) {
                IconButton(onClick = onLanguageClick) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = "Language",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (showAddPerson) {
                IconButton(onClick = onAddPersonClick) {
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = "Add Person",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (showAddDebt) {
                IconButton(onClick = onAddDebtClick) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Debt",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (showSettings) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
