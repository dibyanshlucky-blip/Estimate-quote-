package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "estimate_items",
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
data class EstimateItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val category: String = "Interior",
    val workDescription: String, // e.g. Putty, Primer, Interior Wall Painting, Exterior Painting, Texture, Enamel, Wood Polish
    val brandName: String = "", // e.g. Asian Paints, Berger, Nerolac, Dulux, Indigo, Birla White, Other (Optional)
    val productName: String = "", // e.g. Royale, Apcolite, Tractor, Easy Clean, Apex (Optional)
    val productVariant: String = "", // e.g. Luxury Emulsion, 2-in-1, Matt, etc. (Optional)
    val finish: String = "", // Matt, Sheen, Satin, Gloss, Luxury, Custom (Optional)
    val productPhotoUri: String = "", // Optional product photo URI
    val surfaceType: String = "Wall",
    val quantity: Double = 0.0,
    val unit: String = "sqft",
    val materialRate: Double = 0.0,
    val labourRate: Double = 0.0,
    val coats: Int = 2,
    val remarks: String = "" // Special instructions / site remarks
) {
    val materialAmount: Double
        get() = quantity * materialRate

    val labourAmount: Double
        get() = quantity * labourRate

    val combinedRate: Double
        get() = materialRate + labourRate

    val totalAmountMaterialAndLabour: Double
        get() = materialAmount + labourAmount

    val totalAmountLabourOnly: Double
        get() = labourAmount

    val hasProductDetails: Boolean
        get() = brandName.isNotBlank() || productName.isNotBlank() || productVariant.isNotBlank() || finish.isNotBlank()

    val formattedProductSummary: String
        get() {
            val parts = mutableListOf<String>()
            if (brandName.isNotBlank()) parts.add(brandName)
            if (productName.isNotBlank()) parts.add(productName)
            if (productVariant.isNotBlank()) parts.add(productVariant)
            if (finish.isNotBlank()) parts.add(finish)
            return parts.joinToString(" • ")
        }
}
