package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max

/**
 * Represents a single wall measurement line (Length x Height or Direct Sqft).
 */
/**
 * Represents a single wall measurement line (Length x Height or Direct Sqft).
 * Supports feet and inches (e.g. 14 ft 6 in x 10 ft 3 in).
 */
data class WallMeasurementEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String = "Wall",
    val isDirectArea: Boolean = false,
    val lengthFt: Double = 0.0,
    val lengthIn: Double = 0.0,
    val heightFt: Double = 10.0,
    val heightIn: Double = 0.0,
    val wallCount: Int = 1,
    val directAreaSqft: Double = 0.0
) {
    val effectiveLengthFt: Double
        get() = lengthFt + (lengthIn / 12.0)

    val effectiveHeightFt: Double
        get() = heightFt + (heightIn / 12.0)

    val grossAreaSqft: Double
        get() = if (isDirectArea) directAreaSqft else wallCount * (effectiveLengthFt * effectiveHeightFt)
}

/**
 * Represents a deduction from wall area (Door, Window, Ventilator, Opening, Other).
 * Supports feet and inches for deduction dimensions.
 */
data class WallDeductionEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Door", // Door, Window, Ventilator, Opening, Other
    val isDirectArea: Boolean = false,
    val lengthFt: Double = 3.0,
    val lengthIn: Double = 0.0,
    val heightFt: Double = 7.0,
    val heightIn: Double = 0.0,
    val count: Int = 1,
    val directAreaSqft: Double = 0.0
) {
    val effectiveLengthFt: Double
        get() = lengthFt + (lengthIn / 12.0)

    val effectiveHeightFt: Double
        get() = heightFt + (heightIn / 12.0)

    val deductionAreaSqft: Double
        get() = if (isDirectArea) directAreaSqft else count * (effectiveLengthFt * effectiveHeightFt)
}

/**
 * Represents a ceiling measurement line (Length x Width or Direct Sqft).
 * Supports feet and inches (e.g. 14 ft 6 in x 12 ft 0 in).
 */
data class CeilingMeasurementEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String = "Ceiling",
    val isDirectArea: Boolean = false,
    val lengthFt: Double = 0.0,
    val lengthIn: Double = 0.0,
    val widthFt: Double = 0.0,
    val widthIn: Double = 0.0,
    val directAreaSqft: Double = 0.0
) {
    val effectiveLengthFt: Double
        get() = lengthFt + (lengthIn / 12.0)

    val effectiveWidthFt: Double
        get() = widthFt + (widthIn / 12.0)

    val grossAreaSqft: Double
        get() = if (isDirectArea) directAreaSqft else effectiveLengthFt * effectiveWidthFt
}

/**
 * Represents a deduction from ceiling area (Cutout, Skylight, Opening, Shaft, Other).
 * Supports feet and inches for deduction dimensions.
 */
data class CeilingDeductionEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Cutout", // Cutout, Skylight, Opening, Shaft, Other
    val isDirectArea: Boolean = false,
    val lengthFt: Double = 0.0,
    val lengthIn: Double = 0.0,
    val widthFt: Double = 0.0,
    val widthIn: Double = 0.0,
    val directAreaSqft: Double = 0.0
) {
    val effectiveLengthFt: Double
        get() = lengthFt + (lengthIn / 12.0)

    val effectiveWidthFt: Double
        get() = widthFt + (widthIn / 12.0)

    val deductionAreaSqft: Double
        get() = if (isDirectArea) directAreaSqft else effectiveLengthFt * effectiveWidthFt
}

/**
 * Paint surface selection configuration (Brand, Product type, Variant, Coats, Rates, Remarks).
 */
data class SurfacePaintConfig(
    val brand: String = "Asian Paints",
    val productType: String = "Interior Emulsion",
    val productVariant: String = "",
    val coats: Int = 2,
    val materialRate: Double = 14.0,
    val labourRate: Double = 12.0,
    val remarks: String = ""
) {
    val totalRate: Double
        get() = materialRate + labourRate

    fun getEffectiveRate(isLabourOnly: Boolean): Double {
        return if (isLabourOnly) labourRate else totalRate
    }

    fun calculateAmount(netAreaSqft: Double, isLabourOnly: Boolean): Double {
        return netAreaSqft * getEffectiveRate(isLabourOnly)
    }
}

