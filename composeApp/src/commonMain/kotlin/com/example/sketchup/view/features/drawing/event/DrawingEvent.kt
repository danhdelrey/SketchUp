package com.example.sketchup.view.features.drawing.event

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

sealed interface DrawingEvent {
    data class StartDraw(val offset: Offset) : DrawingEvent
    data class UpdateDraw(val offset: Offset) : DrawingEvent
    data object EndDraw : DrawingEvent
    data object Undo : DrawingEvent
    data object Redo : DrawingEvent
    data class PickColor(val color: Color) : DrawingEvent
    data class ChangeBrushSize(val size: Float) : DrawingEvent
    data class SavePng(val bitmap: ImageBitmap) : DrawingEvent
}