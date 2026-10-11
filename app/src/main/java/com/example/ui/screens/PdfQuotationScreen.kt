package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.util.PdfGenerator

@Composable
fun PdfQuotationScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf(if (projectDetails?.project?.estimateMode == "LABOUR_ONLY") 1 else 0) } // 0: Material+Labour, 1: Labour Only
    val isLabourOnly = selectedFormat == 1

    var showRoomsSummary by remember { mutableStateOf(true) }
    var showTerms by remember { mutableStateOf(true) }

    val project = projectDetails?.project
    val items = projectDetails?.estimateItems ?: emptyList()
    val grandTotal = if (isLabourOnly) projectDetails?.labourGrandTotal ?: 0.0 else projectDetails?.materialAndLabourGrandTotal ?: 0.0
    val subtotal = if (isLabourOnly) projectDetails?.labourSubtotal ?: 0.0 else projectDetails?.materialAndLabourSubtotal ?: 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quotation Format Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "QUOTATION FORMAT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TabRow(
                        selectedTabIndex = selectedFormat,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedFormat]),
                                color = PrimaryNavy
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedFormat == 0,
                            onClick = { selectedFormat = 0 },
                            text = { Text("1. Material + Labour Quote", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedFormat == 1,
                            onClick = { selectedFormat = 1 },
                            text = { Text("2. Labour-Only Quote", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }

        // Action Buttons: WhatsApp Share & Print
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (projectDetails != null) {
                            val proj = projectDetails.project
                            val pdfFile = PdfGenerator.generateQuotationPdf(context, projectDetails, isLabourOnly)
                            val summary = "Hello ${proj.customerName}, please find attached quotation ${proj.estimateNumber} from Divyansh Enterprises for ${proj.siteName}. Total: ${CurrencyUtils.formatInr(grandTotal)}. Contact: +91 98234 56789."
                            PdfGenerator.sharePdfViaWhatsApp(context, pdfFile, proj.mobileNumber, summary)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_share_whatsapp_quote")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "WhatsApp Share", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (projectDetails != null) {
                            val proj = projectDetails.project
                            val pdfFile = PdfGenerator.generateQuotationPdf(context, projectDetails, isLabourOnly)
                            PdfGenerator.printPdf(context, pdfFile, "Quotation_${proj.estimateNumber}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_print_quote")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Print / Save PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // A4 Paper Document Preview Container
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header Letterhead
                    Surface(
                        color = PrimaryNavy,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DIVYANSH ENTERPRISES",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Painting Contractor • Surface Protection & Decorative Finishes",
                                color = AccentGold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Abhimanyu Kumar | Kharadi, Pune, Maharashtra | Ph: +91 98234 56789",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title & Estimate details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isLabourOnly) "LABOUR-ONLY PAINTING QUOTATION" else "PAINTING ESTIMATE & QUOTATION",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy,
                                fontSize = 13.sp
                            )
                            Text(text = "Client: ${project?.customerName ?: ""}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Mobile: ${project?.mobileNumber ?: ""}", fontSize = 11.5.sp, color = Slate700)
                            Text(text = "Flat/Property: ${project?.flatType ?: ""}", fontSize = 11.5.sp, color = Slate700)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Estimate: ${project?.estimateNumber ?: ""}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Date: ${project?.estimateDate ?: ""}", fontSize = 11.sp, color = Slate500)
                            Text(text = "Site: ${project?.siteName ?: ""}", fontSize = 11.5.sp, color = Slate700)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Measurement Summary Snippet
                    if (projectDetails?.rooms?.isNotEmpty() == true) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "MEASUREMENT DETAILS: ${projectDetails.rooms.size} Rooms",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "Wall Area: ${CurrencyUtils.formatSqft(projectDetails.totalRoomNetWallAreaSqft)} | Ceiling: ${CurrencyUtils.formatSqft(projectDetails.totalRoomCeilingAreaSqft)} | Total: ${CurrencyUtils.formatSqft(projectDetails.combinedPaintableSqft)}",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Table Header
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Work Description", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                            Text(text = "Qty", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            if (!isLabourOnly) {
                                Text(text = "Mat / Lab", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.5f))
                            }
                            Text(text = "Amount", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                        }
                    }

                    // Work Items List
                    items.forEachIndexed { idx, itm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(2f)) {
                                Text(text = itm.workDescription, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                if (itm.hasProductDetails) {
                                    val brandPart = if (itm.brandName.isNotBlank()) "${itm.brandName} • " else ""
                                    val prodPart = if (itm.productName.isNotBlank()) itm.productName else ""
                                    val varPart = if (itm.productVariant.isNotBlank()) " (${itm.productVariant})" else ""
                                    val finishPart = if (itm.finish.isNotBlank()) " • ${itm.finish}" else ""
                                    Text(
                                        text = "$brandPart$prodPart$varPart$finishPart • ${itm.coats} coats",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF1D4ED8),
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    Text(text = "${itm.category} • ${itm.coats} coats", fontSize = 9.5.sp, color = Slate500)
                                }
                                if (itm.remarks.isNotBlank()) {
                                    Text(
                                        text = "Remarks: ${itm.remarks}",
                                        fontSize = 9.sp,
                                        color = Color(0xFFB45309),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                            Text(text = "${itm.quantity.toInt()} ${itm.unit}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                            if (!isLabourOnly) {
                                Text(text = "₹${itm.materialRate} / ₹${itm.labourRate}", fontSize = 10.sp, modifier = Modifier.weight(1.5f))
                                Text(
                                    text = CurrencyUtils.formatInr(itm.totalAmountMaterialAndLabour),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.2f)
                                )
                            } else {
                                Text(
                                    text = CurrencyUtils.formatInr(itm.totalAmountLabourOnly),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.2f)
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Financial Summary Box
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Subtotal:", fontSize = 11.sp, color = Slate700)
                                Text(text = CurrencyUtils.formatInr(subtotal), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            if (project?.additionalCharges ?: 0.0 > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Additional Charges:", fontSize = 11.sp, color = Slate700)
                                    Text(text = CurrencyUtils.formatInr(project?.additionalCharges ?: 0.0), fontSize = 11.sp)
                                }
                            }

                            if (project?.discountAmount ?: 0.0 > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Discount:", fontSize = 11.sp, color = Slate700)
                                    Text(text = "-${CurrencyUtils.formatInr(project?.discountAmount ?: 0.0)}", fontSize = 11.sp, color = Color.Red)
                                }
                            }

                            if (project?.isTaxEnabled == true) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "GST (18%):", fontSize = 11.sp, color = Slate700)
                                    val tax = if (isLabourOnly) projectDetails?.labourTaxAmount ?: 0.0 else projectDetails?.materialAndLabourTaxAmount ?: 0.0
                                    Text(text = CurrencyUtils.formatInr(tax), fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "GRAND TOTAL:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                                Text(text = CurrencyUtils.formatInr(grandTotal), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryNavy)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "(${CurrencyUtils.numberToWordsInr(grandTotal)})",
                                fontSize = 10.sp,
                                color = Slate500,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Advance Received: ${CurrencyUtils.formatInr(projectDetails?.totalPaid ?: 0.0)}", fontSize = 11.sp, color = TealSuccess)
                                Text(
                                    text = "Balance Payable: ${CurrencyUtils.formatInr(projectDetails?.balancePayable ?: 0.0)}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (projectDetails?.balancePayable ?: 0.0 > 0) Color(0xFFDC2626) else TealSuccess
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Terms & Conditions
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "TERMS & CONDITIONS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Slate700)
                            Text(
                                text = if (isLabourOnly) "• Paint & all materials supplied by client.\n• 50% advance before start, 40% on primer/putty, 10% on handover.\n• Site water & electricity provided by client.\n• Quotation valid for 30 days."
                                else "• Standard surface preparation, primer, putty & paint materials included.\n• 50% advance before start, 40% on primer/putty, 10% on handover.\n• Site water & electricity provided by client.\n• Quotation valid for 30 days.",
                                fontSize = 9.sp,
                                color = Slate700
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Signature Line
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "________________________", color = Slate500, fontSize = 10.sp)
                            Text(text = "Customer Signature", fontSize = 10.sp, color = Slate700)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "________________________", color = Slate500, fontSize = 10.sp)
                            Text(text = "For Divyansh Enterprises", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                            Text(text = "(Abhimanyu Kumar)", fontSize = 9.sp, color = Slate500)
                        }
                    }
                }
            }
        }
    }
}
