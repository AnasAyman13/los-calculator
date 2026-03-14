package com.line.of.sight.calc.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.line.of.sight.calc.ui.theme.Indigo
import com.line.of.sight.calc.vm.UnitType

@Composable
fun SegmentedUnitToggle(selected: UnitType, onSelect: (UnitType) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .padding(4.dp)
    ) {
        UnitType.values().forEach { unit ->
            val isSelected = selected == unit

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) Indigo else Color.Transparent,
                animationSpec = tween(durationMillis = 200),
                label = "toggle_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color(0xFF64748B),
                animationSpec = tween(durationMillis = 200),
                label = "toggle_text"
            )

            val segmentModifier = if (isSelected) {
                Modifier
                    .shadow(4.dp, RoundedCornerShape(9.dp))
                    .clip(RoundedCornerShape(9.dp))
                    .background(bgColor)
                    .clickable { onSelect(unit) }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            } else {
                Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(bgColor)
                    .clickable { onSelect(unit) }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            }

            Text(
                text = when (unit) {
                    UnitType.METERS -> "Meters"
                    UnitType.FEET -> "Feet"
                },
                modifier = segmentModifier,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}
