package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BusinessProfile
import com.example.data.model.CatalogueItemEntity
import com.example.data.model.PaymentEntity
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
import com.example.util.BackupUtils
import com.example.util.LanguageManager

@Composable
fun SettingsBackupScreen(
    language: AppLanguage,
    businessProfile: BusinessProfile,
    allProjects: List<ProjectEntity>,
    activeProjectDetails: ProjectWithDetails?,
    allCatalogueItems: List<CatalogueItemEntity>,
    allPayments: List<PaymentEntity>,
    onUpdateBusinessProfile: (BusinessProfile) -> Unit,
    onResetBusinessProfile: () -> Unit,
    onResetCatalogue: () -> Unit
) {
    val context = LocalContext.current
    var showEditBusinessDialog by remember { mutableStateOf(false) }
    var showResetCatalogueConfirm by remember { mutableStateOf(false) }
    var showResetBusinessConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Business Profile & Settings Option
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.testTag("card_business_settings")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LanguageManager.get("business_settings", language).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TealSuccess.copy(alpha = 0.25f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF6EE7B7),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Active on Quotes",
                                    color = Color(0xFF6EE7B7),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = businessProfile.businessName.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )

                    if (businessProfile.tagline.isNotBlank()) {
                        Text(
                            text = businessProfile.tagline,
                            color = AccentGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Key Details List
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Proprietor: ",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = businessProfile.ownerName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = businessProfile.phone,
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 12.sp
                        )

                        if (businessProfile.email.isNotBlank()) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = businessProfile.email,
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(14.dp).padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = businessProfile.fullFormattedAddress,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.5.sp
                        )
                    }

                    if (businessProfile.gstin.isNotBlank() || businessProfile.upiId.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (businessProfile.gstin.isNotBlank()) {
                                Text(
                                    text = "GSTIN: ${businessProfile.gstin}",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (businessProfile.upiId.isNotBlank()) {
                                Text(
                                    text = "UPI: ${businessProfile.upiId}",
                                    color = AccentGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Edit & Reset Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showEditBusinessDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_edit_business_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = PrimaryNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = LanguageManager.get("edit_business_settings", language),
                                color = PrimaryNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showResetBusinessConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_reset_business_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Reset", fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        // Backup & Export Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DATA BACKUP & EXPORTS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryNavy,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Full offline database snapshot export and spreadsheet sharing.",
                        fontSize = 11.5.sp,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // JSON Backup
                    Button(
                        onClick = {
                            val jsonString = BackupUtils.exportToJson(
                                projects = allProjects,
                                rooms = activeProjectDetails?.rooms ?: emptyList(),
                                directs = activeProjectDetails?.directMeasurements ?: emptyList(),
                                estimates = activeProjectDetails?.estimateItems ?: emptyList(),
                                payments = allPayments,
                                catalogue = allCatalogueItems
                            )
                            BackupUtils.shareExportFile(context, jsonString, "Divyansh_Backup_${System.currentTimeMillis()}.json", "application/json")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_export_json_backup")
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = LanguageManager.get("export_json", language), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // CSV Export
                    Button(
                        onClick = {
                            if (activeProjectDetails != null) {
                                val csv = BackupUtils.exportToCsv(activeProjectDetails)
                                BackupUtils.shareExportFile(context, csv, "Estimate_${activeProjectDetails.project.estimateNumber}.csv", "text/csv")
                            } else {
                                Toast.makeText(context, "Please select an active project first", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_export_csv")
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = LanguageManager.get("export_csv", language), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Catalogue Rates Management
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CATALOGUE & RATES MAINTENANCE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryNavy,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Manage or restore standard painting labor and material rates for Pune & Kharadi.",
                        fontSize = 11.5.sp,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showResetCatalogueConfirm = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_settings_reset_catalogue")
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = LanguageManager.get("reset_defaults", language), color = PrimaryNavy, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Architecture & System Status
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate100),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SYSTEM & ARCHITECTURE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Slate700,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• Database: SQLite Room Offline-First (divyansh_painting.db)", fontSize = 11.sp, color = Slate700)
                    Text(text = "• Currency: Indian Rupee (₹) • Precision: 64-bit IEEE-754", fontSize = 11.sp, color = Slate700)
                    Text(text = "• Local Storage: SharedPreferences & Internal Cache", fontSize = 11.sp, color = Slate700)
                }
            }
        }
    }

    // Edit Business Settings Dialog
    if (showEditBusinessDialog) {
        EditBusinessSettingsDialog(
            initialProfile = businessProfile,
            language = language,
            onDismiss = { showEditBusinessDialog = false },
            onSave = { updated ->
                onUpdateBusinessProfile(updated)
                showEditBusinessDialog = false
                Toast.makeText(context, LanguageManager.get("business_settings_updated", language), Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Reset Business Profile Confirm Dialog
    if (showResetBusinessConfirm) {
        AlertDialog(
            onDismissRequest = { showResetBusinessConfirm = false },
            title = { Text("Reset Business Profile") },
            text = { Text("Restore default business details for Divyansh Enterprises, Kharadi, Pune?") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetBusinessProfile()
                        showResetBusinessConfirm = false
                        Toast.makeText(context, "Business profile restored to Divyansh Enterprises", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetBusinessConfirm = false }) { Text("Cancel") }
            }
        )
    }

    // Reset Catalogue Confirm Dialog
    if (showResetCatalogueConfirm) {
        AlertDialog(
            onDismissRequest = { showResetCatalogueConfirm = false },
            title = { Text("Reset Catalogue Rates") },
            text = { Text("Are you sure you want to restore default Pune and Kharadi contractor rates for all 18 painting work items?") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetCatalogue()
                        showResetCatalogueConfirm = false
                        Toast.makeText(context, "Default rates restored", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetCatalogueConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun EditBusinessSettingsDialog(
    initialProfile: BusinessProfile,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (BusinessProfile) -> Unit
) {
    var businessName by remember { mutableStateOf(initialProfile.businessName) }
    var ownerName by remember { mutableStateOf(initialProfile.ownerName) }
    var phone by remember { mutableStateOf(initialProfile.phone) }
    var email by remember { mutableStateOf(initialProfile.email) }
    var address by remember { mutableStateOf(initialProfile.address) }
    var city by remember { mutableStateOf(initialProfile.city) }
    var state by remember { mutableStateOf(initialProfile.state) }
    var pincode by remember { mutableStateOf(initialProfile.pincode) }
    var gstin by remember { mutableStateOf(initialProfile.gstin) }
    var tagline by remember { mutableStateOf(initialProfile.tagline) }
    var upiId by remember { mutableStateOf(initialProfile.upiId) }
    var terms by remember { mutableStateOf(initialProfile.termsAndConditions) }
    var validityDays by remember { mutableStateOf(initialProfile.quoteValidityDays.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Business, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = LanguageManager.get("business_settings", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PrimaryNavy
                    )
                    Text(
                        text = "Printed on all quotations, PDFs & receipts",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Business Name
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text(LanguageManager.get("business_name", language)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_business_name")
                )

                // Tagline / Specialization
                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text(LanguageManager.get("business_tagline", language)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_business_tagline")
                )

                // Owner Name
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text(LanguageManager.get("owner_name", language)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_owner_name")
                )

                // Phone & Email
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(LanguageManager.get("business_phone", language)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_business_phone")
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(LanguageManager.get("business_email", language)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_business_email")
                    )
                }

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(LanguageManager.get("business_address", language)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_business_address")
                )

                // City, State, Pincode
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text(LanguageManager.get("business_city", language)) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("input_business_city")
                    )

                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text(LanguageManager.get("business_state", language)) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("input_business_state")
                    )

                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = { Text(LanguageManager.get("business_pincode", language)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("input_business_pincode")
                    )
                }

                // GSTIN & UPI ID
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gstin,
                        onValueChange = { gstin = it },
                        label = { Text(LanguageManager.get("business_gstin", language)) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_business_gstin")
                    )

                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text(LanguageManager.get("business_upi", language)) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_business_upi")
                    )
                }

                // Quote Validity
                OutlinedTextField(
                    value = validityDays,
                    onValueChange = { validityDays = it },
                    label = { Text(LanguageManager.get("quote_validity", language)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_quote_validity")
                )

                // Terms & Conditions
                OutlinedTextField(
                    value = terms,
                    onValueChange = { terms = it },
                    label = { Text(LanguageManager.get("terms_conditions", language)) },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_business_terms")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalDays = validityDays.toIntOrNull() ?: 30
                    val updated = initialProfile.copy(
                        businessName = businessName.ifBlank { "Divyansh Enterprises" },
                        ownerName = ownerName.ifBlank { "Abhimanyu Kumar" },
                        phone = phone.ifBlank { "+91 98234 56789" },
                        email = email,
                        address = address.ifBlank { "Shop No. 4, Kharadi Road, Pune, Maharashtra" },
                        city = city.ifBlank { "Pune" },
                        state = state.ifBlank { "Maharashtra" },
                        pincode = pincode.ifBlank { "411014" },
                        gstin = gstin,
                        tagline = tagline,
                        upiId = upiId,
                        termsAndConditions = terms,
                        quoteValidityDays = finalDays
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_save_business_settings")
            ) {
                Text(
                    text = LanguageManager.get("save_business_settings", language),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_cancel_business_settings")
            ) {
                Text(LanguageManager.get("cancel", language))
            }
        }
    )
}
