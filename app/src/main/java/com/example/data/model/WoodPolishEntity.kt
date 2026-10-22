package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.util.CurrencyUtils
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import kotlin.math.max

/**
 * Unit for Plus (+) and Minus (-) adjustments.
 */
enum class AdjustmentUnit {
    SQFT,
    MULTIPLIER;

    companion object {
        fun fromString(str: String?): AdjustmentUnit {
            return when (str?.uppercase()) {
                "MULTIPLIER", "MULT" -> MULTIPLIER
                else -> SQFT
            }
        }
    }
}

/**
 * Sub-component for complex woodwork (e.g. Wardrobe carcass, shutters, shelves, drawers).
 */
data class WoodComponent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Main Component",
    val heightFt: Double = 0.0,
    val heightIn: Double = 0.0,
    val widthFt: Double = 0.0,
    val widthIn: Double = 0.0,
    val lengthFt: Double = 0.0,
    val lengthIn: Double = 0.0,
    val quantity: Int = 1,
    val polishSides: Double = 1.0,
    val deductionSqft: Double = 0.0,
    val multiplier: Double = 1.0,
    val plusAdjustment: Double = 0.0,
    val plusAdjustmentUnit: AdjustmentUnit = AdjustmentUnit.SQFT,
    val minusAdjustment: Double = 0.0,
    val minusAdjustmentUnit: AdjustmentUnit = AdjustmentUnit.SQFT,
    val remarks: String = ""
) {
    val effectiveHeightFt: Double get() = heightFt + (heightIn / 12.0)
    val effectiveWidthFt: Double get() = widthFt + (widthIn / 12.0)
    val effectiveLengthFt: Double get() = lengthFt + (lengthIn / 12.0)

    val baseAreaSqft: Double get() = effectiveHeightFt * effectiveWidthFt

    val grossAreaSqft: Double get() = baseAreaSqft * polishSides * quantity

    val netBeforeAdjustment: Double get() = max(0.0, grossAreaSqft - deductionSqft)

    val plusAreaSqft: Double get() = if (plusAdjustmentUnit == AdjustmentUnit.MULTIPLIER) netBeforeAdjustment * plusAdjustment else plusAdjustment

    val minusAreaSqft: Double get() = if (minusAdjustmentUnit == AdjustmentUnit.MULTIPLIER) netBeforeAdjustment * minusAdjustment else minusAdjustment

    val finalAreaSqft: Double get() = max(0.0, (netBeforeAdjustment * multiplier) + plusAreaSqft - minusAreaSqft)
}

