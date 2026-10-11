package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogueItemEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealSuccess
import com.example.util.AppLanguage
import com.example.util.CurrencyUtils
import com.example.util.LanguageManager

@Composable
fun CatalogueScreen(
    language: AppLanguage,
    catalogueItems: List<CatalogueItemEntity>,
    onAddCatalogueItem: (CatalogueItemEntity) -> Unit,
    onUpdateCatalogueItem: (CatalogueItemEntity) -> Unit,
    onDeleteCatalogueItem: (Long) -> Unit,
    onResetCatalogue: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var editingItem by remember { mutableStateOf<CatalogueItemEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val categories = listOf("All", "Preparation", "Interior", "Exterior", "Wood & Metal", "Textures & Finishes", "Custom")

    val filteredItems = remember(catalogueItems, selectedCategory) {
        if (selectedCategory == "All") catalogueItems
        else catalogueItems.filter { it.category == selectedCategory }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PUNE & KHARADI PAINTING RATES",
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Contractor master rates for materials, labour & combined coats",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.5.sp
                            )
                        }

                        IconButton(
                            onClick = { showResetConfirm = true },
                            modifier = Modifier.testTag("btn_reset_catalogue")
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset Defaults", tint = Color.White)
                        }
                    }
                }
            }

            // Category filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSel = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) PrimaryNavy else Color.White,
                            shadowElevation = if (isSel) 2.dp else 1.dp,
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.White else Slate700,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Items list
            items(filteredItems) { item ->
                CatalogueItemCard(
                    item = item,
                    language = language,
                    onEdit = { editingItem = item },
                    onDelete = { onDeleteCatalogueItem(item.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = PrimaryNavy,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_catalogue_item")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
    }

    if (showAddDialog || editingItem != null) {
        val target = editingItem
        CatalogueFormDialog(
            initialItem = target,
            onDismiss = {
                showAddDialog = false
                editingItem = null
            },
            onSave = { newItem ->
                if (target == null) {
                    onAddCatalogueItem(newItem)
                } else {
                    onUpdateCatalogueItem(newItem)
                }
                showAddDialog = false
                editingItem = null
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset to Default Rates") },
            text = { Text("This will reload standard Kharadi & Pune contractor painting rates for all items. Proceed?") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetCatalogue()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CatalogueItemCard(
    item: CatalogueItemEntity,
    language: AppLanguage,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.HINDI) item.titleHi else item.titleEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Slate900
                    )
                    Text(
                        text = "${item.brandProduct} • ${item.surfaceType} • ${item.coats} Coats",
                        fontSize = 11.5.sp,
                        color = Slate500
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryNavy, modifier = Modifier.size(18.dp))
                    }
                    if (item.isCustom) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Material", fontSize = 10.5.sp, color = Slate500)
                    Text(text = "₹${item.materialRate}/${item.unit}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                }
                Column {
                    Text(text = "Labour", fontSize = 10.5.sp, color = Slate500)
                    Text(text = "₹${item.labourRate}/${item.unit}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Combined Rate", fontSize = 10.5.sp, color = Slate500)
                    Text(
                        text = "₹${item.combinedRate}/${item.unit}",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealSuccess
                    )
                }
            }

            if (item.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.remarks, fontSize = 10.5.sp, color = Slate500, maxLines = 2)
            }
        }
    }
}

@Composable
private fun CatalogueFormDialog(
    initialItem: CatalogueItemEntity?,
    onDismiss: () -> Unit,
    onSave: (CatalogueItemEntity) -> Unit
) {
    var titleEn by remember { mutableStateOf(initialItem?.titleEn ?: "") }
    var titleHi by remember { mutableStateOf(initialItem?.titleHi ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "Interior") }
    var brand by remember { mutableStateOf(initialItem?.brandProduct ?: "Asian Paints") }
    var surface by remember { mutableStateOf(initialItem?.surfaceType ?: "Wall") }
    var unit by remember { mutableStateOf(initialItem?.unit ?: "sqft") }
    var matRate by remember { mutableStateOf((initialItem?.materialRate ?: 0.0).toString()) }
    var labRate by remember { mutableStateOf((initialItem?.labourRate ?: 0.0).toString()) }
    var coats by remember { mutableStateOf((initialItem?.coats ?: 2).toString()) }
    var remarks by remember { mutableStateOf(initialItem?.remarks ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialItem == null) "Add Catalogue Item" else "Edit Rate", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = titleEn,
                        onValueChange = { titleEn = it },
                        label = { Text("Title (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = titleHi,
                        onValueChange = { titleHi = it },
                        label = { Text("Title (Hindi)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand / Product (e.g. Royale, Apex)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = matRate,
                            onValueChange = { matRate = it },
                            label = { Text("Mat. Rate (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = labRate,
                            onValueChange = { labRate = it },
                            label = { Text("Lab. Rate (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit (sqft/rft)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = coats,
                            onValueChange = { coats = it },
                            label = { Text("Coats") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Remarks") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val base = initialItem ?: CatalogueItemEntity(
                        category = category,
                        titleEn = titleEn,
                        titleHi = titleHi.ifBlank { titleEn },
                        isCustom = true
                    )
                    onSave(
                        base.copy(
                            titleEn = titleEn.ifBlank { "Painting Work" },
                            titleHi = titleHi.ifBlank { titleEn },
                            brandProduct = brand,
                            surfaceType = surface,
                            unit = unit,
                            materialRate = matRate.toDoubleOrNull() ?: 0.0,
                            labourRate = labRate.toDoubleOrNull() ?: 0.0,
                            coats = coats.toIntOrNull() ?: 2,
                            remarks = remarks
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
