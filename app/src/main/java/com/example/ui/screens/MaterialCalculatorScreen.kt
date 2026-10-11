package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectWithDetails
import com.example.data.model.SiteMaterialEstimation
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealSuccess
import com.example.util.AppLanguage
import com.example.util.CurrencyUtils

@Composable
fun MaterialCalculatorScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?
) {
    val context = LocalContext.current
    val projectWallArea = projectDetails?.totalRoomNetWallAreaSqft ?: 1580.0
    val projectCeilArea = projectDetails?.totalRoomCeilingAreaSqft ?: 460.0

    var wallAreaInput by remember(projectDetails) { mutableStateOf(projectWallArea.toInt().toString()) }
    var ceilAreaInput by remember(projectDetails) { mutableStateOf(projectCeilArea.toInt().toString()) }
    var grillDoorInput by remember { mutableStateOf("120") }

    val wallSqft = wallAreaInput.toDoubleOrNull() ?: 0.0
    val ceilSqft = ceilAreaInput.toDoubleOrNull() ?: 0.0
    val grillSqft = grillDoorInput.toDoubleOrNull() ?: 0.0

    val estimation = remember(wallSqft, ceilSqft, grillSqft) {
        SiteMaterialEstimation.calculate(wallSqft, ceilSqft, grillSqft)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PAINT & PUTTY BUCKET ESTIMATOR",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calculates exact litres, 20L buckets, 40kg putty bags & consumable tools",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total Surface Area", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                            Text(
                                text = CurrencyUtils.formatSqft(wallSqft + ceilSqft),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Approx. Material Cost", color = AccentGold, fontSize = 11.sp)
                            Text(
                                text = CurrencyUtils.formatInr(estimation.totalMaterialCost),
                                color = AccentGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Adjustable Area Inputs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SURFACE MEASUREMENTS (SQ.FT)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryNavy,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = wallAreaInput,
                            onValueChange = { wallAreaInput = it },
                            label = { Text("Wall Sqft") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ceilAreaInput,
                            onValueChange = { ceilAreaInput = it },
                            label = { Text("Ceiling Sqft") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = grillDoorInput,
                            onValueChange = { grillDoorInput = it },
                            label = { Text("Grill/Door") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 1-Click WhatsApp Shop Order List Button
        item {
            Button(
                onClick = {
                    val sb = StringBuilder()
                    sb.append("*PAINT & MATERIAL PROCUREMENT LIST*\n")
                    sb.append("*Divyansh Enterprises - Painting Contractors*\n")
                    sb.append("Site: ${projectDetails?.project?.siteName ?: "Site"}\n")
                    sb.append("Client: ${projectDetails?.project?.customerName ?: "Client"}\n\n")
                    sb.append("Please arrange the following materials:\n")
                    estimation.items.forEachIndexed { i, itm ->
                        sb.append("${i + 1}. *${itm.name}*: ${itm.packagingBreakdown} (${itm.totalQuantity.toInt()} ${itm.unit})\n")
                        sb.append("   Spec: ${itm.specification}\n")
                    }
                    sb.append("\nEstimated Material Cost: ${CurrencyUtils.formatInr(estimation.totalMaterialCost)}\n")
                    sb.append("Authorized: Abhimanyu Kumar (Ph: +91 98234 56789)\nKharadi, Pune")

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, sb.toString())
                    }
                    context.startActivity(Intent.createChooser(intent, "Send Material List to Paint Shop"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_share_paint_shop_list")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Paint Shop Order List (WhatsApp)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }

        // Material Breakdown Cards
        items(estimation.items) { item ->
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
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate900
                        )
                        Text(
                            text = item.specification,
                            fontSize = 11.5.sp,
                            color = Slate500
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "Package: ${item.packagingBreakdown}",
                                color = PrimaryNavy,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${item.totalQuantity.toInt()} ${item.unit}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate900
                        )
                        Text(
                            text = CurrencyUtils.formatInr(item.estimatedCost),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp,
                            color = TealSuccess
                        )
                        Text(
                            text = "@ ₹${item.approxRatePerUnit.toInt()}/${item.unit}",
                            fontSize = 10.sp,
                            color = Slate500
                        )
                    }
                }
            }
        }
    }
}
