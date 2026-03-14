package com.line.of.sight.calc.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.line.of.sight.calc.ui.components.SegmentedUnitToggle
import com.line.of.sight.calc.ui.theme.Indigo
import com.line.of.sight.calc.ui.theme.LOSTheme
import com.line.of.sight.calc.ui.theme.Teal
import com.line.of.sight.calc.vm.CalculatorViewModel
import com.line.of.sight.calc.vm.UnitType
import kotlin.math.max
import kotlin.math.min

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var resultsVisible by remember { mutableStateOf(false) }

    // ✅ FIX: Only show results when calculationVersion changes (i.e. user pressed Calculate)
    // NOT on every totalString change (which fires while typing)
    LaunchedEffect(uiState.calculationVersion) {
        if (uiState.calculationVersion > 0) {
            resultsVisible = false
            resultsVisible = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9FAFB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
        ) {
            CompactHeader()

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {

                InfoSectionCard()

                Spacer(modifier = Modifier.height(20.dp))

                InputSectionCard(
                    h1 = uiState.h1Input,
                    h2 = uiState.h2Input,
                    unit = uiState.unit,
                    unitLabel = uiState.unitLabel,
                    errorMessage = uiState.errorMessage,
                    onH1Change = viewModel::onH1Change,
                    onH2Change = viewModel::onH2Change,
                    onUnitChange = viewModel::onUnitChange,
                    onCalculate = viewModel::calculate,
                    onClear = viewModel::clear
                )

                Spacer(modifier = Modifier.height(28.dp))

                AnimatedVisibility(
                    visible = resultsVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        initialOffsetY = { 80 },
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionDivider("Calculation Results")

                        ResultCard(
                            icon = Icons.Default.Settings,
                            label = "Radio Horizon — Station 1",
                            value = uiState.d1String,
                            accent = Indigo
                        )
                        ResultCard(
                            icon = Icons.Default.Settings,
                            label = "Radio Horizon — Station 2",
                            value = uiState.d2String,
                            accent = Teal
                        )

                        TotalResultCard(value = uiState.totalString)

                        Spacer(modifier = Modifier.height(12.dp))

                        SectionDivider("LOS Visualization")

                        LOSDiagram(
                            h1 = uiState.h1Input.toDoubleOrNull() ?: 0.0,
                            h2 = uiState.h2Input.toDoubleOrNull() ?: 0.0,
                            distanceStr = uiState.totalString,
                            unitLabel = uiState.unitLabel
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun CompactHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Brush.horizontalGradient(listOf(Indigo, Teal))),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CellTower,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "LOS Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Line-of-Sight Radio Calculator",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun InfoSectionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Indigo.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Indigo, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "What this tool does",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Indigo
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This calculator estimates the radio line-of-sight distance between two antennas based on their heights above ground.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun InputSectionCard(
    h1: String, h2: String, unit: UnitType, unitLabel: String, errorMessage: String?,
    onH1Change: (String) -> Unit, onH2Change: (String) -> Unit,
    onUnitChange: (UnitType) -> Unit,
    onCalculate: () -> Unit, onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Text(
                "Antenna Heights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(20.dp))

            HeightInputField(
                value = h1,
                onValueChange = onH1Change,
                label = "1st Station Height",
                placeholder = "Example: 50",
                helperText = "Enter antenna height above ground",
                unitLabel = unitLabel
            )

            Spacer(modifier = Modifier.height(16.dp))

            HeightInputField(
                value = h2,
                onValueChange = onH2Change,
                label = "2nd Station Height",
                placeholder = "Example: 15",
                helperText = "Enter antenna height (0 if ground receiver)",
                unitLabel = unitLabel
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Height Units",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            SegmentedUnitToggle(selected = unit, onSelect = onUnitChange)

            Spacer(modifier = Modifier.height(32.dp))

            // ✅ FIX: Both buttons use weight() so Compose divides the row fairly.
            // weight(1.6f) + weight(1f) = Calculate gets ~61%, Clear gets ~39%
            // Clear always has enough space to show the full word "Clear"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calculate — primary action, slightly wider
                Button(
                    onClick = onCalculate,
                    modifier = Modifier
                        .weight(1.6f)           // ← was weight(2f), same idea but Clear gets more room
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Calculate",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        softWrap = false         // ← never wraps to second line
                    )
                }

                // Clear — secondary action
                // weight(1f) guarantees a proportional share of the row width.
                // contentPadding gives the text breathing room inside the button.
                // softWrap = false means it will never try to wrap "Clear".
                Button(
                    onClick = onClear,
                    modifier = Modifier
                        .weight(1f)              // ← was weight(1f) but with weight(2f) on sibling
                        .height(52.dp),          //   that left only 33% — now Clear gets ~39%
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp), // ← explicit padding
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF3F4F6),
                        contentColor = Color.Gray
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Clear",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        softWrap = false         // ← never wraps or clips
                    )
                }
            }
        }
    }
}