/**
 * Putty and Primer specification per surface (Wall Putty, Ceiling Putty, Wall Primer, Ceiling Primer).
 */
data class SurfacePrepConfig(
    val enabled: Boolean = false,
    val title: String = "Putty", // e.g. "Wall Putty", "Ceiling Putty", "Wall Primer", "Ceiling Primer"
    val brand: String = "Birla White",
    val product: String = "WallCare Putty",
    val coats: Int = 2,
    val customAreaSqft: Double = 0.0, // 0.0 means default to surface net area
    val materialRate: Double = 7.0,
    val labourRate: Double = 9.0,
    val remarks: String = ""
) {
    val totalRate: Double
        get() = materialRate + labourRate

    fun getEffectiveRate(isLabourOnly: Boolean): Double {
        return if (isLabourOnly) labourRate else totalRate
    }

    fun getEffectiveArea(surfaceNetArea: Double): Double {
        return if (customAreaSqft > 0.0) customAreaSqft else surfaceNetArea
    }

    fun calculateAmount(surfaceNetArea: Double, isLabourOnly: Boolean): Double {
        if (!enabled) return 0.0
        val area = getEffectiveArea(surfaceNetArea)
        return area * getEffectiveRate(isLabourOnly)
    }
}

/**
 * JSON serialization helper for detailed Room measurements and selections.
 */
object RoomDetailsJsonConverter {

    fun serialize(
        wallEntries: List<WallMeasurementEntry>,
        wallDeductions: List<WallDeductionEntry>,
        wallPaint: SurfacePaintConfig,
        wallPutty: SurfacePrepConfig,
        wallPrimer: SurfacePrepConfig,
        ceilingEntries: List<CeilingMeasurementEntry>,
        ceilingDeductions: List<CeilingDeductionEntry>,
        ceilingPaint: SurfacePaintConfig,
        ceilingPutty: SurfacePrepConfig,
        ceilingPrimer: SurfacePrepConfig
    ): String {
        val root = JSONObject()

        val wallEntriesArr = JSONArray()
        wallEntries.forEach { w ->
            val obj = JSONObject()
            obj.put("id", w.id)
            obj.put("label", w.label)
            obj.put("isDirectArea", w.isDirectArea)
            obj.put("lengthFt", w.lengthFt)
            obj.put("lengthIn", w.lengthIn)
            obj.put("heightFt", w.heightFt)
            obj.put("heightIn", w.heightIn)
            obj.put("wallCount", w.wallCount)
            obj.put("directAreaSqft", w.directAreaSqft)
            wallEntriesArr.put(obj)
        }
        root.put("wallEntries", wallEntriesArr)

        val wallDedArr = JSONArray()
        wallDeductions.forEach { d ->
            val obj = JSONObject()
            obj.put("id", d.id)
            obj.put("name", d.name)
            obj.put("isDirectArea", d.isDirectArea)
            obj.put("lengthFt", d.lengthFt)
            obj.put("lengthIn", d.lengthIn)
            obj.put("heightFt", d.heightFt)
            obj.put("heightIn", d.heightIn)
            obj.put("count", d.count)
            obj.put("directAreaSqft", d.directAreaSqft)
            wallDedArr.put(obj)
        }
        root.put("wallDeductions", wallDedArr)

        root.put("wallPaint", JSONObject().apply {
            put("brand", wallPaint.brand)
            put("productType", wallPaint.productType)
            put("productVariant", wallPaint.productVariant)
            put("coats", wallPaint.coats)
            put("materialRate", wallPaint.materialRate)
            put("labourRate", wallPaint.labourRate)
            put("remarks", wallPaint.remarks)
        })

        root.put("wallPutty", JSONObject().apply {
            put("enabled", wallPutty.enabled)
            put("title", wallPutty.title)
            put("brand", wallPutty.brand)
            put("product", wallPutty.product)
            put("coats", wallPutty.coats)
            put("customAreaSqft", wallPutty.customAreaSqft)
            put("materialRate", wallPutty.materialRate)
            put("labourRate", wallPutty.labourRate)
            put("remarks", wallPutty.remarks)
        })

        root.put("wallPrimer", JSONObject().apply {
            put("enabled", wallPrimer.enabled)
            put("title", wallPrimer.title)
            put("brand", wallPrimer.brand)
            put("product", wallPrimer.product)
            put("coats", wallPrimer.coats)
            put("customAreaSqft", wallPrimer.customAreaSqft)
            put("materialRate", wallPrimer.materialRate)
            put("labourRate", wallPrimer.labourRate)
            put("remarks", wallPrimer.remarks)
        })

        val ceilEntriesArr = JSONArray()
        ceilingEntries.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("label", c.label)
            obj.put("isDirectArea", c.isDirectArea)
            obj.put("lengthFt", c.lengthFt)
            obj.put("lengthIn", c.lengthIn)
            obj.put("widthFt", c.widthFt)
            obj.put("widthIn", c.widthIn)
            obj.put("directAreaSqft", c.directAreaSqft)
            ceilEntriesArr.put(obj)
        }
        root.put("ceilingEntries", ceilEntriesArr)

