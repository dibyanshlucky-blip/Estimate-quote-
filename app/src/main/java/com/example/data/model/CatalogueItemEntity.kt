package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalogue_items")
data class CatalogueItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // Preparation, Interior, Exterior, Wood & Metal, Textures, Custom
    val titleEn: String,
    val titleHi: String,
    val brandProduct: String = "Asian Paints",
    val surfaceType: String = "Wall", // Wall, Ceiling, Exterior Wall, Wood, Metal / Grill, Railing
    val unit: String = "sqft",
    val materialRate: Double = 0.0,
    val labourRate: Double = 0.0,
    val coats: Int = 2,
    val remarks: String = "",
    val isCustom: Boolean = false
) {
    val combinedRate: Double
        get() = materialRate + labourRate
}
