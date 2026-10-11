package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.ProjectWithDetails
import com.example.data.model.RoomMeasurementEntity
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
    onUpdateRoom: (RoomMeasurementEntity) -> Unit,
    onDeleteRoom: (Long) -> Unit,
    onDuplicateRoom: (RoomMeasurementEntity) -> Unit,
    onToggleDeductOpenings: (Boolean) -> Unit,
    onNavigateToEstimates: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingRoom by remember { mutableStateOf<RoomMeasurementEntity?>(null) }
    var roomToDelete by remember { mutableStateOf<RoomMeasurementEntity?>(null) }

    val deductOpenings = projectDetails?.project?.deductOpenings ?: true
    val rooms = projectDetails?.rooms ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                                text = LanguageManager.get("deduct_openings", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate900
                            )
                            Text(
                                text = if (deductOpenings) "Doors, windows & extra areas are subtracted from gross wall area"
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

            // Summary Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MEASUREMENT TOTALS",
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
                                    text = "${rooms.size} Rooms",
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Net Wall Area", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                Text(
                                    text = CurrencyUtils.formatSqft(projectDetails?.totalRoomNetWallAreaSqft ?: 0.0),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Column {
                                Text(text = "Ceiling Area", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                Text(
                                    text = CurrencyUtils.formatSqft(projectDetails?.totalRoomCeilingAreaSqft ?: 0.0),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Column {
                                Text(text = "Total Paintable", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = CurrencyUtils.formatSqft(projectDetails?.totalRoomPaintableAreaSqft ?: 0.0),
                                    color = AccentGold,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
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
                                text = "Tap '+ Add Room' to record measurements",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                    }
                }
            } else {
                items(rooms) { room ->
                    RoomItemCard(
                        room = room,
                        deductOpenings = deductOpenings,
                        language = language,
                        onEdit = { editingRoom = room },
                        onDuplicate = { onDuplicateRoom(room) },
                        onDelete = { roomToDelete = room }
                    )
                }
            }

            // Proceed to Estimates Button
            item {
                Spacer(modifier = Modifier.height(60.dp)) // Padding for FAB
            }
        }

        // Floating Action Button to Add Room
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = PrimaryNavy,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_room")
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = LanguageManager.get("add_room", language),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Add / Edit Room Dialog
    if (showAddDialog || editingRoom != null) {
        val target = editingRoom
        RoomFormDialog(
            language = language,
            initialRoom = target,
            onDismiss = {
                showAddDialog = false
                editingRoom = null
            },
            onSave = { roomName, lFt, lIn, wFt, wIn, hFt, hIn, dCount, dW, dH, winCount, winW, winH, deduct, wall, ceil, notes ->
                if (target == null) {
                    onAddRoom(roomName, lFt, lIn, wFt, wIn, hFt, hIn, dCount, dW, dH, winCount, winW, winH, deduct, wall, ceil, notes)
                } else {
                    onUpdateRoom(
                        target.copy(
                            roomName = roomName,
                            lengthFt = lFt,
                            lengthIn = lIn,
                            widthFt = wFt,
                            widthIn = wIn,
                            heightFt = hFt,
                            heightIn = hIn,
                            doorCount = dCount,
                            doorWidthFt = dW,
                            doorHeightFt = dH,
                            windowCount = winCount,
                            windowWidthFt = winW,
                            windowHeightFt = winH,
                            additionalDeductionsSqft = deduct,
                            wallPaintingEnabled = wall,
                            ceilingPaintingEnabled = ceil,
                            notes = notes
                        )
                    )
                }
                showAddDialog = false
                editingRoom = null
            }
        )
    }

    // Delete Confirmation
    if (roomToDelete != null) {
        AlertDialog(
            onDismissRequest = { roomToDelete = null },
            title = { Text("Delete Room") },
            text = { Text("Are you sure you want to delete '${roomToDelete?.roomName}'?") },
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

@Composable
private fun RoomItemCard(
    room: RoomMeasurementEntity,
    deductOpenings: Boolean,
    language: AppLanguage,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = room.roomName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = Slate900
                        )
                        Text(
                            text = "${room.effectiveLength.toInt()}' × ${room.effectiveWidth.toInt()}' × ${room.effectiveHeight.toInt()}' ft",
                            fontSize = 11.5.sp,
                            color = Slate500
                        )
                    }
                }

                Row {
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Slate500, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Measurement Metrics Grid
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF8FAFC)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Gross Wall Area:", fontSize = 11.5.sp, color = Slate500)
                        Text(text = CurrencyUtils.formatSqft(room.grossWallAreaSqft), fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                    }

                    if (deductOpenings && room.totalDeductionsSqft > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Deductions (${room.doorCount} doors, ${room.windowCount} windows):",
                                fontSize = 11.sp,
                                color = Color(0xFFDC2626)
                            )
                            Text(
                                text = "-${CurrencyUtils.formatSqft(room.totalDeductionsSqft)}",
                                fontSize = 11.sp,
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Net Wall Area:", fontSize = 11.5.sp, color = Slate700, fontWeight = FontWeight.Medium)
                        Text(
                            text = CurrencyUtils.formatSqft(room.getNetWallAreaSqft(deductOpenings)),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    }

                    if (room.ceilingPaintingEnabled) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Ceiling Area:", fontSize = 11.5.sp, color = Slate700)
                            Text(
                                text = CurrencyUtils.formatSqft(room.floorCeilingAreaSqft),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Paintable Area:",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = CurrencyUtils.formatSqft(room.getTotalPaintableAreaSqft(deductOpenings)),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealSuccess
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomFormDialog(
    language: AppLanguage,
    initialRoom: RoomMeasurementEntity?,
    onDismiss: () -> Unit,
    onSave: (
        roomName: String,
        lFt: Double, lIn: Double,
        wFt: Double, wIn: Double,
        hFt: Double, hIn: Double,
        dCount: Int, dW: Double, dH: Double,
        winCount: Int, winW: Double, winH: Double,
        deduct: Double,
        wall: Boolean, ceil: Boolean,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialRoom?.roomName ?: "Master Bedroom") }
    var lFt by remember { mutableStateOf((initialRoom?.lengthFt ?: 14.0).toString()) }
    var lIn by remember { mutableStateOf((initialRoom?.lengthIn ?: 0.0).toString()) }
    var wFt by remember { mutableStateOf((initialRoom?.widthFt ?: 12.0).toString()) }
    var wIn by remember { mutableStateOf((initialRoom?.widthIn ?: 0.0).toString()) }
    var hFt by remember { mutableStateOf((initialRoom?.heightFt ?: 10.0).toString()) }
    var hIn by remember { mutableStateOf((initialRoom?.heightIn ?: 0.0).toString()) }

    var dCount by remember { mutableStateOf((initialRoom?.doorCount ?: 1).toString()) }
    var dW by remember { mutableStateOf((initialRoom?.doorWidthFt ?: 3.0).toString()) }
    var dH by remember { mutableStateOf((initialRoom?.doorHeightFt ?: 7.0).toString()) }

    var winCount by remember { mutableStateOf((initialRoom?.windowCount ?: 1).toString()) }
    var winW by remember { mutableStateOf((initialRoom?.windowWidthFt ?: 4.0).toString()) }
    var winH by remember { mutableStateOf((initialRoom?.windowHeightFt ?: 4.0).toString()) }

    var extraDeduct by remember { mutableStateOf((initialRoom?.additionalDeductionsSqft ?: 0.0).toString()) }
    var wallPainting by remember { mutableStateOf(initialRoom?.wallPaintingEnabled ?: true) }
    var ceilPainting by remember { mutableStateOf(initialRoom?.ceilingPaintingEnabled ?: true) }
    var notes by remember { mutableStateOf(initialRoom?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialRoom == null) "Add Room" else "Edit Room", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Room Name (कमरे का नाम)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Length & Width
                item {
                    Text(text = "Length & Width (Feet & Inches)", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = lFt,
                            onValueChange = { lFt = it },
                            label = { Text("Length (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lIn,
                            onValueChange = { lIn = it },
                            label = { Text("Inches") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = wFt,
                            onValueChange = { wFt = it },
                            label = { Text("Width (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = wIn,
                            onValueChange = { wIn = it },
                            label = { Text("Inches") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Height
                item {
                    Text(text = "Height (ऊंचाई)", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = hFt,
                            onValueChange = { hFt = it },
                            label = { Text("Height (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = hIn,
                            onValueChange = { hIn = it },
                            label = { Text("Inches") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Doors & Windows
                item {
                    Text(text = "Doors & Windows Openings", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = dCount,
                            onValueChange = { dCount = it },
                            label = { Text("Doors") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = dW,
                            onValueChange = { dW = it },
                            label = { Text("Door W (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = dH,
                            onValueChange = { dH = it },
                            label = { Text("Door H (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = winCount,
                            onValueChange = { winCount = it },
                            label = { Text("Windows") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = winW,
                            onValueChange = { winW = it },
                            label = { Text("Win W (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = winH,
                            onValueChange = { winH = it },
                            label = { Text("Win H (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Deductions & Checkboxes
                item {
                    OutlinedTextField(
                        value = extraDeduct,
                        onValueChange = { extraDeduct = it },
                        label = { Text("Extra Deductions (Sqft - e.g. Tiles)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = wallPainting,
                            onCheckedChange = { wallPainting = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryNavy)
                        )
                        Text(text = "Paint Walls (दीवारें)", fontSize = 13.sp)

                        Spacer(modifier = Modifier.width(16.dp))

                        Checkbox(
                            checked = ceilPainting,
                            onCheckedChange = { ceilPainting = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryNavy)
                        )
                        Text(text = "Paint Ceiling (छत)", fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        name.ifBlank { "Room" },
                        lFt.toDoubleOrNull() ?: 12.0,
                        lIn.toDoubleOrNull() ?: 0.0,
                        wFt.toDoubleOrNull() ?: 10.0,
                        wIn.toDoubleOrNull() ?: 0.0,
                        hFt.toDoubleOrNull() ?: 10.0,
                        hIn.toDoubleOrNull() ?: 0.0,
                        dCount.toIntOrNull() ?: 1,
                        dW.toDoubleOrNull() ?: 3.0,
                        dH.toDoubleOrNull() ?: 7.0,
                        winCount.toIntOrNull() ?: 1,
                        winW.toDoubleOrNull() ?: 4.0,
                        winH.toDoubleOrNull() ?: 4.0,
                        extraDeduct.toDoubleOrNull() ?: 0.0,
                        wallPainting,
                        ceilPainting,
                        notes
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text("Save Room")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