        val ceilDedArr = JSONArray()
        ceilingDeductions.forEach { cd ->
            val obj = JSONObject()
            obj.put("id", cd.id)
            obj.put("name", cd.name)
            obj.put("isDirectArea", cd.isDirectArea)
            obj.put("lengthFt", cd.lengthFt)
            obj.put("lengthIn", cd.lengthIn)
            obj.put("widthFt", cd.widthFt)
            obj.put("widthIn", cd.widthIn)
            obj.put("directAreaSqft", cd.directAreaSqft)
            ceilDedArr.put(obj)
        }
        root.put("ceilingDeductions", ceilDedArr)

        root.put("ceilingPaint", JSONObject().apply {
            put("brand", ceilingPaint.brand)
            put("productType", ceilingPaint.productType)
            put("productVariant", ceilingPaint.productVariant)
            put("coats", ceilingPaint.coats)
            put("materialRate", ceilingPaint.materialRate)
            put("labourRate", ceilingPaint.labourRate)
            put("remarks", ceilingPaint.remarks)
        })

        root.put("ceilingPutty", JSONObject().apply {
            put("enabled", ceilingPutty.enabled)
            put("title", ceilingPutty.title)
            put("brand", ceilingPutty.brand)
            put("product", ceilingPutty.product)
            put("coats", ceilingPutty.coats)
            put("customAreaSqft", ceilingPutty.customAreaSqft)
            put("materialRate", ceilingPutty.materialRate)
            put("labourRate", ceilingPutty.labourRate)
            put("remarks", ceilingPutty.remarks)
        })

        root.put("ceilingPrimer", JSONObject().apply {
            put("enabled", ceilingPrimer.enabled)
            put("title", ceilingPrimer.title)
            put("brand", ceilingPrimer.brand)
            put("product", ceilingPrimer.product)
            put("coats", ceilingPrimer.coats)
            put("customAreaSqft", ceilingPrimer.customAreaSqft)
            put("materialRate", ceilingPrimer.materialRate)
            put("labourRate", ceilingPrimer.labourRate)
            put("remarks", ceilingPrimer.remarks)
        })

