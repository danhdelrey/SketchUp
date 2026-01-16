package com.example.sketchup.presentation.drawing

import com.example.sketchup.domain.model.Brush
import com.example.sketchup.domain.model.Drawing
import androidx.compose.ui.geometry.Offset

/**
 * UI State for drawing screen
 * Represents what the UI needs to render
 */
data class DrawingUiState(
    val drawing: Drawing = Drawing.empty(),
    val currentBrush: Brush = Brush.DEFAULT,
    val currentStrokePoints: List<Offset> = emptyList(),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
) {
    val hasUnsavedChanges: Boolean
        get() = drawing.strokes.isNotEmpty()
}

