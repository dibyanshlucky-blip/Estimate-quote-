package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "site_progress",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SiteProgressEntity(
    @PrimaryKey
    val projectId: Long,
    val stage1Scraping: Boolean = false, // Sanding & Cleaning
    val stage2Putty: Boolean = false,    // Putty 1st & 2nd Coat
    val stage3Primer: Boolean = false,   // Primer Application
    val stage4Paint: Boolean = false,    // Final Paint Top Coats
    val stage5Handover: Boolean = false, // Masking Removal, Touchups & Handover
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val completedStagesCount: Int
        get() = (if (stage1Scraping) 1 else 0) +
                (if (stage2Putty) 1 else 0) +
                (if (stage3Primer) 1 else 0) +
                (if (stage4Paint) 1 else 0) +
                (if (stage5Handover) 1 else 0)

    val progressPercent: Int
        get() = (completedStagesCount * 100) / 5
}
