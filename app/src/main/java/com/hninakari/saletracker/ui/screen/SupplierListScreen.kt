package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.data.model.PersonType
import com.hninakari.saletracker.viewmodel.PersonViewModel

@Composable
fun SupplierListScreen(
    personViewModel: PersonViewModel,
    onSupplierClick: (Person) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val allPeople by personViewModel.allPeople.collectAsState(initial = emptyList())
    val suppliers = allPeople.filter { it.type == PersonType.SUPPLIER }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.suppliers),
                fontSize = 24.sp,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Primary,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_supplier))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (suppliers.isEmpty()) {
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
                    Text("🏢", fontSize = 48.sp)
                    Text(
                        text = stringResource(R.string.no_suppliers),
                        fontSize = 18.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.tap_to_add_supplier),
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suppliers) { supplier ->
                    SupplierCard(
                        supplier = supplier,
                        onClick = { onSupplierClick(supplier) }
                    )
                }
            }
        }
    }
}

@Composable
fun SupplierCard(
    supplier: Person,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
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
                    text = supplier.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = supplier.phone.ifEmpty { stringResource(R.string.no_phone) },
                    fontSize = 14.sp,
                    color = TextPrimary.copy(alpha = 0.6f)
                )
                if (supplier.notes.isNotEmpty()) {
                    Text(
                        text = supplier.notes,
                        fontSize = 12.sp,
                        color = TextPrimary.copy(alpha = 0.4f)
                    )
                }
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextPrimary.copy(alpha = 0.3f)
            )
        }
    }
}
