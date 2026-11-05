package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Blind
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Sanitizer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccommodationBadge(
    label: String,
    icon: ImageVector? = null,
    isVerified: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    modifier: Modifier = Modifier
) {
    val displayIcon = icon ?: when {
        label.contains("Wheelchair", ignoreCase = true) || label.contains("Ramp", ignoreCase = true) -> Icons.Default.Accessible
        label.contains("ASL", ignoreCase = true) || label.contains("Sign", ignoreCase = true) -> Icons.Default.Hearing
        label.contains("Sensory", ignoreCase = true) || label.contains("Quiet", ignoreCase = true) -> Icons.Outlined.Spa
        label.contains("Braille", ignoreCase = true) || label.contains("Vision", ignoreCase = true) -> Icons.Default.Blind
        label.contains("Hearing Loop", ignoreCase = true) -> Icons.Default.VolumeUp
        label.contains("AAC", ignoreCase = true) || label.contains("Speech", ignoreCase = true) -> Icons.Default.RecordVoiceOver
        label.contains("Animal", ignoreCase = true) -> Icons.Default.Pets
        label.contains("Restroom", ignoreCase = true) -> Icons.Default.Wc
        else -> Icons.Default.CheckCircle
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("accommodation_badge_$label")
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(1.dp, contentColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = displayIcon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
