package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrandProductRegistry
import com.example.data.model.CeilingDeductionEntry
import com.example.data.model.CeilingMeasurementEntry
import com.example.data.model.RoomDetailsJsonConverter
import com.example.data.model.RoomMeasurementEntity
import com.example.data.model.SurfacePaintConfig
import com.example.data.model.SurfacePrepConfig
import com.example.data.model.WallDeductionEntry
import com.example.data.model.WallMeasurementEntry
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealSuccess
import com.example.util.CurrencyUtils

/**
 * Expandable Room Card with separate Wall & Ceiling Measurement tabs, Deductions,
 * Paint Brand/Product Selection, Putty & Primer controls, and instant automatic summaries.
 */
@Composable
fun ExpandableRoomCard(
    room: RoomMeasurementEntity,
    deductOpeningsGlobal: Boolean,
    isLabourOnlyMode: Boolean,
    onSaveRoom: (RoomMeasurementEntity) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Wall Measurement, 1: Ceiling Measurement, 2: Putty & Primer

    // Parse internal details
    val parsed = remember(room.detailedData, room.id) {
        room.getParsedDetails()
    }

    var wallEntries by remember(parsed) { mutableStateOf(parsed.wallEntries) }
    var wallDeductions by remember(parsed) { mutableStateOf(parsed.wallDeductions) }
    var wallPaint by remember(parsed) { mutableStateOf(parsed.wallPaint) }
    var wallPutty by remember(parsed) { mutableStateOf(parsed.wallPutty) }
    var wallPrimer by remember(parsed) { mutableStateOf(parsed.wallPrimer) }

    var ceilingEntries by remember(parsed) { mutableStateOf(parsed.ceilingEntries) }
    var ceilingDeductions by remember(parsed) { mutableStateOf(parsed.ceilingDeductions) }
    var ceilingPaint by remember(parsed) { mutableStateOf(parsed.ceilingPaint) }
    var ceilingPutty by remember(parsed) { mutableStateOf(parsed.ceilingPutty) }
    var ceilingPrimer by remember(parsed) { mutableStateOf(parsed.ceilingPrimer) }

    // Instant calculations
    val grossWallArea = wallEntries.sumOf { it.grossAreaSqft }
    val totalWallDeduction = wallDeductions.sumOf { it.deductionAreaSqft }
    val netWallArea = if (deductOpeningsGlobal) maxOf(0.0, grossWallArea - totalWallDeduction) else grossWallArea
    val wallAmount = wallPaint.calculateAmount(netWallArea, isLabourOnlyMode)

    val grossCeilingArea = ceilingEntries.sumOf { it.grossAreaSqft }
    val totalCeilingDeduction = ceilingDeductions.sumOf { it.deductionAreaSqft }
    val netCeilingArea = maxOf(0.0, grossCeilingArea - totalCeilingDeduction)
    val ceilingAmount = ceilingPaint.calculateAmount(netCeilingArea, isLabourOnlyMode)

    val wallPuttyAmount = wallPutty.calculateAmount(netWallArea, isLabourOnlyMode)
    val wallPrimerAmount = wallPrimer.calculateAmount(netWallArea, isLabourOnlyMode)
    val ceilingPuttyAmount = ceilingPutty.calculateAmount(netCeilingArea, isLabourOnlyMode)
    val ceilingPrimerAmount = ceilingPrimer.calculateAmount(netCeilingArea, isLabourOnlyMode)

    val totalRoomAmount = wallAmount + ceilingAmount + wallPuttyAmount + wallPrimerAmount + ceilingPuttyAmount + ceilingPrimerAmount

    fun persistChanges() {
        val serialized = RoomDetailsJsonConverter.serialize(
            wallEntries = wallEntries,
            wallDeductions = wallDeductions,
            wallPaint = wallPaint,
            wallPutty = wallPutty,
            wallPrimer = wallPrimer,
            ceilingEntries = ceilingEntries,
            ceilingDeductions = ceilingDeductions,
            ceilingPaint = ceilingPaint,
            ceilingPutty = ceilingPutty,
            ceilingPrimer = ceilingPrimer
        )
        onSaveRoom(
            room.copy(
                detailedData = serialized,
                additionalDeductionsSqft = totalWallDeduction
            )
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("room_card_${room.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Room Name, dimensions badge, expand button, actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expanded = !expanded }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryNavy.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = null,
                            tint = PrimaryNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = room.roomName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Net Wall: ${CurrencyUtils.formatSqft(netWallArea)} • Ceiling: ${CurrencyUtils.formatSqft(netCeilingArea)}",
                            fontSize = 11.5.sp,
                            color = Slate500
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyUtils.formatInr(totalRoomAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = PrimaryNavy
                    )
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Slate500, modifier = Modifier.size(17.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(17.dp))
                    }
                    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = PrimaryNavy
                        )
                    }
                }
            }

            // Quick Compact Summary when collapsed
            if (!expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Wall Paint", fontSize = 10.5.sp, color = Slate500)
                            Text(text = "${wallPaint.brand} - ${wallPaint.productType}", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Ceiling Paint", fontSize = 10.5.sp, color = Slate500)
                            Text(text = "${ceilingPaint.brand} - ${ceilingPaint.productType}", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                        }
                    }
                }
            }

            // Expanded view: Full tabs, inputs, deductions, rates, summaries
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Slate100)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Tab selector
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = PrimaryNavy
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Wall (${CurrencyUtils.formatSqft(netWallArea)})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Ceiling (${CurrencyUtils.formatSqft(netCeilingArea)})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Putty & Primer", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (selectedTab) {
                        0 -> {
                            // ------------------- WALL SECTION -------------------
                            WallSectionContent(
                                wallEntries = wallEntries,
                                wallDeductions = wallDeductions,
                                wallPaint = wallPaint,
                                grossArea = grossWallArea,
                                totalDeductions = totalWallDeduction,
                                netArea = netWallArea,
                                amount = wallAmount,
                                isLabourOnlyMode = isLabourOnlyMode,
                                onAddWallEntry = {
                                    wallEntries = wallEntries + WallMeasurementEntry(label = "Wall ${wallEntries.size + 1}", lengthFt = 12.0, heightFt = 10.0)
                                    persistChanges()
                                },
                                onUpdateWallEntry = { idx, updated ->
                                    val list = wallEntries.toMutableList()
                                    list[idx] = updated
                                    wallEntries = list
                                    persistChanges()
                                },
                                onDeleteWallEntry = { idx ->
                                    if (wallEntries.size > 1) {
                                        val list = wallEntries.toMutableList()
                                        list.removeAt(idx)
                                        wallEntries = list
                                        persistChanges()
                                    }
                                },
                                onAddDeduction = {
                                    wallDeductions = wallDeductions + WallDeductionEntry(name = "Door", lengthFt = 3.0, heightFt = 7.0, count = 1)
                                    persistChanges()
                                },
                                onUpdateDeduction = { idx, updated ->
                                    val list = wallDeductions.toMutableList()
                                    list[idx] = updated
                                    wallDeductions = list
                                    persistChanges()
                                },
                                onDeleteDeduction = { idx ->
                                    val list = wallDeductions.toMutableList()
                                    list.removeAt(idx)
                                    wallDeductions = list
                                    persistChanges()
                                },
                                onUpdatePaint = { updatedPaint ->
                                    wallPaint = updatedPaint
                                    persistChanges()
                                }
                            )
                        }
                        1 -> {
                            // ----------------- CEILING SECTION -----------------
                            CeilingSectionContent(
                                ceilingEntries = ceilingEntries,
                                ceilingDeductions = ceilingDeductions,
                                ceilingPaint = ceilingPaint,
                                grossArea = grossCeilingArea,
                                totalDeductions = totalCeilingDeduction,
                                netArea = netCeilingArea,
                                amount = ceilingAmount,
                                isLabourOnlyMode = isLabourOnlyMode,
                                onAddCeilingEntry = {
                                    ceilingEntries = ceilingEntries + CeilingMeasurementEntry(label = "Ceiling ${ceilingEntries.size + 1}", lengthFt = 14.0, widthFt = 12.0)
                                    persistChanges()
                                },
                                onUpdateCeilingEntry = { idx, updated ->
                                    val list = ceilingEntries.toMutableList()
                                    list[idx] = updated
                                    ceilingEntries = list
                                    persistChanges()
                                },
                                onDeleteCeilingEntry = { idx ->
                                    if (ceilingEntries.size > 1) {
                                        val list = ceilingEntries.toMutableList()
                                        list.removeAt(idx)
                                        ceilingEntries = list
                                        persistChanges()
                                    }
                                },
                                onAddDeduction = {
                                    ceilingDeductions = ceilingDeductions + CeilingDeductionEntry(name = "Cutout", lengthFt = 2.0, widthFt = 2.0)
                                    persistChanges()
                                },
                                onUpdateDeduction = { idx, updated ->
                                    val list = ceilingDeductions.toMutableList()
                                    list[idx] = updated
                                    ceilingDeductions = list
                                    persistChanges()
                                },
                                onDeleteDeduction = { idx ->
                                    val list = ceilingDeductions.toMutableList()
                                    list.removeAt(idx)
                                    ceilingDeductions = list
                                    persistChanges()
                                },
                                onUpdatePaint = { updatedPaint ->
                                    ceilingPaint = updatedPaint
                                    persistChanges()
                                }
                            )
                        }
                        2 -> {
                            // ---------------- PUTTY & PRIMER SECTION ----------------
                            PuttyPrimerSectionContent(
                                wallPutty = wallPutty,
                                wallPrimer = wallPrimer,
                                ceilingPutty = ceilingPutty,
                                ceilingPrimer = ceilingPrimer,
                                netWallArea = netWallArea,
                                netCeilingArea = netCeilingArea,
                                isLabourOnlyMode = isLabourOnlyMode,
                                onUpdateWallPutty = { wallPutty = it; persistChanges() },
                                onUpdateWallPrimer = { wallPrimer = it; persistChanges() },
                                onUpdateCeilingPutty = { ceilingPutty = it; persistChanges() },
                                onUpdateCeilingPrimer = { ceilingPrimer = it; persistChanges() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Room Complete Summary Footer Card
                    RoomSummaryFooter(
                        grossWallArea = grossWallArea,
                        totalWallDeduction = totalWallDeduction,
                        netWallArea = netWallArea,
                        wallPaint = wallPaint,
                        wallAmount = wallAmount,
                        grossCeilingArea = grossCeilingArea,
                        totalCeilingDeduction = totalCeilingDeduction,
                        netCeilingArea = netCeilingArea,
                        ceilingPaint = ceilingPaint,
                        ceilingAmount = ceilingAmount,
                        wallPuttyAmount = wallPuttyAmount,
                        wallPrimerAmount = wallPrimerAmount,
                        ceilingPuttyAmount = ceilingPuttyAmount,
                        ceilingPrimerAmount = ceilingPrimerAmount,
                        totalRoomAmount = totalRoomAmount,
                        isLabourOnlyMode = isLabourOnlyMode
                    )
                }
            }
        }
    }
}

