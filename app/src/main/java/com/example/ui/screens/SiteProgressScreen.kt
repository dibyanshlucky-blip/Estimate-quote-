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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectWithDetails
import com.example.data.model.SiteProgressEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealSuccess
import com.example.util.AppLanguage

@Composable
fun SiteProgressScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?,
    currentProgress: SiteProgressEntity?,
    onSaveProgress: (SiteProgressEntity) -> Unit
) {
    val context = LocalContext.current
    val projectId = projectDetails?.project?.id ?: 1L

    var s1 by remember(currentProgress) { mutableStateOf(currentProgress?.stage1Scraping ?: true) }
    var s2 by remember(currentProgress) { mutableStateOf(currentProgress?.stage2Putty ?: true) }
    var s3 by remember(currentProgress) { mutableStateOf(currentProgress?.stage3Primer ?: false) }
    var s4 by remember(currentProgress) { mutableStateOf(currentProgress?.stage4Paint ?: false) }
    var s5 by remember(currentProgress) { mutableStateOf(currentProgress?.stage5Handover ?: false) }
    var notes by remember(currentProgress) { mutableStateOf(currentProgress?.notes ?: "Putty completed in master bedroom and living room.") }

    val completedCount = (if (s1) 1 else 0) + (if (s2) 1 else 0) + (if (s3) 1 else 0) + (if (s4) 1 else 0) + (if (s5) 1 else 0)
    val progressPct = (completedCount * 100) / 5

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SITE WORK 5-STAGE PROGRESS",
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
                                text = "$progressPct% Complete",
                                color = Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progressPct / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = AccentGold,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Site: ${projectDetails?.project?.siteName ?: "Site"} • Client: ${projectDetails?.project?.customerName ?: "Client"}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 1-Click WhatsApp Progress Update to Customer Button
        item {
            Button(
                onClick = {
                    val sb = StringBuilder()
                    sb.append("🎨 *SITE WORK PROGRESS UPDATE*\n")
                    sb.append("*Divyansh Enterprises - Painting Contractors*\n")
                    sb.append("Site: ${projectDetails?.project?.siteName ?: "Site"}\n")
                    sb.append("Customer: ${projectDetails?.project?.customerName ?: "Customer"}\n\n")
                    sb.append("Progress Status: *$progressPct% Completed*\n\n")
                    sb.append("Stage Checklist:\n")
                    sb.append("1. Sanding & Cleaning: ${if (s1) "✅ Completed" else "⏳ In Progress"}\n")
                    sb.append("2. Putty (1st & 2nd Coat): ${if (s2) "✅ Completed" else "⏳ Pending"}\n")
                    sb.append("3. Wall Primer Application: ${if (s3) "✅ Completed" else "⏳ Pending"}\n")
                    sb.append("4. Paint Topcoats (2 Coats): ${if (s4) "✅ Completed" else "⏳ Pending"}\n")
                    sb.append("5. Touch-ups, Masking & Handover: ${if (s5) "✅ Completed" else "⏳ Pending"}\n\n")
                    if (notes.isNotBlank()) {
                        sb.append("Site Supervisor Notes: $notes\n\n")
                    }
                    sb.append("Contact: Abhimanyu Kumar (+91 98234 56789)\nKharadi, Pune")

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, sb.toString())
                    }
                    context.startActivity(Intent.createChooser(intent, "Send Site Update via WhatsApp"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_share_site_progress")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Progress Update to Client (WhatsApp)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }

        // 5 Stages Checkbox Cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "WORK PHASES & CHECKLIST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryNavy,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    StageCheckRow(
                        stageNum = 1,
                        title = "Stage 1: Sanding & Cleaning (स्क्रैपिंग व सफाई)",
                        subtitle = "Old paint scraping, 80-grit emery sanding, crack filling",
                        checked = s1,
                        onCheckedChange = { s1 = it }
                    )

                    StageCheckRow(
                        stageNum = 2,
                        title = "Stage 2: Wall Putty 1st & 2nd Coat (वॉल पुट्टी)",
                        subtitle = "Birla White putty with smooth knife blade application",
                        checked = s2,
                        onCheckedChange = { s2 = it }
                    )

                    StageCheckRow(
                        stageNum = 3,
                        title = "Stage 3: Wall Primer Application (प्राइमर कोटिंग)",
                        subtitle = "Water thinnable primer for uniform paint adhesion",
                        checked = s3,
                        onCheckedChange = { s3 = it }
                    )

                    StageCheckRow(
                        stageNum = 4,
                        title = "Stage 4: Paint 1st & 2nd Top Coat (पेंटिंग टॉप कोट)",
                        subtitle = "Roller applied emulsion / apex with proper drying intervals",
                        checked = s4,
                        onCheckedChange = { s4 = it }
                    )

                    StageCheckRow(
                        stageNum = 5,
                        title = "Stage 5: Touch-up, Masking Removal & Handover",
                        subtitle = "Switch plate cleaning, floor masking tape removal, final site handover",
                        checked = s5,
                        onCheckedChange = { s5 = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Daily Supervisor Notes / Remarks") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val entity = SiteProgressEntity(
                                projectId = projectId,
                                stage1Scraping = s1,
                                stage2Putty = s2,
                                stage3Primer = s3,
                                stage4Paint = s4,
                                stage5Handover = s5,
                                notes = notes,
                                updatedAt = System.currentTimeMillis()
                            )
                            onSaveProgress(entity)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_save_site_progress")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Save Progress Checklist", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StageCheckRow(
    stageNum: Int,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = PrimaryNavy)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (checked) TealSuccess else Slate900
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Slate500
            )
        }
    }
}
