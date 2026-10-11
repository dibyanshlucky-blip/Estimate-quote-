package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.math.max

@Entity(
    tableName = "room_measurements",
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
data class RoomMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val roomName: String,
    val lengthFt: Double = 0.0,
    val lengthIn: Double = 0.0,
    val widthFt: Double = 0.0,
    val widthIn: Double = 0.0,
    val heightFt: Double = 10.0,
    val heightIn: Double = 0.0,
    val doorCount: Int = 1,
    val doorWidthFt: Double = 3.0,
    val doorHeightFt: Double = 7.0,
    val windowCount: Int = 1,
    val windowWidthFt: Double = 4.0,
    val windowHeightFt: Double = 4.0,
    val additionalDeductionsSqft: Double = 0.0,
    val wallPaintingEnabled: Boolean = true,
    val ceilingPaintingEnabled: Boolean = true,
    val notes: String = ""
) {
    val effectiveLength: Double
        get() = lengthFt + (lengthIn / 12.0)

    val effectiveWidth: Double
        get() = widthFt + (widthIn / 12.0)

    val effectiveHeight: Double
        get() = heightFt + (heightIn / 12.0)

    val floorCeilingAreaSqft: Double
        get() = effectiveLength * effectiveWidth

    val grossWallAreaSqft: Double
        get() = 2 * (effectiveLength + effectiveWidth) * effectiveHeight

    val totalDoorAreaSqft: Double
        get() = doorCount * (doorWidthFt * doorHeightFt)

    val totalWindowAreaSqft: Double
        get() = windowCount * (windowWidthFt * windowHeightFt)

    val totalDeductionsSqft: Double
        get() = totalDoorAreaSqft + totalWindowAreaSqft + additionalDeductionsSqft

    fun getNetWallAreaSqft(deductOpenings: Boolean): Double {
        return if (deductOpenings) {
            max(0.0, grossWallAreaSqft - totalDeductionsSqft)
        } else {
            grossWallAreaSqft
        }
    }

    fun getTotalPaintableAreaSqft(deductOpenings: Boolean): Double {
        val wallArea = if (wallPaintingEnabled) getNetWallAreaSqft(deductOpenings) else 0.0
        val ceilingArea = if (ceilingPaintingEnabled) floorCeilingAreaSqft else 0.0
        return wallArea + ceilingArea
    }
}
