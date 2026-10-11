package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val mobileNumber: String,
    val siteName: String,
    val siteAddress: String,
    val flatType: String, // 1BHK, 2BHK, 3BHK, 4BHK, Office, Commercial, Other
    val estimateNumber: String,
    val estimateDate: String,
    val siteNotes: String = "",
    val customerRequirements: String = "",
    val sitePhotographs: String = "", // Comma-separated URI strings
    val status: String = "Draft", // Draft, Sent, Accepted, Rejected, Completed
    val estimateMode: String = "MATERIAL_AND_LABOUR", // LABOUR_ONLY, MATERIAL_AND_LABOUR, BOTH
    val deductOpenings: Boolean = true,
    val additionalCharges: Double = 0.0,
    val additionalChargesDesc: String = "",
    val discountAmount: Double = 0.0,
    val isTaxEnabled: Boolean = false,
    val taxPercent: Double = 18.0,
    val termsAndConditions: String = "1. 50% advance before work commencement.\n2. 40% upon completion of surface preparation, putty and primer.\n3. 10% balance upon final paint handover.\n4. Electricity and water to be provided by customer at site.\n5. Standard drying times must be observed between successive coats.",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
