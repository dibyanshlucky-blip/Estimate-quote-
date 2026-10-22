package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoorSliding
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AdjustmentUnit
import com.example.data.model.BusinessProfile
import com.example.data.model.ProjectWithDetails
import com.example.data.model.WoodPolishEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.TealSuccess
import com.example.util.AppLanguage
import com.example.util.CurrencyUtils
import com.example.util.LanguageManager
import com.example.util.PdfGenerator
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WoodPolishCalculatorScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?,
    businessProfile: BusinessProfile? = null,
    onSaveItem: (WoodPolishEntity) -> Unit,
    onDeleteItem: (WoodPolishEntity) -> Unit,
    onDuplicateItem: (WoodPolishEntity) -> Unit
) {
    val context = LocalContext.current
    val currentProject = projectDetails?.project
    val items = projectDetails?.woodPolishItems ?: emptyList()
    val isLabourOnly = projectDetails?.isLabourOnlyMode ?: false

    var selectedTab by remember { mutableIntStateOf(0) }
    // 0: Door Polish, 1: Window Polish, 2: Interior Wood Polish

    val currentCategory = when (selectedTab) {
        0 -> "DOOR"
        1 -> "WINDOW"
        else -> "INTERIOR"
    }

    val filteredItems = items.filter { it.category == currentCategory }

    // Dialog State
    var showAddEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<WoodPolishEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<WoodPolishEntity?>(null) }
    var showSummaryDetails by remember { mutableStateOf(true) }

    // Aggregate statistics
    val totalDoorArea = items.filter { it.category == "DOOR" }.sumOf { it.finalBillableAreaSqft }
    val totalWindowArea = items.filter { it.category == "WINDOW" }.sumOf { it.finalBillableAreaSqft }
    val totalInteriorArea = items.filter { it.category == "INTERIOR" }.sumOf { it.finalBillableAreaSqft }
    val totalFrameArea = items.sumOf { if (it.includeFrameInTotal) it.frameAreaSqft * it.effectivePolishSides * it.quantity else 0.0 }
    val totalShutterArea = items.sumOf { (if (it.shutterAreaSqft > 0) it.shutterAreaSqft else it.dimensionBaseAreaSqft) * it.effectivePolishSides * it.quantity }
    val totalDeductionArea = items.sumOf { it.totalDeductionSqft }
    val totalFinalBillableArea = items.sumOf { it.finalBillableAreaSqft }
    val totalLabourCost = items.sumOf { it.labourAmount }
    val totalMaterialCost = items.sumOf { it.materialAmount }
    val grandTotalEstimate = if (isLabourOnly) totalLabourCost else (totalLabourCost + totalMaterialCost)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Module Top Header with Category Submodules
        Surface(
            color = PrimaryNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = LanguageManager.get("woodwork_polish", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = currentProject?.let { "${it.customerName} • ${it.siteName}" } ?: "Select or create site project",
                            fontSize = 11.5.sp,
                            color = AccentGold
                        )
                    }

                    // Estimate Mode Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isLabourOnly) Color(0xFFFEF3C7) else Color(0xFFD1FAE5)
                    ) {
                        Text(
                            text = if (isLabourOnly) "Labour Only" else "Material + Labour",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLabourOnly) Color(0xFFB45309) else Color(0xFF065F46),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3 Submodule Tabs: Door, Window, Interior
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "1. Door Polish (${items.count { it.category == "DOOR" }})",
                                fontSize = 11.5.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "2. Window Polish (${items.count { it.category == "WINDOW" }})",
                                fontSize = 11.5.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "3. Interior Wood (${items.count { it.category == "INTERIOR" }})",
                                fontSize = 11.5.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Summary Dashboard Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFF78350F), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "WOODWORK POLISH SUMMARY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            IconButton(onClick = { showSummaryDetails = !showSummaryDetails }, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = if (showSummaryDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Toggle Summary",
                                    tint = Slate500
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // High level KPIs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF3C7),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "Total Billable Area", fontSize = 10.sp, color = Color(0xFF92400E))
                                    Text(
                                        text = CurrencyUtils.formatSqft(totalFinalBillableArea),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = if (isLabourOnly) "Total Labour Cost" else "Grand Total",
                                        fontSize = 10.sp,
                                        color = Color(0xFF065F46)
                                    )
                                    Text(
                                        text = CurrencyUtils.formatInr(grandTotalEstimate),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF047857)
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(visible = showSummaryDetails) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                HorizontalDivider(color = Color(0xFFE2E8F0))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Submodule breakdown
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "• Doors: ${CurrencyUtils.formatSqft(totalDoorArea)}", fontSize = 11.sp, color = Slate700)
                                    Text(text = "• Windows: ${CurrencyUtils.formatSqft(totalWindowArea)}", fontSize = 11.sp, color = Slate700)
                                    Text(text = "• Interior: ${CurrencyUtils.formatSqft(totalInteriorArea)}", fontSize = 11.sp, color = Slate700)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Shutter: ${CurrencyUtils.formatSqft(totalShutterArea)}", fontSize = 10.5.sp, color = Slate500)
                                    Text(text = "Frame: ${CurrencyUtils.formatSqft(totalFrameArea)}", fontSize = 10.5.sp, color = Slate500)
                                    Text(text = "Deductions: −${CurrencyUtils.formatSqft(totalDeductionArea)}", fontSize = 10.5.sp, color = Color(0xFFDC2626))
                                }

                                if (!isLabourOnly) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Labour: ${CurrencyUtils.formatInr(totalLabourCost)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryNavy)
                                        Text(text = "Material: ${CurrencyUtils.formatInr(totalMaterialCost)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD97706))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Submodule Actions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (selectedTab) {
                                0 -> "DOOR POLISH ITEMS (${filteredItems.size})"
                                1 -> "WINDOW POLISH ITEMS (${filteredItems.size})"
                                else -> "INTERIOR WOOD ITEMS (${filteredItems.size})"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = PrimaryNavy
                        )
                        Text(
                            text = "Accurate Feet & Inches with Multipliers & Adjustments",
                            fontSize = 10.5.sp,
                            color = Slate500
                        )
                    }

                    Button(
                        onClick = {
                            if (currentProject == null) {
                                Toast.makeText(context, "Please select an active project first", Toast.LENGTH_SHORT).show()
                            } else {
                                itemToEdit = null
                                showAddEditDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_add_wood_polish_item")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add Item", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // List of items in current submodule
            if (filteredItems.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (selectedTab) {
                                    0 -> Icons.Default.DoorSliding
                                    1 -> Icons.Default.Window
                                    else -> Icons.Default.Assessment
                                },
                                contentDescription = null,
                                tint = Slate500,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = when (selectedTab) {
                                    0 -> "No door polish measurements added yet"
                                    1 -> "No window polish measurements added yet"
                                    else -> "No interior wood polish measurements added yet"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PrimaryNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap '+ Add Item' to enter dimensions (Ft & In), sides, multiplier and rates.",
                                fontSize = 11.5.sp,
                                color = Slate500,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    WoodPolishItemCard(
                        item = item,
                        isLabourOnly = isLabourOnly,
                        onEdit = {
                            itemToEdit = item
                            showAddEditDialog = true
                        },
                        onDuplicate = { onDuplicateItem(item) },
                        onDelete = { itemToDelete = item }
                    )
                }
            }

            // Quick Generate PDF & WhatsApp Export action card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
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
                                text = "Export Woodwork Quotation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = PrimaryNavy
                            )
                            Text(
                                text = "Professional A4 PDF with Divyansh Enterprises Letterhead",
                                fontSize = 10.5.sp,
                                color = Slate500
                            )
                        }
                        Button(
                            onClick = {
                                if (projectDetails != null) {
                                    val pdfFile = PdfGenerator.generateQuotationPdf(
                                        context = context,
                                        projectDetails = projectDetails,
                                        isLabourOnly = projectDetails.isLabourOnlyMode,
                                        businessProfile = businessProfile
                                    )
                                    PdfGenerator.sharePdfFile(context, pdfFile)
                                } else {
                                    Toast.makeText(context, "Select active project first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "PDF Quote", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Item Dialog
    if (showAddEditDialog && currentProject != null) {
        WoodPolishAddEditDialog(
            category = currentCategory,
            existingItem = itemToEdit,
            projectId = currentProject.id,
            onDismiss = { showAddEditDialog = false },
            onSave = { newItem ->
                onSaveItem(newItem)
                showAddEditDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Wood Polish Item?") },
            text = { Text("Are you sure you want to delete '${itemToDelete?.itemName}'? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { onDeleteItem(it) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Display Card for a single Woodwork & Polish Item with calculation transparency.
 */
@Composable
private fun WoodPolishItemCard(
    item: WoodPolishEntity,
    isLabourOnly: Boolean,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            if (item.multiplier != 1.0 || item.plusAdjustment > 0 || item.minusAdjustment > 0) 1.2.dp else 1.dp,
            if (item.multiplier != 1.0) Color(0xFFF59E0B) else Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Item Name, Item Type & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.itemName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryNavy
                        )
                        if (item.roomName.isNotBlank()) {
                            Text(
                                text = " • ${item.roomName}",
                                fontSize = 11.5.sp,
                                color = Slate500
                            )
                        }
                    }
                    Text(
                        text = "${item.itemType} • Qty: ${item.quantity} • ${if (item.effectivePolishSides == 2.0) "Both Sides" else "${item.effectivePolishSides} Side(s)"}",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Slate700, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dimensions and Components info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Dimensions (H × W)", fontSize = 10.sp, color = Slate500)
                        Text(
                            text = "${item.heightFt.toInt()} ft ${item.heightIn.toInt()} in × ${item.widthFt.toInt()} ft ${item.widthIn.toInt()} in",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = PrimaryNavy
                        )
                        Text(
                            text = "(${String.format(Locale.US, "%.2f ft × %.2f ft", item.effectiveHeightFt, item.effectiveWidthFt)})",
                            fontSize = 9.5.sp,
                            color = Slate500
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Polish Spec", fontSize = 10.sp, color = Slate500)
                        Text(
                            text = item.polishType,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = item.polishBrand,
                            fontSize = 9.5.sp,
                            color = Slate500
                        )
                    }
                }
            }

            // Shutter & Frame breakdown if separate
            if (item.shutterAreaSqft > 0 || item.frameAreaSqft > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (item.shutterAreaSqft > 0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF1F5F9)) {
                            Text(
                                text = "Shutter: ${CurrencyUtils.formatSqft(item.shutterAreaSqft)}",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (item.frameAreaSqft > 0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF1F5F9)) {
                            Text(
                                text = "Frame: ${CurrencyUtils.formatSqft(item.frameAreaSqft)} ${if (!item.includeFrameInTotal) "(Excluded)" else ""}",
                                fontSize = 10.sp,
                                color = if (item.includeFrameInTotal) PrimaryNavy else Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (item.additionalSectionsSqft > 0) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF1F5F9)) {
                            Text(
                                text = "Addl: +${CurrencyUtils.formatSqft(item.additionalSectionsSqft)}",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Calculation Sequence Banner: Base × Multiplier + Plus − Minus = Final Area
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (item.multiplier != 1.0 || item.plusAdjustment > 0 || item.minusAdjustment > 0) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (item.multiplier != 1.0) Color(0xFFD97706) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val baseTxt = CurrencyUtils.formatSqft(item.netAreaBeforeAdjustment)
                            val multTxt = String.format(Locale.US, "%.2f", item.multiplier)
                            val plusTxt = if (item.plusAdjustment > 0) " + ${if (item.plusUnit == AdjustmentUnit.MULTIPLIER) "×${item.plusAdjustment}" else CurrencyUtils.formatSqft(item.plusAdjustment)}" else ""
                            val minusTxt = if (item.minusAdjustment > 0) " − ${if (item.minusUnit == AdjustmentUnit.MULTIPLIER) "×${item.minusAdjustment}" else CurrencyUtils.formatSqft(item.minusAdjustment)}" else ""
                            Text(
                                text = "($baseTxt × $multTxt)$plusTxt$minusTxt",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryNavy
                            )
                            if (item.deductionAreaSqft > 0) {
                                Text(
                                    text = "Gross: ${CurrencyUtils.formatSqft(item.grossAreaSqft)} − Deduction: ${CurrencyUtils.formatSqft(item.totalDeductionSqft)}",
                                    fontSize = 9.5.sp,
                                    color = Slate500
                                )
                            }
                        }

                        Text(
                            text = "= ${CurrencyUtils.formatSqft(item.finalBillableAreaSqft)}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TealSuccess
                        )
                    }

                    if (item.hasNegativeWarning) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Minus adjustment exceeds area. Clamped to 0.00 sq.ft.",
                                fontSize = 9.5.sp,
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rates and Total Cost Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (isLabourOnly) {
                        Text(
                            text = "Labour: ₹${String.format(Locale.US, "%.2f", item.labourRate)}/sq.ft",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate700
                        )
                    } else {
                        Text(
                            text = "Lab: ₹${String.format(Locale.US, "%.2f", item.labourRate)} • Mat: ₹${String.format(Locale.US, "%.2f", item.materialRate)}/sq.ft",
                            fontSize = 10.5.sp,
                            color = Slate700
                        )
                    }
                    if (item.remarks.isNotBlank()) {
                        Text(text = "Note: ${item.remarks}", fontSize = 10.sp, color = Slate500)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryNavy
                ) {
                    Text(
                        text = CurrencyUtils.formatInr(item.getEffectiveAmount(isLabourOnly)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Modal Dialog for Adding / Editing Door, Window or Interior Wood Polish items.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WoodPolishAddEditDialog(
    category: String,
    existingItem: WoodPolishEntity?,
    projectId: Long,
    onDismiss: () -> Unit,
    onSave: (WoodPolishEntity) -> Unit
) {
    var itemName by remember { mutableStateOf(existingItem?.itemName ?: if (category == "DOOR") "Main Entrance Door" else if (category == "WINDOW") "Living Room Window" else "Master Bedroom Wardrobe") }
    var roomName by remember { mutableStateOf(existingItem?.roomName ?: "Hall / Living Room") }
    var itemType by remember { mutableStateOf(existingItem?.itemType ?: if (category == "DOOR") "Single Door" else if (category == "WINDOW") "Wooden Window" else "Wardrobe") }
    var quantityText by remember { mutableStateOf((existingItem?.quantity ?: 1).toString()) }

    // Feet & Inches
    var heightFtText by remember { mutableStateOf((existingItem?.heightFt ?: 7.0).toString()) }
    var heightInText by remember { mutableStateOf((existingItem?.heightIn ?: 0.0).toString()) }
    var widthFtText by remember { mutableStateOf((existingItem?.widthFt ?: 3.0).toString()) }
    var widthInText by remember { mutableStateOf((existingItem?.widthIn ?: 0.0).toString()) }
    var lengthFtText by remember { mutableStateOf((existingItem?.lengthFt ?: 0.0).toString()) }
    var lengthInText by remember { mutableStateOf((existingItem?.lengthIn ?: 0.0).toString()) }

    var polishSides by remember { mutableDoubleStateOf(existingItem?.polishSides ?: 2.0) }
    var isAlreadyCombined by remember { mutableStateOf(existingItem?.isAlreadyCombinedTotal ?: false) }

    // Frame & Shutter separate
    var isFrameSeparate by remember { mutableStateOf(existingItem != null && (existingItem.frameAreaSqft > 0 || existingItem.shutterAreaSqft > 0)) }
    var shutterAreaText by remember { mutableStateOf((existingItem?.shutterAreaSqft ?: 0.0).toString()) }
    var frameAreaText by remember { mutableStateOf((existingItem?.frameAreaSqft ?: 0.0).toString()) }
    var includeFrameInTotal by remember { mutableStateOf(existingItem?.includeFrameInTotal ?: true) }
    var addlSectionsText by remember { mutableStateOf((existingItem?.additionalSectionsSqft ?: 0.0).toString()) }
    var deductionText by remember { mutableStateOf((existingItem?.deductionAreaSqft ?: 0.0).toString()) }

    // Multiplier & Adjustments
    var multiplierText by remember { mutableStateOf(if (existingItem != null) String.format(Locale.US, "%.2f", existingItem.multiplier) else "1.00") }
    var plusAdjText by remember { mutableStateOf((existingItem?.plusAdjustment ?: 0.0).toString()) }
    var plusUnit by remember { mutableStateOf(existingItem?.plusAdjustmentUnit ?: "SQFT") }
    var minusAdjText by remember { mutableStateOf((existingItem?.minusAdjustment ?: 0.0).toString()) }
    var minusUnit by remember { mutableStateOf(existingItem?.minusAdjustmentUnit ?: "SQFT") }

    // Polish Type & Brand & Rates
    var polishType by remember { mutableStateOf(existingItem?.polishType ?: "PU Polish") }
    var polishBrand by remember { mutableStateOf(existingItem?.polishBrand ?: "Asian Paints") }
    var labourRateText by remember { mutableStateOf((existingItem?.labourRate ?: 35.0).toString()) }
    var materialRateText by remember { mutableStateOf((existingItem?.materialRate ?: 40.0).toString()) }
    var remarks by remember { mutableStateOf(existingItem?.remarks ?: "") }

    // Dropdown expanded states
    var polishTypeExpanded by remember { mutableStateOf(false) }
    var itemTypeExpanded by remember { mutableStateOf(false) }

    // Live preview item
    val previewItem = remember(
        itemName, roomName, itemType, quantityText, heightFtText, heightInText, widthFtText, widthInText,
        lengthFtText, lengthInText, polishSides, isAlreadyCombined, shutterAreaText, frameAreaText,
        includeFrameInTotal, addlSectionsText, deductionText, multiplierText, plusAdjText, plusUnit,
        minusAdjText, minusUnit, polishType, polishBrand, labourRateText, materialRateText, remarks
    ) {
        val qty = (quantityText.toIntOrNull() ?: 1).coerceAtLeast(1)
        val hF = heightFtText.toDoubleOrNull() ?: 0.0
        val hI = (heightInText.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99)
        val wF = widthFtText.toDoubleOrNull() ?: 0.0
        val wI = (widthInText.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99)
        val lF = lengthFtText.toDoubleOrNull() ?: 0.0
        val lI = (lengthInText.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99)
        val sArea = shutterAreaText.toDoubleOrNull() ?: 0.0
        val fArea = frameAreaText.toDoubleOrNull() ?: 0.0
        val addl = addlSectionsText.toDoubleOrNull() ?: 0.0
        val ded = deductionText.toDoubleOrNull() ?: 0.0
        val mult = (multiplierText.toDoubleOrNull() ?: 1.0).coerceAtLeast(0.01)
        val plus = plusAdjText.toDoubleOrNull() ?: 0.0
        val minus = minusAdjText.toDoubleOrNull() ?: 0.0
        val lab = labourRateText.toDoubleOrNull() ?: 0.0
        val mat = materialRateText.toDoubleOrNull() ?: 0.0

        WoodPolishEntity(
            id = existingItem?.id ?: 0,
            projectId = projectId,
            category = category,
            itemName = itemName.ifBlank { "Item" },
            roomName = roomName,
            itemType = itemType,
            quantity = qty,
            heightFt = hF,
            heightIn = hI,
            widthFt = wF,
            widthIn = wI,
            lengthFt = lF,
            lengthIn = lI,
            measurementMethod = if (category == "INTERIOR" && lF > 0) "LENGTH_WIDTH" else "DIMENSIONS",
            polishSides = polishSides,
            isAlreadyCombinedTotal = isAlreadyCombined,
            shutterAreaSqft = sArea,
            frameAreaSqft = fArea,
            includeFrameInTotal = includeFrameInTotal,
            additionalSectionsSqft = addl,
            deductionAreaSqft = ded,
            multiplier = mult,
            plusAdjustment = plus,
            plusAdjustmentUnit = plusUnit,
            minusAdjustment = minus,
            minusAdjustmentUnit = minusUnit,
            polishBrand = polishBrand,
            polishType = polishType,
            labourRate = lab,
            materialRate = mat,
            remarks = remarks
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        title = {
            Column {
                Text(
                    text = if (existingItem == null) "Add $category Polish Item" else "Edit $category Polish Item",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryNavy
                )
                Text(
                    text = "Accurate Dimensions, Sides, Multiplier & Adjustments",
                    fontSize = 11.sp,
                    color = Slate500
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Item Name and Room
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name / Location") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = roomName,
                        onValueChange = { roomName = it },
                        label = { Text("Room / Area") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Item Type Selector & Quantity
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = itemTypeExpanded,
                        onExpandedChange = { itemTypeExpanded = !itemTypeExpanded },
                        modifier = Modifier.weight(1.3f)
                    ) {
                        OutlinedTextField(
                            value = itemType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Item Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemTypeExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = itemTypeExpanded,
                            onDismissRequest = { itemTypeExpanded = false }
                        ) {
                            val types = when (category) {
                                "DOOR" -> WoodPolishEntity.DOOR_TYPES
                                "WINDOW" -> WoodPolishEntity.WINDOW_TYPES
                                else -> WoodPolishEntity.INTERIOR_TYPES
                            }
                            types.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t) },
                                    onClick = {
                                        itemType = t
                                        itemTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.7f),
                        singleLine = true
                    )
                }

                // Height: Feet and Inches
                Text(text = "HEIGHT (ऊंचाई) — FEET & INCHES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = heightFtText,
                        onValueChange = { heightFtText = it },
                        label = { Text("Height Feet") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = heightInText,
                        onValueChange = { heightInText = it },
                        label = { Text("Height Inches (0-11)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Width: Feet and Inches
                Text(text = "WIDTH (चौड़ाई) — FEET & INCHES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = widthFtText,
                        onValueChange = { widthFtText = it },
                        label = { Text("Width Feet") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = widthInText,
                        onValueChange = { widthInText = it },
                        label = { Text("Width Inches (0-11)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Polish Sides Selection
                Text(text = "POLISH SIDES (पॉलिश की सतहें)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { polishSides = 1.0; isAlreadyCombined = false },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (polishSides == 1.0 && !isAlreadyCombined) AccentGold else Color(0xFFF1F5F9)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("One Side (1×)", fontSize = 11.sp, color = PrimaryNavy, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { polishSides = 2.0; isAlreadyCombined = false },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (polishSides == 2.0 && !isAlreadyCombined) AccentGold else Color(0xFFF1F5F9)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Both Sides (2×)", fontSize = 11.sp, color = PrimaryNavy, fontWeight = FontWeight.Bold)
                    }
                }

                // Shutter & Frame Separate Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Measure Shutter & Frame Separately", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryNavy)
                        Text(text = "Prevents double counting between frame and shutter", fontSize = 10.sp, color = Slate500)
                    }
                    Switch(
                        checked = isFrameSeparate,
                        onCheckedChange = { isFrameSeparate = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = TealSuccess)
                    )
                }

                if (isFrameSeparate) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF8FAFC), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = shutterAreaText,
                                    onValueChange = { shutterAreaText = it },
                                    label = { Text("Shutter Area (sq.ft)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = frameAreaText,
                                    onValueChange = { frameAreaText = it },
                                    label = { Text("Frame Area (sq.ft)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Include Frame in total calculation", fontSize = 11.5.sp, color = Slate700)
                                Switch(checked = includeFrameInTotal, onCheckedChange = { includeFrameInTotal = it })
                            }
                            OutlinedTextField(
                                value = addlSectionsText,
                                onValueChange = { addlSectionsText = it },
                                label = { Text("Additional Wooden Sections (sq.ft)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }

                // Deduction Area
                OutlinedTextField(
                    value = deductionText,
                    onValueChange = { deductionText = it },
                    label = { Text("Deduction Area (Glass, openings, cutouts in Sq Ft)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Multiplier (×), Plus (+) and Minus (−) Adjustment Section (Mandatory System)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "MULTIPLIER (×) & ADJUSTMENTS (+ / −)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color(0xFF92400E)
                        )

                        // Multiplier field with quick chips
                        OutlinedTextField(
                            value = multiplierText,
                            onValueChange = { multiplierText = it },
                            label = { Text("Multiplier (×) Coefficient") },
                            supportingText = { Text("Default 1.0 (e.g. 0.18, 0.73, 1.2, 1.5, 2.0)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Multiplier Quick Chips
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(0.73, 1.0, 1.2, 1.5, 2.0).forEach { m ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFD97706)),
                                    modifier = Modifier.clickable { multiplierText = String.format(Locale.US, "%.2f", m) }
                                ) {
                                    Text(
                                        text = "${m}×",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Plus (+) Adjustment with unit selector
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = plusAdjText,
                                onValueChange = { plusAdjText = it },
                                label = { Text("Plus (+) Adjustment") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.3f),
                                singleLine = true
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (plusUnit == "SQFT") AccentGold else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706)),
                                modifier = Modifier.clickable { plusUnit = if (plusUnit == "SQFT") "MULTIPLIER" else "SQFT" }
                            ) {
                                Text(
                                    text = if (plusUnit == "SQFT") "Sq Ft" else "Multiplier",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                                )
                            }
                        }

                        // Minus (−) Adjustment with unit selector
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = minusAdjText,
                                onValueChange = { minusAdjText = it },
                                label = { Text("Minus (−) Adjustment") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.3f),
                                singleLine = true
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (minusUnit == "SQFT") AccentGold else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD97706)),
                                modifier = Modifier.clickable { minusUnit = if (minusUnit == "SQFT") "MULTIPLIER" else "SQFT" }
                            ) {
                                Text(
                                    text = if (minusUnit == "SQFT") "Sq Ft" else "Multiplier",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                                )
                            }
                        }
                    }
                }

                // Polish Type & Brand
                Text(text = "POLISH SPECIFICATION & RATES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                ExposedDropdownMenuBox(
                    expanded = polishTypeExpanded,
                    onExpandedChange = { polishTypeExpanded = !polishTypeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = polishType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Polish Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = polishTypeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = polishTypeExpanded,
                        onDismissRequest = { polishTypeExpanded = false }
                    ) {
                        WoodPolishEntity.POLISH_TYPES.forEach { pt ->
                            DropdownMenuItem(
                                text = { Text(pt) },
                                onClick = {
                                    polishType = pt
                                    polishTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = polishBrand,
                    onValueChange = { polishBrand = it },
                    label = { Text("Polish Brand / Product (Asian Paints, MRF, ICA, Sirca)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = labourRateText,
                        onValueChange = { labourRateText = it },
                        label = { Text("Labour Rate ₹/sq.ft") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = materialRateText,
                        onValueChange = { materialRateText = it },
                        label = { Text("Material Rate ₹/sq.ft") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks (e.g. 3 coats PU gloss finish)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Live Preview Calculation Strip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "LIVE RESULT PREVIEW:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text(
                            text = "Final Billable Area: ${CurrencyUtils.formatSqft(previewItem.finalBillableAreaSqft)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TealSuccess
                        )
                        Text(
                            text = "Total Amount: ${CurrencyUtils.formatInr(previewItem.totalAmount)} (Labour: ${CurrencyUtils.formatInr(previewItem.labourAmount)})",
                            fontSize = 11.5.sp,
                            color = PrimaryNavy
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(previewItem) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F))
            ) {
                Text(if (existingItem == null) "Add Item" else "Update Item", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
