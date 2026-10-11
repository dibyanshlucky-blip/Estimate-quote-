package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentScreen(
    language: AppLanguage,
    projectDetails: ProjectWithDetails?,
    onAddPayment: (amount: Double, date: String, method: String, ref: String, remarks: String) -> Unit,
    onDeletePayment: (Long) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    val payments = projectDetails?.payments ?: emptyList()
    val project = projectDetails?.project

    val quotationAmount = projectDetails?.currentGrandTotal ?: 0.0
    val totalPaid = projectDetails?.totalPaid ?: 0.0
    val balanceDue = projectDetails?.balancePayable ?: 0.0

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Balance & Payments KPI Card
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
                                text = "PAYMENTS & OUTSTANDING LEDGER",
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${payments.size} Receipts",
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
                                Text(text = "Quotation Amount", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                Text(
                                    text = CurrencyUtils.formatInr(quotationAmount),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Column {
                                Text(text = "Total Received", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                Text(
                                    text = CurrencyUtils.formatInr(totalPaid),
                                    color = TealSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Balance Due", color = AccentGold, fontSize = 11.sp)
                                Text(
                                    text = CurrencyUtils.formatInr(balanceDue),
                                    color = if (balanceDue > 0) Color(0xFFF87171) else TealSuccess,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Payment Receipt share summary button
            item {
                Button(
                    onClick = {
                        val text = "PAYMENT RECEIPT - DIVYANSH ENTERPRISES\nClient: ${project?.customerName ?: ""}\nSite: ${project?.siteName ?: ""}\nEstimate Total: ${CurrencyUtils.formatInr(quotationAmount)}\nTotal Paid: ${CurrencyUtils.formatInr(totalPaid)}\nOutstanding Balance: ${CurrencyUtils.formatInr(balanceDue)}\nThank you for choosing Divyansh Enterprises!"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Payment Receipt"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Share Printable Payment Receipt", color = PrimaryNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Payments List
            if (payments.isEmpty()) {
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
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Slate500, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No payments recorded yet", fontWeight = FontWeight.Bold, color = Slate700)
                            Text(text = "Tap '+ Record Payment' to log advance or stage payments", fontSize = 12.sp, color = Slate500)
                        }
                    }
                }
            } else {
                items(payments) { payment ->
                    PaymentItemCard(
                        payment = payment,
                        onDelete = { onDeletePayment(payment.id) },
                        onShare = {
                            val msg = "RECEIPT: Received ${CurrencyUtils.formatInr(payment.amount)} from ${project?.customerName} on ${payment.paymentDate} via ${payment.paymentMethod}. Ref: ${payment.referenceNumber}. - Divyansh Enterprises"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, msg)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Receipt"))
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = PrimaryNavy,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_payment")
        ) {
            Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Add Payment")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Record Payment", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAddDialog) {
        AddPaymentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amt, date, method, ref, rem ->
                onAddPayment(amt, date, method, ref, rem)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun PaymentItemCard(
    payment: PaymentEntity,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
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
                    text = CurrencyUtils.formatInr(payment.amount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TealSuccess
                )
                Text(
                    text = "${payment.paymentDate} • ${payment.paymentMethod}",
                    fontSize = 12.sp,
                    color = Slate700,
                    fontWeight = FontWeight.Medium
                )
                if (payment.referenceNumber.isNotBlank()) {
                    Text(text = "Ref: ${payment.referenceNumber}", fontSize = 11.sp, color = Slate500)
                }
                if (payment.remarks.isNotBlank()) {
                    Text(text = payment.remarks, fontSize = 11.sp, color = Slate500)
                }
            }

            Row {
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = PrimaryNavy, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun AddPaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, date: String, method: String, ref: String, remarks: String) -> Unit
) {
    var amountStr by remember { mutableStateOf("10000") }
    var dateStr by remember { mutableStateOf(SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())) }
    var method by remember { mutableStateOf("UPI / GPay / PhonePe") }
    var ref by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("Stage advance payment") }

    val methods = listOf("UPI / GPay / PhonePe", "Cash", "Bank Transfer / NEFT", "Cheque")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Customer Payment", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("Payment Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text(text = "Payment Method", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        methods.forEach { m ->
                            val sel = method == m
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (sel) PrimaryNavy else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { method = m }
                            ) {
                                Text(
                                    text = m,
                                    color = if (sel) Color.White else Slate900,
                                    fontSize = 12.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = ref,
                        onValueChange = { ref = it },
                        label = { Text("Transaction Reference No.") },
                        placeholder = { Text("e.g. UPI/49201938201") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Remarks (e.g. 50% advance)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    onConfirm(amt, dateStr, method, ref, remarks)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text("Save Payment")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
