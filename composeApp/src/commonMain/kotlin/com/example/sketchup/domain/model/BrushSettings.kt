package com.example.sketchup.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Domain model representing brush settings.
 */
data class BrushSettings(
    val color: Color = Color.Black,
    val size: Float = 10f,
    val isEraseMode: Boolean = false
)
