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
    val notes: String = "",
    val detailedData: String = "" // Serialized JSON containing Wall & Ceiling measurements, deductions, paints, putty, primer
) {
    val effectiveLength: Double
        get() = lengthFt + (lengthIn / 12.0)

    val effectiveWidth: Double
        get() = widthFt + (widthIn / 12.0)

    val effectiveHeight: Double
        get() = heightFt + (heightIn / 12.0)

    val floorCeilingAreaSqft: Double
        get() {
            if (detailedData.isNotBlank()) {
                val details = RoomDetailsJsonConverter.deserialize(detailedData, this)
                val grossCeil = details.ceilingEntries.sumOf { it.grossAreaSqft }
                val ceilDed = details.ceilingDeductions.sumOf { it.deductionAreaSqft }
                return max(0.0, grossCeil - ceilDed)
            }
            return effectiveLength * effectiveWidth
        }

    val grossWallAreaSqft: Double
        get() {
            if (detailedData.isNotBlank()) {
                val details = RoomDetailsJsonConverter.deserialize(detailedData, this)
                return details.wallEntries.sumOf { it.grossAreaSqft }
            }
            return 2 * (effectiveLength + effectiveWidth) * effectiveHeight
        }

    val totalDoorAreaSqft: Double
        get() = doorCount * (doorWidthFt * doorHeightFt)

    val totalWindowAreaSqft: Double
        get() = windowCount * (windowWidthFt * windowHeightFt)

    val totalDeductionsSqft: Double
        get() {
            if (detailedData.isNotBlank()) {
                val details = RoomDetailsJsonConverter.deserialize(detailedData, this)
                return details.wallDeductions.sumOf { it.deductionAreaSqft }
            }
            return totalDoorAreaSqft + totalWindowAreaSqft + additionalDeductionsSqft
        }

    fun getNetWallAreaSqft(deductOpenings: Boolean): Double {
        return if (deductOpenings) {
            max(0.0, grossWallAreaSqft - totalDeductionsSqft)
        } else {
            grossWallAreaSqft
        }
    }

    val grossCeilingAreaSqft: Double
        get() {
            if (detailedData.isNotBlank()) {
                val details = RoomDetailsJsonConverter.deserialize(detailedData, this)
                return details.ceilingEntries.sumOf { it.grossAreaSqft }
            }
            return effectiveLength * effectiveWidth
        }

    val totalCeilingDeductionsSqft: Double
        get() {
            if (detailedData.isNotBlank()) {
                val details = RoomDetailsJsonConverter.deserialize(detailedData, this)
                return details.ceilingDeductions.sumOf { it.deductionAreaSqft }
            }
            return 0.0
        }

    val netCeilingAreaSqft: Double
        get() = max(0.0, grossCeilingAreaSqft - totalCeilingDeductionsSqft)

    fun getTotalPaintableAreaSqft(deductOpenings: Boolean): Double {
        val wallArea = if (wallPaintingEnabled) getNetWallAreaSqft(deductOpenings) else 0.0
        val ceilingArea = if (ceilingPaintingEnabled) netCeilingAreaSqft else 0.0
        return wallArea + ceilingArea
    }

    fun getParsedDetails(): RoomDetailsJsonConverter.ParsedRoomDetails {
        return RoomDetailsJsonConverter.deserialize(detailedData, this)
    }
}