        return root.toString()
    }

    data class ParsedRoomDetails(
        val wallEntries: List<WallMeasurementEntry>,
        val wallDeductions: List<WallDeductionEntry>,
        val wallPaint: SurfacePaintConfig,
        val wallPutty: SurfacePrepConfig,
        val wallPrimer: SurfacePrepConfig,
        val ceilingEntries: List<CeilingMeasurementEntry>,
        val ceilingDeductions: List<CeilingDeductionEntry>,
        val ceilingPaint: SurfacePaintConfig,
        val ceilingPutty: SurfacePrepConfig,
        val ceilingPrimer: SurfacePrepConfig
    )

    fun deserialize(jsonStr: String?, fallbackRoom: RoomMeasurementEntity): ParsedRoomDetails {
        if (jsonStr.isNullOrBlank()) {
            return buildFallbackDetails(fallbackRoom)
        }

        return try {
            val root = JSONObject(jsonStr)

            val wallEntries = mutableListOf<WallMeasurementEntry>()
            val wallArr = root.optJSONArray("wallEntries")
            if (wallArr != null) {
                for (i in 0 until wallArr.length()) {
                    val obj = wallArr.getJSONObject(i)
                    wallEntries.add(
                        WallMeasurementEntry(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            label = obj.optString("label", "Wall ${i + 1}"),
                            isDirectArea = obj.optBoolean("isDirectArea", false),
                            lengthFt = obj.optDouble("lengthFt", 0.0),
                            lengthIn = obj.optDouble("lengthIn", 0.0),
                            heightFt = obj.optDouble("heightFt", 10.0),
                            heightIn = obj.optDouble("heightIn", 0.0),
                            wallCount = obj.optInt("wallCount", 1),
                            directAreaSqft = obj.optDouble("directAreaSqft", 0.0)
                        )
                    )
                }
            }
            if (wallEntries.isEmpty()) {
                wallEntries.addAll(buildFallbackWallEntries(fallbackRoom))
            }

            val wallDeductions = mutableListOf<WallDeductionEntry>()
            val wallDedArr = root.optJSONArray("wallDeductions")
            if (wallDedArr != null) {
                for (i in 0 until wallDedArr.length()) {
                    val obj = wallDedArr.getJSONObject(i)
                    wallDeductions.add(
                        WallDeductionEntry(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            name = obj.optString("name", "Opening"),
                            isDirectArea = obj.optBoolean("isDirectArea", false),
                            lengthFt = obj.optDouble("lengthFt", 0.0),
                            lengthIn = obj.optDouble("lengthIn", 0.0),
                            heightFt = obj.optDouble("heightFt", 0.0),
                            heightIn = obj.optDouble("heightIn", 0.0),
                            count = obj.optInt("count", 1),
                            directAreaSqft = obj.optDouble("directAreaSqft", 0.0)
                        )
                    )
                }
            } else {
                wallDeductions.addAll(buildFallbackWallDeductions(fallbackRoom))
            }

            val wallPaint = parseSurfacePaint(
                root.optJSONObject("wallPaint"),
                defaultBrand = "Asian Paints",
                defaultProduct = "Interior Emulsion",
                defaultMatRate = 16.0,
                defaultLabRate = 12.0
            )

            val wallPutty = parseSurfacePrep(
                root.optJSONObject("wallPutty"),
                title = "Wall Putty",
                defaultBrand = "Birla White",
                defaultProduct = "WallCare Putty",
                defaultMatRate = 7.0,
                defaultLabRate = 9.0
            )

            val wallPrimer = parseSurfacePrep(
                root.optJSONObject("wallPrimer"),
                title = "Wall Primer",
                defaultBrand = "Asian Paints",
                defaultProduct = "Decoprime Wall Primer",
                defaultMatRate = 4.5,
                defaultLabRate = 4.0
            )

            val ceilEntries = mutableListOf<CeilingMeasurementEntry>()
            val ceilArr = root.optJSONArray("ceilingEntries")
            if (ceilArr != null) {
                for (i in 0 until ceilArr.length()) {
                    val obj = ceilArr.getJSONObject(i)
                    ceilEntries.add(
                        CeilingMeasurementEntry(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            label = obj.optString("label", "Ceiling ${i + 1}"),
                            isDirectArea = obj.optBoolean("isDirectArea", false),
                            lengthFt = obj.optDouble("lengthFt", 0.0),
                            lengthIn = obj.optDouble("lengthIn", 0.0),
                            widthFt = obj.optDouble("widthFt", 0.0),
                            widthIn = obj.optDouble("widthIn", 0.0),
                            directAreaSqft = obj.optDouble("directAreaSqft", 0.0)
                        )
                    )
                }
            }
            if (ceilEntries.isEmpty()) {
                ceilEntries.addAll(buildFallbackCeilingEntries(fallbackRoom))
            }

            val ceilDeductions = mutableListOf<CeilingDeductionEntry>()
            val ceilDedArr = root.optJSONArray("ceilingDeductions")
            if (ceilDedArr != null) {
                for (i in 0 until ceilDedArr.length()) {
                    val obj = ceilDedArr.getJSONObject(i)
                    ceilDeductions.add(
                        CeilingDeductionEntry(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            name = obj.optString("name", "Opening"),
                            isDirectArea = obj.optBoolean("isDirectArea", false),
                            lengthFt = obj.optDouble("lengthFt", 0.0),
                            lengthIn = obj.optDouble("lengthIn", 0.0),
                            widthFt = obj.optDouble("widthFt", 0.0),
                            widthIn = obj.optDouble("widthIn", 0.0),
                            directAreaSqft = obj.optDouble("directAreaSqft", 0.0)
                        )
                    )
                }
            }

            val ceilingPaint = parseSurfacePaint(
                root.optJSONObject("ceilingPaint"),
                defaultBrand = "Asian Paints",
                defaultProduct = "Ceiling White",
                defaultMatRate = 12.0,
                defaultLabRate = 10.0
            )

            val ceilingPutty = parseSurfacePrep(
                root.optJSONObject("ceilingPutty"),
                title = "Ceiling Putty",
                defaultBrand = "Birla White",
                defaultProduct = "WallCare Putty",
                defaultMatRate = 7.0,
                defaultLabRate = 9.0
            )

            val ceilingPrimer = parseSurfacePrep(
                root.optJSONObject("ceilingPrimer"),
                title = "Ceiling Primer",
                defaultBrand = "Asian Paints",
                defaultProduct = "Decoprime Wall Primer",
                defaultMatRate = 4.5,
                defaultLabRate = 4.0
            )

            ParsedRoomDetails(
                wallEntries = wallEntries,
                wallDeductions = wallDeductions,
                wallPaint = wallPaint,
                wallPutty = wallPutty,
                wallPrimer = wallPrimer,
                ceilingEntries = ceilEntries,
                ceilingDeductions = ceilDeductions,
                ceilingPaint = ceilingPaint,
                ceilingPutty = ceilingPutty,
                ceilingPrimer = ceilingPrimer
            )
        } catch (_: Exception) {
            buildFallbackDetails(fallbackRoom)
        }
    }

    private fun parseSurfacePaint(
        obj: JSONObject?,
        defaultBrand: String,
        defaultProduct: String,
        defaultMatRate: Double,
        defaultLabRate: Double
    ): SurfacePaintConfig {
        if (obj == null) {
            return SurfacePaintConfig(
                brand = defaultBrand,
                productType = defaultProduct,
                materialRate = defaultMatRate,
                labourRate = defaultLabRate
            )
        }
        return SurfacePaintConfig(
            brand = obj.optString("brand", defaultBrand),
            productType = obj.optString("productType", defaultProduct),
            productVariant = obj.optString("productVariant", ""),
            coats = obj.optInt("coats", 2),
            materialRate = obj.optDouble("materialRate", defaultMatRate),
            labourRate = obj.optDouble("labourRate", defaultLabRate),
            remarks = obj.optString("remarks", "")
        )
    }

    private fun parseSurfacePrep(
        obj: JSONObject?,
        title: String,
        defaultBrand: String,
        defaultProduct: String,
        defaultMatRate: Double,
        defaultLabRate: Double
    ): SurfacePrepConfig {
        if (obj == null) {
            return SurfacePrepConfig(
                enabled = false,
                title = title,
                brand = defaultBrand,
                product = defaultProduct,
                materialRate = defaultMatRate,
                labourRate = defaultLabRate
            )
        }
        return SurfacePrepConfig(
            enabled = obj.optBoolean("enabled", false),
            title = obj.optString("title", title),
            brand = obj.optString("brand", defaultBrand),
            product = obj.optString("product", defaultProduct),
            coats = obj.optInt("coats", 2),
            customAreaSqft = obj.optDouble("customAreaSqft", 0.0),
            materialRate = obj.optDouble("materialRate", defaultMatRate),
            labourRate = obj.optDouble("labourRate", defaultLabRate),
            remarks = obj.optString("remarks", "")
        )
    }

    private fun buildFallbackWallEntries(room: RoomMeasurementEntity): List<WallMeasurementEntry> {
        val lFt = room.lengthFt
        val lIn = room.lengthIn
        val wFt = room.widthFt
        val wIn = room.widthIn
        val hFt = room.heightFt
        val hIn = room.heightIn
        return if (room.effectiveLength > 0 && room.effectiveWidth > 0 && room.effectiveHeight > 0) {
            listOf(
                WallMeasurementEntry(label = "Wall 1 & 2 (Length sides)", lengthFt = lFt, lengthIn = lIn, heightFt = hFt, heightIn = hIn, wallCount = 2),
                WallMeasurementEntry(label = "Wall 3 & 4 (Width sides)", lengthFt = wFt, lengthIn = wIn, heightFt = hFt, heightIn = hIn, wallCount = 2)
            )
        } else {
            listOf(WallMeasurementEntry(label = "Wall 1", lengthFt = 12.0, lengthIn = 0.0, heightFt = 10.0, heightIn = 0.0, wallCount = 4))
        }
    }

    private fun buildFallbackWallDeductions(room: RoomMeasurementEntity): List<WallDeductionEntry> {
        val list = mutableListOf<WallDeductionEntry>()
        if (room.doorCount > 0 && room.doorWidthFt > 0 && room.doorHeightFt > 0) {
            list.add(
                WallDeductionEntry(
                    name = "Door",
                    lengthFt = room.doorWidthFt,
                    lengthIn = 0.0,
                    heightFt = room.doorHeightFt,
                    heightIn = 0.0,
                    count = room.doorCount
                )
            )
        }
        if (room.windowCount > 0 && room.windowWidthFt > 0 && room.windowHeightFt > 0) {
            list.add(
                WallDeductionEntry(
                    name = "Window",
                    lengthFt = room.windowWidthFt,
                    lengthIn = 0.0,
                    heightFt = room.windowHeightFt,
                    heightIn = 0.0,
                    count = room.windowCount
                )
            )
        }
        if (room.additionalDeductionsSqft > 0) {
            list.add(
                WallDeductionEntry(
                    name = "Other",
                    isDirectArea = true,
                    directAreaSqft = room.additionalDeductionsSqft
                )
            )
        }
        return list
    }

    private fun buildFallbackCeilingEntries(room: RoomMeasurementEntity): List<CeilingMeasurementEntry> {
        val lFt = room.lengthFt
        val lIn = room.lengthIn
        val wFt = room.widthFt
        val wIn = room.widthIn
        return if (room.effectiveLength > 0 && room.effectiveWidth > 0) {
            listOf(CeilingMeasurementEntry(label = "Ceiling Area", lengthFt = lFt, lengthIn = lIn, widthFt = wFt, widthIn = wIn))
        } else {
            listOf(CeilingMeasurementEntry(label = "Ceiling Area", lengthFt = 12.0, lengthIn = 0.0, widthFt = 10.0, widthIn = 0.0))
        }
    }

    private fun buildFallbackDetails(room: RoomMeasurementEntity): ParsedRoomDetails {
        return ParsedRoomDetails(
            wallEntries = buildFallbackWallEntries(room),
            wallDeductions = buildFallbackWallDeductions(room),
            wallPaint = SurfacePaintConfig(
                brand = "Asian Paints",
                productType = "Interior Emulsion",
                productVariant = "Royale Luxury",
                coats = 2,
                materialRate = 16.0,
                labourRate = 12.0
            ),
            wallPutty = SurfacePrepConfig(
                enabled = true,
                title = "Wall Putty",
                brand = "Birla White",
                product = "WallCare Putty",
                coats = 2,
                materialRate = 7.0,
                labourRate = 9.0
            ),
            wallPrimer = SurfacePrepConfig(
                enabled = true,
                title = "Wall Primer",
                brand = "Asian Paints",
                product = "Decoprime Wall Primer",
                coats = 1,
                materialRate = 4.5,
                labourRate = 4.0
            ),
            ceilingEntries = buildFallbackCeilingEntries(room),
            ceilingDeductions = emptyList(),
            ceilingPaint = SurfacePaintConfig(
                brand = "Asian Paints",
                productType = "Ceiling White",
                productVariant = "Tractor Emulsion",
                coats = 2,
                materialRate = 12.0,
                labourRate = 10.0
            ),
            ceilingPutty = SurfacePrepConfig(
                enabled = false,
                title = "Ceiling Putty",
                brand = "Birla White",
                product = "WallCare Putty",
                coats = 2,
                materialRate = 7.0,
                labourRate = 9.0
            ),
            ceilingPrimer = SurfacePrepConfig(
                enabled = true,
                title = "Ceiling Primer",
                brand = "Asian Paints",
                product = "Decoprime Wall Primer",
                coats = 1,
                materialRate = 4.5,
                labourRate = 4.0
            )
        )
    }
}
