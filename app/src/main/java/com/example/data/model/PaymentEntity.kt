package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val amount: Double = 0.0,
    val paymentDate: String, // e.g. "10 Oct 2026"
    val paymentMethod: String = "UPI", // UPI / GPay / PhonePe, Cash, Bank Transfer, Cheque
    val referenceNumber: String = "",
    val remarks: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