@Composable
private fun WallSectionContent(
    wallEntries: List<WallMeasurementEntry>,
    wallDeductions: List<WallDeductionEntry>,
    wallPaint: SurfacePaintConfig,
    grossArea: Double,
    totalDeductions: Double,
    netArea: Double,
    amount: Double,
    isLabourOnlyMode: Boolean,
    onAddWallEntry: () -> Unit,
    onUpdateWallEntry: (Int, WallMeasurementEntry) -> Unit,
    onDeleteWallEntry: (Int) -> Unit,
    onAddDeduction: () -> Unit,
    onUpdateDeduction: (Int, WallDeductionEntry) -> Unit,
    onDeleteDeduction: (Int) -> Unit,
    onUpdatePaint: (SurfacePaintConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Measurement Entries Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WALL MEASUREMENTS (${wallEntries.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = PrimaryNavy
            )
            Button(
                onClick = onAddWallEntry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("btn_add_wall_measurement")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Wall", color = PrimaryNavy, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        wallEntries.forEachIndexed { index, entry ->
            WallMeasurementRow(
                index = index,
                entry = entry,
                canDelete = wallEntries.size > 1,
                onUpdate = { onUpdateWallEntry(index, it) },
                onDelete = { onDeleteWallEntry(index) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Wall Deductions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WALL DEDUCTIONS (${wallDeductions.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFFDC2626)
                )
                Text(
                    text = "Door, Window, Ventilator, Opening",
                    fontSize = 10.5.sp,
                    color = Slate500
                )
            }
            Button(
                onClick = onAddDeduction,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("btn_add_wall_deduction")
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Deduction", color = Color(0xFFDC2626), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (wallDeductions.isEmpty()) {
            Text(
                text = "No deductions added yet (doors/windows count full area)",
                fontSize = 11.sp,
                color = Slate500,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        } else {
            wallDeductions.forEachIndexed { index, deduction ->
                WallDeductionRow(
                    index = index,
                    deduction = deduction,
                    onUpdate = { onUpdateDeduction(index, it) },
                    onDelete = { onDeleteDeduction(index) }
                )
            }
        }

        // Deductions validation warning
        if (totalDeductions > grossArea && grossArea > 0) {
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚠️ Validation: Total deductions (${CurrencyUtils.formatSqft(totalDeductions)}) exceed gross wall area (${CurrencyUtils.formatSqft(grossArea)}). Net wall area capped at 0.",
                    color = Color(0xFFB91C1C),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Paint Selection for Wall
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF8FAFC),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "WALL PAINT SPECIFICATION & RATE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = PrimaryNavy,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                PaintSelectorForm(
                    config = wallPaint,
                    productTypeList = BrandProductRegistry.wallProductTypes,
                    isLabourOnlyMode = isLabourOnlyMode,
                    onUpdate = onUpdatePaint
                )
            }
        }
    }
}

@Composable
private fun CeilingSectionContent(
    ceilingEntries: List<CeilingMeasurementEntry>,
    ceilingDeductions: List<CeilingDeductionEntry>,
    ceilingPaint: SurfacePaintConfig,
    grossArea: Double,
    totalDeductions: Double,
    netArea: Double,
    amount: Double,
    isLabourOnlyMode: Boolean,
    onAddCeilingEntry: () -> Unit,
    onUpdateCeilingEntry: (Int, CeilingMeasurementEntry) -> Unit,
    onDeleteCeilingEntry: (Int) -> Unit,
    onAddDeduction: () -> Unit,
    onUpdateDeduction: (Int, CeilingDeductionEntry) -> Unit,
    onDeleteDeduction: (Int) -> Unit,
    onUpdatePaint: (SurfacePaintConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Ceiling Entries Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CEILING MEASUREMENTS (${ceilingEntries.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = PrimaryNavy
            )
            Button(
                onClick = onAddCeilingEntry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("btn_add_ceiling_measurement")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Ceiling", color = PrimaryNavy, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        ceilingEntries.forEachIndexed { index, entry ->
            CeilingMeasurementRow(
                index = index,
                entry = entry,
                canDelete = ceilingEntries.size > 1,
                onUpdate = { onUpdateCeilingEntry(index, it) },
                onDelete = { onDeleteCeilingEntry(index) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Ceiling Deductions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CEILING DEDUCTIONS (${ceilingDeductions.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFFDC2626)
                )
                Text(
                    text = "Cutout, Skylight, Opening, Shaft, Other",
                    fontSize = 10.5.sp,
                    color = Slate500
                )
            }
            Button(
                onClick = onAddDeduction,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("btn_add_ceiling_deduction")
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Deduction", color = Color(0xFFDC2626), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (ceilingDeductions.isEmpty()) {
            Text(
                text = "No ceiling deductions (skylights/cutouts) added",
                fontSize = 11.sp,
                color = Slate500,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        } else {
            ceilingDeductions.forEachIndexed { index, deduction ->
                CeilingDeductionRow(
                    index = index,
                    deduction = deduction,
                    onUpdate = { onUpdateDeduction(index, it) },
                    onDelete = { onDeleteDeduction(index) }
                )
            }
        }

        if (totalDeductions > grossArea && grossArea > 0) {
            Surface(
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚠️ Validation: Ceiling deductions exceed gross ceiling area. Net ceiling capped at 0.",
                    color = Color(0xFFB91C1C),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Ceiling Paint Selector
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF8FAFC),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "CEILING PAINT SPECIFICATION & RATE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = PrimaryNavy,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                PaintSelectorForm(
                    config = ceilingPaint,
                    productTypeList = BrandProductRegistry.ceilingProductTypes,
                    isLabourOnlyMode = isLabourOnlyMode,
                    onUpdate = onUpdatePaint
                )
            }
        }
    }
}

@Composable
private fun PuttyPrimerSectionContent(
    wallPutty: SurfacePrepConfig,
    wallPrimer: SurfacePrepConfig,
    ceilingPutty: SurfacePrepConfig,
    ceilingPrimer: SurfacePrepConfig,
    netWallArea: Double,
    netCeilingArea: Double,
    isLabourOnlyMode: Boolean,
    onUpdateWallPutty: (SurfacePrepConfig) -> Unit,
    onUpdateWallPrimer: (SurfacePrepConfig) -> Unit,
    onUpdateCeilingPutty: (SurfacePrepConfig) -> Unit,
    onUpdateCeilingPrimer: (SurfacePrepConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SurfacePrepItemCard(
            title = "Wall Putty (वॉल पुट्टी)",
            config = wallPutty,
            defaultArea = netWallArea,
            isLabourOnlyMode = isLabourOnlyMode,
            onUpdate = onUpdateWallPutty
        )

        SurfacePrepItemCard(
            title = "Wall Primer (वॉल प्राइमर)",
            config = wallPrimer,
            defaultArea = netWallArea,
            isLabourOnlyMode = isLabourOnlyMode,
            onUpdate = onUpdateWallPrimer
        )

        SurfacePrepItemCard(
            title = "Ceiling Putty (सीलिंग पुट्टी)",
            config = ceilingPutty,
            defaultArea = netCeilingArea,
            isLabourOnlyMode = isLabourOnlyMode,
            onUpdate = onUpdateCeilingPutty
        )

        SurfacePrepItemCard(
            title = "Ceiling Primer (सीलिंग प्राइमर)",
            config = ceilingPrimer,
            defaultArea = netCeilingArea,
            isLabourOnlyMode = isLabourOnlyMode,
            onUpdate = onUpdateCeilingPrimer
        )
    }
}

@Composable
private fun SurfacePrepItemCard(
    title: String,
    config: SurfacePrepConfig,
    defaultArea: Double,
    isLabourOnlyMode: Boolean,
    onUpdate: (SurfacePrepConfig) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (config.enabled) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)
        ),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (config.enabled) TealSuccess.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = if (config.enabled) TealSuccess else Slate700
                    )
                    Text(
                        text = if (config.enabled) "Included • ${config.brand} (${config.product})" else "Excluded (not charged)",
                        fontSize = 10.5.sp,
                        color = Slate500
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (config.enabled) {
                        Text(
                            text = CurrencyUtils.formatInr(config.calculateAmount(defaultArea, isLabourOnlyMode)),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TealSuccess,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Switch(
                        checked = config.enabled,
                        onCheckedChange = { onUpdate(config.copy(enabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TealSuccess
                        )
                    )
                }
            }

            if (config.enabled) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFDCFCE7))
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = config.brand,
                        onValueChange = { onUpdate(config.copy(brand = it)) },
                        label = { Text("Brand") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = config.product,
                        onValueChange = { onUpdate(config.copy(product = it)) },
                        label = { Text("Product") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = if (config.customAreaSqft > 0) config.customAreaSqft.toString() else defaultArea.toInt().toString(),
                        onValueChange = { onUpdate(config.copy(customAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Area (sq.ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = config.coats.toString(),
                        onValueChange = { onUpdate(config.copy(coats = it.toIntOrNull() ?: 1)) },
                        label = { Text("Coats") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.7f)
                    )
                    if (!isLabourOnlyMode) {
                        OutlinedTextField(
                            value = config.materialRate.toString(),
                            onValueChange = { onUpdate(config.copy(materialRate = it.toDoubleOrNull() ?: 0.0)) },
                            label = { Text("Mat. ₹") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                    OutlinedTextField(
                        value = config.labourRate.toString(),
                        onValueChange = { onUpdate(config.copy(labourRate = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Lab. ₹") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = config.remarks,
                    onValueChange = { onUpdate(config.copy(remarks = it)) },
                    label = { Text("Remarks (e.g. 2 coats with knife finish)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun WallMeasurementRow(
    index: Int,
    entry: WallMeasurementEntry,
    canDelete: Boolean,
    onUpdate: (WallMeasurementEntry) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Wall ${index + 1}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (entry.isDirectArea) "Direct Area Mode" else "L × H (Ft & In)",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyUtils.formatSqft(entry.grossAreaSqft),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = PrimaryNavy
                    )
                    if (canDelete) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Method toggle and inputs
            if (!entry.isDirectArea) {
                // Row 1: Length (Ft & In) + Height (Ft & In) + Count + Mode switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (entry.lengthFt > 0) entry.lengthFt.toString() else "",
                        onValueChange = { onUpdate(entry.copy(lengthFt = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("L (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = if (entry.lengthIn > 0) entry.lengthIn.toString() else "",
                        onValueChange = { onUpdate(entry.copy(lengthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                        label = { Text("L (in)") },
                        placeholder = { Text("0-11") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = if (entry.heightFt > 0) entry.heightFt.toString() else "",
                        onValueChange = { onUpdate(entry.copy(heightFt = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("H (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = if (entry.heightIn > 0) entry.heightIn.toString() else "",
                        onValueChange = { onUpdate(entry.copy(heightIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                        label = { Text("H (in)") },
                        placeholder = { Text("0-11") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = entry.wallCount.toString(),
                        onValueChange = { onUpdate(entry.copy(wallCount = (it.toIntOrNull() ?: 1).coerceAtLeast(1))) },
                        label = { Text("Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f)
                    )
                    Button(
                        onClick = { onUpdate(entry.copy(isDirectArea = !entry.isDirectArea)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(text = "Sqft", fontSize = 10.sp, color = Slate700)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (entry.directAreaSqft > 0) entry.directAreaSqft.toString() else "",
                        onValueChange = { onUpdate(entry.copy(directAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Direct Area (sq.ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f)
                    )
                    Button(
                        onClick = { onUpdate(entry.copy(isDirectArea = !entry.isDirectArea)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(text = "L×H", fontSize = 10.5.sp, color = Slate700)
                    }
                }
            }
        }
    }
}

@Composable
private fun WallDeductionRow(
    index: Int,
    deduction: WallDeductionEntry,
    onUpdate: (WallDeductionEntry) -> Unit,
    onDelete: () -> Unit
) {
    var typeDropdownOpen by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                        modifier = Modifier.clickable { typeDropdownOpen = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = deduction.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFFB91C1C), modifier = Modifier.size(16.dp))
                        }
                    }

                    DropdownMenu(
                        expanded = typeDropdownOpen,
                        onDismissRequest = { typeDropdownOpen = false }
                    ) {
                        BrandProductRegistry.wallDeductionTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    onUpdate(deduction.copy(name = type))
                                    typeDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "-${CurrencyUtils.formatSqft(deduction.deductionAreaSqft)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626)
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!deduction.isDirectArea) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (deduction.lengthFt > 0) deduction.lengthFt.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(lengthFt = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("L (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = if (deduction.lengthIn > 0) deduction.lengthIn.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(lengthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                        label = { Text("L (in)") },
                        placeholder = { Text("0-11") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = if (deduction.heightFt > 0) deduction.heightFt.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(heightFt = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("H (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = if (deduction.heightIn > 0) deduction.heightIn.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(heightIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                        label = { Text("H (in)") },
                        placeholder = { Text("0-11") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = deduction.count.toString(),
                        onValueChange = { onUpdate(deduction.copy(count = (it.toIntOrNull() ?: 1).coerceAtLeast(1))) },
                        label = { Text("Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f)
                    )
                    Button(
                        onClick = { onUpdate(deduction.copy(isDirectArea = !deduction.isDirectArea)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(text = "Direct", fontSize = 9.5.sp, color = Color(0xFFDC2626))
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (deduction.directAreaSqft > 0) deduction.directAreaSqft.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(directAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Direct Deduction (sq.ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f)
                    )
                    Button(
                        onClick = { onUpdate(deduction.copy(isDirectArea = !deduction.isDirectArea)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(text = "L×H", fontSize = 10.sp, color = Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}

@Composable
private fun CeilingMeasurementRow(
    index: Int,
    entry: CeilingMeasurementEntry,
    canDelete: Boolean,
    onUpdate: (CeilingMeasurementEntry) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ceiling Section ${index + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = Slate700
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyUtils.formatSqft(entry.grossAreaSqft),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = PrimaryNavy
                    )
                    if (canDelete) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!entry.isDirectArea) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (entry.lengthFt > 0) entry.lengthFt.toString() else "",
                        onValueChange = { onUpdate(entry.copy(lengthFt = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("L (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = if (entry.lengthIn > 0) entry.lengthIn.toString() else "",
                        onValueChange = { onUpdate(entry.copy(lengthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                        label = { Text("L (in)") },
                        placeholder = { Text("0-11") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = if (entry.widthFt > 0) entry.widthFt.toString() else "",
                        onValueChange = { onUpdate(entry.copy(widthFt = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("W (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = if (entry.widthIn > 0) entry.widthIn.toString() else "",
                        onValueChange = { onUpdate(entry.copy(widthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                        label = { Text("W (in)") },
                        placeholder = { Text("0-11") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                    Button(
                        onClick = { onUpdate(entry.copy(isDirectArea = !entry.isDirectArea)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(text = "Sqft", fontSize = 10.sp, color = Slate700)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (entry.directAreaSqft > 0) entry.directAreaSqft.toString() else "",
                        onValueChange = { onUpdate(entry.copy(directAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Direct Area (sq.ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f)
                    )
                    Button(
                        onClick = { onUpdate(entry.copy(isDirectArea = !entry.isDirectArea)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(text = "L×W", fontSize = 10.5.sp, color = Slate700)
                    }
                }
            }
        }
    }
}

@Composable
private fun CeilingDeductionRow(
    index: Int,
    deduction: CeilingDeductionEntry,
    onUpdate: (CeilingDeductionEntry) -> Unit,
    onDelete: () -> Unit
) {
    var typeDropdownOpen by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                        modifier = Modifier.clickable { typeDropdownOpen = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = deduction.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFFB91C1C), modifier = Modifier.size(16.dp))
                        }
                    }

                    DropdownMenu(
                        expanded = typeDropdownOpen,
                        onDismissRequest = { typeDropdownOpen = false }
                    ) {
                        BrandProductRegistry.ceilingDeductionTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    onUpdate(deduction.copy(name = type))
                                    typeDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "-${CurrencyUtils.formatSqft(deduction.deductionAreaSqft)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626)
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!deduction.isDirectArea) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (deduction.lengthFt > 0) deduction.lengthFt.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(lengthFt = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("L (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = if (deduction.lengthIn > 0) deduction.lengthIn.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(lengthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                        label = { Text("L (in)") },
                        placeholder = { Text("0-11") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = if (deduction.widthFt > 0) deduction.widthFt.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(widthFt = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("W (ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = if (deduction.widthIn > 0) deduction.widthIn.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(widthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                        label = { Text("W (in)") },
                        placeholder = { Text("0-11") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f)
                    )
                    Button(
                        onClick = { onUpdate(deduction.copy(isDirectArea = !deduction.isDirectArea)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(text = "Direct", fontSize = 9.5.sp, color = Color(0xFFDC2626))
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (deduction.directAreaSqft > 0) deduction.directAreaSqft.toString() else "",
                        onValueChange = { onUpdate(deduction.copy(directAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Direct Deduction (sq.ft)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f)
                    )
                    Button(
                        onClick = { onUpdate(deduction.copy(isDirectArea = !deduction.isDirectArea)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(text = "L×W", fontSize = 10.sp, color = Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}

@Composable
private fun PaintSelectorForm(
    config: SurfacePaintConfig,
    productTypeList: List<String>,
    isLabourOnlyMode: Boolean,
    onUpdate: (SurfacePaintConfig) -> Unit
) {
    var brandDropdownOpen by remember { mutableStateOf(false) }
    var productDropdownOpen by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Brand & Product Type Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Brand Dropdown
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = config.brand,
                    onValueChange = { onUpdate(config.copy(brand = it)) },
                    label = { Text("Brand") },
                    trailingIcon = {
                        IconButton(onClick = { brandDropdownOpen = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = brandDropdownOpen,
                    onDismissRequest = { brandDropdownOpen = false }
                ) {
                    BrandProductRegistry.brandOptions.forEach { b ->
                        DropdownMenuItem(
                            text = { Text(b) },
                            onClick = {
                                onUpdate(config.copy(brand = b))
                                brandDropdownOpen = false
                            }
                        )
                    }
                }
            }

            // Product Type Dropdown
            Box(modifier = Modifier.weight(1.2f)) {
                OutlinedTextField(
                    value = config.productType,
                    onValueChange = { onUpdate(config.copy(productType = it)) },
                    label = { Text("Product Type") },
                    trailingIcon = {
                        IconButton(onClick = { productDropdownOpen = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = productDropdownOpen,
                    onDismissRequest = { productDropdownOpen = false }
                ) {
                    productTypeList.forEach { pt ->
                        DropdownMenuItem(
                            text = { Text(pt) },
                            onClick = {
                                onUpdate(config.copy(productType = pt))
                                productDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Product Variant & Coats
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = config.productVariant,
                onValueChange = { onUpdate(config.copy(productVariant = it)) },
                label = { Text("Product Variant / Name (optional)") },
                modifier = Modifier.weight(1.4f),
                singleLine = true
            )

            OutlinedTextField(
                value = config.coats.toString(),
                onValueChange = { onUpdate(config.copy(coats = (it.toIntOrNull() ?: 1).coerceAtLeast(1))) },
                label = { Text("Coats") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.7f)
            )
        }

        // Rates Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (!isLabourOnlyMode) {
                OutlinedTextField(
                    value = config.materialRate.toString(),
                    onValueChange = { onUpdate(config.copy(materialRate = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Material Rate (₹/sq.ft)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = config.labourRate.toString(),
                onValueChange = { onUpdate(config.copy(labourRate = it.toDoubleOrNull() ?: 0.0)) },
                label = { Text("Labour Rate (₹/sq.ft)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            Surface(
                color = PrimaryNavy.copy(alpha = 0.08f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(0.9f)
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Total Rate", fontSize = 10.sp, color = Slate500)
                    Text(
                        text = "₹${config.getEffectiveRate(isLabourOnlyMode)}/sqft",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                }
            }
        }

        // Remarks
        OutlinedTextField(
            value = config.remarks,
            onValueChange = { onUpdate(config.copy(remarks = it)) },
            label = { Text("Remarks (e.g. 2 coats over primer, matt finish)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
private fun RoomSummaryFooter(
    grossWallArea: Double,
    totalWallDeduction: Double,
    netWallArea: Double,
    wallPaint: SurfacePaintConfig,
    wallAmount: Double,
    grossCeilingArea: Double,
    totalCeilingDeduction: Double,
    netCeilingArea: Double,
    ceilingPaint: SurfacePaintConfig,
    ceilingAmount: Double,
    wallPuttyAmount: Double,
    wallPrimerAmount: Double,
    ceilingPuttyAmount: Double,
    ceilingPrimerAmount: Double,
    totalRoomAmount: Double,
    isLabourOnlyMode: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "ROOM CALCULATION SUMMARY",
                color = AccentGold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // WALL SUMMARY
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "WALL SUMMARY:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                Text(text = CurrencyUtils.formatInr(wallAmount), color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Text(
                text = "Gross: ${CurrencyUtils.formatSqft(grossWallArea)} − Ded: ${CurrencyUtils.formatSqft(totalWallDeduction)} = Net: ${CurrencyUtils.formatSqft(netWallArea)}",
                color = Color(0xFFCBD5E1),
                fontSize = 10.5.sp
            )
            Text(
                text = "Paint: ${wallPaint.brand} ${wallPaint.productType} • ${wallPaint.coats} Coats @ ₹${wallPaint.getEffectiveRate(isLabourOnlyMode)}/sq.ft",
                color = Color(0xFFE2E8F0),
                fontSize = 10.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))

            // CEILING SUMMARY
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "CEILING SUMMARY:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                Text(text = CurrencyUtils.formatInr(ceilingAmount), color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Text(
                text = "Gross: ${CurrencyUtils.formatSqft(grossCeilingArea)} − Ded: ${CurrencyUtils.formatSqft(totalCeilingDeduction)} = Net: ${CurrencyUtils.formatSqft(netCeilingArea)}",
                color = Color(0xFFCBD5E1),
                fontSize = 10.5.sp
            )
            Text(
                text = "Paint: ${ceilingPaint.brand} ${ceilingPaint.productType} • ${ceilingPaint.coats} Coats @ ₹${ceilingPaint.getEffectiveRate(isLabourOnlyMode)}/sq.ft",
                color = Color(0xFFE2E8F0),
                fontSize = 10.5.sp
            )

            val totalPrep = wallPuttyAmount + wallPrimerAmount + ceilingPuttyAmount + ceilingPrimerAmount
            if (totalPrep > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Putty & Primer Total:", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    Text(text = CurrencyUtils.formatInr(totalPrep), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "ROOM TOTAL (${if (isLabourOnlyMode) "Labour Only" else "Material + Labour"}):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = CurrencyUtils.formatInr(totalRoomAmount), color = AccentGold, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
            }
        }
    }
}
