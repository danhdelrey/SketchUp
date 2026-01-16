package com.example.sketchup.presentation.drawing.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.sketchup.domain.model.DrawingPath

/**
 * UI state for the drawing screen.
 * Represents the complete state of the drawing UI.
 */
data class DrawingState(
    val paths: List<DrawingPath> = emptyList(),
    val currentDrawingPath: DrawingPath? = null,
    val selectedColor: Color = Color.Black,
    val brushSize: Float = 10f,
    val isEraseMode: Boolean = false,
    val currentTouchPosition: Offset? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)
