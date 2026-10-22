package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.data.model.BusinessProfile

@Composable
fun WarrantyCertificateScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?,
    businessProfile: BusinessProfile? = null
) {
    val context = LocalContext.current
    val project = projectDetails?.project
    var warrantyYears by remember { mutableStateOf(2) }

    val completionDate = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.YEAR, warrantyYears)
    val expiryDate = SimpleDateFormat("dd MMM yyyy", Locale.US).format(cal.time)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Toggle Warranty duration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Warranty Duration:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { warrantyYears = 1 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (warrantyYears == 1) PrimaryNavy else Color(0xFFF1F5F9),
                                contentColor = if (warrantyYears == 1) Color.White else Slate700
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("1 Year")
                        }
                        Button(
                            onClick = { warrantyYears = 2 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (warrantyYears == 2) PrimaryNavy else Color(0xFFF1F5F9),
                                contentColor = if (warrantyYears == 2) Color.White else Slate700
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("2 Years")
                        }
                    }
                }
            }
        }

        // Action button
        item {
            Button(
                onClick = {
                    val bName = businessProfile?.businessName ?: "Divyansh Enterprises"
                    val bOwner = businessProfile?.ownerName ?: "Abhimanyu Kumar"
                    val bCity = businessProfile?.city ?: "Kharadi, Pune"
                    val bState = businessProfile?.state ?: "Maharashtra"
                    val bPhone = businessProfile?.phone ?: "+91 98234 56789"
                    val msg = "★ CERTIFICATE OF COMPLETION & SERVICE WARRANTY ★\n$bName - Painting Contractors\n$bCity, $bState\n\nThis certifies that the painting work at:\nSite: ${project?.siteName ?: "Site"}\nAddress: ${project?.siteAddress ?: "$bCity"}\nCustomer: ${project?.customerName ?: "Customer"}\n\nhas been completed successfully on $completionDate under the supervision of $bOwner.\n\nWarranty Coverage: $warrantyYears-Year Workmanship Warranty (Valid until $expiryDate).\nCovers paint flaking, bubbling, or improper adhesion.\n\nAuthorized Signatory:\n$bOwner, $bName\nPhone: $bPhone"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, msg)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Warranty Certificate"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_share_warranty_whatsapp")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Share Certificate on WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            }
        }

        // Certificate Container Preview (Framed certificate)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AccentGold, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = (businessProfile?.businessName ?: "DIVYANSH ENTERPRISES").uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryNavy,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                    val ownerStr = businessProfile?.ownerName ?: "Abhimanyu Kumar"
                    val cityStr = businessProfile?.city ?: "Kharadi, Pune"
                    val phoneStr = businessProfile?.phone ?: "+91 98234 56789"
                    Text(
                        text = "$cityStr • Ph: $phoneStr",
                        fontSize = 11.sp,
                        color = Slate500
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = AccentGold, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "CERTIFICATE OF COMPLETION & WORK WARRANTY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PrimaryNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "This is to certify that the professional painting work for the property specified below has been completed to the highest standards of workmanship using premium quality paints and surface preparation materials.",
                        fontSize = 11.5.sp,
                        color = Slate700,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Customer Name:", fontSize = 11.5.sp, color = Slate500)
                                Text(text = project?.customerName ?: "Customer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Site / Property:", fontSize = 11.5.sp, color = Slate500)
                                Text(text = project?.siteName ?: "Site", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Completion Date:", fontSize = 11.5.sp, color = Slate500)
                                Text(text = completionDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Warranty Expiry:", fontSize = 11.5.sp, color = Slate500)
                                Text(text = expiryDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealSuccess)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Warranty Terms: Covers peeling, blistering, flaking or abnormal fading caused by improper application under standard indoor conditions for $warrantyYears years from handover.",
                        fontSize = 10.5.sp,
                        color = Slate500,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "____________________", color = Slate500, fontSize = 11.sp)
                            Text(text = "Customer Sign", fontSize = 10.5.sp, color = Slate700)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val signOwner = businessProfile?.ownerName ?: "Abhimanyu Kumar"
                            val signBusiness = businessProfile?.businessName ?: "Divyansh Enterprises"
                            Text(text = signOwner, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = PrimaryNavy)
                            Text(text = "For $signBusiness", fontSize = 10.5.sp, color = Slate500)
                        }
                    }
                }
            }
        }
    }
}
