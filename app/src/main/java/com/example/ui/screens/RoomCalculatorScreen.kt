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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.CeilingMeasurementEntry
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectWithDetails
import com.example.data.model.RoomDetailsJsonConverter
import com.example.data.model.RoomMeasurementEntity
import com.example.data.model.SurfacePaintConfig
import com.example.data.model.SurfacePrepConfig
import com.example.data.model.WallDeductionEntry
import com.example.data.model.WallMeasurementEntry
import com.example.ui.components.ExpandableRoomCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
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
fun RoomCalculatorScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?,
    onAddRoom: (
        roomName: String,
        lengthFt: Double, lengthIn: Double,
        widthFt: Double, widthIn: Double,
        heightFt: Double, heightIn: Double,
        doorCount: Int, doorWidth: Double, doorHeight: Double,
        windowCount: Int, windowWidth: Double, windowHeight: Double,
        deductions: Double,
        wallPainting: Boolean, ceilingPainting: Boolean,
        notes: String
    ) -> Unit,
    onSaveDetailedRoom: (RoomMeasurementEntity) -> Unit,
    onUpdateRoom: (RoomMeasurementEntity) -> Unit,
    onDeleteRoom: (Long) -> Unit,
    onDuplicateRoom: (RoomMeasurementEntity) -> Unit,
    onToggleDeductOpenings: (Boolean) -> Unit,
    onUpdateProjectMode: (String) -> Unit,
    onNavigateToEstimates: () -> Unit
) {
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var roomToDelete by remember { mutableStateOf<RoomMeasurementEntity?>(null) }

    val project = projectDetails?.project
    val deductOpenings = project?.deductOpenings ?: true
    val isLabourOnly = project?.estimateMode == "LABOUR_ONLY"
    val rooms = projectDetails?.rooms ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mode Selector: Labour Only vs Material + Labour
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ESTIMATE MODE (अनुमान मोड)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = PrimaryNavy,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isLabourOnly) Color(0xFFFEF3C7) else Color(0xFFDBEAFE)
                            ) {
                                Text(
                                    text = if (isLabourOnly) "Labour Only (केवल लेबर)" else "Material + Labour (मटेरियल + लेबर)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLabourOnly) Color(0xFF92400E) else PrimaryNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TabRow(
                            selectedTabIndex = if (isLabourOnly) 0 else 1,
                            containerColor = Color.Transparent,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[if (isLabourOnly) 0 else 1]),
                                    color = PrimaryNavy
                                )
                            }
                        ) {
                            Tab(
                                selected = isLabourOnly,
                                onClick = { onUpdateProjectMode("LABOUR_ONLY") },
                                text = { Text("1. Labour Only", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = !isLabourOnly,
                                onClick = { onUpdateProjectMode("MATERIAL_AND_LABOUR") },
                                text = { Text("2. Material + Labour", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
            }

            // Deduct Openings Global Switch Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
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
                                text = "Deduct Openings (दीवार कटौती लागू करें)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Slate900
                            )
                            Text(
                                text = if (deductOpenings) "Doors, windows & openings are subtracted from gross wall area"
                                else "Count full gross wall area without subtracting openings",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                        Switch(
                            checked = deductOpenings,
                            onCheckedChange = { onToggleDeductOpenings(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryNavy
                            ),
                            modifier = Modifier.testTag("switch_deduct_openings")
                        )
                    }
                }
            }

            // PROJECT SUMMARY Banner Card
            item {
                ProjectComprehensiveSummaryCard(
                    projectDetails = projectDetails,
                    isLabourOnly = isLabourOnly
                )
            }

            // Quick Add Room / Navigate Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAddRoomDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_add_room_modal")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "+ Add New Room", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToEstimates,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_go_to_quotation")
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Rates & Quotation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Room Cards List
            if (rooms.isEmpty()) {
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
                            Icon(
                                imageVector = Icons.Default.MeetingRoom,
                                contentDescription = null,
                                tint = Slate500,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No rooms added yet",
                                fontWeight = FontWeight.Bold,
                                color = Slate700
                            )
                            Text(
                                text = "Tap '+ Add New Room' to add Bedroom, Hall, Kitchen with independent Wall & Ceiling measurements",
                                fontSize = 12.sp,
                                color = Slate500,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(rooms, key = { it.id }) { room ->
                    ExpandableRoomCard(
                        room = room,
                        deductOpeningsGlobal = deductOpenings,
                        isLabourOnlyMode = isLabourOnly,
                        onSaveRoom = { onSaveDetailedRoom(it) },
                        onDuplicate = { onDuplicateRoom(room) },
                        onDelete = { roomToDelete = room }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddRoomDialog = true },
            containerColor = PrimaryNavy,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_add_room")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Room")
        }
    }

    // Add Room Dialog (Room name presets, initial L x W x H, brand & paint options)
    if (showAddRoomDialog) {
        AddRoomPresetDialog(
            onDismiss = { showAddRoomDialog = false },
            onSave = { roomEntity ->
                onSaveDetailedRoom(roomEntity)
                showAddRoomDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (roomToDelete != null) {
        AlertDialog(
            onDismissRequest = { roomToDelete = null },
            title = { Text("Delete Room") },
            text = { Text("Are you sure you want to delete '${roomToDelete?.roomName}' and all its wall and ceiling measurements?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        roomToDelete?.let { onDeleteRoom(it.id) }
                        roomToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { roomToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * PROJECT SUMMARY Component (Item 7 Requirement):
 * Total Gross Wall Area, Total Wall Deduction, Total Net Wall Area, Total Gross Ceiling Area,
 * Total Ceiling Deduction, Total Net Ceiling Area, Putty Cost, Primer Cost, Material Cost, Labour Cost, Subtotal, Grand Total.
 */
@Composable
private fun ProjectComprehensiveSummaryCard(
    projectDetails: ProjectWithDetails?,
    isLabourOnly: Boolean
) {
    val totalGrossWall = projectDetails?.totalGrossWallAreaSqft ?: 0.0
    val totalWallDed = projectDetails?.totalWallDeductionSqft ?: 0.0
    val totalNetWall = projectDetails?.totalRoomNetWallAreaSqft ?: 0.0

    val totalGrossCeil = projectDetails?.totalGrossCeilingAreaSqft ?: 0.0
    val totalCeilDed = projectDetails?.totalCeilingDeductionSqft ?: 0.0
    val totalNetCeil = projectDetails?.totalRoomCeilingAreaSqft ?: 0.0

    val puttyCost = projectDetails?.totalRoomPuttyCost ?: 0.0
    val primerCost = projectDetails?.totalRoomPrimerCost ?: 0.0
    val matCost = projectDetails?.totalRoomMaterialCost ?: 0.0
    val labCost = projectDetails?.totalRoomLabourCost ?: 0.0

    val subtotal = if (isLabourOnly) labCost else (matCost + labCost)
    val discount = projectDetails?.project?.discountAmount ?: 0.0
    val tax = if (projectDetails?.project?.isTaxEnabled == true) (subtotal - discount).coerceAtLeast(0.0) * ((projectDetails.project.taxPercent) / 100.0) else 0.0
    val grandTotal = (subtotal - discount).coerceAtLeast(0.0) + tax

    Card(
        colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROJECT SUMMARY (प्रोजेक्ट सारांश)",
                    color = AccentGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${projectDetails?.rooms?.size ?: 0} Rooms",
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // WALL SUMMARY ROW
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "WALL SUMMARY", color = Color(0xFFCBD5E1), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Gross: ${CurrencyUtils.formatSqft(totalGrossWall)} − Ded: ${CurrencyUtils.formatSqft(totalWallDed)}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.5.sp
                    )
                    Text(
                        text = "Total Net Wall: ${CurrencyUtils.formatSqft(totalNetWall)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // CEILING SUMMARY ROW
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "CEILING SUMMARY", color = Color(0xFFCBD5E1), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Gross: ${CurrencyUtils.formatSqft(totalGrossCeil)} − Ded: ${CurrencyUtils.formatSqft(totalCeilDed)}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.5.sp
                    )
                    Text(
                        text = "Total Net Ceiling: ${CurrencyUtils.formatSqft(totalNetCeil)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Financial breakdowns
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Putty Cost: ${CurrencyUtils.formatInr(puttyCost)}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                Text(text = "Total Primer Cost: ${CurrencyUtils.formatInr(primerCost)}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
            }

            if (!isLabourOnly) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Total Material Cost: ${CurrencyUtils.formatInr(matCost)}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    Text(text = "Total Labour Cost: ${CurrencyUtils.formatInr(labCost)}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                }
            } else {
                Text(text = "Total Labour Cost: ${CurrencyUtils.formatInr(labCost)}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Subtotal: ${CurrencyUtils.formatInr(subtotal)}", color = Color.White, fontSize = 11.5.sp)
                if (discount > 0) {
                    Text(text = "Discount: −${CurrencyUtils.formatInr(discount)}", color = Color(0xFFFCA5A5), fontSize = 11.sp)
                }
                if (tax > 0) {
                    Text(text = "GST (18%): +${CurrencyUtils.formatInr(tax)}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
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
                    Text(
                        text = "GRAND TOTAL (${if (isLabourOnly) "Labour Only" else "Material + Labour"}):",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                    Text(
                        text = CurrencyUtils.formatInr(grandTotal),
                        color = AccentGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddRoomPresetDialog(
    onDismiss: () -> Unit,
    onSave: (RoomMeasurementEntity) -> Unit
) {
    var selectedPreset by remember { mutableStateOf("Bedroom") }
    var roomName by remember { mutableStateOf("Bedroom") }
    var lengthFt by remember { mutableStateOf("14") }
    var lengthIn by remember { mutableStateOf("0") }
    var widthFt by remember { mutableStateOf("12") }
    var widthIn by remember { mutableStateOf("0") }
    var heightFt by remember { mutableStateOf("10") }
    var heightIn by remember { mutableStateOf("0") }

    var wallPaintBrand by remember { mutableStateOf("Asian Paints") }
    var wallProductType by remember { mutableStateOf("Interior Emulsion") }
    var ceilingProductType by remember { mutableStateOf("Ceiling White") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Room (नया कमरा जोड़ें)", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(text = "Select Room Preset:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BrandProductRegistry.roomCategories.forEach { preset ->
                            val isSel = selectedPreset == preset
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) PrimaryNavy else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clickable {
                                        selectedPreset = preset
                                        roomName = preset
                                    }
                                    .border(
                                        width = 1.dp,
                                        color = if (isSel) PrimaryNavy else Color(0xFFCBD5E1),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Text(
                                    text = preset,
                                    color = if (isSel) Color.White else Slate700,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = roomName,
                        onValueChange = { roomName = it },
                        label = { Text("Room Name / Label") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text(text = "Dimensions (Feet & Inches / फुट व इंच)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Spacer(modifier = Modifier.height(4.dp))
                    // Length Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = lengthFt,
                            onValueChange = { lengthFt = it },
                            label = { Text("Length (Ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lengthIn,
                            onValueChange = { lengthIn = it },
                            label = { Text("Length (In)") },
                            placeholder = { Text("0-11") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    // Width Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = widthFt,
                            onValueChange = { widthFt = it },
                            label = { Text("Width (Ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = widthIn,
                            onValueChange = { widthIn = it },
                            label = { Text("Width (In)") },
                            placeholder = { Text("0-11") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    // Height Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = heightFt,
                            onValueChange = { heightFt = it },
                            label = { Text("Height (Ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = heightIn,
                            onValueChange = { heightIn = it },
                            label = { Text("Height (In)") },
                            placeholder = { Text("0-11") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(text = "Default Paints for Wall & Ceiling", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = wallProductType,
                            onValueChange = { wallProductType = it },
                            label = { Text("Wall Paint") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ceilingProductType,
                            onValueChange = { ceilingProductType = it },
                            label = { Text("Ceiling Paint") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lF = lengthFt.toDoubleOrNull() ?: 14.0
                    val lI = lengthIn.toDoubleOrNull() ?: 0.0
                    val wF = widthFt.toDoubleOrNull() ?: 12.0
                    val wI = widthIn.toDoubleOrNull() ?: 0.0
                    val hF = heightFt.toDoubleOrNull() ?: 10.0
                    val hI = heightIn.toDoubleOrNull() ?: 0.0

                    val wallEntries = listOf(
                        WallMeasurementEntry(label = "Wall 1 & 2 (Length sides)", lengthFt = lF, lengthIn = lI, heightFt = hF, heightIn = hI, wallCount = 2),
                        WallMeasurementEntry(label = "Wall 3 & 4 (Width sides)", lengthFt = wF, lengthIn = wI, heightFt = hF, heightIn = hI, wallCount = 2)
                    )
                    val wallDeductions = listOf(
                        WallDeductionEntry(name = "Door", lengthFt = 3.0, lengthIn = 0.0, heightFt = 7.0, heightIn = 0.0, count = 1),
                        WallDeductionEntry(name = "Window", lengthFt = 4.0, lengthIn = 0.0, heightFt = 4.0, heightIn = 0.0, count = 1)
                    )
                    val ceilingEntries = listOf(
                        CeilingMeasurementEntry(label = "Ceiling Area", lengthFt = lF, lengthIn = lI, widthFt = wF, widthIn = wI)
                    )

                    val serialized = RoomDetailsJsonConverter.serialize(
                        wallEntries = wallEntries,
                        wallDeductions = wallDeductions,
                        wallPaint = SurfacePaintConfig(
                            brand = wallPaintBrand,
                            productType = wallProductType,
                            productVariant = "",
                            coats = 2,
                            materialRate = 16.0,
                            labourRate = 12.0
                        ),
                        wallPutty = SurfacePrepConfig(
                            enabled = true,
                            title = "Wall Putty",
                            brand = "Birla White",
                            product = "WallCare Putty",
                            coats = 2,
                            materialRate = 7.0,
                            labourRate = 9.0
                        ),
                        wallPrimer = SurfacePrepConfig(
                            enabled = true,
                            title = "Wall Primer",
                            brand = "Asian Paints",
                            product = "Decoprime Wall Primer",
                            coats = 1,
                            materialRate = 4.5,
                            labourRate = 4.0
                        ),
                        ceilingEntries = ceilingEntries,
                        ceilingDeductions = emptyList(),
                        ceilingPaint = SurfacePaintConfig(
                            brand = wallPaintBrand,
                            productType = ceilingProductType,
                            productVariant = "",
                            coats = 2,
                            materialRate = 12.0,
                            labourRate = 10.0
                        ),
                        ceilingPutty = SurfacePrepConfig(
                            enabled = false,
                            title = "Ceiling Putty",
                            brand = "Birla White",
                            product = "WallCare Putty",
                            coats = 2,
                            materialRate = 7.0,
                            labourRate = 9.0
                        ),
                        ceilingPrimer = SurfacePrepConfig(
                            enabled = true,
                            title = "Ceiling Primer",
                            brand = "Asian Paints",
                            product = "Decoprime Wall Primer",
                            coats = 1,
                            materialRate = 4.5,
                            labourRate = 4.0
                        )
                    )

                    val newRoom = RoomMeasurementEntity(
                        projectId = 0L,
                        roomName = roomName.ifBlank { "Bedroom" },
                        lengthFt = lF,
                        lengthIn = lI,
                        widthFt = wF,
                        widthIn = wI,
                        heightFt = hF,
                        heightIn = hI,
                        doorCount = 1,
                        doorWidthFt = 3.0,
                        doorHeightFt = 7.0,
                        windowCount = 1,
                        windowWidthFt = 4.0,
                        windowHeightFt = 4.0,
                        wallPaintingEnabled = true,
                        ceilingPaintingEnabled = true,
                        detailedData = serialized
                    )
                    onSave(newRoom)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text("Add Room")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
