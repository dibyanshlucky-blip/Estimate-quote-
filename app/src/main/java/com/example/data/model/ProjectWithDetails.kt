package com.example.data.model

data class ProjectWithDetails(
    val project: ProjectEntity,
    val rooms: List<RoomMeasurementEntity> = emptyList(),
    val directMeasurements: List<DirectMeasurementEntity> = emptyList(),
    val estimateItems: List<EstimateItemEntity> = emptyList(),
    val payments: List<PaymentEntity> = emptyList()
) {
    // Room calculations
    val totalRoomPaintableAreaSqft: Double
        get() = rooms.sumOf { it.getTotalPaintableAreaSqft(project.deductOpenings) }

    val totalRoomCeilingAreaSqft: Double
        get() = rooms.filter { it.ceilingPaintingEnabled }.sumOf { it.floorCeilingAreaSqft }

    val totalRoomNetWallAreaSqft: Double
        get() = rooms.filter { it.wallPaintingEnabled }.sumOf { it.getNetWallAreaSqft(project.deductOpenings) }

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
            val netWall = r.getNetWallAreaSqft(project.deductOpenings)
            details.wallPaint.calculateAmount(netWall, isLabourOnlyMode)
        }

    val totalRoomCeilingPaintAmount: Double
        get() = rooms.filter { it.ceilingPaintingEnabled }.sumOf { r ->
            val details = r.getParsedDetails()
            details.ceilingPaint.calculateAmount(r.netCeilingAreaSqft, isLabourOnlyMode)
        }

    val totalRoomPuttyCost: Double
        get() = rooms.sumOf { r ->
            val details = r.getParsedDetails()
            val wallNet = if (r.wallPaintingEnabled) r.getNetWallAreaSqft(project.deductOpenings) else 0.0
            val ceilNet = if (r.ceilingPaintingEnabled) r.netCeilingAreaSqft else 0.0
            val wPutty = details.wallPutty.calculateAmount(wallNet, isLabourOnlyMode)
            val cPutty = details.ceilingPutty.calculateAmount(ceilNet, isLabourOnlyMode)
            wPutty + cPutty
        }

    val totalRoomPrimerCost: Double
        get() = rooms.sumOf { r ->
            val details = r.getParsedDetails()
            val wallNet = if (r.wallPaintingEnabled) r.getNetWallAreaSqft(project.deductOpenings) else 0.0
            val ceilNet = if (r.ceilingPaintingEnabled) r.netCeilingAreaSqft else 0.0
            val wPrimer = details.wallPrimer.calculateAmount(wallNet, isLabourOnlyMode)
            val cPrimer = details.ceilingPrimer.calculateAmount(ceilNet, isLabourOnlyMode)
            wPrimer + cPrimer
        }

    val totalRoomMaterialCost: Double
        get() = rooms.sumOf { r ->
            val details = r.getParsedDetails()
            val wallNet = if (r.wallPaintingEnabled) r.getNetWallAreaSqft(project.deductOpenings) else 0.0
            val ceilNet = if (r.ceilingPaintingEnabled) r.netCeilingAreaSqft else 0.0
            val wallMat = wallNet * details.wallPaint.materialRate
            val ceilMat = ceilNet * details.ceilingPaint.materialRate
            val wPuttyMat = if (details.wallPutty.enabled) details.wallPutty.getEffectiveArea(wallNet) * details.wallPutty.materialRate else 0.0
            val cPuttyMat = if (details.ceilingPutty.enabled) details.ceilingPutty.getEffectiveArea(ceilNet) * details.ceilingPutty.materialRate else 0.0
            val wPrimerMat = if (details.wallPrimer.enabled) details.wallPrimer.getEffectiveArea(wallNet) * details.wallPrimer.materialRate else 0.0
            val cPrimerMat = if (details.ceilingPrimer.enabled) details.ceilingPrimer.getEffectiveArea(ceilNet) * details.ceilingPrimer.materialRate else 0.0
            wallMat + ceilMat + wPuttyMat + cPuttyMat + wPrimerMat + cPrimerMat
        }

    val totalRoomLabourCost: Double
        get() = rooms.sumOf { r ->
            val details = r.getParsedDetails()
            val wallNet = if (r.wallPaintingEnabled) r.getNetWallAreaSqft(project.deductOpenings) else 0.0
            val ceilNet = if (r.ceilingPaintingEnabled) r.netCeilingAreaSqft else 0.0
            val wallLab = wallNet * details.wallPaint.labourRate
            val ceilLab = ceilNet * details.ceilingPaint.labourRate
            val wPuttyLab = if (details.wallPutty.enabled) details.wallPutty.getEffectiveArea(wallNet) * details.wallPutty.labourRate else 0.0
            val cPuttyLab = if (details.ceilingPutty.enabled) details.ceilingPutty.getEffectiveArea(ceilNet) * details.ceilingPutty.labourRate else 0.0
            val wPrimerLab = if (details.wallPrimer.enabled) details.wallPrimer.getEffectiveArea(wallNet) * details.wallPrimer.labourRate else 0.0
            val cPrimerLab = if (details.ceilingPrimer.enabled) details.ceilingPrimer.getEffectiveArea(ceilNet) * details.ceilingPrimer.labourRate else 0.0
            wallLab + ceilLab + wPuttyLab + cPuttyLab + wPrimerLab + cPrimerLab
        }

    val totalRoomDetailedAmount: Double
        get() = if (isLabourOnlyMode) totalRoomLabourCost else (totalRoomMaterialCost + totalRoomLabourCost)

    // Financial calculations - Labour only
    val labourSubtotal: Double
        get() = if (estimateItems.isNotEmpty()) {
            estimateItems.sumOf { it.labourAmount }
        } else {
            totalRoomLabourCost
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
            estimateItems.sumOf { it.materialAmount }
        } else {
            totalRoomMaterialCost
        }

    val materialAndLabourSubtotal: Double
        get() = if (estimateItems.isNotEmpty()) {
            materialSubtotal + labourSubtotal
        } else {
            totalRoomMaterialCost + totalRoomLabourCost
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
