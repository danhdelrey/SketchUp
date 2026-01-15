package com.example.sketchup.view.features.drawing.state

import androidx.compose.ui.graphics.Color
import com.example.sketchup.data.model.DrawingPath

data class DrawingState(
    val paths: List<DrawingPath> = emptyList(),
    val currentDrawingPath: DrawingPath? = null,
    val selectedColor: Color = Color.Black,
    val brushSize: Float = 10f,
    val isEraseMode: Boolean = false,
    val currentTouchPosition: androidx.compose.ui.geometry.Offset? = null
)