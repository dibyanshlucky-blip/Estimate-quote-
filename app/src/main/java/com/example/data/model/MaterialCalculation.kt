package com.example.data.model

import kotlin.math.ceil

data class MaterialItemEstimate(
    val name: String,
    val specification: String,
    val totalQuantity: Double,
    val unit: String,
    val packagingBreakdown: String,
    val approxRatePerUnit: Double,
    val estimatedCost: Double
)

data class SiteMaterialEstimation(
    val wallAreaSqft: Double,
    val ceilingAreaSqft: Double,
    val otherAreaSqft: Double,
    val items: List<MaterialItemEstimate>
) {
    val totalMaterialCost: Double
        get() = items.sumOf { it.estimatedCost }

    companion object {
        fun calculate(wallSqft: Double, ceilingSqft: Double, grillDoorSqft: Double = 120.0): SiteMaterialEstimation {
            val totalSurface = wallSqft + ceilingSqft

            // 1. Wall Putty (2 coats @ 15 sqft/kg)
            val puttyKg = ceil(wallSqft / 15.0)
            val putty40kgBags = ceil(puttyKg / 40.0).toInt()
            val puttyCost = putty40kgBags * 950.0 // ~₹950 per 40kg bag (Birla White / Asian Paints)

            // 2. Interior Primer (1 coat @ 130 sqft/L)
            val primerLitre = ceil(totalSurface / 130.0)
            val primer20LDrums = (primerLitre / 20).toInt()
            val primerRemaining = (primerLitre % 20).toInt()
            val primerCost = primerLitre * 140.0 // ~₹140 per Litre

            // 3. Wall Paint Emulsion (2 coats @ 65 sqft/L)
            val wallPaintLitre = ceil(wallSqft / 65.0)
            val wallPaint20L = (wallPaintLitre / 20).toInt()
            val wallPaintRem = (wallPaintLitre % 20).toInt()
            val wallPaintCost = wallPaintLitre * 320.0 // ~₹320/L average premium emulsion

            // 4. Ceiling Paint (White Emulsion - 2 coats @ 65 sqft/L)
            val ceilPaintLitre = ceil(ceilingSqft / 65.0)
            val ceilPaintCost = ceilPaintLitre * 220.0 // ~₹220/L Tractor white

            // 5. Enamel Paint (Doors/Grills - 2 coats @ 80 sqft/L)
            val enamelLitre = ceil(grillDoorSqft / 80.0)
            val enamelCost = enamelLitre * 350.0

            // 6. Sandpaper (80 & 120 grit emery paper)
            val sandpaperSheets = ceil(totalSurface / 80.0).toInt().coerceAtLeast(10)
            val sandpaperCost = sandpaperSheets * 15.0

            // 7. Masking Tape Rolls
            val tapeRolls = ceil(totalSurface / 350.0).toInt().coerceAtLeast(4)
            val tapeCost = tapeRolls * 60.0

            // 8. Paint Rollers & Brushes
            val rollers = ceil(totalSurface / 800.0).toInt().coerceAtLeast(2)
            val brushes = ceil(totalSurface / 600.0).toInt().coerceAtLeast(2)
            val toolCost = (rollers * 180.0) + (brushes * 90.0)

            val itemList = listOf(
                MaterialItemEstimate(
                    name = "Wall Putty (2 Coats)",
                    specification = "Birla White / Asian Paints Acrylic Putty",
                    totalQuantity = puttyKg,
                    unit = "kg",
                    packagingBreakdown = "$putty40kgBags × 40kg Bags",
                    approxRatePerUnit = 23.75,
                    estimatedCost = puttyCost
                ),
                MaterialItemEstimate(
                    name = "Interior Wall Primer",
                    specification = "Asian Paints Decoprime (Water thinnable)",
                    totalQuantity = primerLitre,
                    unit = "Litre",
                    packagingBreakdown = if (primer20LDrums > 0) "$primer20LDrums × 20L + ${primerRemaining}L" else "${primerLitre.toInt()} Litres",
                    approxRatePerUnit = 140.0,
                    estimatedCost = primerCost
                ),
                MaterialItemEstimate(
                    name = "Wall Topcoat Emulsion",
                    specification = "Asian Paints Royale / Apcolite Premium",
                    totalQuantity = wallPaintLitre,
                    unit = "Litre",
                    packagingBreakdown = if (wallPaint20L > 0) "$wallPaint20L × 20L + ${wallPaintRem}L" else "${wallPaintLitre.toInt()} Litres",
                    approxRatePerUnit = 320.0,
                    estimatedCost = wallPaintCost
                ),
                MaterialItemEstimate(
                    name = "Ceiling White Paint",
                    specification = "Asian Paints Tractor Emulsion Super White",
                    totalQuantity = ceilPaintLitre,
                    unit = "Litre",
                    packagingBreakdown = "${ceilPaintLitre.toInt()} Litres",
                    approxRatePerUnit = 220.0,
                    estimatedCost = ceilPaintCost
                ),
                MaterialItemEstimate(
                    name = "Gloss Enamel (Grills & Doors)",
                    specification = "Asian Paints Apcolite Premium Gloss Enamel",
                    totalQuantity = enamelLitre,
                    unit = "Litre",
                    packagingBreakdown = "${enamelLitre.toInt()} Litres",
                    approxRatePerUnit = 350.0,
                    estimatedCost = enamelCost
                ),
                MaterialItemEstimate(
                    name = "Waterproof Sandpaper",
                    specification = "80 Grit (Rough) & 120 Grit (Fine) Emery Sheets",
                    totalQuantity = sandpaperSheets.toDouble(),
                    unit = "Sheets",
                    packagingBreakdown = "$sandpaperSheets Sheets",
                    approxRatePerUnit = 15.0,
                    estimatedCost = sandpaperCost
                ),
                MaterialItemEstimate(
                    name = "Masking Tape (1-inch)",
                    specification = "Edge protection & floor switch masking",
                    totalQuantity = tapeRolls.toDouble(),
                    unit = "Rolls",
                    packagingBreakdown = "$tapeRolls Rolls",
                    approxRatePerUnit = 60.0,
                    estimatedCost = tapeCost
                ),
                MaterialItemEstimate(
                    name = "Rollers & Brushes",
                    specification = "9-inch Microfiber Rollers & 3-4 inch Brushes",
                    totalQuantity = (rollers + brushes).toDouble(),
                    unit = "Pieces",
                    packagingBreakdown = "$rollers Rollers + $brushes Brushes",
                    approxRatePerUnit = 135.0,
                    estimatedCost = toolCost
                )
            )

            return SiteMaterialEstimation(
                wallAreaSqft = wallSqft,
                ceilingAreaSqft = ceilingSqft,
                otherAreaSqft = grillDoorSqft,
                items = itemList
            )
        }
    }
}
