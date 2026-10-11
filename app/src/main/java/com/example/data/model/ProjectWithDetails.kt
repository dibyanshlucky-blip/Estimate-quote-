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

    // Financial calculations - Labour only
    val labourSubtotal: Double
        get() = estimateItems.sumOf { it.labourAmount }

    val labourNetBeforeTax: Double
        get() = maxOf(0.0, labourSubtotal + project.additionalCharges - project.discountAmount)

    val labourTaxAmount: Double
        get() = if (project.isTaxEnabled) labourNetBeforeTax * (project.taxPercent / 100.0) else 0.0

    val labourGrandTotal: Double
        get() = labourNetBeforeTax + labourTaxAmount

    // Financial calculations - Material + Labour
    val materialSubtotal: Double
        get() = estimateItems.sumOf { it.materialAmount }

    val materialAndLabourSubtotal: Double
        get() = materialSubtotal + labourSubtotal

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

    // Payments summary
    val totalPaid: Double
        get() = payments.sumOf { it.amount }

    val balancePayable: Double
        get() = maxOf(0.0, currentGrandTotal - totalPaid)
}
