package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PainScaleVisualizer(
    currentLevel: Int,
    currentLocation: String,
    currentQuality: String,
    onLevelSelected: (Int) -> Unit,
    onLocationSelected: (String) -> Unit,
    onQualitySelected: (String) -> Unit,
    onSpeakAssessment: () -> Unit,
    highContrastMode: Boolean,
    modifier: Modifier = Modifier
) {
    val painLevels = listOf(
        PainFaceData(0, "0 - No Hurt", Color(0xFF2E7D32)),
        PainFaceData(2, "2 - Mild", Color(0xFF689F38)),
        PainFaceData(4, "4 - Moderate", Color(0xFFFBC02D)),
        PainFaceData(6, "6 - Severe", Color(0xFFF57C00)),
        PainFaceData(8, "8 - Very Severe", Color(0xFFD32F2F)),
        PainFaceData(10, "10 - Worst", Color(0xFF7B1FA2))
    )

    val locations = listOf(
        "Head & Neck", "Chest", "Abdomen", "Lower Back",
        "Joints / Limbs", "Neuropathic / Nerve", "Surgical Site", "Generalized"
    )

    val qualities = listOf(
        "Aching", "Sharp / Stabbing", "Burning",
        "Throbbing", "Cramping", "Dull Pressure", "Pins & Needles"
    )

    val activeColor = painLevels.minByOrNull { kotlin.math.abs(it.level - currentLevel) }?.color ?: Color(0xFFF57C00)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (highContrastMode) CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(Color.White)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("pain_scale_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Wong-Baker Pain Assessment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Visual clinical communication for pain intensity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(activeColor.copy(alpha = 0.2f))
                        .border(1.5.dp, activeColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Level $currentLevel / 10",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = activeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Faces Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                painLevels.forEach { face ->
                    val isSelected = kotlin.math.abs(face.level - currentLevel) <= 1
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .testTag("pain_face_${face.level}")
                            .clickable { onLevelSelected(face.level) }
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) face.color.copy(alpha = 0.25f) else Color.Transparent)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) face.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            PainFaceCanvas(level = face.level, faceColor = face.color)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${face.level}",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (isSelected) face.color else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slider for fine-tuning
            Slider(
                value = currentLevel.toFloat(),
                onValueChange = { onLevelSelected(it.toInt()) },
                valueRange = 0f..10f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = activeColor,
                    activeTrackColor = activeColor
                ),
                modifier = Modifier.fillMaxWidth().testTag("pain_level_slider")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pain Location
            Text(
                text = "Pain Location:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(locations) { loc ->
                    val selected = loc == currentLocation
                    FilterChip(
                        selected = selected,
                        onClick = { onLocationSelected(loc) },
                        label = { Text(loc, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("location_chip_$loc")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pain Quality
            Text(
                text = "Pain Sensation & Quality:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(qualities) { qual ->
                    val selected = qual == currentQuality
                    FilterChip(
                        selected = selected,
                        onClick = { onQualitySelected(qual) },
                        label = { Text(qual, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        modifier = Modifier.testTag("quality_chip_$qual")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Speak Pain Assessment Aloud Button
            Button(
                onClick = onSpeakAssessment,
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("speak_pain_assessment_button")
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Announce Pain Report to Clinician",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

data class PainFaceData(val level: Int, val label: String, val color: Color)

@Composable
fun PainFaceCanvas(level: Int, faceColor: Color) {
    Canvas(modifier = Modifier.size(28.dp)) {
        val strokeWidth = 2.dp.toPx()
        val eyeRadius = 2.dp.toPx()
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Eyes
        val eyeY = centerY - 4.dp.toPx()
        val leftEyeX = centerX - 6.dp.toPx()
        val rightEyeX = centerX + 6.dp.toPx()

        drawCircle(color = faceColor, radius = eyeRadius, center = Offset(leftEyeX, eyeY))
        drawCircle(color = faceColor, radius = eyeRadius, center = Offset(rightEyeX, eyeY))

        // Eyebrows / Furrow for higher levels
        if (level >= 6) {
            drawLine(
                color = faceColor,
                start = Offset(leftEyeX - 3.dp.toPx(), eyeY - 4.dp.toPx()),
                end = Offset(leftEyeX + 3.dp.toPx(), eyeY - 2.dp.toPx()),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = faceColor,
                start = Offset(rightEyeX + 3.dp.toPx(), eyeY - 4.dp.toPx()),
                end = Offset(rightEyeX - 3.dp.toPx(), eyeY - 2.dp.toPx()),
                strokeWidth = strokeWidth
            )
        }

        // Mouth arc according to pain level
        when (level) {
            0 -> {
                // Wide happy smile
                drawArc(
                    color = faceColor,
                    startAngle = 10f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(centerX - 8.dp.toPx(), centerY - 2.dp.toPx()),
                    size = Size(16.dp.toPx(), 12.dp.toPx()),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            2 -> {
                // Mild smile
                drawArc(
                    color = faceColor,
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(centerX - 6.dp.toPx(), centerY),
                    size = Size(12.dp.toPx(), 8.dp.toPx()),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            4 -> {
                // Neutral flat line
                drawLine(
                    color = faceColor,
                    start = Offset(centerX - 6.dp.toPx(), centerY + 5.dp.toPx()),
                    end = Offset(centerX + 6.dp.toPx(), centerY + 5.dp.toPx()),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
            6 -> {
                // Frown / slight sad arc
                drawArc(
                    color = faceColor,
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(centerX - 7.dp.toPx(), centerY + 5.dp.toPx()),
                    size = Size(14.dp.toPx(), 8.dp.toPx()),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            8 -> {
                // Deep distress frown
                drawArc(
                    color = faceColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - 8.dp.toPx(), centerY + 4.dp.toPx()),
                    size = Size(16.dp.toPx(), 10.dp.toPx()),
                    style = Stroke(width = strokeWidth + 0.5f, cap = StrokeCap.Round)
                )
            }
            else -> {
                // Intense crying / open grimace
                drawArc(
                    color = faceColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - 8.dp.toPx(), centerY + 3.dp.toPx()),
                    size = Size(16.dp.toPx(), 12.dp.toPx()),
                    style = Stroke(width = strokeWidth + 0.5f, cap = StrokeCap.Round)
                )
                // Tear drops
                drawCircle(color = faceColor, radius = 1.5.dp.toPx(), center = Offset(leftEyeX - 1.dp.toPx(), eyeY + 4.dp.toPx()))
                drawCircle(color = faceColor, radius = 1.5.dp.toPx(), center = Offset(rightEyeX + 1.dp.toPx(), eyeY + 4.dp.toPx()))
            }
        }
    }
}
