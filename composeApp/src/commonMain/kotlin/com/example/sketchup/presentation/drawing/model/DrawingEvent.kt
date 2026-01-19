package com.example.sketchup.presentation.drawing.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * UI events for the drawing screen.
 * Represents all possible user interactions.
 */
sealed interface DrawingEvent {
    data class StartDraw(val offset: Offset) : DrawingEvent
    data class UpdateDraw(val offset: Offset) : DrawingEvent
    data object EndDraw : DrawingEvent
    data object Undo : DrawingEvent
    data object Redo : DrawingEvent
    data class PickColor(val color: Color) : DrawingEvent
    data class ChangeOpacity(val opacity: Float) : DrawingEvent
    data class ChangeBrushSize(val size: Float) : DrawingEvent
    data class SavePng(val bytes: ByteArray) : DrawingEvent
    data object ToggleEraseMode : DrawingEvent
    data object Clear : DrawingEvent
}
