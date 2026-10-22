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
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
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

    var wallMultiplier by remember(parsed) { mutableStateOf(parsed.wallMultiplier) }
    var ceilingMultiplier by remember(parsed) { mutableStateOf(parsed.ceilingMultiplier) }
    var wallPaintingEnabled by remember(room.id, room.wallPaintingEnabled) { mutableStateOf(room.wallPaintingEnabled) }
    var ceilingPaintingEnabled by remember(room.id, room.ceilingPaintingEnabled) { mutableStateOf(room.ceilingPaintingEnabled) }
    var wallRemarks by remember(parsed) { mutableStateOf(parsed.wallRemarks.ifBlank { parsed.wallPaint.remarks }) }
    var ceilingRemarks by remember(parsed) { mutableStateOf(parsed.ceilingRemarks.ifBlank { parsed.ceilingPaint.remarks }) }

    var quickLengthFt by remember(room.id, room.lengthFt) { mutableStateOf(if (room.lengthFt > 0) room.lengthFt.toInt().toString() else "14") }
    var quickLengthIn by remember(room.id, room.lengthIn) { mutableStateOf(room.lengthIn.toInt().toString()) }
    var quickWidthFt by remember(room.id, room.widthFt) { mutableStateOf(if (room.widthFt > 0) room.widthFt.toInt().toString() else "12") }
    var quickWidthIn by remember(room.id, room.widthIn) { mutableStateOf(room.widthIn.toInt().toString()) }
    var quickHeightFt by remember(room.id, room.heightFt) { mutableStateOf(if (room.heightFt > 0) room.heightFt.toInt().toString() else "10") }
    var quickHeightIn by remember(room.id, room.heightIn) { mutableStateOf(room.heightIn.toInt().toString()) }

    // Instant independent calculations
    val grossWallArea = wallEntries.sumOf { it.grossAreaSqft }
    val totalWallDeduction = wallDeductions.sumOf { it.deductionAreaSqft }
    val netWallArea = if (deductOpeningsGlobal) maxOf(0.0, grossWallArea - totalWallDeduction) else grossWallArea
    val finalWallArea = if (wallPaintingEnabled) netWallArea * wallMultiplier else 0.0
    val wallAmount = if (wallPaintingEnabled) wallPaint.calculateAmount(finalWallArea, isLabourOnlyMode) else 0.0

    val grossCeilingArea = ceilingEntries.sumOf { it.grossAreaSqft }
    val totalCeilingDeduction = ceilingDeductions.sumOf { it.deductionAreaSqft }
    val netCeilingArea = maxOf(0.0, grossCeilingArea - totalCeilingDeduction)
    val finalCeilingArea = if (ceilingPaintingEnabled) netCeilingArea * ceilingMultiplier else 0.0
    val ceilingAmount = if (ceilingPaintingEnabled) ceilingPaint.calculateAmount(finalCeilingArea, isLabourOnlyMode) else 0.0

    val wallPuttyAmount = if (wallPaintingEnabled) wallPutty.calculateAmount(finalWallArea, isLabourOnlyMode) else 0.0
    val wallPrimerAmount = if (wallPaintingEnabled) wallPrimer.calculateAmount(finalWallArea, isLabourOnlyMode) else 0.0
    val ceilingPuttyAmount = if (ceilingPaintingEnabled) ceilingPutty.calculateAmount(finalCeilingArea, isLabourOnlyMode) else 0.0
    val ceilingPrimerAmount = if (ceilingPaintingEnabled) ceilingPrimer.calculateAmount(finalCeilingArea, isLabourOnlyMode) else 0.0

    val totalRoomAmount = wallAmount + ceilingAmount + wallPuttyAmount + wallPrimerAmount + ceilingPuttyAmount + ceilingPrimerAmount

    fun persistChanges() {
        val serialized = RoomDetailsJsonConverter.serialize(
            wallEntries = wallEntries,
            wallDeductions = wallDeductions,
            wallPaint = wallPaint.copy(remarks = wallRemarks),
            wallPutty = wallPutty,
            wallPrimer = wallPrimer,
            ceilingEntries = ceilingEntries,
            ceilingDeductions = ceilingDeductions,
            ceilingPaint = ceilingPaint.copy(remarks = ceilingRemarks),
            ceilingPutty = ceilingPutty,
            ceilingPrimer = ceilingPrimer,
            wallMultiplier = wallMultiplier,
            ceilingMultiplier = ceilingMultiplier,
            wallPaintingEnabled = wallPaintingEnabled,
            ceilingPaintingEnabled = ceilingPaintingEnabled,
            wallRemarks = wallRemarks,
            ceilingRemarks = ceilingRemarks
        )
        onSaveRoom(
            room.copy(
                detailedData = serialized,
                additionalDeductionsSqft = totalWallDeduction,
                wallPaintingEnabled = wallPaintingEnabled,
                ceilingPaintingEnabled = ceilingPaintingEnabled
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = room.roomName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            if (room.lengthFt > 0 && room.widthFt > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF1F5F9)
                                ) {
                                    Text(
                                        text = "${room.lengthFt.toInt()}'${room.lengthIn.toInt()}\"×${room.widthFt.toInt()}'${room.widthIn.toInt()}\"",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryNavy,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
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

            // Expanded view: Quick Room Size, Tabs, Inputs, Deductions, Rates, Summaries
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Slate100)
                    Spacer(modifier = Modifier.height(10.dp))

                    // ---------------- QUICK ROOM SIZE BOX (कमरे का त्वरित नाप) ----------------
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Straighten, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Room Size (कमरे का मुख्य नाप)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = PrimaryNavy
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PrimaryNavy.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "Easy L × W × H Mode",
                                        fontSize = 9.5.sp,
                                        color = PrimaryNavy,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Length
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Length (लंबाई)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        OutlinedTextField(
                                            value = quickLengthFt,
                                            onValueChange = { quickLengthFt = it },
                                            label = { Text("Ft") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1.1f),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = quickLengthIn,
                                            onValueChange = { quickLengthIn = it },
                                            label = { Text("In") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(0.9f),
                                            singleLine = true
                                        )
                                    }
                                }

                                // Width
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Width (चौड़ाई)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        OutlinedTextField(
                                            value = quickWidthFt,
                                            onValueChange = { quickWidthFt = it },
                                            label = { Text("Ft") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1.1f),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = quickWidthIn,
                                            onValueChange = { quickWidthIn = it },
                                            label = { Text("In") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(0.9f),
                                            singleLine = true
                                        )
                                    }
                                }

                                // Height
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Height (ऊंचाई)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        OutlinedTextField(
                                            value = quickHeightFt,
                                            onValueChange = { quickHeightFt = it },
                                            label = { Text("Ft") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1.1f),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = quickHeightIn,
                                            onValueChange = { quickHeightIn = it },
                                            label = { Text("In") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(0.9f),
                                            singleLine = true
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val lF = quickLengthFt.toDoubleOrNull() ?: 14.0
                                    val lI = quickLengthIn.toDoubleOrNull() ?: 0.0
                                    val wF = quickWidthFt.toDoubleOrNull() ?: 12.0
                                    val wI = quickWidthIn.toDoubleOrNull() ?: 0.0
                                    val hF = quickHeightFt.toDoubleOrNull() ?: 10.0
                                    val hI = quickHeightIn.toDoubleOrNull() ?: 0.0

                                    wallEntries = listOf(
                                        WallMeasurementEntry(label = "Wall 1 & 2 (Length sides)", lengthFt = lF, lengthIn = lI, heightFt = hF, heightIn = hI, wallCount = 2),
                                        WallMeasurementEntry(label = "Wall 3 & 4 (Width sides)", lengthFt = wF, lengthIn = wI, heightFt = hF, heightIn = hI, wallCount = 2)
                                    )
                                    ceilingEntries = listOf(
                                        CeilingMeasurementEntry(label = "Ceiling Area", lengthFt = lF, lengthIn = lI, widthFt = wF, widthIn = wI)
                                    )
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
                                            lengthFt = lF,
                                            lengthIn = lI,
                                            widthFt = wF,
                                            widthIn = wI,
                                            heightFt = hF,
                                            heightIn = hI,
                                            detailedData = serialized,
                                            additionalDeductionsSqft = totalWallDeduction
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Apply Size to All Walls & Ceiling (लागू करें)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
                                wallPaintingEnabled = wallPaintingEnabled,
                                onToggleWallPainting = {
                                    wallPaintingEnabled = it
                                    persistChanges()
                                },
                                wallMultiplier = wallMultiplier,
                                onUpdateWallMultiplier = {
                                    wallMultiplier = it
                                    persistChanges()
                                },
                                wallRemarks = wallRemarks,
                                onUpdateWallRemarks = {
                                    wallRemarks = it
                                    persistChanges()
                                },
                                wallEntries = wallEntries,
                                wallDeductions = wallDeductions,
                                wallPaint = wallPaint,
                                grossArea = grossWallArea,
                                totalDeductions = totalWallDeduction,
                                netArea = netWallArea,
                                finalArea = finalWallArea,
                                amount = wallAmount,
                                isLabourOnlyMode = isLabourOnlyMode,
                                onAddWallEntry = {
                                    wallEntries = wallEntries + WallMeasurementEntry(label = "Wall ${wallEntries.size + 1}", lengthFt = 12.0, heightFt = 10.0)
                                    persistChanges()
                                },
                                onSplitAllWalls = {
                                    val newList = mutableListOf<WallMeasurementEntry>()
                                    var countIdx = 1
                                    wallEntries.forEach { entry ->
                                        if (entry.wallCount > 1) {
                                            for (i in 1..entry.wallCount) {
                                                val sideName = if (entry.label.contains("Length")) "Length Side $i"
                                                    else if (entry.label.contains("Width")) "Width Side $i"
                                                    else "Wall $countIdx"
                                                newList.add(
                                                    entry.copy(
                                                        id = java.util.UUID.randomUUID().toString(),
                                                        label = "Wall $countIdx ($sideName)",
                                                        wallCount = 1
                                                    )
                                                )
                                                countIdx++
                                            }
                                        } else {
                                            newList.add(entry)
                                            countIdx++
                                        }
                                    }
                                    wallEntries = newList
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
                                onAddQuickDeduction = { name, lFt, lIn, hFt, hIn, count ->
                                    wallDeductions = wallDeductions + WallDeductionEntry(name = name, lengthFt = lFt, lengthIn = lIn, heightFt = hFt, heightIn = hIn, count = count)
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
                                ceilingPaintingEnabled = ceilingPaintingEnabled,
                                onToggleCeilingPainting = {
                                    ceilingPaintingEnabled = it
                                    persistChanges()
                                },
                                ceilingMultiplier = ceilingMultiplier,
                                onUpdateCeilingMultiplier = {
                                    ceilingMultiplier = it
                                    persistChanges()
                                },
                                ceilingRemarks = ceilingRemarks,
                                onUpdateCeilingRemarks = {
                                    ceilingRemarks = it
                                    persistChanges()
                                },
                                ceilingEntries = ceilingEntries,
                                ceilingDeductions = ceilingDeductions,
                                ceilingPaint = ceilingPaint,
                                grossArea = grossCeilingArea,
                                totalDeductions = totalCeilingDeduction,
                                netArea = netCeilingArea,
                                finalArea = finalCeilingArea,
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
                                onAddQuickDeduction = { name, lFt, lIn, wFt, wIn ->
                                    ceilingDeductions = ceilingDeductions + CeilingDeductionEntry(name = name, lengthFt = lFt, lengthIn = lIn, widthFt = wFt, widthIn = wIn)
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
                        wallPaintingEnabled = wallPaintingEnabled,
                        grossWallArea = grossWallArea,
                        totalWallDeduction = totalWallDeduction,
                        netWallArea = netWallArea,
                        wallMultiplier = wallMultiplier,
                        finalWallArea = finalWallArea,
                        wallPaint = wallPaint,
                        wallAmount = wallAmount,
                        ceilingPaintingEnabled = ceilingPaintingEnabled,
                        grossCeilingArea = grossCeilingArea,
                        totalCeilingDeduction = totalCeilingDeduction,
                        netCeilingArea = netCeilingArea,
                        ceilingMultiplier = ceilingMultiplier,
                        finalCeilingArea = finalCeilingArea,
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
    wallPaintingEnabled: Boolean,
    onToggleWallPainting: (Boolean) -> Unit,
    wallMultiplier: Double,
    onUpdateWallMultiplier: (Double) -> Unit,
    wallRemarks: String,
    onUpdateWallRemarks: (String) -> Unit,
    wallEntries: List<WallMeasurementEntry>,
    wallDeductions: List<WallDeductionEntry>,
    wallPaint: SurfacePaintConfig,
    grossArea: Double,
    totalDeductions: Double,
    netArea: Double,
    finalArea: Double,
    amount: Double,
    isLabourOnlyMode: Boolean,
    onAddWallEntry: () -> Unit,
    onSplitAllWalls: (() -> Unit)? = null,
    onUpdateWallEntry: (Int, WallMeasurementEntry) -> Unit,
    onDeleteWallEntry: (Int) -> Unit,
    onAddDeduction: () -> Unit,
    onAddQuickDeduction: (String, Double, Double, Double, Double, Int) -> Unit,
    onUpdateDeduction: (Int, WallDeductionEntry) -> Unit,
    onDeleteDeduction: (Int) -> Unit,
    onUpdatePaint: (SurfacePaintConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Wall Enable / Disable Header Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = if (wallPaintingEnabled) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (wallPaintingEnabled) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatPaint,
                        contentDescription = null,
                        tint = if (wallPaintingEnabled) TealSuccess else Slate500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Wall Measurement Section",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = if (wallPaintingEnabled) PrimaryNavy else Slate700
                        )
                        Text(
                            text = if (wallPaintingEnabled) "Wall painting is active in calculations" else "Wall painting disabled (excluded from total)",
                            fontSize = 10.sp,
                            color = Slate500
                        )
                    }
                }
                Switch(
                    checked = wallPaintingEnabled,
                    onCheckedChange = onToggleWallPainting,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealSuccess
                    )
                )
            }
        }

        // Multiplier Coefficient & Remarks
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "WALL MULTIPLIER & REMARKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (wallMultiplier > 0) String.format(java.util.Locale.US, "%.2f", wallMultiplier) else "1.00",
                        onValueChange = {
                            val v = (it.toDoubleOrNull() ?: 1.0).coerceAtLeast(0.01)
                            onUpdateWallMultiplier(v)
                        },
                        label = { Text("Wall Multiplier") },
                        supportingText = { Text("Default: 1.00 (Net × Coeff)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = wallRemarks,
                        onValueChange = onUpdateWallRemarks,
                        label = { Text("Wall Remarks") },
                        placeholder = { Text("e.g. Feature wall, smooth plaster") },
                        modifier = Modifier.weight(1.8f),
                        singleLine = true
                    )
                }
            }
        }

        // Live Calculation Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WALL CALCULATION BREAKDOWN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PrimaryNavy,
                        letterSpacing = 0.5.sp
                    )
                    Surface(
                        color = if (wallPaintingEnabled) TealSuccess.copy(alpha = 0.15f) else Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (wallPaintingEnabled) "ACTIVE" else "DISABLED",
                            color = if (wallPaintingEnabled) TealSuccess else Color(0xFFB91C1C),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gross Area: ${CurrencyUtils.formatSqft(grossArea)} − Deduction: ${CurrencyUtils.formatSqft(totalDeductions)} = Net Area: ${CurrencyUtils.formatSqft(netArea)}",
                    fontSize = 10.5.sp,
                    color = Slate700
                )
                Text(
                    text = "Net Area (${CurrencyUtils.formatSqft(netArea)}) × Multiplier ($wallMultiplier) = Final Wall Area: ${CurrencyUtils.formatSqft(finalArea)}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryNavy
                )
                val rate = wallPaint.getEffectiveRate(isLabourOnlyMode)
                if (rate > 0) {
                    Text(
                        text = "Final Area (${CurrencyUtils.formatSqft(finalArea)}) × Rate (₹${String.format(java.util.Locale.US, "%.2f", rate)}/sq.ft) = Wall Amount: ${CurrencyUtils.formatInr(amount)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealSuccess
                    )
                } else {
                    Text(
                        text = "Wall Rate: Rate Not Entered (Please enter rate per sq ft below)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }

        // Measurement Entries Header with Individual Wall Multiplier Support
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "WALL MEASUREMENTS (${wallEntries.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryNavy
                    )
                    val customCoeffCount = wallEntries.count { it.multiplier != 1.0 }
                    if (customCoeffCount > 0) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "$customCoeffCount wall coeff modified",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "Configure dimensions & multiplier coefficient separately per wall",
                    fontSize = 10.sp,
                    color = Slate500
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (wallEntries.any { it.wallCount > 1 } && onSplitAllWalls != null) {
                    OutlinedButton(
                        onClick = onSplitAllWalls,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_split_walls_separate")
                    ) {
                        Text(text = "Split 4 Walls", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryNavy)
                    }
                }

                Button(
                    onClick = onAddWallEntry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_add_wall_measurement")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Wall", color = PrimaryNavy, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
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
                Text(text = "+ Custom", color = Color(0xFFDC2626), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Quick 1-Tap Deduction Chips (आसान 1-टैप कटौती)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNavy.copy(alpha = 0.25f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAddQuickDeduction("Door", 3.0, 0.0, 7.0, 0.0, 1) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Text("+ Door (दरवाजा)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text("3×7 ft (21 sqft)", fontSize = 9.sp, color = Slate500)
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNavy.copy(alpha = 0.25f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAddQuickDeduction("Window", 4.0, 0.0, 4.0, 0.0, 1) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Text("+ Window (खिड़की)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text("4×4 ft (16 sqft)", fontSize = 9.sp, color = Slate500)
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNavy.copy(alpha = 0.25f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAddQuickDeduction("Ventilator", 2.0, 0.0, 2.0, 0.0, 1) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Text("+ Vent (रोशनदान)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text("2×2 ft (4 sqft)", fontSize = 9.sp, color = Slate500)
                }
            }
        }

        if (wallDeductions.isEmpty()) {
            Text(
                text = "No deductions added yet (tap a chip above to add Door or Window)",
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
    ceilingPaintingEnabled: Boolean,
    onToggleCeilingPainting: (Boolean) -> Unit,
    ceilingMultiplier: Double,
    onUpdateCeilingMultiplier: (Double) -> Unit,
    ceilingRemarks: String,
    onUpdateCeilingRemarks: (String) -> Unit,
    ceilingEntries: List<CeilingMeasurementEntry>,
    ceilingDeductions: List<CeilingDeductionEntry>,
    ceilingPaint: SurfacePaintConfig,
    grossArea: Double,
    totalDeductions: Double,
    netArea: Double,
    finalArea: Double,
    amount: Double,
    isLabourOnlyMode: Boolean,
    onAddCeilingEntry: () -> Unit,
    onUpdateCeilingEntry: (Int, CeilingMeasurementEntry) -> Unit,
    onDeleteCeilingEntry: (Int) -> Unit,
    onAddDeduction: () -> Unit,
    onAddQuickDeduction: (String, Double, Double, Double, Double) -> Unit,
    onUpdateDeduction: (Int, CeilingDeductionEntry) -> Unit,
    onDeleteDeduction: (Int) -> Unit,
    onUpdatePaint: (SurfacePaintConfig) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Ceiling Enable / Disable Header Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = if (ceilingPaintingEnabled) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (ceilingPaintingEnabled) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatPaint,
                        contentDescription = null,
                        tint = if (ceilingPaintingEnabled) TealSuccess else Slate500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Ceiling Measurement Section",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = if (ceilingPaintingEnabled) PrimaryNavy else Slate700
                        )
                        Text(
                            text = if (ceilingPaintingEnabled) "Ceiling painting is active in calculations" else "Ceiling painting disabled (excluded from total)",
                            fontSize = 10.sp,
                            color = Slate500
                        )
                    }
                }
                Switch(
                    checked = ceilingPaintingEnabled,
                    onCheckedChange = onToggleCeilingPainting,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealSuccess
                    )
                )
            }
        }

        // Multiplier Coefficient & Remarks
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "CEILING MULTIPLIER & REMARKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (ceilingMultiplier > 0) String.format(java.util.Locale.US, "%.2f", ceilingMultiplier) else "1.00",
                        onValueChange = {
                            val v = (it.toDoubleOrNull() ?: 1.0).coerceAtLeast(0.01)
                            onUpdateCeilingMultiplier(v)
                        },
                        label = { Text("Ceiling Multiplier") },
                        supportingText = { Text("Default: 1.00 (Independent of wall)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ceilingRemarks,
                        onValueChange = onUpdateCeilingRemarks,
                        label = { Text("Ceiling Remarks") },
                        placeholder = { Text("e.g. False ceiling, cornice, white finish") },
                        modifier = Modifier.weight(1.8f),
                        singleLine = true
                    )
                }
            }
        }

        // Live Calculation Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CEILING CALCULATION BREAKDOWN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PrimaryNavy,
                        letterSpacing = 0.5.sp
                    )
                    Surface(
                        color = if (ceilingPaintingEnabled) TealSuccess.copy(alpha = 0.15f) else Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (ceilingPaintingEnabled) "ACTIVE" else "DISABLED",
                            color = if (ceilingPaintingEnabled) TealSuccess else Color(0xFFB91C1C),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gross Area: ${CurrencyUtils.formatSqft(grossArea)} − Deduction: ${CurrencyUtils.formatSqft(totalDeductions)} = Net Area: ${CurrencyUtils.formatSqft(netArea)}",
                    fontSize = 10.5.sp,
                    color = Slate700
                )
                Text(
                    text = "Net Area (${CurrencyUtils.formatSqft(netArea)}) × Multiplier ($ceilingMultiplier) = Final Ceiling Area: ${CurrencyUtils.formatSqft(finalArea)}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryNavy
                )
                val rate = ceilingPaint.getEffectiveRate(isLabourOnlyMode)
                if (rate > 0) {
                    Text(
                        text = "Final Area (${CurrencyUtils.formatSqft(finalArea)}) × Rate (₹${String.format(java.util.Locale.US, "%.2f", rate)}/sq.ft) = Ceiling Amount: ${CurrencyUtils.formatInr(amount)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealSuccess
                    )
                } else {
                    Text(
                        text = "Ceiling Rate: Rate Not Entered (Please enter rate per sq ft below)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }

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
                Text(text = "+ Custom", color = Color(0xFFDC2626), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Quick Ceiling Deduction Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNavy.copy(alpha = 0.25f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAddQuickDeduction("Cutout", 2.0, 0.0, 2.0, 0.0) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Text("+ Fan Box / Cutout", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text("2×2 ft (4 sqft)", fontSize = 9.sp, color = Slate500)
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNavy.copy(alpha = 0.25f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAddQuickDeduction("Skylight", 3.0, 0.0, 3.0, 0.0) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Text("+ Skylight", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text("3×3 ft (9 sqft)", fontSize = 9.sp, color = Slate500)
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNavy.copy(alpha = 0.25f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAddQuickDeduction("Shaft", 2.0, 0.0, 3.0, 0.0) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Text("+ Shaft / Opening", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text("2×3 ft (6 sqft)", fontSize = 9.sp, color = Slate500)
                }
            }
        }

        if (ceilingDeductions.isEmpty()) {
            Text(
                text = "No ceiling deductions added (tap a chip above to add Fan Cutout or Skylight)",
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
    var multiplierText by remember(entry.id, entry.multiplier) {
        mutableStateOf(if (entry.multiplier == 1.0) "1.00" else String.format(java.util.Locale.US, "%.2f", entry.multiplier))
    }

    val presetCoeffs = listOf(
        1.00 to "1.0× Normal",
        1.15 to "1.15× Mild",
        1.25 to "1.25× Texture",
        1.50 to "1.5× Rough",
        2.00 to "2.0× Double"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (entry.multiplier != 1.0) 1.5.dp else 1.dp,
            if (entry.multiplier != 1.0) Color(0xFFF59E0B) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row: Wall Label Input/Display, Gross Area Badge with Coeff, Mode Toggle, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = entry.label,
                    onValueChange = { onUpdate(entry.copy(label = it)) },
                    placeholder = { Text("Wall ${index + 1}") },
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(end = 6.dp)
                        .testTag("input_wall_label_$index"),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (entry.multiplier != 1.0) Color(0xFFFEF3C7) else PrimaryNavy.copy(alpha = 0.08f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (entry.multiplier != 1.0) {
                                Text(
                                    text = "Coeff: ${String.format(java.util.Locale.US, "%.2f", entry.multiplier)} • ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                            }
                            Text(
                                text = CurrencyUtils.formatSqft(entry.grossAreaSqft),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = if (entry.multiplier != 1.0) Color(0xFF92400E) else PrimaryNavy
                            )
                        }
                    }

                    // Mode Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (entry.isDirectArea) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (entry.isDirectArea) Color(0xFFD97706) else Color(0xFFCBD5E1)),
                        modifier = Modifier.clickable { onUpdate(entry.copy(isDirectArea = !entry.isDirectArea)) }
                    ) {
                        Text(
                            text = if (entry.isDirectArea) "Direct Sqft" else "L × H",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (entry.isDirectArea) Color(0xFF92400E) else Slate700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }

                    if (canDelete) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (!entry.isDirectArea) {
                // 2-Column Spacious Layout: Length Column on Left, Height Column on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Length Column (L)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Length (लंबाई)", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = if (entry.lengthFt > 0) entry.lengthFt.toString() else "",
                                onValueChange = { onUpdate(entry.copy(lengthFt = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Ft") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (entry.lengthIn > 0) entry.lengthIn.toString() else "",
                                onValueChange = { onUpdate(entry.copy(lengthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                                label = { Text("In") },
                                placeholder = { Text("0-11") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f),
                                singleLine = true
                            )
                        }
                    }

                    // Height Column (H)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Height (ऊंचाई)", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = if (entry.heightFt > 0) entry.heightFt.toString() else "",
                                onValueChange = { onUpdate(entry.copy(heightFt = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Ft") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (entry.heightIn > 0) entry.heightIn.toString() else "",
                                onValueChange = { onUpdate(entry.copy(heightIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                                label = { Text("In") },
                                placeholder = { Text("0-11") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Readable Dimensions Display: e.g. "12 ft 6 in × 10 ft 3 in (12.50 ft × 10.25 ft)"
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${entry.lengthFt.toInt()} ft ${entry.lengthIn.toInt()} in × ${entry.heightFt.toInt()} ft ${entry.heightIn.toInt()} in",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryNavy
                        )
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f ft × %.2f ft", entry.effectiveLengthFt, entry.effectiveHeightFt)}",
                            fontSize = 10.5.sp,
                            color = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Separate Multiplier Coefficient and Number of Walls Stepper Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Multiplier Coefficient for this specific wall
                    OutlinedTextField(
                        value = multiplierText,
                        onValueChange = { text ->
                            multiplierText = text
                            val v = text.toDoubleOrNull()
                            if (v != null && v > 0.0) {
                                onUpdate(entry.copy(multiplier = v))
                            }
                        },
                        label = { Text("Wall Multiplier (दीवार गुणांक)") },
                        supportingText = {
                            Text(
                                if (entry.multiplier != 1.0) "Coeff: ×${String.format(java.util.Locale.US, "%.2f", entry.multiplier)}"
                                else "Default: 1.00 (Normal)"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("input_wall_multiplier_$index"),
                        singleLine = true
                    )

                    // Wall Quantity Stepper
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Walls Count",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FilledTonalIconButton(
                                onClick = { onUpdate(entry.copy(wallCount = maxOf(1, entry.wallCount - 1))) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Text(
                                text = "${entry.wallCount}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            FilledTonalIconButton(
                                onClick = { onUpdate(entry.copy(wallCount = entry.wallCount + 1)) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                // Quick Multiplier Preset Chips for this specific wall
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    presetCoeffs.forEach { (coeff, label) ->
                        val isSelected = kotlin.math.abs(entry.multiplier - coeff) < 0.01
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSelected) AccentGold else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                if (isSelected) Color(0xFFD97706) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.clickable {
                                multiplierText = String.format(java.util.Locale.US, "%.2f", coeff)
                                onUpdate(entry.copy(multiplier = coeff))
                            }
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryNavy else Slate700,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (entry.directAreaSqft > 0) entry.directAreaSqft.toString() else "",
                        onValueChange = { onUpdate(entry.copy(directAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Direct Area (sq.ft / वर्ग फुट)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("input_direct_wall_area_$index"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = multiplierText,
                        onValueChange = { text ->
                            multiplierText = text
                            val v = text.toDoubleOrNull()
                            if (v != null && v > 0.0) {
                                onUpdate(entry.copy(multiplier = v))
                            }
                        },
                        label = { Text("Multiplier (गुणांक)") },
                        supportingText = {
                            Text(
                                if (entry.multiplier != 1.0) "Coeff: ×${String.format(java.util.Locale.US, "%.2f", entry.multiplier)}"
                                else "Default: 1.00"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_wall_multiplier_$index"),
                        singleLine = true
                    )
                }

                // Quick Multiplier Preset Chips for Direct Sqft
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    presetCoeffs.forEach { (coeff, label) ->
                        val isSelected = kotlin.math.abs(entry.multiplier - coeff) < 0.01
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSelected) AccentGold else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                if (isSelected) Color(0xFFD97706) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.clickable {
                                multiplierText = String.format(java.util.Locale.US, "%.2f", coeff)
                                onUpdate(entry.copy(multiplier = coeff))
                            }
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryNavy else Slate700,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Calculation Breakdown Strip for this specific wall
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (entry.multiplier != 1.0) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (entry.multiplier != 1.0) Color(0xFF93C5FD) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (entry.multiplier != 1.0) {
                        Column {
                            Text(
                                text = "Base: ${CurrencyUtils.formatSqft(entry.baseGrossAreaSqft)} × Multiplier: ${String.format(java.util.Locale.US, "%.2f", entry.multiplier)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryNavy
                            )
                            Text(
                                text = "Extra: +${CurrencyUtils.formatSqft(entry.grossAreaSqft - entry.baseGrossAreaSqft)}",
                                fontSize = 9.5.sp,
                                color = Color(0xFF2563EB)
                            )
                        }
                        Text(
                            text = "= ${CurrencyUtils.formatSqft(entry.grossAreaSqft)}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealSuccess
                        )
                    } else {
                        Text(
                            text = "Wall Gross Area (Multiplier: 1.00):",
                            fontSize = 10.5.sp,
                            color = Slate500
                        )
                        Text(
                            text = CurrencyUtils.formatSqft(entry.grossAreaSqft),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
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
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row: Type Dropdown, Area Badge, Mode Toggle, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                        modifier = Modifier.clickable { typeDropdownOpen = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = deduction.name, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
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

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = "-${CurrencyUtils.formatSqft(deduction.deductionAreaSqft)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (deduction.isDirectArea) Color(0xFFFEE2E2) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                        modifier = Modifier.clickable { onUpdate(deduction.copy(isDirectArea = !deduction.isDirectArea)) }
                    ) {
                        Text(
                            text = if (deduction.isDirectArea) "Direct" else "L × H",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(15.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (!deduction.isDirectArea) {
                // 2-Column Spacious Layout: Length & Height
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Width / L (चौड़ाई)", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = if (deduction.lengthFt > 0) deduction.lengthFt.toString() else "",
                                onValueChange = { onUpdate(deduction.copy(lengthFt = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Ft") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (deduction.lengthIn > 0) deduction.lengthIn.toString() else "",
                                onValueChange = { onUpdate(deduction.copy(lengthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                                label = { Text("In") },
                                placeholder = { Text("0-11") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f),
                                singleLine = true
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Height (ऊंचाई)", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = if (deduction.heightFt > 0) deduction.heightFt.toString() else "",
                                onValueChange = { onUpdate(deduction.copy(heightFt = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Ft") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (deduction.heightIn > 0) deduction.heightIn.toString() else "",
                                onValueChange = { onUpdate(deduction.copy(heightIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                                label = { Text("In") },
                                placeholder = { Text("0-11") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Deduction Count Stepper Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quantity / संख्या:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate700
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = { onUpdate(deduction.copy(count = maxOf(1, deduction.count - 1))) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text(
                            text = "${deduction.count}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        FilledTonalIconButton(
                            onClick = { onUpdate(deduction.copy(count = deduction.count + 1)) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = if (deduction.directAreaSqft > 0) deduction.directAreaSqft.toString() else "",
                    onValueChange = { onUpdate(deduction.copy(directAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Direct Deduction Area (sq.ft / वर्ग फुट)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row: Section Label, Area Badge, Mode Toggle, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.label.ifBlank { "Ceiling Section ${index + 1}" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = Slate900
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PrimaryNavy.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = CurrencyUtils.formatSqft(entry.grossAreaSqft),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = PrimaryNavy,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (entry.isDirectArea) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (entry.isDirectArea) Color(0xFFD97706) else Color(0xFFCBD5E1)),
                        modifier = Modifier.clickable { onUpdate(entry.copy(isDirectArea = !entry.isDirectArea)) }
                    ) {
                        Text(
                            text = if (entry.isDirectArea) "Direct Sqft" else "L × W",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (entry.isDirectArea) Color(0xFF92400E) else Slate700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (canDelete) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (!entry.isDirectArea) {
                // 2-Column Spacious Layout: Length & Width
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Length (लंबाई)", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = if (entry.lengthFt > 0) entry.lengthFt.toString() else "",
                                onValueChange = { onUpdate(entry.copy(lengthFt = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Ft") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (entry.lengthIn > 0) entry.lengthIn.toString() else "",
                                onValueChange = { onUpdate(entry.copy(lengthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                                label = { Text("In") },
                                placeholder = { Text("0-11") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f),
                                singleLine = true
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Width (चौड़ाई)", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = if (entry.widthFt > 0) entry.widthFt.toString() else "",
                                onValueChange = { onUpdate(entry.copy(widthFt = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Ft") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (entry.widthIn > 0) entry.widthIn.toString() else "",
                                onValueChange = { onUpdate(entry.copy(widthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                                label = { Text("In") },
                                placeholder = { Text("0-11") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Readable Dimensions Display: e.g. "12 ft 6 in × 10 ft 3 in (12.50 ft × 10.25 ft)"
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${entry.lengthFt.toInt()} ft ${entry.lengthIn.toInt()} in × ${entry.widthFt.toInt()} ft ${entry.widthIn.toInt()} in",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryNavy
                        )
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f ft × %.2f ft", entry.effectiveLengthFt, entry.effectiveWidthFt)}",
                            fontSize = 10.5.sp,
                            color = Slate500
                        )
                    }
                }
            } else {
                OutlinedTextField(
                    value = if (entry.directAreaSqft > 0) entry.directAreaSqft.toString() else "",
                    onValueChange = { onUpdate(entry.copy(directAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Direct Ceiling Area (sq.ft / वर्ग फुट)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
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
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                        modifier = Modifier.clickable { typeDropdownOpen = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = deduction.name, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
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

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = "-${CurrencyUtils.formatSqft(deduction.deductionAreaSqft)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (deduction.isDirectArea) Color(0xFFFEE2E2) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                        modifier = Modifier.clickable { onUpdate(deduction.copy(isDirectArea = !deduction.isDirectArea)) }
                    ) {
                        Text(
                            text = if (deduction.isDirectArea) "Direct" else "L × W",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(15.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (!deduction.isDirectArea) {
                // 2-Column Spacious Layout: Length & Width
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Length (लंबाई)", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = if (deduction.lengthFt > 0) deduction.lengthFt.toString() else "",
                                onValueChange = { onUpdate(deduction.copy(lengthFt = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Ft") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (deduction.lengthIn > 0) deduction.lengthIn.toString() else "",
                                onValueChange = { onUpdate(deduction.copy(lengthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                                label = { Text("In") },
                                placeholder = { Text("0-11") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f),
                                singleLine = true
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Width (चौड़ाई)", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = if (deduction.widthFt > 0) deduction.widthFt.toString() else "",
                                onValueChange = { onUpdate(deduction.copy(widthFt = it.toDoubleOrNull() ?: 0.0)) },
                                label = { Text("Ft") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (deduction.widthIn > 0) deduction.widthIn.toString() else "",
                                onValueChange = { onUpdate(deduction.copy(widthIn = (it.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 11.99))) },
                                label = { Text("In") },
                                placeholder = { Text("0-11") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f),
                                singleLine = true
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = if (deduction.directAreaSqft > 0) deduction.directAreaSqft.toString() else "",
                    onValueChange = { onUpdate(deduction.copy(directAreaSqft = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Direct Deduction Area (sq.ft / वर्ग फुट)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
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
                color = if (config.hasValidRate(isLabourOnlyMode)) PrimaryNavy.copy(alpha = 0.08f) else Color(0xFFFEF2F2),
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
                    if (config.hasValidRate(isLabourOnlyMode)) {
                        Text(
                            text = "₹${String.format(java.util.Locale.US, "%.2f", config.getEffectiveRate(isLabourOnlyMode))}/sqft",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    } else {
                        Text(
                            text = "Rate Not Entered",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
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
    wallPaintingEnabled: Boolean,
    grossWallArea: Double,
    totalWallDeduction: Double,
    netWallArea: Double,
    wallMultiplier: Double,
    finalWallArea: Double,
    wallPaint: SurfacePaintConfig,
    wallAmount: Double,
    ceilingPaintingEnabled: Boolean,
    grossCeilingArea: Double,
    totalCeilingDeduction: Double,
    netCeilingArea: Double,
    ceilingMultiplier: Double,
    finalCeilingArea: Double,
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
                text = "ROOM CALCULATION SUMMARY (विस्तृत गणना)",
                color = AccentGold,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 1. WALL SUMMARY
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "WALL SUMMARY ${if (!wallPaintingEnabled) "(DISABLED)" else ""}:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
                if (wallPaintingEnabled) {
                    if (wallPaint.hasValidRate(isLabourOnlyMode)) {
                        Text(text = CurrencyUtils.formatInr(wallAmount), color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        Text(text = "Rate Not Entered", color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                } else {
                    Text(text = "Excluded", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }
            Text(
                text = "• Gross Wall: ${CurrencyUtils.formatSqft(grossWallArea)} | Deduction: ${CurrencyUtils.formatSqft(totalWallDeduction)} | Net: ${CurrencyUtils.formatSqft(netWallArea)}",
                color = Color(0xFFCBD5E1),
                fontSize = 10.5.sp
            )
            Text(
                text = "• Wall Multiplier: ${String.format(java.util.Locale.US, "%.2f", wallMultiplier)} | Final Wall Area: ${CurrencyUtils.formatSqft(finalWallArea)}",
                color = Color(0xFFE2E8F0),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "• Wall Paint: ${wallPaint.brand} - ${wallPaint.productType} (${wallPaint.coats} Coats) @ " +
                        (if (wallPaint.hasValidRate(isLabourOnlyMode)) "₹${String.format(java.util.Locale.US, "%.2f", wallPaint.getEffectiveRate(isLabourOnlyMode))}/sq.ft" else "Rate Not Entered"),
                color = Color(0xFFE2E8F0),
                fontSize = 10.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))

            // 2. CEILING SUMMARY
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "CEILING SUMMARY ${if (!ceilingPaintingEnabled) "(DISABLED)" else ""}:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
                if (ceilingPaintingEnabled) {
                    if (ceilingPaint.hasValidRate(isLabourOnlyMode)) {
                        Text(text = CurrencyUtils.formatInr(ceilingAmount), color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        Text(text = "Rate Not Entered", color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                } else {
                    Text(text = "Excluded", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }
            Text(
                text = "• Gross Ceiling: ${CurrencyUtils.formatSqft(grossCeilingArea)} | Deduction: ${CurrencyUtils.formatSqft(totalCeilingDeduction)} | Net: ${CurrencyUtils.formatSqft(netCeilingArea)}",
                color = Color(0xFFCBD5E1),
                fontSize = 10.5.sp
            )
            Text(
                text = "• Ceiling Multiplier: ${String.format(java.util.Locale.US, "%.2f", ceilingMultiplier)} | Final Ceiling Area: ${CurrencyUtils.formatSqft(finalCeilingArea)}",
                color = Color(0xFFE2E8F0),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "• Ceiling Paint: ${ceilingPaint.brand} - ${ceilingPaint.productType} (${ceilingPaint.coats} Coats) @ " +
                        (if (ceilingPaint.hasValidRate(isLabourOnlyMode)) "₹${String.format(java.util.Locale.US, "%.2f", ceilingPaint.getEffectiveRate(isLabourOnlyMode))}/sq.ft" else "Rate Not Entered"),
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

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))

            // 3. FINAL SUMMARY
            val combinedArea = finalWallArea + finalCeilingArea
            Text(text = "FINAL SUMMARY:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            Text(
                text = "• Total Wall Area: ${CurrencyUtils.formatSqft(finalWallArea)} | Total Ceiling Area: ${CurrencyUtils.formatSqft(finalCeilingArea)}",
                color = Color(0xFFCBD5E1),
                fontSize = 10.5.sp
            )
            Text(
                text = "• Combined Total Area: ${CurrencyUtils.formatSqft(combinedArea)}",
                color = Color(0xFFCBD5E1),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "• Total Wall Amount: ${CurrencyUtils.formatInr(wallAmount)} | Total Ceiling Amount: ${CurrencyUtils.formatInr(ceilingAmount)}",
                color = Color(0xFFCBD5E1),
                fontSize = 10.5.sp
            )

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
                    Text(text = "GRAND TOTAL ESTIMATE (${if (isLabourOnlyMode) "Labour Only" else "Material + Labour"}):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = CurrencyUtils.formatInr(totalRoomAmount), color = AccentGold, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
            }
        }
    }
}
