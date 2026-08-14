package com.hninakari.saletracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.utils.DateUtils

@Composable
fun DateFilterChips(
    selectedFilter: DateUtils.DateFilter,
    onFilterSelected: (DateUtils.DateFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = DateUtils.DateFilter.values()
    
    // Use LazyRow for horizontal scrolling if needed
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            // Get display name
            val label = when (filter) {
                DateUtils.DateFilter.TODAY -> "ယနေ့"
                DateUtils.DateFilter.THIS_WEEK -> "ဤတစ်ပတ်"
                DateUtils.DateFilter.THIS_MONTH -> "ဤလ"
                DateUtils.DateFilter.THIS_YEAR -> "ဤနှစ်"
                DateUtils.DateFilter.ALL_TIME -> "အားလုံး"
            }
            
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { 
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                },
                modifier = Modifier
                    .height(36.dp)
                    .width(80.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary.copy(alpha = 0.25f),
                    selectedLabelColor = Primary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = MaterialTheme.shapes.medium,
                border = null
            )
        }
    }
}
