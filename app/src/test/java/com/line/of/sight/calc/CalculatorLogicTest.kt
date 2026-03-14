package com.line.of.sight.calc

import com.line.of.sight.calc.vm.radioHorizonKm
import com.line.of.sight.calc.vm.totalLOSKm
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorLogicTest {

    @Test
    fun testRadioHorizonKm_PositiveInput_ReturnsCorrectValue() {
        // According to formula: d = 3.57 * sqrt(h)
        val h = 100.0
        val expected = 3.57 * kotlin.math.sqrt(h) // 35.7
        val actual = radioHorizonKm(h)
        assertEquals(expected, actual, 0.001)
    }

    @Test
    fun testRadioHorizonKm_ZeroInput_ReturnsZero() {
        val h = 0.0
        val actual = radioHorizonKm(h)
        assertEquals(0.0, actual, 0.001)
    }

    @Test
    fun testRadioHorizonKm_NegativeInput_ReturnsZero() {
        val h = -10.0
        val actual = radioHorizonKm(h)
        assertEquals(0.0, actual, 0.001)
    }

    @Test
    fun testTotalLOSKm_WithBothHeights() {
        val h1 = 100.0
        val h2 = 400.0
        
        // expected d1 = 35.7
        // expected d2 = 3.57 * 20 = 71.4
        // expected total = 107.1
        val expected = radioHorizonKm(h1) + radioHorizonKm(h2)
        val actual = totalLOSKm(h1, h2)
        assertEquals(expected, actual, 0.001)
    }
}
