package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.DirectMeasurementEntity
import com.example.data.model.ProjectWithDetails
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirectCalculatorScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?,
    onAddDirect: (surfaceName: String, quantity: Double, unit: String, notes: String) -> Unit,
    onDeleteDirect: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val directItems = projectDetails?.directMeasurements ?: emptyList()
    val totalDirectSqft = projectDetails?.totalDirectSqft ?: 0.0

    // Predefined quick direct surfaces as specified in prompt
    val quickSurfaces = listOf(
        "Interior Wall Area",
        "Ceiling Area",
        "Exterior Wall Area",
        "Staircase Area",
        "Door Surface Area",
        "Window Surface Area",
        "Safety Grill Area",
        "Railing (Running Feet)",
        "Beams & Columns Area"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Direct Sqft Overview Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DIRECT AREA MEASUREMENTS",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Direct Sqft Total", color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                            Text(
                                text = CurrencyUtils.formatSqft(totalDirectSqft),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Combined with Rooms", color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                            Text(
                                text = CurrencyUtils.formatSqft(projectDetails?.combinedPaintableSqft ?: totalDirectSqft),
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Entry Templates
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIRECT SURFACE MEASUREMENTS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900
                        )
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_add_direct_entry")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Entry", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Supports Sqft, Sq.Metres (auto-converted), Running Feet (rft), Pieces, and Lumpsum.",
                        fontSize = 11.5.sp,
                        color = Slate500
                    )
                }
            }
        }

        // Direct Items List
        if (directItems.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SquareFoot, contentDescription = null, tint = Slate500, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "No direct entries recorded", fontWeight = FontWeight.Bold, color = Slate700)
                        Text(text = "Tap '+ Add Entry' to add external walls, grills, railings, or known sqft", fontSize = 11.5.sp, color = Slate500)
                    }
                }
            }
        } else {
            items(directItems) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.surfaceName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate900
                            )
                            if (item.notes.isNotBlank()) {
                                Text(text = item.notes, fontSize = 11.sp, color = Slate500)
                            }
                            if (item.unit.lowercase() in listOf("sqm", "sq.m", "sq.mtr")) {
                                Text(
                                    text = "Converted: ${String.format("%.1f", item.calculatedSqft)} sq.ft (1 sq.m = 10.76 sq.ft)",
                                    fontSize = 11.sp,
                                    color = TealSuccess,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEFF6FF)
                            ) {
                                Text(
                                    text = "${item.quantity.toInt()} ${item.unit}",
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            IconButton(onClick = { onDeleteDirect(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var surfaceName by remember { mutableStateOf(quickSurfaces.first()) }
        var isCustomSurface by remember { mutableStateOf(false) }
        var customName by remember { mutableStateOf("") }
        var quantityStr by remember { mutableStateOf("100") }
        var selectedUnit by remember { mutableStateOf("sqft") }
        var notes by remember { mutableStateOf("") }

        val units = listOf("sqft", "sqm", "rft", "pieces", "lumpsum")

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Direct Measurement", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text(text = "Surface Category", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickSurfaces.forEach { surface ->
                                val sel = !isCustomSurface && surfaceName == surface
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (sel) PrimaryNavy else Color(0xFFF1F5F9),
                                    modifier = Modifier.clickable {
                                        surfaceName = surface
                                        isCustomSurface = false
                                        if (surface.contains("Railing")) selectedUnit = "rft"
                                    }
                                ) {
                                    Text(
                                        text = surface,
                                        color = if (sel) Color.White else Slate700,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCustomSurface) PrimaryNavy else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { isCustomSurface = true }
                            ) {
                                Text(
                                    text = "+ Custom Surface",
                                    color = if (isCustomSurface) Color.White else Slate700,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (isCustomSurface) {
                        item {
                            OutlinedTextField(
                                value = customName,
                                onValueChange = { customName = it },
                                label = { Text("Custom Surface Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = quantityStr,
                                onValueChange = { quantityStr = it },
                                label = { Text("Quantity") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text(text = "Unit (इकाई)", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            units.forEach { u ->
                                val sel = selectedUnit == u
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (sel) PrimaryNavy else Color(0xFFF1F5F9),
                                    modifier = Modifier.clickable { selectedUnit = u }
                                ) {
                                    Text(
                                        text = u,
                                        color = if (sel) Color.White else Slate700,
                                        fontSize = 11.5.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes / Specification") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = if (isCustomSurface) customName.ifBlank { "Custom Surface" } else surfaceName
                        val qty = quantityStr.toDoubleOrNull() ?: 0.0
                        onAddDirect(finalName, qty, selectedUnit, notes)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
