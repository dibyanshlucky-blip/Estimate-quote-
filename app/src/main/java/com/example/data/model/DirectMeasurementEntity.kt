package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "direct_measurements",
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
data class DirectMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val surfaceName: String, // e.g. "Interior Wall", "Ceiling", "Exterior Wall", "Staircase", "Grills", "Railing", etc.
    val quantity: Double = 0.0,
    val unit: String = "sqft", // "sqft", "sqm", "rft", "pieces", "lumpsum"
    val notes: String = ""
) {
    // 1 Square Metre = 10.7639 Square Feet
    val calculatedSqft: Double
        get() = when (unit.lowercase()) {
            "sqm", "sq.m", "sq m", "sq.mtr" -> quantity * 10.7639
            "sqft", "sq.ft", "sq ft" -> quantity
            else -> 0.0 // rft, pieces, lumpsum remain in their own units
        }
}
