package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.ProjectEntity
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealSuccess
import com.example.util.AppLanguage
import com.example.util.LanguageManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerSiteScreen(
    language: AppLanguage,
    currentProject: ProjectEntity?,
    onSaveProject: (ProjectEntity) -> Unit,
    onNavigateToRooms: () -> Unit
) {
    var customerName by remember(currentProject) { mutableStateOf(currentProject?.customerName ?: "") }
    var mobileNumber by remember(currentProject) { mutableStateOf(currentProject?.mobileNumber ?: "") }
    var siteName by remember(currentProject) { mutableStateOf(currentProject?.siteName ?: "") }
    var siteAddress by remember(currentProject) { mutableStateOf(currentProject?.siteAddress ?: "") }
    var flatType by remember(currentProject) { mutableStateOf(currentProject?.flatType ?: "2BHK") }
    var estimateNumber by remember(currentProject) { mutableStateOf(currentProject?.estimateNumber ?: "DE-2026-001") }
    var estimateDate by remember(currentProject) {
        mutableStateOf(currentProject?.estimateDate ?: SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date()))
    }
    var siteNotes by remember(currentProject) { mutableStateOf(currentProject?.siteNotes ?: "") }
    var customerReqs by remember(currentProject) { mutableStateOf(currentProject?.customerRequirements ?: "") }
    var status by remember(currentProject) { mutableStateOf(currentProject?.status ?: "Draft") }

    val photoUris = remember(currentProject) {
        val list = currentProject?.sitePhotographs?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        mutableStateOf(list.toMutableList())
    }

    // Photo picker (Android zero-permission photo picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val updated = photoUris.value.toMutableList()
            updated.addAll(uris.map { it.toString() })
            photoUris.value = updated
        }
    }

    val flatTypes = listOf("1BHK", "2BHK", "3BHK", "4BHK", "Office", "Commercial", "Other")
    val statuses = listOf("Draft", "Sent", "Accepted", "Rejected", "Completed")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Customer & Site Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CUSTOMER & SITE DETAILS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text(LanguageManager.get("customer_name", language)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Slate500) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_customer_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNavy,
                            focusedLabelColor = PrimaryNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text(LanguageManager.get("mobile_number", language)) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate500) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_customer_mobile"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNavy,
                            focusedLabelColor = PrimaryNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = siteName,
                        onValueChange = { siteName = it },
                        label = { Text(LanguageManager.get("site_name", language)) },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = Slate500) },
                        placeholder = { Text("e.g. Gera World of Joy, Kharadi") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_site_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNavy,
                            focusedLabelColor = PrimaryNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = siteAddress,
                        onValueChange = { siteAddress = it },
                        label = { Text(LanguageManager.get("site_address", language)) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Slate500) },
                        placeholder = { Text("e.g. Kharadi Bypass, Pune, MH") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_site_address"),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNavy,
                            focusedLabelColor = PrimaryNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = LanguageManager.get("flat_type", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        flatTypes.forEach { type ->
                            val selected = flatType == type
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (selected) PrimaryNavy else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clickable { flatType = type }
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) PrimaryNavy else Color(0xFFCBD5E1),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Text(
                                    text = type,
                                    color = if (selected) Color.White else Slate700,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Estimate Metadata & Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ESTIMATE METADATA & STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = estimateNumber,
                            onValueChange = { estimateNumber = it },
                            label = { Text(LanguageManager.get("estimate_no", language)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_estimate_number"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = estimateDate,
                            onValueChange = { estimateDate = it },
                            label = { Text(LanguageManager.get("estimate_date", language)) },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate500) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_estimate_date"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = LanguageManager.get("status", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        statuses.forEach { st ->
                            val isSelected = status == st
                            val stColor = when (st) {
                                "Accepted" -> TealSuccess
                                "Sent" -> Color(0xFFD97706)
                                "Rejected" -> Color(0xFFDC2626)
                                "Completed" -> PrimaryNavy
                                else -> Color(0xFF64748B)
                            }
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) stColor else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clickable { status = st }
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) stColor else Color(0xFFCBD5E1),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Text(
                                    text = st,
                                    color = if (isSelected) Color.White else Slate700,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Site Notes & Photographs Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SITE INSPECTION & PHOTOS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = siteNotes,
                        onValueChange = { siteNotes = it },
                        label = { Text(LanguageManager.get("site_notes", language)) },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = Slate500) },
                        placeholder = { Text("e.g. Seepage repair needed, cracks filled with putty...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_site_notes"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customerReqs,
                        onValueChange = { customerReqs = it },
                        label = { Text(LanguageManager.get("customer_reqs", language)) },
                        placeholder = { Text("e.g. Royale in Living Room, Tractor in Kitchen...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_customer_reqs"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${LanguageManager.get("site_photos", language)} (${photoUris.value.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_attach_photos")
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = LanguageManager.get("add_photo", language), color = PrimaryNavy, fontSize = 12.sp)
                        }
                    }

                    if (photoUris.value.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(photoUris.value) { uriStr ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFE2E8F0),
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.TopEnd) {
                                        Text(
                                            text = "📷 Photo",
                                            fontSize = 10.sp,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                        IconButton(
                                            onClick = {
                                                val updated = photoUris.value.toMutableList()
                                                updated.remove(uriStr)
                                                photoUris.value = updated
                                            },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val base = currentProject ?: ProjectEntity(
                            customerName = customerName,
                            mobileNumber = mobileNumber,
                            siteName = siteName,
                            siteAddress = siteAddress,
                            flatType = flatType,
                            estimateNumber = estimateNumber,
                            estimateDate = estimateDate
                        )
                        val updated = base.copy(
                            customerName = customerName.ifBlank { "Client" },
                            mobileNumber = mobileNumber,
                            siteName = siteName.ifBlank { "Site" },
                            siteAddress = siteAddress,
                            flatType = flatType,
                            estimateNumber = estimateNumber,
                            estimateDate = estimateDate,
                            siteNotes = siteNotes,
                            customerRequirements = customerReqs,
                            status = status,
                            sitePhotographs = photoUris.value.joinToString(","),
                            updatedAt = System.currentTimeMillis()
                        )
                        onSaveProject(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_customer_site"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageManager.get("save", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onNavigateToRooms,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_go_to_measurements"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Proceed to Room Measurements (कमरा माप)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