@Entity(
    tableName = "wood_polish_items",
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
data class WoodPolishEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val category: String = "DOOR", // "DOOR", "WINDOW", "INTERIOR"
    val itemName: String,
    val roomName: String = "",
    val itemType: String = "Single Door",
    val quantity: Int = 1,
    val heightFt: Double = 7.0,
    val heightIn: Double = 0.0,
    val widthFt: Double = 3.0,
    val widthIn: Double = 0.0,
    val lengthFt: Double = 0.0,
    val lengthIn: Double = 0.0,
    val measurementMethod: String = "DIMENSIONS", // "DIMENSIONS" (H x W), "LENGTH_WIDTH" (L x W), "DIRECT_SQFT", "CUSTOM"
    val polishSides: Double = 2.0, // 1.0 = One Side, 2.0 = Both Sides, or custom decimal
    val isAlreadyCombinedTotal: Boolean = false, // If true, polish sides multiplier is 1.0 to prevent double-counting
    val shutterAreaSqft: Double = 0.0, // Separate shutter measurement if specified
    val frameAreaSqft: Double = 0.0, // Separate frame measurement if specified
    val includeFrameInTotal: Boolean = true, // Option to include or exclude frame from shutter calculation
    val additionalSectionsSqft: Double = 0.0, // Additional wooden sections (architraves, beading, pelmet, grill)
    val deductionAreaSqft: Double = 0.0, // Openings, glass, metal cutouts
    val multiplier: Double = 1.0, // Multiplier (×): e.g. 0.18, 0.73, 1.2, 1.5, 2.0
    val plusAdjustment: Double = 0.0,
    val plusAdjustmentUnit: String = "SQFT", // "SQFT" or "MULTIPLIER"
    val minusAdjustment: Double = 0.0,
    val minusAdjustmentUnit: String = "SQFT", // "SQFT" or "MULTIPLIER"
    val polishBrand: String = "Asian Paints",
    val polishType: String = "PU Polish", // "Melamine Polish", "PU Polish", "Duco Paint", "NC Polish", "Veneer Polish", etc.
    val labourRate: Double = 35.0,
    val materialRate: Double = 40.0,
    val remarks: String = "",
    val componentsJson: String = "", // Sub-components list for complex interior woodwork
    val updatedAt: Long = System.currentTimeMillis()
) {
    // --- Accurate Feet & Inches conversions ---
    val effectiveHeightFt: Double get() = heightFt + (heightIn / 12.0)
    val effectiveWidthFt: Double get() = widthFt + (widthIn / 12.0)
    val effectiveLengthFt: Double get() = lengthFt + (lengthIn / 12.0)

    val readableDimensions: String
        get() = when (measurementMethod) {
            "DIRECT_SQFT" -> "${CurrencyUtils.formatSqft(shutterAreaSqft.takeIf { it > 0 } ?: (effectiveHeightFt * effectiveWidthFt))} Direct"
            "LENGTH_WIDTH" -> "${lengthFt.toInt()}'${lengthIn.toInt()}\" × ${widthFt.toInt()}'${widthIn.toInt()}\""
            else -> "${heightFt.toInt()}'${heightIn.toInt()}\" × ${widthFt.toInt()}'${widthIn.toInt()}\""
        }

    val dimensionBaseAreaSqft: Double
        get() = when (measurementMethod) {
            "DIRECT_SQFT" -> if (shutterAreaSqft > 0) shutterAreaSqft else effectiveHeightFt * effectiveWidthFt
            "LENGTH_WIDTH" -> effectiveLengthFt * effectiveWidthFt
            else -> effectiveHeightFt * effectiveWidthFt
        }

    /**
     * Base single-side area before quantity or side multiplier.
     * Incorporates separate shutter + frame + additional sections if provided.
     */
    val singleUnitBaseAreaSqft: Double
        get() {
            return if (shutterAreaSqft > 0 || frameAreaSqft > 0 || additionalSectionsSqft > 0) {
                val sArea = if (shutterAreaSqft > 0) shutterAreaSqft else dimensionBaseAreaSqft
                val fArea = if (includeFrameInTotal) frameAreaSqft else 0.0
                sArea + fArea + additionalSectionsSqft
            } else {
                dimensionBaseAreaSqft
            }
        }

    val effectivePolishSides: Double
        get() = if (isAlreadyCombinedTotal) 1.0 else polishSides

    val grossAreaSqft: Double
        get() = singleUnitBaseAreaSqft * effectivePolishSides * quantity

    val totalDeductionSqft: Double
        get() = deductionAreaSqft * quantity

    val netAreaBeforeAdjustment: Double
        get() = max(0.0, grossAreaSqft - totalDeductionSqft)

    val plusUnit: AdjustmentUnit get() = AdjustmentUnit.fromString(plusAdjustmentUnit)
    val minusUnit: AdjustmentUnit get() = AdjustmentUnit.fromString(minusAdjustmentUnit)

    val plusAreaSqft: Double
        get() = if (plusUnit == AdjustmentUnit.MULTIPLIER) netAreaBeforeAdjustment * plusAdjustment else plusAdjustment

    val minusAreaSqft: Double
        get() = if (minusUnit == AdjustmentUnit.MULTIPLIER) netAreaBeforeAdjustment * minusAdjustment else minusAdjustment

    /**
     * Formula per prompt:
     * Final Area = (Net Area × Multiplier) + Plus Area − Minus Area
     * Cannot be negative.
     */
    val finalBillableAreaSqft: Double
        get() {
            val multiplied = netAreaBeforeAdjustment * multiplier
            val result = multiplied + plusAreaSqft - minusAreaSqft
            return max(0.0, result)
        }

    val hasNegativeWarning: Boolean
        get() = minusAreaSqft > (netAreaBeforeAdjustment * multiplier) + plusAreaSqft

    // Costs
    val labourAmount: Double get() = finalBillableAreaSqft * labourRate
    val materialAmount: Double get() = finalBillableAreaSqft * materialRate
    val totalAmount: Double get() = labourAmount + materialAmount

    fun getEffectiveAmount(isLabourOnly: Boolean): Double {
        return if (isLabourOnly) labourAmount else totalAmount
    }

    /**
     * Parse sub-components if any
     */
    fun getParsedComponents(): List<WoodComponent> {
        if (componentsJson.isBlank()) return emptyList()
        return try {
            val list = mutableListOf<WoodComponent>()
            val arr = JSONArray(componentsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    WoodComponent(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        name = obj.optString("name", "Component ${i + 1}"),
                        heightFt = obj.optDouble("heightFt", 0.0),
                        heightIn = obj.optDouble("heightIn", 0.0),
                        widthFt = obj.optDouble("widthFt", 0.0),
                        widthIn = obj.optDouble("widthIn", 0.0),
                        lengthFt = obj.optDouble("lengthFt", 0.0),
                        lengthIn = obj.optDouble("lengthIn", 0.0),
                        quantity = obj.optInt("quantity", 1),
                        polishSides = obj.optDouble("polishSides", 1.0),
                        deductionSqft = obj.optDouble("deductionSqft", 0.0),
                        multiplier = obj.optDouble("multiplier", 1.0),
                        plusAdjustment = obj.optDouble("plusAdjustment", 0.0),
                        plusAdjustmentUnit = AdjustmentUnit.fromString(obj.optString("plusAdjustmentUnit", "SQFT")),
                        minusAdjustment = obj.optDouble("minusAdjustment", 0.0),
                        minusAdjustmentUnit = AdjustmentUnit.fromString(obj.optString("minusAdjustmentUnit", "SQFT")),
                        remarks = obj.optString("remarks", "")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        fun serializeComponents(components: List<WoodComponent>): String {
            val arr = JSONArray()
            components.forEach { c ->
                val obj = JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("heightFt", c.heightFt)
                    put("heightIn", c.heightIn)
                    put("widthFt", c.widthFt)
                    put("widthIn", c.widthIn)
                    put("lengthFt", c.lengthFt)
                    put("lengthIn", c.lengthIn)
                    put("quantity", c.quantity)
                    put("polishSides", c.polishSides)
                    put("deductionSqft", c.deductionSqft)
                    put("multiplier", c.multiplier)
                    put("plusAdjustment", c.plusAdjustment)
                    put("plusAdjustmentUnit", c.plusAdjustmentUnit.name)
                    put("minusAdjustment", c.minusAdjustment)
                    put("minusAdjustmentUnit", c.minusAdjustmentUnit.name)
                    put("remarks", c.remarks)
                }
                arr.put(obj)
            }
            return arr.toString()
        }

        val POLISH_TYPES = listOf(
            "Melamine Polish",
            "PU Polish",
            "Duco Paint",
            "NC Polish",
            "Veneer Polish",
            "Shilar Polish",
            "Hand Polish",
            "Interior Wood Polish",
            "Other / Custom"
        )

        val DOOR_TYPES = listOf(
            "Main Door",
            "Single Door",
            "Double Door",
            "Flush Door",
            "Panel Door",
            "Door Shutter",
            "Door Frame",
            "Custom Door"
        )

        val WINDOW_TYPES = listOf(
            "Wooden Window",
            "Window Frame",
            "Wooden Shutter",
            "Wooden Grill",
            "Custom Window"
        )

        val INTERIOR_TYPES = listOf(
            "Wardrobe",
            "Kitchen Cabinet",
            "Wooden Cabinet",
            "Wooden Shelves",
            "Wooden Partition",
            "Wooden Panels",
            "Wooden Ceiling",
            "Wooden Furniture",
            "Wooden Door and Frame",
            "Other Custom Wooden Surfaces"
        )

        val POPULAR_POLISH_BRANDS = listOf(
            "Asian Paints (Acolac / Emporio)",
            "MRF Vapocure",
            "ICA Italian Wood Finishes",
            "Sirca Wood Coatings",
            "Pidilite Wudfin",
            "Berger WoodKeeper",
            "Sheenlac",
            "Custom / Local Spirit"
        )
    }
}
