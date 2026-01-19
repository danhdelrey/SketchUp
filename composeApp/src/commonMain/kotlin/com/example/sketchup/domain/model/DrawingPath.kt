package com.example.sketchup.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Domain model representing a drawing path.
 * This is a pure business entity independent of any framework.
 */
data class DrawingPath(
    val points: List<Offset>,
    val color: Color,
    val opacity: Float,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)
