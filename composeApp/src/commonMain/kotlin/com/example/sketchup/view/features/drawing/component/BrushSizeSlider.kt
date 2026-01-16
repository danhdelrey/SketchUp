package com.example.sketchup.view.features.drawing.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BrushSizeSlider(
    color: Color,
    size: Float,
    onSizeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 1f..100f
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            value = size,
            onValueChange = onSizeChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                activeTrackColor = color,
                activeTickColor = color,
                thumbColor = color,
                inactiveTrackColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = size.toInt().toString(),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
