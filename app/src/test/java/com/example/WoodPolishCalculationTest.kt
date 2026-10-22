package com.example

import com.example.data.model.AdjustmentUnit
import com.example.data.model.WoodPolishEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Validates Woodwork & Polish Measurement calculation engine:
 * 1. Feet and inches conversion
 * 2. Single-side and both-side area calculations
 * 3. Separate frame and shutter calculations
 * 4. Multiplier values (0.18, 0.73, 1.2, 1.5)
 * 5. Plus/minus adjustments with Sq Ft and Multiplier units
 * 6. Specification prompt test case: 20 base * 1.5 + 0.73 - 0.18 = 30.55 sq.ft
 * 7. Labour-only vs Material + Labour calculations
 * 8. Zero clamping and negative warnings
 */
class WoodPolishCalculationTest {

    @Test
    fun testFeetAndInchesConversion() {
        val item = WoodPolishEntity(
            projectId = 1L,
            itemName = "Test Door",
            heightFt = 7.0,
            heightIn = 6.0, // 7.5 ft
            widthFt = 3.0,
            widthIn = 4.0,  // 3.3333 ft
            polishSides = 1.0,
            quantity = 1
        )
        assertEquals(7.5, item.effectiveHeightFt, 0.001)
        assertEquals(3.3333, item.effectiveWidthFt, 0.001)
        assertEquals(25.0, item.grossAreaSqft, 0.01)
    }

    @Test
    fun testPromptExampleCalculation() {
        // From prompt Section 6 Calculation Example:
        // Base Area = 20 Sq Ft
        // Multiplier = 1.5
        // Plus Adjustment = 0.73 Sq Ft
        // Minus Adjustment = 0.18 Sq Ft
        // Final Area = (20 × 1.5) + 0.73 − 0.18 = 30.55 Sq Ft
        val item = WoodPolishEntity(
            projectId = 1L,
            itemName = "Main Door Shutter",
            measurementMethod = "DIRECT_SQFT",
            shutterAreaSqft = 20.0,
            quantity = 1,
            polishSides = 1.0,
            multiplier = 1.5,
            plusAdjustment = 0.73,
            plusAdjustmentUnit = "SQFT",
            minusAdjustment = 0.18,
            minusAdjustmentUnit = "SQFT",
            labourRate = 50.0,
            materialRate = 60.0
        )

        assertEquals(20.0, item.grossAreaSqft, 0.001)
        assertEquals(30.55, item.finalBillableAreaSqft, 0.001)
        assertFalse(item.hasNegativeWarning)

        // Labour only: 30.55 * 50 = 1527.50
        assertEquals(1527.50, item.getEffectiveAmount(isLabourOnly = true), 0.01)

        // Material + Labour: 30.55 * (50 + 60) = 3360.50
        assertEquals(3360.50, item.getEffectiveAmount(isLabourOnly = false), 0.01)
    }

    @Test
    fun testMultiplierAdjustments() {
        // Base area = 100 Sq Ft
        // Multiplier = 1.2
        // Plus adjustment = 0.10 (Multiplier) -> +10 Sq Ft
        // Minus adjustment = 0.05 (Multiplier) -> -5 Sq Ft
        // Final Area = (100 * 1.2) + 10 - 5 = 125 Sq Ft
        val item = WoodPolishEntity(
            projectId = 1L,
            itemName = "Wardrobe",
            measurementMethod = "DIRECT_SQFT",
            shutterAreaSqft = 100.0,
            quantity = 1,
            polishSides = 1.0,
            multiplier = 1.2,
            plusAdjustment = 0.10,
            plusAdjustmentUnit = "MULTIPLIER",
            minusAdjustment = 0.05,
            minusAdjustmentUnit = "MULTIPLIER"
        )

        assertEquals(100.0, item.grossAreaSqft, 0.001)
        assertEquals(125.0, item.finalBillableAreaSqft, 0.001)
    }

    @Test
    fun testBothSidesAndQuantity() {
        // 7ft x 3ft = 21 sqft
        // 2 sides = 42 sqft
        // Qty 2 = 84 sqft
        // Deduction = 4 sqft per unit -> total deduction = 8 sqft
        // Net = 76 sqft
        // Multiplier 1.0 -> 76 sqft
        val item = WoodPolishEntity(
            projectId = 1L,
            itemName = "Double Door",
            heightFt = 7.0,
            heightIn = 0.0,
            widthFt = 3.0,
            widthIn = 0.0,
            polishSides = 2.0,
            quantity = 2,
            deductionAreaSqft = 4.0,
            multiplier = 1.0
        )

        assertEquals(84.0, item.grossAreaSqft, 0.001)
        assertEquals(8.0, item.totalDeductionSqft, 0.001)
        assertEquals(76.0, item.netAreaBeforeAdjustment, 0.001)
        assertEquals(76.0, item.finalBillableAreaSqft, 0.001)
    }

    @Test
    fun testShutterAndFrameSeparateMeasurements() {
        // Shutter = 21 sqft, Frame = 15 sqft
        // Include frame = true -> Base = 36 sqft
        val withFrame = WoodPolishEntity(
            projectId = 1L,
            itemName = "Door with Frame",
            shutterAreaSqft = 21.0,
            frameAreaSqft = 15.0,
            includeFrameInTotal = true,
            polishSides = 1.0,
            quantity = 1
        )
        assertEquals(36.0, withFrame.singleUnitBaseAreaSqft, 0.001)

        // Include frame = false -> Base = 21 sqft
        val withoutFrame = withFrame.copy(includeFrameInTotal = false)
        assertEquals(21.0, withoutFrame.singleUnitBaseAreaSqft, 0.001)
    }

    @Test
    fun testZeroClampingAndNegativeWarning() {
        val item = WoodPolishEntity(
            projectId = 1L,
            itemName = "Small Trim",
            measurementMethod = "DIRECT_SQFT",
            shutterAreaSqft = 5.0,
            quantity = 1,
            polishSides = 1.0,
            multiplier = 1.0,
            minusAdjustment = 10.0, // Exceeds area
            minusAdjustmentUnit = "SQFT"
        )

        assertTrue(item.hasNegativeWarning)
        assertEquals(0.0, item.finalBillableAreaSqft, 0.001)
    }
}
