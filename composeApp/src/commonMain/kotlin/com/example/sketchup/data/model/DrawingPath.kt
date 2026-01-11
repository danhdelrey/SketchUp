package com.example.sketchup.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class DrawingPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)