package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectWithDetails
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
import com.example.viewmodel.AppScreen

import com.example.data.model.BusinessProfile

data class DashboardItem(
    val id: Int,
    val titleKey: String,
    val subtitleKey: String,
    val icon: ImageVector,
    val targetScreen: AppScreen,
    val accentColor: Color,
    val tag: String
)

@Composable
fun DashboardScreen(
    language: AppLanguage,
    businessProfile: BusinessProfile? = null,
    allProjects: List<ProjectEntity>,
    activeProjectDetails: ProjectWithDetails?,
    onNavigate: (AppScreen) -> Unit,
    onNewEstimateClick: () -> Unit
) {
    // KPI Metrics calculation
    val totalProjectsCount = allProjects.size
    val pendingQuotesCount = allProjects.count { it.status == "Draft" || it.status == "Sent" }
    val acceptedQuotesCount = allProjects.count { it.status == "Accepted" || it.status == "Completed" }

    // Dashboard Items
    val dashboardCards = listOf(
        DashboardItem(1, "new_estimate", "Create fresh site quote", Icons.Default.AddCircle, AppScreen.CUSTOMER_SITE, PrimaryNavy, "card_new_estimate"),
        DashboardItem(18, "woodwork_polish", "woodwork_polish_desc", Icons.Default.MeetingRoom, AppScreen.WOOD_POLISH_CALCULATOR, Color(0xFF92400E), "card_wood_polish"),
    DashboardItem(2, "room_measurements", "L x W x H & Deductions", Icons.Default.MeetingRoom, AppScreen.ROOM_CALCULATOR, PrimaryBlue, "card_room_calc"),
    DashboardItem(3, "direct_sqft", "Quick area entry", Icons.Default.SquareFoot, AppScreen.DIRECT_CALCULATOR, Color(0xFF0284C7), "card_direct_sqft"),
    DashboardItem(4, "customer_mgmt", "Client, site & photos", Icons.Default.Handshake, AppScreen.CUSTOMER_SITE, Color(0xFF0D9488), "card_customer_mgmt"),
    DashboardItem(5, "saved_projects", "All estimates & sites", Icons.Default.ReceiptLong, AppScreen.SAVED_PROJECTS, Color(0xFF4F46E5), "card_saved_projects"),
    DashboardItem(6, "rate_list", "Pune & Kharadi rates", Icons.Default.PriceChange, AppScreen.CATALOGUE, AccentGold, "card_rate_list"),
    DashboardItem(7, "material_labour", "Complete painting quote", Icons.Default.FormatPaint, AppScreen.ESTIMATE_BUILDER, Color(0xFFD97706), "card_mat_labour"),
    DashboardItem(8, "labour_only", "Painter work charges", Icons.Default.Calculate, AppScreen.ESTIMATE_BUILDER, Color(0xFFEA580C), "card_labour_only"),
    DashboardItem(9, "pdf_quotations", "Print & WhatsApp A4", Icons.Default.PictureAsPdf, AppScreen.PDF_QUOTATION, Color(0xFFDC2626), "card_pdf_quotes"),
    DashboardItem(10, "payments_balance", "Advance & stage ledger", Icons.Default.Payments, AppScreen.PAYMENTS, TealSuccess, "card_payments"),
    DashboardItem(11, "reports", "Revenue & summary", Icons.Default.Assessment, AppScreen.REPORTS, Color(0xFF7C3AED), "card_reports"),
    DashboardItem(12, "settings_backup", "JSON & CSV export", Icons.Default.Settings, AppScreen.SETTINGS_BACKUP, Slate700, "card_settings"),
    DashboardItem(13, "material_calc", "Litres & Putty Bags", Icons.Default.ShoppingCart, AppScreen.MATERIAL_CALCULATOR, Color(0xFF0284C7), "card_material_calc"),
    DashboardItem(14, "color_palette", "Asian Paints & Berger", Icons.Default.Palette, AppScreen.COLOR_PALETTE, Color(0xFF8B5CF6), "card_color_palette"),
    DashboardItem(15, "labour_attendance", "Daily Karigar Wages", Icons.Default.Badge, AppScreen.LABOUR_ATTENDANCE, TealSuccess, "card_labour_attendance"),
    DashboardItem(16, "site_progress", "5-Phase Work Status", Icons.Default.Timeline, AppScreen.SITE_PROGRESS, Color(0xFFD97706), "card_site_progress"),
    DashboardItem(17, "warranty_cert", "Completion Guarantee", Icons.Default.Verified, AppScreen.WARRANTY_CERTIFICATE, Color(0xFFB45309), "card_warranty_cert")
)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Business Profile Strip
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = businessProfile?.businessName ?: LanguageManager.get("app_title", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrimaryNavy
                        )
                        Text(
                            text = "${businessProfile?.ownerName ?: LanguageManager.get("contractor_name", language)} • ${businessProfile?.city ?: "Kharadi, Pune"}",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier.testTag("chip_business_settings_status")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Business Settings",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Settings",
                                color = PrimaryBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 5 KPI Metrics Carousel / Grid
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "BUSINESS OVERVIEW",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Projects
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = LanguageManager.get("total_projects", language),
                        value = totalProjectsCount.toString(),
                        accentColor = PrimaryNavy
                    )
                    // Pending Quotes
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = LanguageManager.get("pending_quotes", language),
                        value = pendingQuotesCount.toString(),
                        accentColor = Color(0xFFD97706)
                    )
                    // Accepted Quotes
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = LanguageManager.get("accepted_quotes", language),
                        value = acceptedQuotesCount.toString(),
                        accentColor = TealSuccess
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Estimated
                    val estAmt = activeProjectDetails?.currentGrandTotal ?: 0.0
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = LanguageManager.get("total_estimated", language),
                        value = CurrencyUtils.formatInr(estAmt),
                        accentColor = PrimaryBlue
                    )
                    // Pending Payments
                    val balAmt = activeProjectDetails?.balancePayable ?: 0.0
                    KpiCard(
                        modifier = Modifier.weight(1f),
                        title = LanguageManager.get("pending_payments", language),
                        value = CurrencyUtils.formatInr(balAmt),
                        accentColor = if (balAmt > 0) Color(0xFFDC2626) else TealSuccess
                    )
                }
            }
        }

        // Active Project Quick Banner (if selected)
        if (activeProjectDetails != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ACTIVE SITE: ${activeProjectDetails.project.siteName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = PrimaryNavy
                                )
                                Text(
                                    text = "${activeProjectDetails.project.customerName} (${activeProjectDetails.project.flatType}) • ${activeProjectDetails.project.estimateNumber}",
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (activeProjectDetails.project.status == "Accepted") Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = activeProjectDetails.project.status,
                                    color = if (activeProjectDetails.project.status == "Accepted") TealSuccess else Color(0xFFB45309),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Paintable Area", fontSize = 11.sp, color = Slate500)
                                Text(
                                    text = CurrencyUtils.formatSqft(activeProjectDetails.combinedPaintableSqft),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Slate900
                                )
                            }
                            Column {
                                Text(text = "Estimate Total", fontSize = 11.sp, color = Slate500)
                                Text(
                                    text = CurrencyUtils.formatInr(activeProjectDetails.currentGrandTotal),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = PrimaryBlue
                                )
                            }
                            Column {
                                Text(text = "Balance Due", fontSize = 11.sp, color = Slate500)
                                Text(
                                    text = CurrencyUtils.formatInr(activeProjectDetails.balancePayable),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (activeProjectDetails.balancePayable > 0) Color(0xFFDC2626) else TealSuccess
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dedicated Woodwork & Polish Measurement Module Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigate(AppScreen.WOOD_POLISH_CALCULATOR) }
                    .testTag("banner_woodwork_polish"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF78350F)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFDE68A),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MeetingRoom,
                                    contentDescription = null,
                                    tint = Color(0xFF78350F),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = LanguageManager.get("woodwork_polish", language),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF59E0B)
                                ) {
                                    Text(
                                        text = "NEW",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Door • Window • Interior Polish (Ft & In, Multiplier, +/-)",
                                fontSize = 11.sp,
                                color = Color(0xFFFDE68A)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Section Title: Application Modules
        item {
            Padding(16.dp, 10.dp, 16.dp, 6.dp) {
                Text(
                    text = "APPLICATION MODULES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        // Render 12 Dashboard Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val chunked = dashboardCards.chunked(2)
                chunked.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { item ->
                            DashboardActionCard(
                                modifier = Modifier.weight(1f),
                                item = item,
                                language = language,
                                onClick = {
                                    if (item.targetScreen == AppScreen.CUSTOMER_SITE && item.id == 1) {
                                        onNewEstimateClick()
                                    } else {
                                        onNavigate(item.targetScreen)
                                    }
                                }
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.5.sp,
                color = Slate500,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DashboardActionCard(
    modifier: Modifier = Modifier,
    item: DashboardItem,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag(item.tag)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.titleKey,
                    tint = item.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = LanguageManager.get(item.titleKey, language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Slate900,
                    maxLines = 1
                )
                Text(
                    text = item.subtitleKey,
                    fontSize = 10.5.sp,
                    color = Slate500,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun Padding(
    start: androidx.compose.ui.unit.Dp,
    top: androidx.compose.ui.unit.Dp,
    end: androidx.compose.ui.unit.Dp,
    bottom: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.padding(start = start, top = top, end = end, bottom = bottom)) {
        content()
    }
}
