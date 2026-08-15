package com.hninakari.saletracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.core.ui.theme.AppThemeColors
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
    val appColors = AppThemeColors.colors

    val backgroundColor = appColors.barBackground
    val contentColor = appColors.barContent

    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )

                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = contentColor.copy(alpha = 0.6f)
                    )
                }
            }
        },

        navigationIcon = {
            if (showMenu && !showBack) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = contentColor
                    )
                }
            } else if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = contentColor
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
                        tint = contentColor
                    )
                }
            }

            if (showLanguage) {
                IconButton(onClick = onLanguageClick) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = "Language",
                        tint = contentColor
                    )
                }
            }

            if (showAddPerson) {
                IconButton(onClick = onAddPersonClick) {
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = "Add Person",
                        tint = contentColor
                    )
                }
            }

            if (showAddDebt) {
                IconButton(onClick = onAddDebtClick) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Debt",
                        tint = contentColor
                    )
                }
            }

            if (showSettings) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = contentColor
                    )
                }
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        )
    )
}
