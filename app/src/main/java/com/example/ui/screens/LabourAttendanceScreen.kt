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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
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
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.ProjectWithDetails
import com.example.data.model.WorkerEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealSuccess
import com.example.util.AppLanguage
import com.example.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LabourAttendanceScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?,
    workers: List<WorkerEntity>,
    attendanceRecords: List<AttendanceRecordEntity>,
    onAddWorker: (name: String, role: String, rate: Double, mobile: String) -> Unit,
    onRecordAttendance: (workerId: Long, date: String, status: String, overtime: Double, advance: Double, notes: String) -> Unit,
    onDeleteAttendance: (Long) -> Unit
) {
    var showAddWorkerDialog by remember { mutableStateOf(false) }
    var showAttendanceDialog by remember { mutableStateOf(false) }

    // Wage Calculations
    val workerMap = remember(workers) { workers.associateBy { it.id } }

    var totalWagesEarned = 0.0
    var totalAdvancePaid = 0.0

    attendanceRecords.forEach { rec ->
        val w = workerMap[rec.workerId]
        val dailyRate = w?.dailyRate ?: 850.0
        val earned = when (rec.status) {
            "Full Day" -> dailyRate + (rec.overtimeHours * (dailyRate / 8.0))
            "Half Day" -> (dailyRate / 2.0) + (rec.overtimeHours * (dailyRate / 8.0))
            else -> 0.0
        }
        totalWagesEarned += earned
        totalAdvancePaid += rec.advancePaid
    }

    val netPayableToLabour = (totalWagesEarned - totalAdvancePaid).coerceAtLeast(0.0)
    val billedLabourToClient = projectDetails?.labourSubtotal ?: 0.0
    val contractorNetProfit = billedLabourToClient - totalWagesEarned

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profitability & Wage Overview Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DAILY LABOUR & WAGE REGISTER",
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
                                    text = "${attendanceRecords.size} Days Logged",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Total Wages", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                Text(
                                    text = CurrencyUtils.formatInr(totalWagesEarned),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Column {
                                Text(text = "Advance Given", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                Text(
                                    text = CurrencyUtils.formatInr(totalAdvancePaid),
                                    color = Color(0xFFFCD34D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Net Balance Due", color = AccentGold, fontSize = 11.sp)
                                Text(
                                    text = CurrencyUtils.formatInr(netPayableToLabour),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contractor Margin Card
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estimated Contractor Margin:",
                                    color = Color.White,
                                    fontSize = 11.5.sp
                                )
                                Text(
                                    text = CurrencyUtils.formatInr(contractorNetProfit),
                                    color = if (contractorNetProfit >= 0) TealSuccess else Color(0xFFF87171),
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAttendanceDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_log_attendance")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Log Attendance", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showAddWorkerDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_add_worker")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add Painter", color = PrimaryNavy, fontSize = 12.sp)
                    }
                }
            }

            // Attendance Logs List
            if (attendanceRecords.isEmpty()) {
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
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Slate500, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No attendance logged yet", fontWeight = FontWeight.Bold, color = Slate700)
                            Text(text = "Tap 'Log Attendance' to mark painter presence & advances", fontSize = 11.5.sp, color = Slate500)
                        }
                    }
                }
            } else {
                items(attendanceRecords) { record ->
                    val worker = workerMap[record.workerId]
                    AttendanceItemCard(
                        record = record,
                        worker = worker,
                        onDelete = { onDeleteAttendance(record.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (showAddWorkerDialog) {
        AddWorkerDialog(
            onDismiss = { showAddWorkerDialog = false },
            onConfirm = { name, role, rate, mob ->
                onAddWorker(name, role, rate, mob)
                showAddWorkerDialog = false
            }
        )
    }

    if (showAttendanceDialog) {
        RecordAttendanceDialog(
            workers = workers,
            onDismiss = { showAttendanceDialog = false },
            onConfirm = { wId, dt, st, ot, adv, nts ->
                onRecordAttendance(wId, dt, st, ot, adv, nts)
                showAttendanceDialog = false
            }
        )
    }
}

@Composable
private fun AttendanceItemCard(
    record: AttendanceRecordEntity,
    worker: WorkerEntity?,
    onDelete: () -> Unit
) {
    val dailyRate = worker?.dailyRate ?: 850.0
    val earned = when (record.status) {
        "Full Day" -> dailyRate + (record.overtimeHours * (dailyRate / 8.0))
        "Half Day" -> (dailyRate / 2.0) + (record.overtimeHours * (dailyRate / 8.0))
        else -> 0.0
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
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
                    text = worker?.name ?: "Painter",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Slate900
                )
                Text(
                    text = "${record.date} • ${worker?.role ?: "Karigar"}",
                    fontSize = 11.5.sp,
                    color = Slate500
                )
                if (record.notes.isNotBlank()) {
                    Text(text = record.notes, fontSize = 11.sp, color = Slate700)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (record.status) {
                        "Full Day" -> Color(0xFFD1FAE5)
                        "Half Day" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    }
                ) {
                    Text(
                        text = record.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (record.status) {
                            "Full Day" -> TealSuccess
                            "Half Day" -> Color(0xFFB45309)
                            else -> Color(0xFFDC2626)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Earned: ${CurrencyUtils.formatInr(earned)}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Slate900
                )
                if (record.advancePaid > 0) {
                    Text(
                        text = "Adv: -${CurrencyUtils.formatInr(record.advancePaid)}",
                        fontSize = 10.5.sp,
                        color = Color(0xFFDC2626)
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun AddWorkerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, role: String, rate: Double, mob: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Master Painter (कारीगर)") }
    var rateStr by remember { mutableStateOf("900") }
    var mobile by remember { mutableStateOf("") }

    val roles = listOf("Master Painter (कारीगर)", "Helper (मजदूर)", "Polisher (पॉलिशर)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Painter / Worker", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Worker Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Role", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                roles.forEach { r ->
                    val sel = role == r
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (sel) PrimaryNavy else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                role = r
                                if (r.contains("Helper")) rateStr = "550"
                                else if (r.contains("Polisher")) rateStr = "1000"
                                else rateStr = "900"
                            }
                    ) {
                        Text(
                            text = r,
                            color = if (sel) Color.White else Slate900,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = rateStr,
                    onValueChange = { rateStr = it },
                    label = { Text("Daily Wage Rate (₹/day)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text("Mobile Number (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, role, rateStr.toDoubleOrNull() ?: 900.0, mobile)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun RecordAttendanceDialog(
    workers: List<WorkerEntity>,
    onDismiss: () -> Unit,
    onConfirm: (workerId: Long, date: String, status: String, overtime: Double, advance: Double, notes: String) -> Unit
) {
    var selectedWorkerId by remember { mutableStateOf(workers.firstOrNull()?.id ?: 0L) }
    var dateStr by remember { mutableStateOf(SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())) }
    var status by remember { mutableStateOf("Full Day") }
    var overtimeStr by remember { mutableStateOf("0") }
    var advanceStr by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    val statusOptions = listOf("Full Day", "Half Day", "Absent")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Daily Attendance", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text(text = "Select Worker", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        workers.forEach { w ->
                            val sel = selectedWorkerId == w.id
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (sel) PrimaryNavy else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedWorkerId = w.id }
                            ) {
                                Text(
                                    text = "${w.name} (${w.role}) - ₹${w.dailyRate.toInt()}/day",
                                    color = if (sel) Color.White else Slate900,
                                    fontSize = 11.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text(text = "Status", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        statusOptions.forEach { opt ->
                            val sel = status == opt
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (sel) PrimaryNavy else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { status = opt }
                            ) {
                                Text(
                                    text = opt,
                                    color = if (sel) Color.White else Slate700,
                                    fontSize = 11.5.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = overtimeStr,
                            onValueChange = { overtimeStr = it },
                            label = { Text("OT (Hours)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = advanceStr,
                            onValueChange = { advanceStr = it },
                            label = { Text("Kharcha (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Work Done (e.g. Putty 2nd coat)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        selectedWorkerId,
                        dateStr,
                        status,
                        overtimeStr.toDoubleOrNull() ?: 0.0,
                        advanceStr.toDoubleOrNull() ?: 0.0,
                        notes
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text("Log Entry")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
