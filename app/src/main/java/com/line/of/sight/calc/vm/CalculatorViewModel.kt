package com.line.of.sight.calc.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

enum class UnitType { METERS, FEET }

data class UiState(
    val h1Input: String = "",
    val h2Input: String = "",
    val unit: UnitType = UnitType.METERS,
    val d1String: String = "0 km",
    val d2String: String = "0 km",
    val totalString: String = "0 km",
    val unitLabel: String = "km",
    val errorMessage: String? = null,
   
    val calculationVersion: Int = 0
)

fun radioHorizon(height: Double, unit: UnitType): Double {
    if (height <= 0.0) return 0.0
    return if (unit == UnitType.METERS) 3.57 * kotlin.math.sqrt(height)
    else 1.22 * kotlin.math.sqrt(height)
}

class CalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

     
    fun onH1Change(s: String) {
        _uiState.update { it.copy(h1Input = s, errorMessage = null) }
    }

    fun onH2Change(s: String) {
        _uiState.update { it.copy(h2Input = s, errorMessage = null) }
    }

    fun onUnitChange(newUnit: UnitType) {
        val currentState = _uiState.value
        if (currentState.unit == newUnit) return

        val h1 = currentState.h1Input.toDoubleOrNull() ?: 0.0
        val h2 = currentState.h2Input.toDoubleOrNull() ?: 0.0

        val (newH1, newH2) = if (newUnit == UnitType.FEET)
            (h1 * 3.28084) to (h2 * 3.28084)
        else
            (h1 * 0.3048) to (h2 * 0.3048)

        _uiState.update {
            it.copy(
                unit = newUnit,
                h1Input = if (h1 == 0.0) "" else String.format(Locale.US, "%.2f", newH1),
                h2Input = if (h2 == 0.0) "" else String.format(Locale.US, "%.2f", newH2),
                unitLabel = if (newUnit == UnitType.METERS) "km" else "miles"
            )
        }
        // No autoCalculate — user must press Calculate again
    }

    //  The ONLY entry point for calculation
    fun calculate() {
        val state = _uiState.value
        if (state.h1Input.isBlank() && state.h2Input.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter at least one antenna height.") }
            return
        }
        performCalculation()
    }

    private fun performCalculation() {
        val state = _uiState.value
        val h1 = state.h1Input.toDoubleOrNull() ?: 0.0
        val h2 = state.h2Input.toDoubleOrNull() ?: 0.0
        val d1 = radioHorizon(h1, state.unit)
        val d2 = radioHorizon(h2, state.unit)
        val total = d1 + d2
        val label = if (state.unit == UnitType.METERS) "km" else "miles"

        _uiState.update {
            it.copy(
                d1String = String.format(Locale.US, "%.2f %s", d1, label),
                d2String = String.format(Locale.US, "%.2f %s", d2, label),
                totalString = String.format(Locale.US, "%.2f %s", total, label),
                unitLabel = label,
                errorMessage = null,
                calculationVersion = it.calculationVersion + 1  //  signals UI to reveal results
            )
        }
    }

    fun clear() {
        _uiState.update { UiState(unit = it.unit) }
    }
}