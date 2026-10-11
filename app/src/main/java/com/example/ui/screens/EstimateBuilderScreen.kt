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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.BrandProductRegistry
import com.example.data.model.CatalogueItemEntity
import com.example.data.model.EstimateItemEntity
import com.example.data.model.ProjectEntity
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

@Composable
fun EstimateBuilderScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?,
    catalogueItems: List<CatalogueItemEntity>,
    onAddItem: (EstimateItemEntity) -> Unit,
    onUpdateItem: (EstimateItemEntity) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onUpdateProject: (ProjectEntity) -> Unit,
    onAutoPopulate: (List<CatalogueItemEntity>) -> Unit,
    onSaveProductToCatalogue: ((EstimateItemEntity) -> Unit)? = null,
    onNavigateToPdf: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Material + Labour, 1: Labour Only, 2: Compare Both
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<EstimateItemEntity?>(null) }
    var showCatalogueSelector by remember { mutableStateOf(false) }

    val project = projectDetails?.project
    val items = projectDetails?.estimateItems ?: emptyList()

    // Financial calculations
    val isLabourOnly = selectedTab == 1
    val subtotal = if (isLabourOnly) projectDetails?.labourSubtotal ?: 0.0 else projectDetails?.materialAndLabourSubtotal ?: 0.0
    val addCharges = project?.additionalCharges ?: 0.0
    val discount = project?.discountAmount ?: 0.0
    val isTaxEnabled = project?.isTaxEnabled ?: false
    val taxPercent = project?.taxPercent ?: 18.0
    val taxAmount = if (isLabourOnly) projectDetails?.labourTaxAmount ?: 0.0 else projectDetails?.materialAndLabourTaxAmount ?: 0.0
    val grandTotal = if (isLabourOnly) projectDetails?.labourGrandTotal ?: 0.0 else projectDetails?.materialAndLabourGrandTotal ?: 0.0

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mode Tabs (Material+Labour, Labour Only, Compare)
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = PrimaryNavy,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PrimaryNavy
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            project?.let { onUpdateProject(it.copy(estimateMode = "MATERIAL_AND_LABOUR")) }
                        },
                        text = { Text("Material + Labour", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            project?.let { onUpdateProject(it.copy(estimateMode = "LABOUR_ONLY")) }
                        },
                        text = { Text("Labour Only", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Compare", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Labour-Only Disclaimer Banner
            if (selectedTab == 1) {
                item {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LanguageManager.get("labour_only_notice", language),
                                fontSize = 11.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }
            }

            // Quick Auto-Populate Button Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showCatalogueSelector = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_auto_populate_estimate")
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Auto-Fill from Measurements", fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = onNavigateToPdf,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_view_pdf")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "PDF Quote", fontSize = 11.5.sp)
                    }
                }
            }

            // Side-by-side comparison view
            if (selectedTab == 2) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ESTIMATE TOTALS COMPARISON",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Labour only column
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(text = "LABOUR ONLY", fontWeight = FontWeight.Bold, color = Color(0xFFB45309), fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "Subtotal: ${CurrencyUtils.formatInr(projectDetails?.labourSubtotal ?: 0.0)}", fontSize = 11.sp)
                                        Text(text = "Grand Total:", fontSize = 11.sp, color = Slate500)
                                        Text(
                                            text = CurrencyUtils.formatInr(projectDetails?.labourGrandTotal ?: 0.0),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = Color(0xFFB45309)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Material + Labour column
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(text = "MATERIAL + LABOUR", fontWeight = FontWeight.Bold, color = PrimaryNavy, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "Subtotal: ${CurrencyUtils.formatInr(projectDetails?.materialAndLabourSubtotal ?: 0.0)}", fontSize = 11.sp)
                                        Text(text = "Grand Total:", fontSize = 11.sp, color = Slate500)
                                        Text(
                                            text = CurrencyUtils.formatInr(projectDetails?.materialAndLabourGrandTotal ?: 0.0),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = PrimaryNavy
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Estimate Items List
            if (items.isEmpty()) {
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
                            Text(text = "No work items in estimate yet", fontWeight = FontWeight.Bold, color = Slate700)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tap 'Auto-Fill from Measurements' or '+' to add items with brands and remarks", fontSize = 12.sp, color = Slate500)
                        }
                    }
                }
            } else {
                items(items) { item ->
                    EstimateItemCard(
                        item = item,
                        isLabourOnly = isLabourOnly,
                        onEdit = { editingItem = item },
                        onDelete = { onDeleteItem(item.id) }
                    )
                }
            }

            // Financial Summary & Adjustments Card
            if (items.isNotEmpty()) {
                item {
                    FinancialAdjustmentsCard(
                        language = language,
                        subtotal = subtotal,
                        additionalCharges = addCharges,
                        discount = discount,
                        isTaxEnabled = isTaxEnabled,
                        taxPercent = taxPercent,
                        taxAmount = taxAmount,
                        grandTotal = grandTotal,
                        onUpdate = { newAdd, newDisc, newTaxEnabled, newTaxPct ->
                            project?.let {
                                onUpdateProject(
                                    it.copy(
                                        additionalCharges = newAdd,
                                        discountAmount = newDisc,
                                        isTaxEnabled = newTaxEnabled,
                                        taxPercent = newTaxPct
                                    )
                                )
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddItemDialog = true },
            containerColor = PrimaryNavy,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_estimate_item")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
    }

    // Add / Edit Item Dialog
    if (showAddItemDialog || editingItem != null) {
        val target = editingItem
        EstimateItemFormDialog(
            initialItem = target,
            onDismiss = {
                showAddItemDialog = false
                editingItem = null
            },
            onSave = { newItem, saveToCatalogue ->
                if (target == null) {
                    onAddItem(newItem)
                } else {
                    onUpdateItem(newItem)
                }
                if (saveToCatalogue && onSaveProductToCatalogue != null) {
                    onSaveProductToCatalogue(newItem)
                }
                showAddItemDialog = false
                editingItem = null
            }
        )
    }

    // Catalogue auto-selector dialog
    if (showCatalogueSelector) {
        CatalogueMultiSelectorDialog(
            catalogueItems = catalogueItems,
            onDismiss = { showCatalogueSelector = false },
            onConfirm = { selected ->
                onAutoPopulate(selected)
                showCatalogueSelector = false
            }
        )
    }
}

@Composable
private fun EstimateItemCard(
    item: EstimateItemEntity,
    isLabourOnly: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Work title & actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.workDescription,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Slate900
                    )
                    Text(
                        text = "${item.category} • ${item.surfaceType}",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryNavy, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dedicated Product & Brand Details Specification Badge
            if (item.hasProductDetails) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (item.brandName.isNotBlank()) {
                                Surface(
                                    color = Color(0xFFDBEAFE),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = item.brandName,
                                        color = Color(0xFF1D4ED8),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (item.productName.isNotBlank()) {
                                Text(
                                    text = item.productName,
                                    color = PrimaryNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }

                            if (item.productVariant.isNotBlank()) {
                                Text(
                                    text = "(${item.productVariant})",
                                    color = Slate700,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (item.finish.isNotBlank()) {
                                Text(
                                    text = "Finish: ${item.finish}",
                                    color = Slate700,
                                    fontSize = 10.5.sp
                                )
                            }
                            Text(
                                text = "Coats: ${item.coats}",
                                color = Slate700,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Remarks Section
            if (item.remarks.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFFBEB),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Remarks: ${item.remarks}",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 15.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Measurement & Rate Calculations Bar
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF8FAFC)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.quantity.toInt()} ${item.unit}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Slate700
                    )

                    if (!isLabourOnly) {
                        Text(
                            text = "Mat: ₹${item.materialRate} + Lab: ₹${item.labourRate}",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                        Text(
                            text = CurrencyUtils.formatInr(item.totalAmountMaterialAndLabour),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = PrimaryNavy
                        )
                    } else {
                        Text(
                            text = "Lab Rate: ₹${item.labourRate}/${item.unit}",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                        Text(
                            text = CurrencyUtils.formatInr(item.totalAmountLabourOnly),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialAdjustmentsCard(
    language: AppLanguage,
    subtotal: Double,
    additionalCharges: Double,
    discount: Double,
    isTaxEnabled: Boolean,
    taxPercent: Double,
    taxAmount: Double,
    grandTotal: Double,
    onUpdate: (addCharges: Double, discount: Double, isTaxEnabled: Boolean, taxPct: Double) -> Unit
) {
    var addStr by remember(additionalCharges) { mutableStateOf(if (additionalCharges > 0) additionalCharges.toInt().toString() else "") }
    var discStr by remember(discount) { mutableStateOf(if (discount > 0) discount.toInt().toString() else "") }
    var taxChecked by remember(isTaxEnabled) { mutableStateOf(isTaxEnabled) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "BILLING & TOTAL SUMMARY",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = PrimaryNavy,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Items Subtotal:", fontSize = 12.sp, color = Slate700)
                Text(text = CurrencyUtils.formatInr(subtotal), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = addStr,
                    onValueChange = {
                        addStr = it
                        onUpdate(it.toDoubleOrNull() ?: 0.0, discStr.toDoubleOrNull() ?: 0.0, taxChecked, taxPercent)
                    },
                    label = { Text("Extra Charges (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = discStr,
                    onValueChange = {
                        discStr = it
                        onUpdate(addStr.toDoubleOrNull() ?: 0.0, it.toDoubleOrNull() ?: 0.0, taxChecked, taxPercent)
                    },
                    label = { Text("Discount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // GST Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = taxChecked,
                        onCheckedChange = {
                            taxChecked = it
                            onUpdate(addStr.toDoubleOrNull() ?: 0.0, discStr.toDoubleOrNull() ?: 0.0, it, taxPercent)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryNavy)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add 18% GST", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                if (taxChecked) {
                    Text(text = "+${CurrencyUtils.formatInr(taxAmount)}", fontSize = 12.sp, color = Slate700)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = PrimaryNavy,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GRAND TOTAL:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = CurrencyUtils.formatInr(grandTotal),
                        color = AccentGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun EstimateItemFormDialog(
    initialItem: EstimateItemEntity?,
    onDismiss: () -> Unit,
    onSave: (EstimateItemEntity, saveToCatalogue: Boolean) -> Unit
) {
    var desc by remember { mutableStateOf(initialItem?.workDescription ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "Interior") }
    var surface by remember { mutableStateOf(initialItem?.surfaceType ?: "Wall") }
    var qtyStr by remember { mutableStateOf((initialItem?.quantity ?: 500.0).toString()) }
    var unit by remember { mutableStateOf(initialItem?.unit ?: "sqft") }
    var matRateStr by remember { mutableStateOf((initialItem?.materialRate ?: 0.0).toString()) }
    var labRateStr by remember { mutableStateOf((initialItem?.labourRate ?: 10.0).toString()) }

    // Dedicated Product & Brand Fields
    var brandName by remember { mutableStateOf(initialItem?.brandName ?: "") }
    var productName by remember { mutableStateOf(initialItem?.productName ?: "") }
    var productVariant by remember { mutableStateOf(initialItem?.productVariant ?: "") }
    var finish by remember { mutableStateOf(initialItem?.finish ?: "Matt") }
    var coats by remember { mutableStateOf(initialItem?.coats ?: 2) }
    var remarks by remember { mutableStateOf(initialItem?.remarks ?: "") }
    var saveToCatalogue by remember { mutableStateOf(false) }

    var brandDropdownExpanded by remember { mutableStateOf(false) }
    var productDropdownExpanded by remember { mutableStateOf(false) }

    val suggestedProducts = remember(brandName) {
        if (brandName.isNotBlank()) BrandProductRegistry.getSuggestedProducts(brandName)
        else BrandProductRegistry.getSuggestedProducts("Asian Paints")
    }

    val popularVariants = listOf("Matt Finish", "Sheen", "Luxury Emulsion", "Premium Emulsion", "Water Thinnable", "High Gloss", "Satin", "Standard")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FormatPaint, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialItem == null) "Add Work Item" else "Edit Work Item",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = PrimaryNavy
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Work Description & Suggestions
                item {
                    Column {
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Work Description *") },
                            placeholder = { Text("e.g. Interior Wall Painting, Putty, Primer") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Quick Suggestion:", fontSize = 10.5.sp, color = Slate500)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            listOf("Wall Putty Application", "Interior Wall Primer", "Interior Wall Painting", "Exterior Wall Painting", "Texture Finish", "Door & Window Enamel", "Melamine Wood Polish").forEach { sugg ->
                                Surface(
                                    color = if (desc == sugg) PrimaryNavy else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.clickable { desc = sugg }
                                ) {
                                    Text(
                                        text = sugg,
                                        fontSize = 10.sp,
                                        color = if (desc == sugg) Color.White else Slate700,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Measurement Quantity & Unit
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = qtyStr,
                            onValueChange = { qtyStr = it },
                            label = { Text("Quantity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Rates
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = matRateStr,
                            onValueChange = { matRateStr = it },
                            label = { Text("Mat. Rate (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = labRateStr,
                            onValueChange = { labRateStr = it },
                            label = { Text("Lab. Rate (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Divider to Brand & Product Section
                item {
                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sell, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "BRAND & PRODUCT DETAILS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = PrimaryNavy
                                    )
                                }
                                if (brandName.isNotBlank() || productName.isNotBlank()) {
                                    TextButton(
                                        onClick = {
                                            brandName = ""
                                            productName = ""
                                            productVariant = ""
                                            finish = ""
                                        }
                                    ) {
                                        Text("Clear", fontSize = 11.sp, color = Color.Red.copy(alpha = 0.8f))
                                    }
                                }
                            }
                            Text(
                                text = "Optional. Specify exact paint brand, product & finish or leave blank if not finalized.",
                                fontSize = 10.sp,
                                color = Slate500
                            )
                        }
                    }
                }

                // Brand Name Dropdown & Quick Chips
                item {
                    Column {
                        ExposedDropdownMenuBox(
                            expanded = brandDropdownExpanded,
                            onExpandedChange = { brandDropdownExpanded = !brandDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = brandName,
                                onValueChange = { brandName = it },
                                label = { Text("Brand Name (Optional)") },
                                placeholder = { Text("e.g. Asian Paints, Berger, Nerolac") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = brandDropdownExpanded,
                                onDismissRequest = { brandDropdownExpanded = false }
                            ) {
                                BrandProductRegistry.popularBrands.forEach { b ->
                                    DropdownMenuItem(
                                        text = { Text(b, fontSize = 12.5.sp) },
                                        onClick = {
                                            brandName = b
                                            brandDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Brand Quick Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            listOf("Asian Paints", "Berger Paints", "Nerolac", "Dulux", "Birla White", "Indigo Paints").forEach { b ->
                                Surface(
                                    color = if (brandName == b) PrimaryNavy else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.clickable { brandName = b }
                                ) {
                                    Text(
                                        text = b,
                                        fontSize = 10.sp,
                                        color = if (brandName == b) Color.White else Slate700,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Product Name (Editable dropdown / input)
                item {
                    Column {
                        ExposedDropdownMenuBox(
                            expanded = productDropdownExpanded,
                            onExpandedChange = { productDropdownExpanded = !productDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = productName,
                                onValueChange = { productName = it },
                                label = { Text("Product Name (Optional)") },
                                placeholder = { Text("e.g. Royale, Apcolite, Tractor, Easy Clean") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = productDropdownExpanded,
                                onDismissRequest = { productDropdownExpanded = false }
                            ) {
                                suggestedProducts.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p, fontSize = 12.5.sp) },
                                        onClick = {
                                            productName = p
                                            productDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Suggested Products quick chips
                        if (suggestedProducts.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                suggestedProducts.take(5).forEach { p ->
                                    val shortName = p.take(22)
                                    Surface(
                                        color = if (productName == p) PrimaryNavy else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.clickable { productName = p }
                                    ) {
                                        Text(
                                            text = shortName,
                                            fontSize = 9.5.sp,
                                            color = if (productName == p) Color.White else Slate700,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Product Variant / Grade
                item {
                    Column {
                        OutlinedTextField(
                            value = productVariant,
                            onValueChange = { productVariant = it },
                            label = { Text("Variant / Grade (Optional)") },
                            placeholder = { Text("e.g. Matt Finish, Luxury Emulsion, 2-in-1") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            popularVariants.take(4).forEach { v ->
                                Surface(
                                    color = if (productVariant == v) PrimaryNavy else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.clickable { productVariant = v }
                                ) {
                                    Text(
                                        text = v,
                                        fontSize = 10.sp,
                                        color = if (productVariant == v) Color.White else Slate700,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Finish Selection
                item {
                    Column {
                        Text(text = "Finish:", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            BrandProductRegistry.commonFinishes.forEach { f ->
                                Surface(
                                    color = if (finish == f) PrimaryNavy else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.clickable { finish = f }
                                ) {
                                    Text(
                                        text = f,
                                        fontSize = 10.5.sp,
                                        color = if (finish == f) Color.White else Slate700,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Number of Coats
                item {
                    Column {
                        Text(text = "Number of Coats: $coats", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            (1..4).forEach { coatNum ->
                                Surface(
                                    color = if (coats == coatNum) PrimaryNavy else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { coats = coatNum }
                                ) {
                                    Text(
                                        text = "$coatNum Coat${if (coatNum > 1) "s" else ""}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (coats == coatNum) Color.White else Slate700,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Remarks / Special Instructions
                item {
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Remarks & Special Instructions") },
                        placeholder = { Text("e.g. Apply over approved primer; final shade subject to customer approval.") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Save to Product Catalogue option
                item {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { saveToCatalogue = !saveToCatalogue }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = saveToCatalogue,
                                onCheckedChange = { saveToCatalogue = it },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryNavy)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Save to Reusable Product Catalogue",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate900
                                )
                                Text(
                                    text = "Save this brand & product configuration to your permanent Pune rate list.",
                                    fontSize = 10.sp,
                                    color = Slate500
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val base = initialItem ?: EstimateItemEntity(
                        projectId = 0,
                        workDescription = desc
                    )
                    val updated = base.copy(
                        workDescription = desc.ifBlank { "Painting Item" },
                        category = category,
                        brandName = brandName.trim(),
                        productName = productName.trim(),
                        productVariant = productVariant.trim(),
                        finish = finish.trim(),
                        surfaceType = surface,
                        quantity = qtyStr.toDoubleOrNull() ?: 100.0,
                        unit = unit,
                        materialRate = matRateStr.toDoubleOrNull() ?: 0.0,
                        labourRate = labRateStr.toDoubleOrNull() ?: 0.0,
                        coats = coats,
                        remarks = remarks.trim()
                    )
                    onSave(updated, saveToCatalogue)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text("Save Item")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CatalogueMultiSelectorDialog(
    catalogueItems: List<CatalogueItemEntity>,
    onDismiss: () -> Unit,
    onConfirm: (List<CatalogueItemEntity>) -> Unit
) {
    val selectedIds = remember { mutableStateOf(catalogueItems.take(5).map { it.id }.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Items to Populate", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(catalogueItems) { item ->
                    val isChecked = selectedIds.value.contains(item.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val set = selectedIds.value.toMutableSet()
                                if (isChecked) set.remove(item.id) else set.add(item.id)
                                selectedIds.value = set
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val set = selectedIds.value.toMutableSet()
                                if (checked) set.add(item.id) else set.remove(item.id)
                                selectedIds.value = set
                            },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryNavy)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = item.titleEn, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                            Text(
                                text = "${item.brandProduct} • Mat: ₹${item.materialRate} • Lab: ₹${item.labourRate}/${item.unit}",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selected = catalogueItems.filter { selectedIds.value.contains(it.id) }
                    onConfirm(selected)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text("Populate (${selectedIds.value.size})")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