@Composable
private fun HeightInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    helperText: String,
    unitLabel: String
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(Icons.Default.Straighten, null, tint = Indigo, modifier = Modifier.size(20.dp)) },
            suffix = { Text(unitLabel, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Indigo,
                unfocusedBorderColor = Color(0xFFE5E7EB)
            )
        )
        Text(
            text = helperText,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
        )
    }
}

@Composable
private fun ResultCard(icon: ImageVector, label: String, value: String, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
private fun TotalResultCard(value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Indigo, Teal)))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Maximum Line-of-Sight", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}

@Composable
fun LOSDiagram(h1: Double, h2: Double, distanceStr: String, unitLabel: String) {
    val maxH = max(100.0, max(h1, h2))
    val normH1 = (h1 / maxH).toFloat().coerceIn(0.1f, 1f)
    val normH2 = (h2 / maxH).toFloat().coerceIn(0.1f, 1f)

    val animH1 by animateFloatAsState(normH1, label = "h1")
    val animH2 by animateFloatAsState(normH2, label = "h2")
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)) {
            val w = size.width
            val h = size.height
            val groundY = h * 0.8f
            val maxAntHeight = h * 0.5f

            val earthPath = Path().apply {
                moveTo(0f, groundY + 10f)
                quadraticTo(w / 2, groundY - 20f, w, groundY + 10f)
            }
            drawPath(earthPath, Color(0xFFE5E7EB), style = Stroke(width = 4f, cap = StrokeCap.Round))

            val ant1X = w * 0.15f
            val ant1TopY = groundY - (maxAntHeight * animH1)
            drawLine(Indigo, Offset(ant1X, groundY), Offset(ant1X, ant1TopY), strokeWidth = 6f, cap = StrokeCap.Round)
            drawCircle(Indigo.copy(alpha = 0.2f), 12f, Offset(ant1X, ant1TopY))

            val ant2X = w * 0.85f
            val ant2TopY = groundY - (maxAntHeight * animH2)
            drawLine(Teal, Offset(ant2X, groundY), Offset(ant2X, ant2TopY), strokeWidth = 6f, cap = StrokeCap.Round)
            drawCircle(Teal.copy(alpha = 0.2f), 12f, Offset(ant2X, ant2TopY))

            val midX = (ant1X + ant2X) / 2
            val midY = min(ant1TopY, ant2TopY) - 15f
            val losPath = Path().apply {
                moveTo(ant1X, ant1TopY)
                quadraticTo(midX, midY, ant2X, ant2TopY)
            }
            drawPath(
                losPath,
                Color(0xFFF59E0B),
                style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
            )

            val styleMedium = TextStyle(fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            val styleSmall  = TextStyle(fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Normal)

            val distLayout = textMeasurer.measure(distanceStr, style = styleMedium)
            drawText(textLayoutResult = distLayout, topLeft = Offset(midX - distLayout.size.width / 2f, midY - 20f))

            if (h1 > 0) {
                val h1Str = "${h1.toInt()}$unitLabel"
                val h1Layout = textMeasurer.measure(h1Str, style = styleSmall)
                drawText(textLayoutResult = h1Layout, topLeft = Offset(ant1X - h1Layout.size.width - 12f, ant1TopY + 10f))
            }

            if (h2 > 0) {
                val h2Str = "${h2.toInt()}$unitLabel"
                val h2Layout = textMeasurer.measure(h2Str, style = styleSmall)
                drawText(textLayoutResult = h2Layout, topLeft = Offset(ant2X + 12f, ant2TopY + 10f))
            }
        }
    }
}

@Composable
private fun SectionDivider(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Divider(modifier = Modifier.weight(1f), color = Color(0xFFF3F4F6))
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    LOSTheme { CalculatorScreen() }
}