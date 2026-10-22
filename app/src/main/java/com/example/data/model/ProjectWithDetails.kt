package com.example.data.model

data class ProjectWithDetails(
    val project: ProjectEntity,
    val rooms: List<RoomMeasurementEntity> = emptyList(),
    val directMeasurements: List<DirectMeasurementEntity> = emptyList(),
    val estimateItems: List<EstimateItemEntity> = emptyList(),
    val payments: List<PaymentEntity> = emptyList(),
    val woodPolishItems: List<WoodPolishEntity> = emptyList()
) {
    // Wood & Polish rollups
    val totalWoodPolishFinalAreaSqft: Double
        get() = woodPolishItems.sumOf { it.finalBillableAreaSqft }

    val totalDoorPolishAreaSqft: Double
        get() = woodPolishItems.filter { it.category == "DOOR" }.sumOf { it.finalBillableAreaSqft }

    val totalWindowPolishAreaSqft: Double
        get() = woodPolishItems.filter { it.category == "WINDOW" }.sumOf { it.finalBillableAreaSqft }

    val totalInteriorPolishAreaSqft: Double
        get() = woodPolishItems.filter { it.category == "INTERIOR" }.sumOf { it.finalBillableAreaSqft }

    val totalWoodPolishLabourCost: Double
        get() = woodPolishItems.sumOf { it.labourAmount }

    val totalWoodPolishMaterialCost: Double
        get() = woodPolishItems.sumOf { it.materialAmount }

    val totalWoodPolishAmount: Double
        get() = woodPolishItems.sumOf { it.getEffectiveAmount(isLabourOnlyMode) }

    // Room calculations
    val totalRoomPaintableAreaSqft: Double
        get() = rooms.sumOf { it.getTotalPaintableAreaSqft(project.deductOpenings) }

    val totalRoomCeilingAreaSqft: Double
        get() = rooms.filter { it.ceilingPaintingEnabled }.sumOf { it.finalCeilingAreaSqft }

    val totalRoomNetWallAreaSqft: Double
        get() = rooms.filter { it.wallPaintingEnabled }.sumOf { it.finalWallAreaSqft }

    // Direct sqft calculations
    val totalDirectSqft: Double
        get() = directMeasurements.sumOf { it.calculatedSqft }

    // Grand total area
    val combinedPaintableSqft: Double
        get() = totalRoomPaintableAreaSqft + totalDirectSqft

    // Room-wise Detailed Surface Calculations
    val totalGrossWallAreaSqft: Double
        get() = rooms.sumOf { it.grossWallAreaSqft }

    val totalWallDeductionSqft: Double
        get() = rooms.sumOf { it.totalDeductionsSqft }

    val totalGrossCeilingAreaSqft: Double
        get() = rooms.sumOf { it.grossCeilingAreaSqft }

    val totalCeilingDeductionSqft: Double
        get() = rooms.sumOf { it.totalCeilingDeductionsSqft }

    val isLabourOnlyMode: Boolean
        get() = project.estimateMode == "LABOUR_ONLY"

    // Detailed Room-wise cost rollups
    val totalRoomWallPaintAmount: Double
        get() = rooms.filter { it.wallPaintingEnabled }.sumOf { r ->
            val details = r.getParsedDetails()
            details.wallPaint.calculateAmount(r.finalWallAreaSqft, isLabourOnlyMode)
        }

    val totalRoomCeilingPaintAmount: Double
        get() = rooms.filter { it.ceilingPaintingEnabled }.sumOf { r ->
            val details = r.getParsedDetails()
            details.ceilingPaint.calculateAmount(r.finalCeilingAreaSqft, isLabourOnlyMode)
        }

    val totalRoomPuttyCost: Double
        get() = rooms.sumOf { r ->
            val details = r.getParsedDetails()
            val wallFinal = if (r.wallPaintingEnabled) r.finalWallAreaSqft else 0.0
            val ceilFinal = if (r.ceilingPaintingEnabled) r.finalCeilingAreaSqft else 0.0
            val wPutty = details.wallPutty.calculateAmount(wallFinal, isLabourOnlyMode)
            val cPutty = details.ceilingPutty.calculateAmount(ceilFinal, isLabourOnlyMode)
            wPutty + cPutty
        }

    val totalRoomPrimerCost: Double
        get() = rooms.sumOf { r ->
            val details = r.getParsedDetails()
            val wallFinal = if (r.wallPaintingEnabled) r.finalWallAreaSqft else 0.0
            val ceilFinal = if (r.ceilingPaintingEnabled) r.finalCeilingAreaSqft else 0.0
            val wPrimer = details.wallPrimer.calculateAmount(wallFinal, isLabourOnlyMode)
            val cPrimer = details.ceilingPrimer.calculateAmount(ceilFinal, isLabourOnlyMode)
            wPrimer + cPrimer
        }

    val totalRoomMaterialCost: Double
        get() = rooms.sumOf { r ->
            val details = r.getParsedDetails()
            val wallFinal = if (r.wallPaintingEnabled) r.finalWallAreaSqft else 0.0
            val ceilFinal = if (r.ceilingPaintingEnabled) r.finalCeilingAreaSqft else 0.0
            val wallMat = wallFinal * details.wallPaint.materialRate
            val ceilMat = ceilFinal * details.ceilingPaint.materialRate
            val wPuttyMat = if (details.wallPutty.enabled) details.wallPutty.getEffectiveArea(wallFinal) * details.wallPutty.materialRate else 0.0
            val cPuttyMat = if (details.ceilingPutty.enabled) details.ceilingPutty.getEffectiveArea(ceilFinal) * details.ceilingPutty.materialRate else 0.0
            val wPrimerMat = if (details.wallPrimer.enabled) details.wallPrimer.getEffectiveArea(wallFinal) * details.wallPrimer.materialRate else 0.0
            val cPrimerMat = if (details.ceilingPrimer.enabled) details.ceilingPrimer.getEffectiveArea(ceilFinal) * details.ceilingPrimer.materialRate else 0.0
            wallMat + ceilMat + wPuttyMat + cPuttyMat + wPrimerMat + cPrimerMat
        }

    val totalRoomLabourCost: Double
        get() = rooms.sumOf { r ->
            val details = r.getParsedDetails()
            val wallFinal = if (r.wallPaintingEnabled) r.finalWallAreaSqft else 0.0
            val ceilFinal = if (r.ceilingPaintingEnabled) r.finalCeilingAreaSqft else 0.0
            val wallLab = wallFinal * details.wallPaint.labourRate
            val ceilLab = ceilFinal * details.ceilingPaint.labourRate
            val wPuttyLab = if (details.wallPutty.enabled) details.wallPutty.getEffectiveArea(wallFinal) * details.wallPutty.labourRate else 0.0
            val cPuttyLab = if (details.ceilingPutty.enabled) details.ceilingPutty.getEffectiveArea(ceilFinal) * details.ceilingPutty.labourRate else 0.0
            val wPrimerLab = if (details.wallPrimer.enabled) details.wallPrimer.getEffectiveArea(wallFinal) * details.wallPrimer.labourRate else 0.0
            val cPrimerLab = if (details.ceilingPrimer.enabled) details.ceilingPrimer.getEffectiveArea(ceilFinal) * details.ceilingPrimer.labourRate else 0.0
            wallLab + ceilLab + wPuttyLab + cPuttyLab + wPrimerLab + cPrimerLab
        }

    val totalRoomDetailedAmount: Double
        get() = if (isLabourOnlyMode) totalRoomLabourCost else (totalRoomMaterialCost + totalRoomLabourCost)

    // Financial calculations - Labour only
    val labourSubtotal: Double
        get() = if (estimateItems.isNotEmpty()) {
            estimateItems.sumOf { it.labourAmount } + totalWoodPolishLabourCost
        } else {
            totalRoomLabourCost + totalWoodPolishLabourCost
        }

    val labourNetBeforeTax: Double
        get() = maxOf(0.0, labourSubtotal + project.additionalCharges - project.discountAmount)

    val labourTaxAmount: Double
        get() = if (project.isTaxEnabled) labourNetBeforeTax * (project.taxPercent / 100.0) else 0.0

    val labourGrandTotal: Double
        get() = labourNetBeforeTax + labourTaxAmount

    // Financial calculations - Material + Labour
    val materialSubtotal: Double
        get() = if (estimateItems.isNotEmpty()) {
            estimateItems.sumOf { it.materialAmount } + totalWoodPolishMaterialCost
        } else {
            totalRoomMaterialCost + totalWoodPolishMaterialCost
        }

    val materialAndLabourSubtotal: Double
        get() = if (estimateItems.isNotEmpty()) {
            materialSubtotal + labourSubtotal
        } else {
            totalRoomMaterialCost + totalRoomLabourCost + totalWoodPolishAmount
        }

    val materialAndLabourNetBeforeTax: Double
        get() = maxOf(0.0, materialAndLabourSubtotal + project.additionalCharges - project.discountAmount)

    val materialAndLabourTaxAmount: Double
        get() = if (project.isTaxEnabled) materialAndLabourNetBeforeTax * (project.taxPercent / 100.0) else 0.0

    val materialAndLabourGrandTotal: Double
        get() = materialAndLabourNetBeforeTax + materialAndLabourTaxAmount

    // Active accepted / current grand total based on selected estimate mode
    val currentGrandTotal: Double
        get() = when (project.estimateMode) {
            "LABOUR_ONLY" -> labourGrandTotal
            else -> materialAndLabourGrandTotal
        }

    val currentSubtotal: Double
        get() = when (project.estimateMode) {
            "LABOUR_ONLY" -> labourSubtotal
            else -> materialAndLabourSubtotal
        }

    val currentTaxAmount: Double
        get() = when (project.estimateMode) {
            "LABOUR_ONLY" -> labourTaxAmount
            else -> materialAndLabourTaxAmount
        }

    // Payments summary
    val totalPaid: Double
        get() = payments.sumOf { it.amount }

    val balancePayable: Double
        get() = maxOf(0.0, currentGrandTotal - totalPaid)
}
