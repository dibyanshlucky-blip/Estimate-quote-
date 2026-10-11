package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ColorPaletteLibrary
import com.example.data.model.ColorShade
import com.example.data.model.ProjectWithDetails
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealSuccess
import com.example.util.AppLanguage

@Composable
fun ColorPaletteScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?
) {
    val context = LocalContext.current
    val shades = ColorPaletteLibrary.shades
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Whites & Creams", "Warm Accents", "Royal Blues", "Natural Greens", "Modern Neutrals")

    // Room shade selections
    var livingBase by remember { mutableStateOf("Cotton Wool (L104)") }
    var livingAccent by remember { mutableStateOf("Royale Blue / Deep Ocean (7246)") }
    var bedBase by remember { mutableStateOf("Almond Frost (0313)") }
    var bedAccent by remember { mutableStateOf("Olive Grove (7683)") }

    val filteredShades = remember(selectedCategory) {
        if (selectedCategory == "All") shades else shades.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = AccentGold, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COLOR SHADE & ROOM PALETTE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Curated Asian Paints & Berger shades with room-wise recommendations",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        // Room Palette Selection Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PROPOSED SITE COLOR SCHEME",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryNavy,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    RoomShadeRow(room = "Living Room Base", shade = livingBase)
                    RoomShadeRow(room = "Living Room Feature Wall", shade = livingAccent)
                    RoomShadeRow(room = "Master Bedroom Base", shade = bedBase)
                    RoomShadeRow(room = "Master Bedroom Bedwall", shade = bedAccent)

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val msg = "PROPOSED COLOR PALETTE - DIVYANSH ENTERPRISES\nSite: ${projectDetails?.project?.siteName ?: "Site"}\nClient: ${projectDetails?.project?.customerName ?: "Client"}\n\n• Living Room Base: $livingBase\n• Living Room Accent: $livingAccent\n• Master Bedroom Base: $bedBase\n• Master Bed Accent: $bedAccent\n• All Ceilings: Super White (Tractor)\n\nSelected from Asian Paints Royale & Apcolite catalog."
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, msg)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Color Palette"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_share_palette_whatsapp")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Share Color Scheme with Client (WhatsApp)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Category filter chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSel = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) PrimaryNavy else Color.White,
                        shadowElevation = if (isSel) 2.dp else 1.dp,
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSel) Color.White else Slate700,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Swatch cards
        items(filteredShades) { shade ->
            ColorSwatchCard(
                shade = shade,
                onSelectAsAccent = { livingAccent = "${shade.name} (${shade.code})" },
                onSelectAsBase = { livingBase = "${shade.name} (${shade.code})" }
            )
        }
    }
}

@Composable
private fun RoomShadeRow(room: String, shade: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = room, fontSize = 12.sp, color = Slate700, fontWeight = FontWeight.Medium)
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFF1F5F9)
        ) {
            Text(
                text = shade,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryNavy,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun ColorSwatchCard(
    shade: ColorShade,
    onSelectAsBase: () -> Unit,
    onSelectAsAccent: () -> Unit
) {
    val swatchColor = try {
        Color(android.graphics.Color.parseColor(shade.hexCode))
    } catch (_: Exception) {
        Color.LightGray
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(swatchColor)
                    .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shade.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Slate900
                )
                Text(
                    text = "${shade.brand} • Code: ${shade.code}",
                    fontSize = 11.5.sp,
                    color = Slate500
                )
                Text(
                    text = "Recommended: ${shade.recommendedFor}",
                    fontSize = 10.5.sp,
                    color = PrimaryNavy,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.clickable { onSelectAsAccent() }
                ) {
                    Text(
                        text = "+ Feature Wall",
                        color = PrimaryNavy,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.clickable { onSelectAsBase() }
                ) {
                    Text(
                        text = "+ Base Wall",
                        color = Slate700,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
